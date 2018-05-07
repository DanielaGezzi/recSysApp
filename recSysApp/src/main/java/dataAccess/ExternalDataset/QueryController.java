package dataAccess.ExternalDataset;

import java.util.List;

import model.Film;
import model.Location;

public interface QueryController {
	
	List<Film> getCandidateFilms(String latitude, String longitude, String name, String city, String state, String country);

}
