package edu.eci.dosw.tdd.core.model;

import lombok.Data;

@Data
public class Book {
    private Integer id;
    private String title;
    private String author;
    private boolean available;
}