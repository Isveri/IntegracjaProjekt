package com.example.is_projekt.repositories;

import com.example.is_projekt.model.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatisticsRepository extends JpaRepository<Statistics,Long> {

}
