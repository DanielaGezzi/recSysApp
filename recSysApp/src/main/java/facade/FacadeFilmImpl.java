package facade;

import java.util.ArrayList;
import java.util.List;

import dataAccess.QueryController;
import dataAccess.QueryControllerSPARQL;
import model.Film;
import utils.FastText;

public class FacadeFilmImpl implements FacadeFilm {
	
	@Override
	public List<Film> getCandidateFilms(String location){
		
		List<Film> result = new ArrayList<Film>();
		FastText fastText = new FastText();
		QueryController queryController = new QueryControllerSPARQL();
		result = queryController.getCandidateFilms(QueryControllerSPARQL.ENDPOINT_LinkedMDB, location);
		
		for(Film film : result) {
			film.setVector(fastText.getVector(film.getTitle()));
		}
		
		return result;
		
	}

}
