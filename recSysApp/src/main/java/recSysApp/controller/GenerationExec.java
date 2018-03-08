package recSysApp.controller;

import java.util.ArrayList;
import java.util.List;

import facade.FacadeFilm;
import facade.FacadeFilmImpl;
import model.Film;

public class GenerationExec {
	
	
	public GenerationExec() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 *  
	 * @param     	
	 * @return list of candidate resources from LOD (film with vector from fastText)
	 */
	public List<Film> getRelatedFilms(String location) {
		List<Film> result = new ArrayList<Film>();
		FacadeFilm facadeFilm = new FacadeFilmImpl();
		result = facadeFilm.getCandidateFilms(location);

		return result;
		
	}

}
