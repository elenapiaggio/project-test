package movies.data;

import movies.model.Genre;
import movies.model.Movie;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

public class MovieRepositoryIntegrationTest {

  private MovieRepositoryJdbc movieRepository;
  private DriverManagerDataSource dataSource;

  @Before
  public void setUp() throws Exception {
    dataSource =
      new DriverManagerDataSource("jdbc:h2:mem:test;MODE=MYSQL", "sa", "sa");

    ScriptUtils.executeSqlScript(dataSource.getConnection(),
      new ClassPathResource("sql-scripts/test-data.sql"));

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    movieRepository = new MovieRepositoryJdbc(jdbcTemplate);
  }

  @Test
  public void loadAllMovies() throws SQLException {
    Collection<Movie> movies = movieRepository.findAll();

    assertThat( movies, is(Arrays.asList(
      new Movie(1, "Dark Knight", 152, Genre.ACTION),
      new Movie(2, "Memento", 113, Genre.THRILLER),
      new Movie(3, "Matrix", 136, Genre.ACTION)
    )) );
  }

  @Test
  public void loadMovieById(){
    Movie movie = movieRepository.findById(2);
    assertThat(movie, is(new Movie(2, "Memento", 113, Genre.THRILLER)));
  }

  @Test
  public void insertAMovie() {
    Movie movie = new Movie(4,"Al diablo con el diablo", 112, Genre.COMEDY);
    movieRepository.saveOrUpdate(movie);
    Movie movieFromDB = movieRepository.findById(4);
    assertThat(movieFromDB, is(movie));
  }

  @After
  public void tearDown() throws Exception {
    final Statement statement = dataSource.getConnection().createStatement();
    statement.execute("drop all objects delete files");
  }
}