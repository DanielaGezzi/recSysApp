package facade;

import java.util.ArrayList;
import java.util.List;

import dataAccess.ExternalDataset.QueryController;
import dataAccess.ExternalDataset.QueryControllerRDF4J;
import model.Film;
import model.Location;
import utils.FastText;

public class FacadeFilmImpl implements FacadeFilm {
	
	@Override
	public List<Film> getCandidateFilms(Location location){
		
		List<Film> result = new ArrayList<Film>();
		FastText fastText = FastText.getFastText();
		QueryController queryController = new QueryControllerRDF4J();
		result = queryController.getCandidateFilms( location.getLatitude(),
													location.getLongitude(),
													location.getName(),
													location.getCity(),
													location.getState(),
													location.getCountry() );		
		for(Film film : result) {
			try {
				film.setVector(fastText.getVector(film.getTitle().replace(" ", "_")));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return result;
		
	}

}
