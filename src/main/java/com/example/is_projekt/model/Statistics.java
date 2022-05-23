package com.example.is_projekt.model;


import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotNull
    private Integer weight;

//    @NotNull
    private Integer year;

//    @NotNull
    private Integer price;

//    @NotNull
    private String type;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;
}
