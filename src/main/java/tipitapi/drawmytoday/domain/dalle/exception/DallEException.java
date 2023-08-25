package tipitapi.drawmytoday.domain.dalle.exception;

public abstract class DallEException extends Exception {

    public DallEException(String message) {
        super(message);
    }

    public DallEException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
