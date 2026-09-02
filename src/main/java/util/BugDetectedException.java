package util;

public class BugDetectedException extends RuntimeException {
    public BugDetectedException(String template, Object... args) {
        super(String.format(template, args));
    }
}
