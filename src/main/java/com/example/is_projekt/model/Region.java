package com.example.is_projekt.model;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotNull
    private String name;

//    @NotNull
    private Integer huntedAnimals;

    @OneToMany(mappedBy = "region",cascade = CascadeType.MERGE)
    private List<Statistics> stats;
}
