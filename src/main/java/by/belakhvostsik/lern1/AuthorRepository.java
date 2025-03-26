package by.belakhvostsik.lern1;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Метод, вызывающий N+1 проблему
    @Query("SELECT a FROM Author a")
    List<Author> findAllAuthors();

    // Решение 1: JOIN FETCH
   /* Плюсы:
         Один запрос к БД.
         Нет дублирования авторов (благодаря DISTINCT).
      Минусы:
         Может возвращать избыточные данные (если у автора много книг).*/
    @Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.books")
    List<Author> findAllAuthorsWithJoinFetch();

    // Решение 2: EntityGraph
    /*Плюсы:
         Чистый синтаксис без явного JOIN.
         Подходит для сложных сценариев с несколькими связями.
       Минусы:
         Может генерировать SQL с CROSS JOIN, что не всегда эффективно.*/
    @EntityGraph(attributePaths = "books")//Указываем какой аттрибут нужно загружать
    @Query("SELECT a FROM Author a")
    List<Author> findAllAuthorsWithEntityGraph();

    //Решение 4: Использует DTO для выборки только нужных данных
    /*Плюсы:
       Нет N+1, так как данные агрегируются на уровне БД.
       Минимальный объём передаваемых данных.
    Минусы:
        Не подходит, если нужны полные объекты Book.*/
    @Query("""
    SELECT a.name as authorName, COUNT(b.id) as bookCount FROM Author a LEFT JOIN a.books b GROUP BY a.id, a.name
    """)
    List<AuthorBookCountDTO> findAuthorsWithBookCount();


    //Решение 5: Кеширование
/* Плюсы:
      Уменьшает количество запросов к БД после первого обращения.
      Эффективно для часто читаемых данных.
   Минусы:
      Требует настройки кэша.
      Не решает проблему полностью при первом обращении. */
    @Query("SELECT a FROM Author a")
    List<Author> findAllAuthorsCache();
}
