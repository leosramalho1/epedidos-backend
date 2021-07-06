package br.com.inovasoft.epedidos.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class FormatUtil {

    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String FORMAT_DD_MM_YYYY = "dd/MM/yyyy";

    public static String onlyNumbers(String value) {
        if(StringUtils.isBlank(value)) {
            return value;
        }

        return value.replaceAll("^\\D", StringUtils.EMPTY);
    }

    public static String formataTelefone(String tel) {
        return tel;
    }

    public static Object formataCpfCnpj(String doc) {
        return doc;
    }

    public static String formataData(String date) {
        return formataData(date, FORMAT_YYYY_MM_DD, FORMAT_DD_MM_YYYY);
    }

    public static String formataData(LocalDate date, String format) {
        if(Objects.isNull(date)) {
            return StringUtils.EMPTY;
        }

        return date.format(DateTimeFormatter.ofPattern(format));
    }

    public static String formataData(String date, String inputFormat, String outputFormat) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(inputFormat))
                .format(DateTimeFormatter.ofPattern(outputFormat));
    }

    public static String formataValor(BigDecimal valor) {

        if(valor == null) {
            return StringUtils.EMPTY;
        }

        return NumberFormat
                .getCurrencyInstance(new Locale("pt", "BR"))
                .format(valor);
    }

    public static String formataNumero(Number valor) {

        if(valor == null) {
            return StringUtils.EMPTY;
        }

        return NumberFormat
                .getInstance(new Locale("pt", "BR"))
                .format(valor);
    }
}
