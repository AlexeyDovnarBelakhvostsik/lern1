package by.belakhvostsik.lern1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lern1Application implements CommandLineRunner {

    private final AuthorService authorService;

    public Lern1Application(AuthorService authorService) {
        this.authorService = authorService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Lern1Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        authorService.demonstrateNPlusOneProblem();//Проблема
        authorService.demonstrateJoinFetchSolution();//Решение 1
        authorService.demonstrateEntityGraphSolution();//Решение 2
        authorService.demonstrateDtoProjectionSolution();//Решение 4
    }
}
