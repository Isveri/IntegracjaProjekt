package com.example.is_projekt.bootstrap;

import com.example.is_projekt.model.Region;
import com.example.is_projekt.model.Statistics;
import com.example.is_projekt.repositories.RegionRepository;
import com.example.is_projekt.repositories.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


@Component
@RequiredArgsConstructor
public class BootLoader implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final StatisticsRepository statisticsRepository;

    @Override
    public void run(String... args) throws Exception {
        getStatsFromCsv();

    }

    private void getStatsFromCsv() {
        List<List<String>> records = new ArrayList<>();
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:stats.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                records.add(getRecordFromLine(scanner.nextLine()));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        addDataToDatabase(records);
    }

    private List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());

            }
        }
        return values;
    }

    private void addDataToDatabase(List<List<String>> records) {
        Region region;
        records.remove(0);
        for (List<String> li : records) {
            Statistics statistics = new Statistics();
            for (int i = 0; i < li.size(); i++) {
                region = new Region();
                switch (i) {
                    case 0:
                        if (regionRepository.findByName(li.get(i)).isPresent()) {

                        }else {
                            region.setName(li.get(i));
                            regionRepository.save(region);
                            statistics.setRegion(region);
                        }
                        region = regionRepository.findByName(li.get(i)).orElse(null);
                        statistics.setRegion(region);
                        break;
                    case 1:
                        statistics.setType(li.get(i));
                        break;
                    case 2:
                        statistics.setYear(Integer.valueOf(li.get(i)));
                        break;
                    case 3:
                        statistics.setWeight(Integer.valueOf(li.get(i)));
                        break;
                    case 4:
                        statistics.setPrice(Integer.valueOf(li.get(i)));
                        break;
                }
                statisticsRepository.save(statistics);

            }
        }
    }
}
