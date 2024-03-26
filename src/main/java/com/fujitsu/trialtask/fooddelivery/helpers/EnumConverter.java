package com.fujitsu.trialtask.fooddelivery.helpers;

public class EnumConverter {
    /**
     * Converts a string to an enum. Accepts kebab-case, camelCase, PascalCase, and snake_case.
     *
     * @param input     the string to convert
     * @param enumClass the enum class to convert to
     * @param <T>       the enum type
     *
     * @return the enum value or null if the string does not match any enum value
     */
    public static <T extends Enum<T>> T convertStringToEnum(String input, Class<T> enumClass) {
        try {
            if (input.chars().anyMatch(Character::isLowerCase)) {
                input = convertToUpperCaseUnderscore(input);
            }

            return Enum.valueOf(enumClass, input);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String convertToUpperCaseUnderscore(String input) {
        return input
                .replaceAll("-", "_")  // kebab-case to snake_case
                .replaceAll("(.)(\\p{Upper})", "$1_$2")  // camelCase and PascalCase to snake_case
                .toUpperCase();  // snake_case to UPPER_SNAKE_CASE
    }
}
