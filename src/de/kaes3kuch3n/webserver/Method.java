package de.kaes3kuch3n.webserver;

public enum Method {
    GET,
    POST,
    PUT,
    DELETE;

    /**
     * Converts a string to the method it represents
     * @param methoString The string to convert
     * @return The fitting method, if one exists, otherwise null
     */
    public static Method fromString(String methoString) {
        for (Method method : values()) {
            if (method.toString().equalsIgnoreCase(methoString))
                return method;
        }

        return null;
    }
}
