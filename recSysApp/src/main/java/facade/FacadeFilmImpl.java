package facade;

import java.util.ArrayList;
import java.util.List;

import dataAccess.ExternalDataset.QueryController;
import dataAccess.ExternalDataset.QueryControllerRDF4J;
import model.Film;
import utils.FastText;

public class FacadeFilmImpl implements FacadeFilm {
	
	@Override
	public List<Film> getCandidateFilms(String location){
		
		List<Film> result = new ArrayList<Film>();
		FastText fastText = FastText.getFastText();
		QueryController queryController = new QueryControllerRDF4J();
		result = queryController.getCandidateFilms(QueryControllerRDF4J.ENDPOINT_LinkedMDB, location);
		
		for(Film film : result) {
			film.setVector(fastText.getVector(film.getTitle()));
		}
		
		return result;
		
	}

}
