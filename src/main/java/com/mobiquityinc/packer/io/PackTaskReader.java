package com.mobiquityinc.packer.io;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.domain.PackTask;
import com.mobiquityinc.packer.domain.Thing;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * Reads task to package from the given input stream according the documented format.
 * Method {@link #readNext()} is used to read the next task,
 * if it's the end of stream the method will throw {@link IllegalStateException}.
 * Use {@link #hasNext} to define if there is something to read.
 *
 * The class also implements convenient method {@link #readAll()} to read all tasks.
 */
public class PackTaskReader implements AutoCloseable {

    private Scanner scanner;

    public PackTaskReader(InputStream is) {
        this.scanner = new Scanner(is);
    }

    public boolean hasNext() {
        return scanner.hasNextLine();
    }

    public PackTask readNext() throws APIException {
        if (!hasNext()) {
            throw new IllegalStateException("All tasks has been already read");
        }
        checkNextToken(Token.DOUBLE);
        final double weight = readWeight();
        checkAndSkipColon();
        final List<Thing> things = readThings(scanner);
        return new PackTask(weight, things);
    }

    public List<PackTask> readAll() throws APIException {
        final List<PackTask> tasks = new ArrayList<>();
        while (hasNext()) {
            tasks.add(readNext());
        }
        return tasks;
    }

    private List<Thing> readThings(Scanner scanner) throws APIException {
        final List<Thing> things = new ArrayList<>();
        checkNextToken(Token.THING);
        while (scanner.hasNext(Token.THING.pattern)) {
            scanner.next(Token.THING.pattern);
            final MatchResult matchResult = scanner.match();
            final int index = Integer.valueOf(matchResult.group(1));
            final double weight = Double.valueOf(matchResult.group(2));
            final double cost = Double.valueOf(matchResult.group(3));
            things.add(new Thing(index, weight, cost));
        }
        return things;
    }

    private int readWeight() throws APIException {
        checkNextToken(Token.DOUBLE);
        return scanner.nextInt();
    }

    private void checkNextToken(Token token) throws APIException {
        if (!scanner.hasNext(token.pattern)) {
            throw new APIException.IncorrectInputException("Check the input format! It's incorrect!");
        }
    }

    private void checkAndSkipColon() throws APIException {
        checkNextToken(Token.COLON);
        scanner.skip(Token.COLON.pattern);
    }

    @Override
    public void close() throws Exception {
        scanner.close();
    }

    private enum Token {

        DOUBLE("(\\d+|\\d*\\.\\d+)"),
        COLON("\\s*:\\s*"),
        THING("\\((\\d+),(\\d+|\\d*\\.\\d+),€((\\d+|\\d*\\.\\d+))\\)");

        private String pattern;

        Token(String pattern) {
            this.pattern = pattern;
        }
    }
}
