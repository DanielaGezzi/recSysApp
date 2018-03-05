package recSysApp.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Page;
import com.restfb.types.User;

import model.FacebookPage;

@Path("/facebook")
public class FacebookExec {
	
	@POST
	@Path("/user/save")	
    @Consumes(MediaType.APPLICATION_JSON)
	public Response saveFacebookUser(String queryParam) {

		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, String> map = gson.fromJson(queryParam, Map.class);
    	String accessToken = map.get("accessToken");
    	
    	List<FacebookPage> facebookPageList = new ArrayList<FacebookPage>();
		List<String> facebookPageNameList = new ArrayList<String>();
		
		FacebookClient fbClient = new DefaultFacebookClient(accessToken, Version.LATEST);
		
		//get user info
		User user = fbClient.fetchObject("me", User.class);
		model.User myUser = new model.User(user.getId(), user.getName(), accessToken);
		
		//get user likes info
		Connection<Page> likes = fbClient.fetchConnection("me/likes", Page.class);
        for(List<Page> userLikes: likes) {
	          for ( Page page : userLikes){		         
	              Page singlePage = fbClient.fetchObject(page.getId(), Page.class, 
	            		  Parameter.with("fields", "category"));
	              if(singlePage.getCategory().equals("Movie") | 
	            		  singlePage.getCategory().equals("TV Show") ) {
	            	  FacebookPage facebookPage = new FacebookPage(page.getId(), page.getName().replaceAll(" ", "_"), singlePage.getCategory());
	            	  facebookPageList.add(facebookPage);
	            	  facebookPageNameList.add(page.getName().replaceAll(" ", "_"));
	            	  java.nio.file.Path file = Paths.get("src/main/resources/data/FacebookMovieList.txt");
	            	  System.out.println(file.getRoot());
				      try {
						Files.write(file, facebookPageNameList, Charset.forName("UTF-8"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	              }
	          }
        }
		
		
		return Response.status(200).build();
	}
	

}
