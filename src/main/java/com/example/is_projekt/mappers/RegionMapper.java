package com.example.is_projekt.mappers;

import com.example.is_projekt.model.Region;
import com.example.is_projekt.modelDTO.RegionDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(builder = @Builder(disableBuilder = true))
public abstract class RegionMapper {
    public abstract RegionDTO mapRegionToRegionDTO(Region region);
}
