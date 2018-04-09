package recSysApp.controller;

import java.util.List;
import com.restfb.types.User;
import facade.FacadeFilm;
import facade.FacadeFilmImpl;
import facade.FacadeUser;
import facade.FacadeUserImpl;
import model.FacebookPage;
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
		
		FacadeFilm facadeFilm = new FacadeFilmImpl();
		return facadeFilm.getCandidateFilms(location);

	}
	
	public List<FacebookPage> getUserFacebookLikes(User facebookUser){
		
		FacadeUser facadeUser = new FacadeUserImpl();
		return facadeUser.getUserLikes(facebookUser);
		
	}

}
