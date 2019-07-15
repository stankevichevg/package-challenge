package com.mobiquityinc.packer.validation.thing;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.exception.APIException.ValidationException;
import com.mobiquityinc.packer.domain.Thing;
import com.mobiquityinc.packer.validation.ValidationRule;

/**
 * Checks that a thing has weight up to the given maximum value (default 100.0).
 */
public class MaxThingWeightRule implements ValidationRule<Thing> {

    private static final double DEFAULT_MAX_WEIGHT = 100.0;

    private final double max;

    public MaxThingWeightRule() {
        this(DEFAULT_MAX_WEIGHT);
    }

    public MaxThingWeightRule(double max) {
        this.max = max;
    }

    @Override
    public void validate(Thing thing) throws APIException {
        if (thing.getWeight() > max) {
            throw new ValidationException(
                String.format("Max weight that a thing can have is ≤ %s, given: %s", max, thing.getWeight())
            );
        }
    }
}
