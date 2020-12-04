package br.com.inovasoft.epedidos.util;

public class SuggestionUtil {
    private final static String SPLIT_TOKEN = " - ";

    public static String build(Long id, String text) {
        return id + SPLIT_TOKEN + text;
    }

    public static Long extractId(String suggestion) {
        return Long.parseLong(suggestion.substring(0, suggestion.indexOf(SPLIT_TOKEN)).trim());
    }
}
