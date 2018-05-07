package facade;

import java.util.List;

import model.Film;
import model.Location;

public interface FacadeFilm {

	List<Film> getCandidateFilms(Location location);
}
