package org.pinae.rafiki;

public class StringUtils {

    /**
     * Checks if a String is empty.
     *
     * @param inString the String to check, may be null
     * @return true if the String is null or empty
     */
    public static boolean isEmpty(String inString) {
        return inString == null || inString.isEmpty();
    }

    /**
     * Checks if a String is not empty.
     *
     * @param inString the String to check, may be null
     * @return true if the String is not null and not empty
     */
    public static boolean isNotEmpty(String inString) {
        return !isEmpty(inString);
    }

    /**
     * Checks if a String is not blank.
     *
     * @param inString the String to check, may be null
     * @return true if the String is not null, not empty, and does not consist solely of whitespace characters
     */
    public static boolean isNotBlank(String inString) {
        return inString != null && !inString.trim().isEmpty();
    }

    public static void main(String[] args) {
        // Test cases
        System.out.println("isEmpty Tests:");
        System.out.println(isEmpty(null));          // true
        System.out.println(isEmpty(""));            // true
        System.out.println(isEmpty(" "));           // false
        System.out.println(isEmpty("Hello"));       // false

        System.out.println("\nisNotEmpty Tests:");
        System.out.println(isNotEmpty(null));       // false
        System.out.println(isNotEmpty(""));         // false
        System.out.println(isNotEmpty(" "));        // true
        System.out.println(isNotEmpty("Hello"));    // true

        System.out.println("\nisNotBlank Tests:");
        System.out.println(isNotBlank(null));       // false
        System.out.println(isNotBlank(""));         // false
        System.out.println(isNotBlank(" "));        // false
        System.out.println(isNotBlank("  \t\n"));   // false
        System.out.println(isNotBlank("Hello"));    // true
        System.out.println(isNotBlank(" Hello World ")); // true
    }
}