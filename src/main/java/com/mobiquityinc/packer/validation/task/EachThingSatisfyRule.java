package com.mobiquityinc.packer.validation.task;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.packer.domain.PackTask;
import com.mobiquityinc.packer.domain.Thing;
import com.mobiquityinc.packer.validation.ValidationRule;

/**
 * Checks that all things of a task satisfy the given rule.
 */
public class EachThingSatisfyRule implements ValidationRule<PackTask> {

    private final ValidationRule<Thing> thingRule;

    public EachThingSatisfyRule(ValidationRule<Thing> thingRule) {
        this.thingRule = thingRule;
    }

    @Override
    public void validate(PackTask task) throws APIException {
        for (Thing thing : task.getThings()) {
            thingRule.validate(thing);
        }
    }
}
