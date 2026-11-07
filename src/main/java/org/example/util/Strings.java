package org.example.util;

public final class Strings {
    private Strings() {}

    public static boolean isBlankOrNull(
            final String string
    ) {
        return string == null || string.isBlank();
    }
}
