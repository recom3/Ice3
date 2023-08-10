package com.recom3.snow3.utility;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Recom3 on 31/05/2023.
 */

public class NumberFormatter {
    public static CharSequence formatDecimalWithTwoMaximumFractionDigits(double value) {
        NumberFormat df = DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(value);
    }

    public static CharSequence getAbsNumberWithFormat(String valeu) {
        NumberFormat nf = NumberFormat.getInstance();
        long absValue = Math.abs(Long.parseLong(valeu));
        return nf.format(absValue);
    }
}
