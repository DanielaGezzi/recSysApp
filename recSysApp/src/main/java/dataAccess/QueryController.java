package dataAccess;

import java.util.List;

import model.Film;

public interface QueryController {
	
	List<String> getCandidateFilms(String endPoint, String Location);

}
