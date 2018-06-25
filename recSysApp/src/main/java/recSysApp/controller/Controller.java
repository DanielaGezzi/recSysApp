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
import utils.InterviewFileWriter;


@Path("/services")
public class Controller{
	
	/*@GET
	@Path("/init")
    @Produces(MediaType.TEXT_PLAIN)
	public String init() {
		LensKitRecommender.getLensKitRecommender();
		return "ok";
	}*/

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
		
		Map<String,Object> result = new HashMap<String,Object>();
		Location location = new Location(name, city, state, country, latitude, longitude);
		//get user info
		FacebookExec fbExecutioner = new FacebookExec(accessToken);
		User facebookUser = fbExecutioner.getFacebookUserInfo();
		FacadeUser facadeUser = new FacadeUserImpl();
		model.User user = facadeUser.getUser(facebookUser);
		if(user != null) {
			//generate list of candidates from LOD
			GenerationExec genExecutioner = new GenerationExec();
			List<Film> filmList = genExecutioner.getRelatedFilms(location);
			//get user likes
			List<FacebookPage> facebookPageList = genExecutioner.getUserFacebookLikes(user);
			//rank film by word2vect + fbLikes
			RankingExec rankExecutioner = new RankingExec();
			List<Film> filmListVect = rankExecutioner.rankFilmsByVect(filmList, facebookPageList);
			//rank film by lenskit
			List<String> filmListLensKit = rankExecutioner.rankFilmByLenskit(filmList, user, 5);
			result.put("lk", filmListLensKit);
			result.put("w2v", filmListVect);
		}
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
		if(user != null) {
			FacadeLensKit facadeLensKit = new FacadeLensKitImpl();
			facadeLensKit.saveRatings(Long.parseLong(user.getId()), ratings);
			return Response.status(200).build();
		}
		return Response.status(500).build();
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
		Map<String, Double> w2vNov = (Map<String,Double>) map.get("w2vnov");
		@SuppressWarnings("unchecked")
		Map<String, Double> w2vDCG = (Map<String,Double>) map.get("w2vdcg");
		@SuppressWarnings("unchecked")
		Map<String,Double> lkNov = (Map<String,Double>) map.get("lknov");
		@SuppressWarnings("unchecked")
		Map<String,Double> lkDCG = (Map<String,Double>) map.get("lkdcg");
    	String accessToken = (String) map.get("accessToken");
		FacebookExec fbExecutioner = new FacebookExec(accessToken);
		User facebookUser = fbExecutioner.getFacebookUserInfo();
		FacadeUser facadeUser = new FacadeUserImpl();
		model.User user = facadeUser.getUser(facebookUser);
		if(user != null) {
				InterviewFileWriter.write(user, w2vNov, w2vDCG, lkNov, lkDCG);
				return Response.status(200).build();
		}
		return Response.status(500).build();
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
