package cn.hellobike.hippo.exception;

public class ErrorException extends Exception {
    public ErrorException() {
    }

    public ErrorException(String message) {
        super(message);
    }
}
