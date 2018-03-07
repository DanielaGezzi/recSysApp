package recSysApp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import model.Film;


@Path("/services")
public class Controller {

	@POST
	@Path("/facebook/user")	
    @Consumes(MediaType.APPLICATION_JSON)
	public Response saveFacebookUser(String queryParam) {

		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, String> map = gson.fromJson(queryParam, Map.class);
    	String accessToken = map.get("accessToken");
    	
    	FacebookExec fbExecutioner = new FacebookExec(accessToken);
		fbExecutioner.getFacebookUser();
		fbExecutioner.getFacebookUserLikes();		
		
		return Response.status(200).build();
	}
	
	@GET
	@Path("/film/location/{location}")	
    @Produces(MediaType.APPLICATION_JSON)
	public List<Film> getFilmByLocationName(@PathParam("location") String locationName) {
		
		List<Film> filmList = new ArrayList<Film>();
		GenerationExec genExecutioner = new GenerationExec();
		filmList = genExecutioner.getRelatedFilm(locationName);
		
		return filmList;
	}
}
