package recSysApp.controller;

import java.util.ArrayList;
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

import model.FacebookPage;
import model.Film;


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
		fbExecutioner.getFacebookUserInfo();
		return Response.status(200).build();
	}
	
	
	@GET
	@Path("/film/location/{location}")
    @Produces(MediaType.APPLICATION_JSON)
	public List<Film> getFilmByLocationName(@PathParam("location") String locationName) {
		
		List<Film> filmList = new ArrayList<Film>();
		GenerationExec genExecutioner = new GenerationExec();
		filmList = genExecutioner.getRelatedFilms(locationName);
		//RankingExec rankExecutioner = new RankingExec();
		//filmList = rankExecutioner.rankFilms(filmList, this.user);
		
		return filmList;
	}
	
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Film> getFilmByLocationNameTest(@QueryParam("accessToken") String accessToken,
												@QueryParam("location") String location) {
    	
    	FacebookExec fbExecutioner = new FacebookExec(accessToken);
		List<FacebookPage> facebookPageList = fbExecutioner.getFacebookUserLikesTest();
		List<Film> filmList = new ArrayList<Film>();
		GenerationExec genExecutioner = new GenerationExec();
		filmList = genExecutioner.getRelatedFilms(location);
		RankingExec rankExecutioner = new RankingExec();
		filmList = rankExecutioner.rankFilms(filmList, facebookPageList);

		System.out.println(filmList);
		return filmList;
		
	}
}
