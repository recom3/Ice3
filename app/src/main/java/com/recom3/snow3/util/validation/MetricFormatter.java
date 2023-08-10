package com.recom3.snow3.util.validation;

import com.recom3.snow3.model.MetricFormat;

/**
 * Created by Recom3 on 25/01/2022.
 */

public class MetricFormatter {
    private static final double METER_IN_FEET = 3.28084D;

    private static final double METER_IN_MILE = 6.21371E-4D;

    private static final double MILES_IN_KILOMETER = 0.621371D;

    public static MetricFormat formatFeetOrMiles(double paramDouble) {
        return (paramDouble < 1000.0D) ? new MetricFormat(MetricFormat.MetricSuffix.FEET, 3.28084D * paramDouble) : new MetricFormat(MetricFormat.MetricSuffix.MILES, 6.21371E-4D * paramDouble);
    }

    public static MetricFormat formatMeterOrKilometers(double paramDouble) {
        return (paramDouble < 1000.0D) ? new MetricFormat(MetricFormat.MetricSuffix.METER, paramDouble) : new MetricFormat(MetricFormat.MetricSuffix.KILOMETER, paramDouble / 1000.0D);
    }

    public static MetricFormat formatMilesPerHour(double paramDouble) {
        return new MetricFormat(MetricFormat.MetricSuffix.MILES_PER_HOUR, 0.621371D * paramDouble);
    }
}
