package com.gomezrondon.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;




@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Table("movie")
public class Movie {

    @Id
    private Integer id;


    private String name;

    public Movie(String title) {
        this.name = title;
        this.id = null;
    }
}