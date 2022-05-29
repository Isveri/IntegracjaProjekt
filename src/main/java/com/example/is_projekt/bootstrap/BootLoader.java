package com.example.is_projekt.bootstrap;

import com.example.is_projekt.model.Region;
import com.example.is_projekt.model.Statistics;
import com.example.is_projekt.model.StatisticsJSONObject;
import com.example.is_projekt.model.User;
import com.example.is_projekt.repositories.RegionRepository;
import com.example.is_projekt.repositories.StatisticsRepository;
import com.example.is_projekt.repositories.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class BootLoader implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final UserRepository userRepository;
    private final StatisticsRepository statisticsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User user = User.builder()
                .username("Admin")
                .password(passwordEncoder.encode("admin"))
                .build();

        if(userRepository.findAll().size()==0 && regionRepository.findAll().size()==0 && statisticsRepository.findAll().size()==0) {
            userRepository.save(user);
            loadDataFromXMLJSON();
//            getStatsFromCsv();
        }
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
//    private void getStatsFromCsv() {
//        List<List<String>> records = new ArrayList<>();
//        List<List<String>> regions = new ArrayList<>();
//        File file = null;
//
//        /**
//         * Read from CSV file
//         */
//
//        try {
//            file = ResourceUtils.getFile("classpath:stats.csv");
//            Scanner scanner = new Scanner(file);
//            while (scanner.hasNextLine()) {
//                records.add(getRecordFromLine(scanner.nextLine()));
//            }
//            file = ResourceUtils.getFile("classpath:hunted.xml");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//
//        /**
//         * Read from XML file
//         */
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//
//        try {
//            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
//            DocumentBuilder db = dbf.newDocumentBuilder();
//
//            Document doc = db.parse(file);
//
//            NodeList list = doc.getElementsByTagName("row");
//
//            for (int temp = 0; temp < list.getLength(); temp++) {
//                List<String> tmp = new ArrayList<>();
//                Node node = list.item(temp);
//
//                if (node.getNodeType() == Node.ELEMENT_NODE) {
//                    Element element = (Element) node;
//                    String nazwa = element.getElementsByTagName("Nazwa").item(0).getTextContent();
//                    String ilosc = element.getElementsByTagName("Ilość").item(0).getTextContent();
//
//                    tmp.add(nazwa);
//                    tmp.add(ilosc);
//                    regions.add(tmp);
//                }
//            }
//        } catch (ParserConfigurationException | SAXException | IOException e) {
//            e.printStackTrace();
//        }
//
//        addDataToDatabase(records, regions);
//    }
//
//    /**
//     * Split record in line
//     *
//     * @param line
//     * @return
//     */
//    private List<String> getRecordFromLine(String line) {
//        List<String> values = new ArrayList<String>();
//        try (Scanner rowScanner = new Scanner(line)) {
//            rowScanner.useDelimiter(",");
//            while (rowScanner.hasNext()) {
//                values.add(rowScanner.next());
//
//            }
//        }
//        return values;
//    }
//
//
//    private void addDataToDatabase(List<List<String>> records, List<List<String>> regions) {
//
//        /**
//         * add to database from records array
//         */
//        Region region;
//        records.remove(0);
//        for (List<String> li : records) {
//            Statistics statistics = new Statistics();
//            region = new Region();
//            if (regionRepository.findByName(li.get(0)).isPresent()) {
//
//            } else {
//                region.setName(li.get(0));
//                regionRepository.save(region);
//                statistics.setRegion(region);
//            }
//            region = regionRepository.findByName(li.get(0)).orElse(null);
//            statistics.setRegion(region);
//            statistics.setType(li.get(1));
//            statistics.setYear(Integer.valueOf(li.get(2)));
//            statistics.setWeight(Integer.valueOf(li.get(3)));
//            statistics.setPrice(Integer.valueOf(li.get(4)));
//            statisticsRepository.save(statistics);
//        }
//
//
//        /**
//         * Add to database from regions array
//         */
//        int licznik = 0;
//        int suma = 0;
//        for (List<String> li : regions) {
//            if (licznik == 12) {
//                suma += Integer.parseInt(li.get(1));
//                Region region1 = regionRepository.findByName(li.get(0)).orElseThrow(null);
//                region1.setHuntedAnimals(suma);
//                regionRepository.save(region1);
//                licznik = 0;
//                suma = 0;
//            } else {
//                licznik++;
//                suma += Integer.parseInt(li.get(1));
//            }
//        }
//    }
}