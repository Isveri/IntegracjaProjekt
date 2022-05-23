package com.example.is_projekt.services;

import com.example.is_projekt.model.Statistics;
import com.example.is_projekt.modelDTO.StatisticsDTO;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.List;

public interface StatisticsService {

    Statistics showStatsByType(String name);
    List<StatisticsDTO> getAllStats();

    void saveToXML()throws ParserConfigurationException, TransformerException;

}
