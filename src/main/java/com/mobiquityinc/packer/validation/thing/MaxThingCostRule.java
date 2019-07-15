package com.mobiquityinc.packer.validation.thing;

import com.mobiquityinc.exception.APIException;
import com.mobiquityinc.exception.APIException.ValidationException;
import com.mobiquityinc.packer.domain.Thing;
import com.mobiquityinc.packer.validation.ValidationRule;

/**
 * Checks that a thing has cost up to the given maximum value (default 100.0).
 */
public class MaxThingCostRule implements ValidationRule<Thing> {

    private static final double DEFAULT_MAX_COST = 100.0;

    private final double max;

    public MaxThingCostRule() {
        this(DEFAULT_MAX_COST);
    }

    public MaxThingCostRule(double max) {
        this.max = max;
    }

    @Override
    public void validate(Thing thing) throws APIException {
        if (thing.getCost() > max) {
            throw new ValidationException(
                String.format("Max cost that a thing can have is ≤ %s, given: %s", max, thing.getCost())
            );
        }
    }
}
