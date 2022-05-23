package com.example.is_projekt.modelDTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class RegionDTO {
    private Long id;
    private String name;
    private Integer huntedAnimals;
}
