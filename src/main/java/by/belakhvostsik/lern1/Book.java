package by.belakhvostsik.lern1;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Data
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    public Book(String title) {
        this.title = title;
    }

    public Book() {

    }
}
