package util;

/**
 * JSONSchemaException
 */
public class JSONSchemaException extends RuntimeException {

    public JSONSchemaException(String template, Object... args) {
        super(String.format(template, args));
    }
}
