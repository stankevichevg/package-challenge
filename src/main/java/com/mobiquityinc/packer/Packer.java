package com.mobiquityinc.packer;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.exception.APIException.SystemException;
import com.mobiquityinc.packer.domain.PackTask;
import com.mobiquityinc.packer.domain.Package;
import com.mobiquityinc.packer.domain.Thing;
import com.mobiquityinc.packer.io.PackTaskReader;
import com.mobiquityinc.packer.io.PackageWriter;
import com.mobiquityinc.packer.validation.ValidationRule;
import com.mobiquityinc.packer.validation.task.EachThingSatisfyRule;
import com.mobiquityinc.packer.validation.task.MaxPackageWeightRule;
import com.mobiquityinc.packer.validation.task.MaxThingsNumberRule;
import com.mobiquityinc.packer.validation.thing.MaxThingCostRule;
import com.mobiquityinc.packer.validation.thing.MaxThingWeightRule;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.mobiquityinc.packer.validation.ValidationRule.allOf;
import static java.util.stream.Collectors.toList;

/**
 * Solves list of packaging tasks in a separate thread pool. Use {@link #pack(List)} method to start the process.
 * The algorithm is aimed to solve general knapsack problem for which there is no any polynomial complexity algorithms.
 * For a moment it uses simple brute force algorithm (without branch-and-bound improvement) to find a solution.
 * Only slight modification was made to the canonical implementation to find solution with the minimum weight.
 */
public class Packer implements AutoCloseable {

    private static final ValidationRule<PackTask> DEFAULT_TASK_VALIDATION_RULE;

    private static final int DEFAULT_THREAD_POOL_SIZE = 4;

    static {
        DEFAULT_TASK_VALIDATION_RULE = allOf(
            new MaxPackageWeightRule(),
            new MaxThingsNumberRule(),
            new EachThingSatisfyRule(
                allOf(
                    new MaxThingCostRule(),
                    new MaxThingWeightRule()
                )
            )
        );
    }

    private final ValidationRule<PackTask> validationRule;
    private final ExecutorService executorService;

    public Packer(int threadPoolSize, ValidationRule<PackTask> validationRule) {
        this.validationRule = validationRule;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public Packer() {
        this(DEFAULT_THREAD_POOL_SIZE, DEFAULT_TASK_VALIDATION_RULE);
    }

    @Override
    public void close() throws Exception {
        this.executorService.shutdownNow();
    }

    /**
     * Solve packaging problem for the given list of tasks. For each task it creates {@link CallablePackTask}
     * and submits to the executor service to do work in a separate thread. When all tasks are submitted it's
     * waiting until all tasks will complete and collects results.
     *
     * @param tasks list of tasks to solve
     * @return List of built packages as a results of the task executions
     * @throws APIException if something is going wrong
     */
    public List<Package> pack(List<PackTask> tasks) throws APIException {
        validationRule.validateAll(tasks);
        final List<Future<Package>> futures = tasks.stream()
            .map(task -> executorService.submit(new CallablePackTask(task))).collect(toList());
        final List<Package> result = new ArrayList<>();
        for (Future<Package> f : futures) {
            try {
                result.add(f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * The main library method.
     * Reads input file with the given path and returns result in a serialized to string format.
     *
     * @param filePath path of a file to read tasks from
     * @return string representation of the result
     * @throws APIException if something is going wrong
     */
    public static String pack(String filePath) throws APIException {
        try {
            return pack(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            throw new APIException.FileNotFoundException(e);
        }
    }

    /**
     * The main library method.
     * Reads input file with the given path and returns result in a serialized to string format.
     *
     * @param inputStream input stream to read tasks from
     * @return string representation of the result
     * @throws APIException if something is going wrong
     */
    protected static String pack(InputStream inputStream) throws APIException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (
            PackTaskReader reader = new PackTaskReader(inputStream);
            Packer packer = new Packer();
            PackageWriter writer = new PackageWriter(out);) {
            final List<PackTask> tasks = reader.readAll();
            packer.pack(tasks).forEach(writer::write);
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            throw new SystemException(e);
        }
        return new String(out.toByteArray());
    }

    /**
     * Task to submit for execution in a separate thread. Solves the given task and returns built package.
     */
    private static final class CallablePackTask implements Callable<Package> {

        private static final ValidationRule<PackTask> UP_TO_15_THINGS = new MaxThingsNumberRule(15);

        private final PackTask task;

        private CallablePackTask(PackTask task) {
            this.task = task;
        }

        @Override
        public Package call() throws Exception {
            return bruteForce(this.task);
        }

        /**
         * Apply canonical knapsack problem brute force algorithm to solve the task.
         *
         * @param task the task to solve.
         * @return built package
         */
        public Package bruteForce(PackTask task) throws APIException {
            UP_TO_15_THINGS.validate(task);
            double bestCost = 0;
            double bestWeight = Double.MAX_VALUE;
            int bestPack = 0;
            // use the fact that we can have up to 15 elements (use bits of short instead of int)
            for (short i = 1; i < (i << task.getThings().size()); i++) {
                double costSum = 0;
                double weightSum = 0;
                for (short j = 0; j < task.getThings().size(); j++) {
                    if ((i & (1 << j)) > 0) {
                        final Thing includedThing = task.getThings().get(j);
                        costSum += includedThing.getCost();
                        weightSum += includedThing.getWeight();
                    }
                }
                if (weightSum < task.getMaxWeight() && isBetter(weightSum, costSum, bestWeight, bestCost)) {
                    bestPack = i;
                    bestWeight = weightSum;
                    bestCost = costSum;
                }
            }
            return createPackage(task.getThings(), bestPack);
        }

        private boolean isBetter(double weightSum, double costSum, double bestWeight, double bestCost) {
            return costSum > bestCost || costSum == bestCost && bestWeight > weightSum;
        }

        private Package createPackage(List<Thing> allThings, int set) {
            final List<Thing> packageThings = new ArrayList<>();
            for (int j = 0; j < allThings.size(); j++) {
                if ((set & (1 << j)) > 0) {
                    packageThings.add(allThings.get(j));
                }
            }
            return new Package(packageThings);
        }
    }

}
