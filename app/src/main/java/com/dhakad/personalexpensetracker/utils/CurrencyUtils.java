package com.dhakad.personalexpensetracker.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class for formatting currency values.
 */
public class CurrencyUtils {

    /**
     * Format amount in Indian Rupee.
     */
    public static String format(double amount) {

        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        return formatter.format(amount);
    }

}