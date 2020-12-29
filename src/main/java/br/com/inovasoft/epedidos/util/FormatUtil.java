package br.com.inovasoft.epedidos.util;

import org.apache.commons.lang3.StringUtils;

public class FormatUtil {

    public static String onlyNumbers(String value) {
        if(StringUtils.isBlank(value)) {
            return value;
        }

        return value.replaceAll("^\\D", StringUtils.EMPTY);
    }
}
