package recSysApp.controller;


import java.util.HashMap;
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
import utils.FastText;
import utils.InterviewFileWriter;


@Path("/services")
public class Controller {
	
	@POST
	@Path("/init")
	public void init() {
		FastText.getFastText();
	}

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
	public Map<String,Object> getFilmByLocation(@QueryParam("accessToken") String accessToken,
										@QueryParam("latitude") String latitude,
										@QueryParam("longitude") String longitude,
										@QueryParam("place") String name,
										@QueryParam("city") String city,
										@QueryParam("state") String state,
										@QueryParam("country") String country) {

		Location location = new Location(name, city, state, country, latitude, longitude);
		//get user info
		FacebookExec fbExecutioner = new FacebookExec(accessToken);
		User facebookUser = fbExecutioner.getFacebookUserInfo();
		FacadeUser facadeUser = new FacadeUserImpl();
		model.User user = facadeUser.getUser(facebookUser);
		//generate list of candidates from LOD
		GenerationExec genExecutioner = new GenerationExec();
		List<Film> filmList = genExecutioner.getRelatedFilms(location);
		//get user likes
		List<FacebookPage> facebookPageList = genExecutioner.getUserFacebookLikes(user.getFacebookID());
		//rank film by word2vect + fbLikes
		RankingExec rankExecutioner = new RankingExec();
		List<Film> filmListVect = rankExecutioner.rankFilmsByVect(filmList, facebookPageList);
		//rank film by lenskit
		List<String> filmListLensKit = rankExecutioner.rankFilmByLenskit(filmList, user, 5);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("lk", filmListLensKit);
		result.put("w2v", filmListVect);
		return result;
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
		@SuppressWarnings("unchecked")
		Map<String, String> ratings = (Map<String,String>) map.get("ratings");
    	String accessToken = (String) map.get("accessToken");
		FacebookExec fbExecutioner = new FacebookExec(accessToken);
		User facebookUser = fbExecutioner.getFacebookUserInfo();
		FacadeUser facadeUser = new FacadeUserImpl();
		model.User user = facadeUser.getUser(facebookUser);
		FacadeLensKit facadeLensKit = new FacadeLensKitImpl();
		facadeLensKit.saveRatings(Long.parseLong(user.getId()), ratings);
		return Response.status(200).build();
	}
	
	@POST
	@Path("/interview")	
    @Consumes(MediaType.APPLICATION_JSON)
	public Response saveInterview(String requestPayload) {
		
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, Object> map = gson.fromJson(requestPayload, Map.class);
		System.out.println(map);
		@SuppressWarnings("unchecked")
		Map<String, Double> w2v = (Map<String,Double>) map.get("w2v");
		@SuppressWarnings("unchecked")
		Map<String,Double> lk = (Map<String,Double>) map.get("lk");
		System.out.println(w2v);
		System.out.println(lk);
    	String accessToken = (String) map.get("accessToken");
		FacebookExec fbExecutioner = new FacebookExec(accessToken);
		User facebookUser = fbExecutioner.getFacebookUserInfo();
		FacadeUser facadeUser = new FacadeUserImpl();
		model.User user = facadeUser.getUser(facebookUser);
		InterviewFileWriter.write(user, w2v, lk);
		return Response.status(200).build();
	}
	
	/*@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Long> test(){
		FacebookExec fbExecutioner = new FacebookExec("");
		User facebookUser = fbExecutioner.getFacebookUserInfo();
		FacadeUser facadeUser = new FacadeUserImpl();
		model.User user = facadeUser.getUser(facebookUser);
		FacadeLensKit facadeLensKit = new FacadeLensKitImpl();
		return facadeLensKit.getRecommendations(Long.parseLong(user.getId()), 10);

	}*/
}
