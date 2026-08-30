package util;

import java.util.stream.Stream;

public class CaseConverter {

    /*
     * snake_case
     * SCREAMING_SNAKE_CASE
     *
     * kebab-case
     * Train-Case
     *
     * flatcase
     * UPPERCASE
     *
     * camelCase
     * PascalCase
     *
     * Title Case
     *
     */

    private static String[] pascalOrCamelToSnake(String... toConvert) {
        var converted = new String[toConvert.length];
        for (int i = 0; i < toConvert.length; i++) {
            converted[i] = String.join("_", Stream
                    .of(toConvert[i].replaceAll("([a-z])([A-Z])", "$1 $2").split(" "))
                    .map(s -> s.toLowerCase())
                    .toArray(String[]::new));

        }
        return converted;
    }

    public static String pascalToTitle(String toConvert) {
        return toConvert.replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    public static String pascalToCamel(String toConvert) {
        return String.valueOf(toConvert.charAt(0)).toLowerCase() + toConvert.substring(1);
    }

    public static String[] pascalToSnake(String[] toConvert) {
        return pascalOrCamelToSnake(toConvert);
    }

    public static String pascalToSnake(String toConvert) {
        return pascalOrCamelToSnake(toConvert)[0];
    }

    public static String[] camelToSnake(String[] toConvert) {
        return pascalOrCamelToSnake(toConvert);
    }

    public static String camelToSnake(String toConvert) {
        return pascalOrCamelToSnake(toConvert)[0];
    }

    public static String camelToPascal(String toConvert) {
        return toConvert.substring(0, 1).toUpperCase() + toConvert.substring(1);
    }
}
