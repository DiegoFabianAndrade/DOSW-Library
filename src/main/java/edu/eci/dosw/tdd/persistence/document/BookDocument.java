package edu.eci.dosw.tdd.persistence.document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDocument {

    @Id
    private Integer id;

    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;
    private boolean available;
    private List<String> categories;
    private String publicationType;
    private LocalDate publicationDate;
    private String isbn;
    private BookMetadataDoc metadata;
    private LocalDateTime catalogAddedAt;
}
