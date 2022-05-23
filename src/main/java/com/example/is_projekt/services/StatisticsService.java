package com.example.is_projekt.services;

import com.example.is_projekt.model.Statistics;
import com.example.is_projekt.modelDTO.StatisticsDTO;

import java.util.List;

public interface StatisticsService {

    Statistics showStatsByType(String name);
    List<StatisticsDTO> getAllStats();
}
