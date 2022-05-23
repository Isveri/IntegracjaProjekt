package com.example.is_projekt.mappers;

import com.example.is_projekt.model.Statistics;
import com.example.is_projekt.modelDTO.StatisticsDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(builder = @Builder(disableBuilder = true))
public abstract class StatisticsMapper {

    public abstract StatisticsDTO mapStatisticsToStatisticsDTO(Statistics statistics);

    public abstract Statistics mapStatisticsDTOToStatistics(StatisticsDTO statisticsDTO);
}
