package com.example.is_projekt.model;


import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Double weight;

    @NotNull
    private String year;

    @NotNull
    private Double price;

    @NotNull
    private String type;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "region_id")
    private Region region;
}
