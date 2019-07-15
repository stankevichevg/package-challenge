package com.mobiquityinc.packer.validation;

import com.mobiquityinc.exception.APIException;

import java.util.Collection;

/**
 * Interface of a business validation rule
 * that can be applied to object to check if it satisfy some business requirements.
 */
public interface ValidationRule<O> {

    /**
     * Validates given objects for some business rules.
     *
     * @param obj object to validate
     * @throws APIException if something is going wrong
     */
    void validate(O obj) throws APIException;

    /**
     * Do validation for the given collection of objects.
     *
     * @param objs collection of objects to validate
     * @throws APIException if something is going wrong
     */
    default void validateAll(Collection<O> objs) throws APIException {
        for (O obj : objs) {
            validate(obj);
        }
    }

    /**
     * Helper method to make {@link AllOfValidationRule}.
     *
     * @param rules array of rules
     * @param <A> the type of the objects to validate
     * @return created validation rule that check objects to be valid for all given rules.
     */
    static <A> ValidationRule<A> allOf(ValidationRule<A>... rules) {
        return new AllOfValidationRule<A>(rules);
    }

    /**
     * The rule that controls that all of the underling rules are satisfied by the given object.
     *
     * @param <O> the type of the object to validate
     */
    class AllOfValidationRule<O> implements ValidationRule<O> {

        private ValidationRule<O>[] rules;

        public AllOfValidationRule(ValidationRule<O>[] rules) {
            this.rules = rules;
        }

        @Override
        public void validate(O obj) throws APIException {
            for (ValidationRule<O> rule : rules) {
                rule.validate(obj);
            }
        }
    }
}
