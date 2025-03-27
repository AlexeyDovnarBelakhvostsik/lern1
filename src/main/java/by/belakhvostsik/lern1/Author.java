package by.belakhvostsik.lern1;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Cacheable
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "authorCache"
)

@Data
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Author(String name) {
        this.name = name;
    }
    // Решение 3: Пакетная загрузка (Batch Fetching)
    /* Плюсы:
          Уменьшает количество запросов до N / batch_size.
          Не требует изменения JPQL.
       Минусы:
          Всё равно выполняется несколько запросов (но меньше, чем N+1).*/
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @org.hibernate.annotations.Cache(
            usage = CacheConcurrencyStrategy.READ_WRITE,
            region = "by.belakhvostsik.lern1.Author.books"
    )
    // @BatchSize(size = 5)
    private List<Book> books = new ArrayList<>();

    public Author() {

    }

    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this); // Устанавливаем обратную связь
    }
}
