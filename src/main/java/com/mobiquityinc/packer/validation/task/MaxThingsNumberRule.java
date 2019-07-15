package com.mobiquityinc.packer.validation.task;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.exception.APIException.ValidationException;
import com.mobiquityinc.packer.domain.PackTask;
import com.mobiquityinc.packer.validation.ValidationRule;

/**
 * Checks that package has up to the given maximum value number of things (default 15).
 */
public class MaxThingsNumberRule implements ValidationRule<PackTask> {

    private static final int DEFAULT_MAX_THINGS_NUMBER = 15;

    private final int max;

    public MaxThingsNumberRule() {
        this(DEFAULT_MAX_THINGS_NUMBER);
    }

    public MaxThingsNumberRule(int max) {
        this.max = max;
    }

    @Override
    public void validate(PackTask task) throws APIException {
        if (task.getThings().size() > max) {
            throw new ValidationException(
                String.format("Task might have up to %s things to pack from, given: %s", max, task.getThings().size())
            );
        }
    }
}
