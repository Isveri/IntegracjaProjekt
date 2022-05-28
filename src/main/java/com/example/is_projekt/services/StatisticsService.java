package com.example.is_projekt.services;

import com.example.is_projekt.model.Statistics;
import com.example.is_projekt.modelDTO.StatisticsDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public interface StatisticsService {

    Statistics showStatsByType(String name);
    List<StatisticsDTO> getAllStats();
    List<StatisticsDTO> getStatsForYear(int year);

    void saveToXML()throws ParserConfigurationException, TransformerException;
    void saveToJSON();
    ResponseEntity<Resource> getResourceResponseEntity(File file) throws FileNotFoundException;
}
