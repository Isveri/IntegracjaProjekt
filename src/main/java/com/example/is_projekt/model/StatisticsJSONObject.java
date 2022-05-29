package com.example.is_projekt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatisticsJSONObject {

    private String nazwa;
    private String zwierzeta_lowne;
    private Integer rok;
    private Integer ilosc;
    private Integer wartosc;

}
