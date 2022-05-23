package com.example.is_projekt.controllers;

import com.example.is_projekt.modelDTO.StatisticsDTO;
import com.example.is_projekt.repositories.StatisticsRepository;
import com.example.is_projekt.services.StatisticsService;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/stats")
public class StatisticsController {
    private final StatisticsRepository statisticsRepository;
    private final StatisticsService statisticsService;
    @GetMapping("/all")
    private ResponseEntity<List<StatisticsDTO>> getAllStats(){
        return ResponseEntity.ok(statisticsService.getAllStats());
    }

    @GetMapping(value = "/data.json",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadJSON() throws IOException {
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:data.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
