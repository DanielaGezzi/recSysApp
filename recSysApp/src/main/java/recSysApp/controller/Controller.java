package recSysApp.controller;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;


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
}
