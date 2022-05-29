package com.example.is_projekt.services;

import com.example.is_projekt.mappers.StatisticsMapper;
import com.example.is_projekt.model.Region;
import com.example.is_projekt.model.Statistics;
import com.example.is_projekt.model.StatisticsJSONObject;
import com.example.is_projekt.modelDTO.StatisticsDTO;
import com.example.is_projekt.repositories.RegionRepository;
import com.example.is_projekt.repositories.StatisticsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.persistence.EntityManager;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository statisticsRepository;
    private final StatisticsMapper statisticsMapper;
    private final RegionRepository regionRepository;
    private final EntityManager entityManager;


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

    @Override
    public List<StatisticsDTO> getStatsForYear(int year) {
        Predicate<Statistics> isThisYear = statistics -> statistics.getYear() == year && statistics.getType().equals("ogółem");
        return statisticsRepository.findAll()
                .stream().filter(isThisYear)
                .map(statisticsMapper::mapStatisticsToStatisticsDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tworzenie schematu dokumentu XML do zapisania w pliku
     * na podstawie danych zawartych w bazie.
     */
    @Override
    public void saveToXML() throws ParserConfigurationException, TransformerException {
        List<StatisticsDTO> statsToSave = getAllStats();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        int licznik = 0;
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("root");
        doc.appendChild(rootElement);
        for (StatisticsDTO stat : statsToSave) {
            licznik++;
            Element row = doc.createElement("row-" + licznik);
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

            String path="";
            URL res = getClass().getClassLoader().getResource("stats.xml");
            try {
                File file = Paths.get(res.toURI()).toFile();
                path=file.getAbsolutePath();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            try (FileOutputStream output =
                         new FileOutputStream(path)) {
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

    /**
     * Funkcja do zapisywania danych z bazy w pliku JSON. Sprawdzić folder /target/classes/stats.json jesli w glownym folderze plik sie nie zmieni
     */
    @Override
    public void saveToJSON() {
        List<StatisticsDTO> statsToSave = getAllStats();
        ObjectMapper mapper = new ObjectMapper();
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:stats.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            mapper.writeValue(file, statsToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    @Override
    public List<StatisticsDTO> loadDataToDatabase() {
        loadDataFromXMLJSON();
        return getAllStats();
    }



    @Override
    public List<StatisticsDTO> removeAllData() {
        List<StatisticsDTO> stats;
        statisticsRepository.deleteAll();
        stats = statisticsRepository.findAll()
                .stream()
                .map(statisticsMapper::mapStatisticsToStatisticsDTO)
                .collect(Collectors.toList());
        return stats;
    }

    private void loadDataFromXMLJSON() {
        List<List<String>> regions = new ArrayList<>();
        File file = null;

        /**
         * Read from XML file
         */

        try {
            file = ResourceUtils.getFile("classpath:hunted.xml");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(file);

            NodeList list = doc.getElementsByTagName("row");

            for (int temp = 0; temp < list.getLength(); temp++) {
                List<String> tmp = new ArrayList<>();
                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String nazwa = element.getElementsByTagName("Nazwa").item(0).getTextContent();
                    String ilosc = element.getElementsByTagName("Ilość").item(0).getTextContent();

                    tmp.add(nazwa);
                    tmp.add(ilosc);
                    regions.add(tmp);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        addDataToDatabase(regions);
    }


    private void addDataToDatabase(List<List<String>> regions) {

        /**
         * Read from JSON file
         */
        List<StatisticsJSONObject> stats = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<StatisticsJSONObject>> typeReference = new TypeReference<List<StatisticsJSONObject>>(){};
        InputStream inputStream = TypeReference.class.getResourceAsStream("/data.json");
        try {
            stats = mapper.readValue(inputStream,typeReference);
            System.out.println("Statistics saved");
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Unable to save statistics:/ " + e.getMessage());
        }

        /**
         * add to database from records array
         */
        Region region;
        for (StatisticsJSONObject stat : stats) {
            Statistics newStat = new Statistics();
            region = new Region();
            if (regionRepository.findByName(stat.getNazwa()).isPresent()) {

            } else {
                region.setName(stat.getNazwa());
                regionRepository.save(region);
                newStat.setPrice(stat.getWartosc());
                newStat.setType(stat.getZwierzeta_lowne());
                newStat.setWeight(stat.getIlosc());
                newStat.setYear(stat.getRok());
                newStat.setRegion(region);
            }
            region = regionRepository.findByName(stat.getNazwa()).orElse(null);
            newStat.setPrice(stat.getWartosc());
            newStat.setType(stat.getZwierzeta_lowne());
            newStat.setWeight(stat.getIlosc());
            newStat.setYear(stat.getRok());
            newStat.setRegion(region);
            statisticsRepository.save(newStat);
        }


        /**
         * Add to database from regions array
         */
        int licznik = 0;
        int suma = 0;
        for (List<String> li : regions) {
            if (licznik == 12) {
                suma += Integer.parseInt(li.get(1));
                Region region1 = regionRepository.findByName(li.get(0)).orElseThrow(null);
                region1.setHuntedAnimals(suma);
                regionRepository.save(region1);
                licznik = 0;
                suma = 0;
            } else {
                licznik++;
                suma += Integer.parseInt(li.get(1));
            }
        }
    }

}


