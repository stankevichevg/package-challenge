package com.mobiquityinc.exception;

/**
 * Base class for all exceptions which can occur during execution.
 */
public abstract class APIException extends Exception {

    public APIException(String message) {
        super(message);
    }

    public APIException(Throwable cause) {
        super(cause);
    }

    /**
     * Thrown when the input file can not be found.
     */
    public static final class FileNotFoundException extends APIException {

        public FileNotFoundException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Thrown when some business validation was not passed.
     */
    public static final class ValidationException extends APIException {

        public ValidationException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when input format is not fit the requirements.
     */
    public static final class IncorrectInputException extends APIException {

        public IncorrectInputException(String message) {
            super(message);
        }
    }

    /**
     * System level exception to wrap other types exceptions that can occur during execution.
     */
    public static final class SystemException extends APIException {

        public SystemException(Throwable cause) {
            super(cause);
        }
    }

}
