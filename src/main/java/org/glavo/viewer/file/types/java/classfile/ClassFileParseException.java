package org.glavo.viewer.file.types.java.classfile;

public class ClassFileParseException extends RuntimeException {
    public ClassFileParseException() {
    }

    public ClassFileParseException(String message) {
        super(message);
    }

    public ClassFileParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassFileParseException(Throwable cause) {
        super(cause);
    }

    public ClassFileParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
