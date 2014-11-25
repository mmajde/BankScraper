package majde.marek.bankscrapers.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParsingHelper {

    private static final int NON_BREAKING_SPACE = 160;
    private static final String WHITESPACE_CHARS = "[(\\s)]";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm");

    public static LocalDateTime parseTransactionDate(String dateStr) {
        dateStr = replaceNonBreakingSpace(dateStr);
        return LocalDateTime.parse(dateStr, DATE_FORMATTER);
    }

    public static BigDecimal parseMoney(String amountStr) {
        amountStr = replaceSeparator(amountStr);
        amountStr = replaceWhitespaces(amountStr);
        amountStr = replaceNonBreakingSpace(amountStr);
        return new BigDecimal(amountStr);
    }

    private static String replaceWhitespaces(String amountStr) {
        return amountStr.replaceAll(WHITESPACE_CHARS, "");
    }

    private static String replaceSeparator(String amountStr) {
        return amountStr.replace(",", ".");
    }

    private static String replaceNonBreakingSpace(String amountStr) {
        return amountStr.replace(String.valueOf((char) NON_BREAKING_SPACE), "");
    }
}
