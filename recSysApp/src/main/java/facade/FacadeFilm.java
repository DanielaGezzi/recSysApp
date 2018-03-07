package facade;

import java.util.List;

import model.Film;

public interface FacadeFilm {

	List<Film> getCandidateFilms(String location);
}
