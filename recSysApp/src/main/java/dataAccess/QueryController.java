package dataAccess;

import java.util.List;

import model.Film;

public interface QueryController {
	
	List<Film> getCandidateFilms(String endPoint, String Location);

}
