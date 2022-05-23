package com.example.is_projekt.services;

import com.example.is_projekt.mappers.StatisticsMapper;
import com.example.is_projekt.model.Statistics;
import com.example.is_projekt.modelDTO.StatisticsDTO;
import com.example.is_projekt.repositories.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository statisticsRepository;
    private final StatisticsMapper statisticsMapper;

    @Override
    public Statistics showStatsByType(String name) {
        return null;
    }

    @Override
    public List<StatisticsDTO> getAllStats() {
        return statisticsRepository.findAll()
                .stream()
                .map(statisticsMapper::mapStatisticsToStatisticsDTO)
                .collect(Collectors.toList());
    }
}
