package by.belakhvostsik.lern1;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @PostConstruct
    public void initData() {
        Author author1 = new Author("Лев Толстой");
        author1.addBook(new Book("Война и мир"));
        author1.addBook(new Book("Анна Каренина"));

        Author author2 = new Author("Фёдор Достоевский");
        author2.addBook(new Book("Преступление и наказание"));

        Author author3 = new Author("Николай Гоголь");
        author3.addBook(new Book("Мертвые души"));

        Author author4 = new Author("Александр Пушкин");
        author4.addBook(new Book("Пророк"));

        Author author5 = new Author("Иван Тургенев");
        author5.addBook(new Book("Отцы и дети"));

        Author author6 = new Author("Лев Толстой");
        author6.addBook(new Book("Анна Каренина"));

        Author author7 = new Author("Антон Чехов");
        author7.addBook(new Book("Сказки Мельпомены"));

        Author author8 = new Author("Михаил Булгаков");
        author8.addBook(new Book("Мастер и Маргарита"));

        Author author9 = new Author("Марина Цветаева");
        author9.addBook(new Book("Вечерний альбом"));

        Author author10 = new Author("Анна Ахматова");
        author10.addBook(new Book("Реквием"));

        Author author11 = new Author("Фёдор Тютчев");
        author11.addBook(new Book("Люблю грозу в начале мая"));

        authorRepository.saveAll(List.of(author1, author2, author3, author4, author5, author6, author7, author8,author9,author10,author11));
    }

    @Transactional(readOnly = true)
    public void demonstrateNPlusOneProblem() {
        System.out.println("\n=== Демонстрация N+1 проблемы ===");
        long startTime = System.currentTimeMillis();
        List<Author> authors = authorRepository.findAllAuthors();
        authors.forEach(author -> {
            System.out.println(author.getName() + " имеет " +
                    author.getBooks().size() + " книг(и)");
        });
        System.out.printf("Выполнено за %d мс\n",
                System.currentTimeMillis() - startTime);
    }

    @Transactional(readOnly = true)
    public void demonstrateJoinFetchSolution() {
        System.out.println("\n=== Решение через JOIN FETCH ===");
        long startTime = System.currentTimeMillis();
        List<Author> authors = authorRepository.findAllAuthorsWithJoinFetch();
        authors.forEach(author -> {
            System.out.println(author.getName() + " имеет " +
                    author.getBooks().size() + " книг(и)");
        });
        System.out.printf("Выполнено за %d мс\n",
                System.currentTimeMillis() - startTime);
    }

    @Transactional(readOnly = true)
    public void demonstrateEntityGraphSolution() {
        System.out.println("\n=== Решение через EntityGraph ===");
        long startTime = System.currentTimeMillis();
        List<Author> authors = authorRepository.findAllAuthorsWithEntityGraph();
        authors.forEach(author -> {
            System.out.println(author.getName() + " имеет " +
                    author.getBooks().size() + " книг(и)");
        });
        System.out.printf("Выполнено за %d мс\n",
                System.currentTimeMillis() - startTime);
    }

    @Transactional(readOnly = true)
    public void demonstrateDtoProjectionSolution() {
        System.out.println("\n=== Решение через DTO проекцию ===");
        long startTime = System.currentTimeMillis();

        List<AuthorBookCountDTO> authors = authorRepository.findAuthorsWithBookCount();
        authors.forEach(dto -> {
            System.out.println(dto.getAuthorName() + " имеет " +
                    dto.getBookCount() + " книг(и)");
        });

        System.out.printf("Выполнено за %d мс\n",
                System.currentTimeMillis() - startTime);
    }

}
