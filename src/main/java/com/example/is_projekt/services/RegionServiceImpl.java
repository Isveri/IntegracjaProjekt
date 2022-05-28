package com.example.is_projekt.services;

import com.example.is_projekt.mappers.RegionMapper;
import com.example.is_projekt.modelDTO.RegionDTO;
import com.example.is_projekt.repositories.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RegionServiceImpl implements RegionService {
    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;

    @Override
    public List<RegionDTO> getRegions() {
        return regionRepository.findAll()
                .stream()
                .map(regionMapper::mapRegionToRegionDTO)
                .collect(Collectors.toList());
    }
}
