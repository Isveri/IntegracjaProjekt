package com.example.is_projekt.services;

import com.example.is_projekt.mappers.StatisticsMapper;
import com.example.is_projekt.model.Statistics;
import com.example.is_projekt.modelDTO.StatisticsDTO;
import com.example.is_projekt.repositories.RegionRepository;
import com.example.is_projekt.repositories.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository statisticsRepository;
    private final StatisticsMapper statisticsMapper;
    private final RegionRepository regionRepository;

    @Override
    public Statistics showStatsByType(String name) {
        return null;
    }

    /**
     * Wyświetlenie wszystkich danych z kolumny statistics
     */
    @Override
    public List<StatisticsDTO> getAllStats() {
        return statisticsRepository.findAll()
                .stream()
                .map(statisticsMapper::mapStatisticsToStatisticsDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tworzenie schematu dokumentu XML do zapisania w pliku
     * na podstawie danych zawartych w bazie.
     */
    @Override
    public void saveToXML() throws ParserConfigurationException, TransformerException{
        List<StatisticsDTO> statsToSave = getAllStats();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        int licznik =0;
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("root");
        doc.appendChild(rootElement);
        for(StatisticsDTO stat:statsToSave) {
            licznik++;
            Element row = doc.createElement("row-"+licznik);
            rootElement.appendChild(row);

            Element region = doc.createElement("Region");
            row.appendChild(region);
            region.setTextContent(stat.getRegion().getName());

            Element id = doc.createElement("id");
            row.appendChild(id);
            id.setTextContent(String.valueOf(stat.getId()));

            Element weight = doc.createElement("Ilosc");
            row.appendChild(weight);
            weight.setTextContent(String.valueOf(stat.getWeight()));

            Element year = doc.createElement("Rok");
            row.appendChild(year);
            year.setTextContent(String.valueOf(stat.getYear()));

            Element price = doc.createElement("Wartosc");
            row.appendChild(price);
            price.setTextContent(String.valueOf(stat.getPrice()));

            Element type = doc.createElement("Zwierzęta_łowne");
            row.appendChild(type);
            type.setTextContent(stat.getType());

            try (FileOutputStream output =
                         new FileOutputStream("C:\\Studia\\Semestr 6\\Integracja systemów\\Laboratorium\\Projekt\\IS_projekt\\src\\main\\resources\\stats.xml")) {
                writeXml(doc, output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Funkcja do zapisywania fragmentów XML'a
     */
    private static void writeXml(Document doc,
                                 OutputStream output)
            throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);

    }

    @Override
    public void saveToJSON() {

    }



    /**
     * Funkcja do przesyłania plików w odpowiedzi REST
     */
    public ResponseEntity<Resource> getResourceResponseEntity(File file) throws FileNotFoundException {
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

