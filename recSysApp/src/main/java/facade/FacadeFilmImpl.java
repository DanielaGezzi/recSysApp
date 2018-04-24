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
		List<String> location2 = new ArrayList<String>();
		location2.add("Rome");
		location2.add("Italy");
		result = queryController.getCandidateFilmsTest("41.90083", "12.48311", location2);
		
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
