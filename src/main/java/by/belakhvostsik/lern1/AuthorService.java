package by.belakhvostsik.lern1;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(AuthorService.class);

    @PostConstruct
    @Transactional
    public void initData() {
        Author author1 = new Author("Лев Толстой");
        author1.addBook(new Book("Война и мир"));
        author1.addBook(new Book("Анна Каренина"));


        Author author2 = new Author("Фёдор Достоевский");
        author2.addBook(new Book("Преступление и наказание"));

/*
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
 */

        authorRepository.saveAll(List.of(author1, author2 /*, author3, author4, author5, author6, author7, author8,author9,author10,author11*/));
    }

    /**
     * Демонстрирует работу кэша второго уровня Hibernate.
     * Метод выполняет два чтения списка авторов из базы данных,
     * чтобы показать, как кэш второго уровня позволяет избежать повторных запросов к базе данных.
     * Второе чтение извлекает данные из кэша, что должно уменьшить количество SQL-запросов.
     * В конце метод выводит статистику кэша, включая количество попаданий (hits),
     * промахов (misses) и добавлений (puts) в кэш.
     */
    @Transactional(readOnly = true)
    public void demonstrateSecondLevelCache() {
        logger.info("\n ===> Первое чтение (Данные из БД) <===");
        List<Author> authors1 = authorRepository.findAllAuthorsCache();
        authors1.forEach(author -> logger.info("{} - {} книг", author.getName(), author.getBooks().size()));

        logger.info("\n ===> Второе чтение (данные из кэша) <===");
        List<Author> authors2 = authorRepository.findAllAuthorsCache();
        authors2.forEach(author -> logger.info("{} - {} книг", author.getName(), author.getBooks().size()));

        @SuppressWarnings("resource")
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory()
                .unwrap(SessionFactory.class);

        Statistics stats = sessionFactory.getStatistics();
        logger.info("\n <=== Статистика кэша ===> ");
        logger.info("Cache Hits: {}", stats.getSecondLevelCacheHitCount());
        logger.info("Cache Misses: {}", stats.getSecondLevelCacheMissCount());
        logger.info("Cache Puts: {}", stats.getSecondLevelCachePutCount());

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

    @Transactional(readOnly = true)
    public void demonstrateAuthorsWithBooks() {
        System.out.println("\n=== Решение через кеширование ===");
        long startTime = System.currentTimeMillis();

        List<Author> authors = authorRepository.findAllAuthorsCache();
        authors.forEach(author -> {
            System.out.println(
                    "Автор: " + author.getName() +
                            ", Книг: " + author.getBooks().size()
            );
        });
        System.out.printf("Выполнено за %d мс\n",
                System.currentTimeMillis() - startTime);
    }
}
