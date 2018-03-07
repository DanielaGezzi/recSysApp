package recSysApp.controller;

import java.util.ArrayList;
import java.util.List;

import dataAccess.QueryController;
import dataAccess.QueryControllerSPARQL;
import facade.FacadeFilm;
import facade.FacadeFilmImpl;
import model.Film;

public class GenerationExec {
	
	/**
	 *  
	 * @param     	
	 * @return list of candidate resources from LOD (film with vector from fastText)
	 */
	public List<Film> getRelatedFilm(String location) {
		List<Film> result = new ArrayList<Film>();
		FacadeFilm facadeFilm = new FacadeFilmImpl();
		result = facadeFilm.getCandidateFilms(location);

		return result;
		
	}

}
