package com.mobiquityinc.packer.validation.task;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.exception.APIException.ValidationException;
import com.mobiquityinc.packer.domain.PackTask;
import com.mobiquityinc.packer.validation.ValidationRule;

/**
 * Checks that package has weight up to the given maximum value (default 100.0).
 */
public class MaxPackageWeightRule implements ValidationRule<PackTask> {

    private static final double DEFAULT_MAX_WEIGHT = 100.0;

    private final double max;

    public MaxPackageWeightRule() {
        this(DEFAULT_MAX_WEIGHT);
    }

    public MaxPackageWeightRule(double max) {
        this.max = max;
    }

    @Override
    public void validate(PackTask task) throws APIException {
        if (task.getMaxWeight() > max) {
            throw new ValidationException(
                String.format("Max weight that a package can take is ≤ %s, given: %s", max, task.getMaxWeight())
            );
        }
    }
}
