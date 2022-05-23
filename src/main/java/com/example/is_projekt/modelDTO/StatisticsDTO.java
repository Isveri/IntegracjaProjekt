package com.example.is_projekt.modelDTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class StatisticsDTO {
    private Long id;
    private Integer weight;
    private Integer year;
    private Integer price;
    private String type;
    private RegionDTO regionDTO;
}
