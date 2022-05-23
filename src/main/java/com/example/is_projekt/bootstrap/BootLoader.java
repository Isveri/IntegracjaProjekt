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
        List<List<String>> regions = new ArrayList<>();
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


        try {
            file = ResourceUtils.getFile("classpath:hunted.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                regions.add(getRecordFromLine(scanner.nextLine()));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        addDataToDatabase(records, regions);
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

    private void addDataToDatabase(List<List<String>> records, List<List<String>> regions) {
        Region region;
        records.remove(0);
        for (List<String> li : records) {
            Statistics statistics = new Statistics();
            region = new Region();
            if (regionRepository.findByName(li.get(0)).isPresent()) {

            } else {
                region.setName(li.get(0));
                regionRepository.save(region);
                statistics.setRegion(region);
            }
            region = regionRepository.findByName(li.get(0)).orElse(null);
            statistics.setRegion(region);
            statistics.setType(li.get(1));
            statistics.setYear(Integer.valueOf(li.get(2)));
            statistics.setWeight(Integer.valueOf(li.get(3)));
            statistics.setPrice(Integer.valueOf(li.get(4)));
            statisticsRepository.save(statistics);
        }


        int licznik = 0;
        int suma = 0;
        for (List<String> li : regions) {
            if (licznik == 12) {
                suma += Integer.parseInt(li.get(3));
                Region region1 = regionRepository.findByName(li.get(0)).orElseThrow(null);
                region1.setHuntedAnimals(suma);
                regionRepository.save(region1);
                licznik = 0;
                suma = 0;

            } else {
                licznik++;
                suma += Integer.parseInt(li.get(3));
            }
        }
    }
}