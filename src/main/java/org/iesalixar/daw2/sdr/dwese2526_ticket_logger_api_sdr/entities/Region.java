package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="regions")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name="code", nullable = false, length = 2)
    private String code;

    @Column(name="name", nullable = false, length = 100)
    private String name;

    @OneToMany(
            mappedBy = "region",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            orphanRemoval = false
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Province> provinces = new ArrayList<>();



    public Region(String code, String name){
        this.code=code;
        this.name=name;
    }



}