package com.example.is_projekt.modelDTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class StatisticsObjectJSON {
    private String Nazwa;
    private String Zwierzeta_lowne;
    private Integer Rok;
    private Integer Ilosc;
    private Integer Wartosc;

}
