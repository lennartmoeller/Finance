package com.lennartmoeller.finance.util.smoother;

import com.lennartmoeller.finance.dto.StatsMetricDTO;
import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.util.DateRange;

import java.util.HashMap;
import java.util.Map;

public abstract class Smoother<T> {

	protected final Map<T, StatsMetricDTO> data;

	protected Smoother() {
		this.data = new HashMap<>();
	}

	protected abstract void add(T key, CategorySmoothType smoothType, Long amount);

	protected abstract StatsMetricDTO get(T key);

	protected abstract DateRange getDateRange(T key, CategorySmoothType smoothType);

	protected void addRawToData(T key, double amount) {
		StatsMetricDTO statsMetricDTO = this.data.getOrDefault(key, StatsMetricDTO.empty());
		statsMetricDTO.setRaw(statsMetricDTO.getRaw() + amount);
		this.data.put(key, statsMetricDTO);
	}

	protected void addSmoothedToData(T key, double amount) {
		StatsMetricDTO statsMetricDTO = this.data.getOrDefault(key, StatsMetricDTO.empty());
		statsMetricDTO.setSmoothed(statsMetricDTO.getSmoothed() + amount);
		this.data.put(key, statsMetricDTO);
	}

}
