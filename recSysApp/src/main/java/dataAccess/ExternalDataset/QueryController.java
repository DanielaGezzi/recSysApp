package dataAccess.ExternalDataset;

import java.util.List;

import model.Film;

public interface QueryController {
	
	String generateQuery(String endPoint, String location);
	List<Film> getCandidateFilms(String endPoint, String location);
	List<Film> getCandidateFilmsTest(String lat, String lon, List<String> location);

}
