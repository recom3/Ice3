package com.recom3.snow3.service;

import com.google.inject.Inject;
import com.recom3.snow3.model.MetricFormat;
import com.recom3.snow3.repository.AirwaveRepository;
import com.recom3.snow3.util.validation.MetricFormatter;

/**
 * Created by Recom3 on 25/01/2022.
 */

//@ContextSingleton
public class AirwaveService {
    //@Inject
    public AirwaveRepository mAirwaveRepository;

    public MetricFormat getCurrentMetricFromMeterWithValue(double paramDouble) {
        return this.mAirwaveRepository.isMilesMetric() ? MetricFormatter.formatFeetOrMiles(paramDouble) : MetricFormatter.formatMeterOrKilometers(paramDouble);
    }

    public MetricFormat getCurrentMetricFromMeterWithValue(String paramString) {
        return getCurrentMetricFromMeterWithValue(Double.parseDouble(paramString));
    }

    public boolean isBuddyTracking() {
        return this.mAirwaveRepository.isBuddyTracking();
    }

    public boolean isMilesMetric() {
        return this.mAirwaveRepository.isMilesMetric();
    }

    public void setBuddyTracking(Boolean paramBoolean) {
        this.mAirwaveRepository.setBuddyTracking(paramBoolean);
    }
}
