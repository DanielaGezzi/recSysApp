package recSysApp.controller;


import java.util.List;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.restfb.types.User;

import facade.FacadeLensKit;
import facade.FacadeLensKitImpl;
import facade.FacadeUser;
import facade.FacadeUserImpl;
import model.FacebookPage;
import model.Film;
import model.Location;
import utils.LensKitHelper;


@Path("/services")
public class Controller {

	// sarebbe opportuno avere un client REST che stora le informazioni utente
	// e che per ogni richiesta al server REST invia sempre le info di autenticazione
	// in modo da essere autenticato ad ogni richiesta.
	// In questo modo viene preservata la condizione stateless come richiesto dal paradigma REST

	@POST
	@Path("/facebook/user")	
    @Consumes(MediaType.APPLICATION_JSON)
	public Response saveFacebookUser(String requestPayload) {

		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, String> map = gson.fromJson(requestPayload, Map.class);
    	String accessToken = map.get("accessToken");
    	
    	FacebookExec fbExecutioner = new FacebookExec(accessToken);
		fbExecutioner.saveFacebookUserInfo();
		return Response.status(200).build();
	}
	
	
	@GET
	@Path("/film/location")
    @Produces(MediaType.APPLICATION_JSON)
	public List<Film> getFilmByLocation(@QueryParam("accessToken") String accessToken,
										@QueryParam("latitude") String latitude,
										@QueryParam("longitude") String longitude,
										@QueryParam("place") String name,
										@QueryParam("city") String city,
										@QueryParam("state") String state,
										@QueryParam("country") String country) {

		Location location = new Location(name, city, state, country, latitude, longitude);
		//System.out.println(location);
		FacebookExec fbExecutioner = new FacebookExec(accessToken);
		User facebookUser = fbExecutioner.getFacebookUserInfo();
		GenerationExec genExecutioner = new GenerationExec();
		List<Film> filmList = genExecutioner.getRelatedFilms(location);
		List<FacebookPage> facebookPageList = genExecutioner.getUserFacebookLikes(facebookUser);
		RankingExec rankExecutioner = new RankingExec();
		filmList = rankExecutioner.rankFilms(filmList, facebookPageList);
		
		return filmList;
	}
	
	@GET
	@Path("/film/askToRate/{n}")
    @Produces(MediaType.APPLICATION_JSON)
	public List<String> getAskToRateFilms(@PathParam("n") int n){
		FacadeLensKit facadeLensKit = new FacadeLensKitImpl();
		return facadeLensKit.getLogPopularityEntropyFilms().subList(0, n);
		
	}	
	
	@POST
	@Path("/film/newRatings")	
    @Consumes(MediaType.APPLICATION_JSON)
	public Response saveUserRatings(String requestPayload) {
		
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, Object> map = gson.fromJson(requestPayload, Map.class);
		Map<String, Double> ratings = (Map<String,Double>) map.get("ratings");
    	String accessToken = (String) map.get("accessToken");
		FacebookExec fbExecutioner = new FacebookExec(accessToken);
		User facebookUser = fbExecutioner.getFacebookUserInfo();
		FacadeUser facadeUser = new FacadeUserImpl();
		model.User user = facadeUser.getUser(facebookUser);
		FacadeLensKit facadeLensKit = new FacadeLensKitImpl();
		facadeLensKit.saveRatings(Long.parseLong(user.getId()), ratings);
		System.out.println(ratings);
		return Response.status(200).build();
	}
}
