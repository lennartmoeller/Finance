package com.lennartmoeller.finance.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class StatsMetricDTO {

    private double raw;
    private double smoothed;

    public static StatsMetricDTO empty() {
        StatsMetricDTO dto = new StatsMetricDTO();
        dto.setRaw(0.0);
        dto.setSmoothed(0.0);
        return dto;
    }

    public static StatsMetricDTO add(List<StatsMetricDTO> statsMetricDTOs) {
        StatsMetricDTO result = new StatsMetricDTO();
        result.setRaw(
                statsMetricDTOs.stream().mapToDouble(StatsMetricDTO::getRaw).sum());
        result.setSmoothed(statsMetricDTOs.stream()
                .mapToDouble(StatsMetricDTO::getSmoothed)
                .sum());
        return result;
    }

    public static StatsMetricDTO multiply(StatsMetricDTO statsMetricDTO, double factor) {
        StatsMetricDTO result = new StatsMetricDTO();
        result.setRaw(statsMetricDTO.getRaw() * factor);
        result.setSmoothed(statsMetricDTO.getSmoothed() * factor);
        return result;
    }

    public static StatsMetricDTO mean(List<StatsMetricDTO> statsMetricDTOs) {
        if (statsMetricDTOs.isEmpty()) {
            return StatsMetricDTO.empty();
        }
        StatsMetricDTO result = StatsMetricDTO.add(statsMetricDTOs);
        result.setRaw(result.getRaw() / statsMetricDTOs.size());
        result.setSmoothed(result.getSmoothed() / statsMetricDTOs.size());
        return result;
    }
}
