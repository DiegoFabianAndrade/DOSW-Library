package edu.eci.dosw.tdd.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookMetadataDoc {
    private Integer pages;
    private String language;
    private String publisher;
}
