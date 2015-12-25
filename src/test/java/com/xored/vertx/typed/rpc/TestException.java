package com.xored.vertx.typed.rpc;

/**
 * @author Konstantin Zaitsev
 */
public class TestException extends RuntimeException {

    /** Serial version UID. */
    private static final long serialVersionUID = -7461517917025980016L;

    private short errorCode;

    public TestException(short errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }


    /**
     * @return the errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }
}
