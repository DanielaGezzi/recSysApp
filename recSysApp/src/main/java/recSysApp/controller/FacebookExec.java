package recSysApp.controller;

import java.util.ArrayList;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Page;
import com.restfb.types.User;

import facade.FacadeFacebookPage;
import facade.FacadeFacebookPageImpl;
import facade.FacadeUser;
import facade.FacadeUserImpl;
import model.FacebookPage;

public class FacebookExec {
	
	private FacebookClient fbClient;
	
	public FacebookExec(String accessToken){
		this.fbClient = new DefaultFacebookClient(accessToken, Version.LATEST);
	}

	public void getFacebookUserInfo() {
    	//FacadeUser facadeUser = new FacadeUserImpl();
		//get user info try catch
		User user = fbClient.fetchObject("me", User.class);
		//save user by facadeUser -> userSPARQL -> userGraphDB in local DB 
		//facadeUser.saveUserfromFacebook(user);
		//add link "likes" for every facebookPage facadeUser -> userSPARQL -> userGraphDB in local DB
		List<FacebookPage> facebookPageList = getFacebookUserLikes(); 		

		
		
	}
	
	private List<FacebookPage> getFacebookUserLikes() {
		
    	List<FacebookPage> facebookPageList = new ArrayList<FacebookPage>();	
    	FacadeFacebookPage facadeFbPage = new FacadeFacebookPageImpl();
	
		//get user likes info try catch
		Connection<Page> likes = fbClient.fetchConnection("me/likes", Page.class);
		
        for(List<Page> userLikes: likes) {
	          for ( Page page : userLikes){		         
	              Page singlePage = fbClient.fetchObject(page.getId(), Page.class, 
	            		  Parameter.with("fields", "category"));
	              if(singlePage.getCategory().equals("Movie") | 
	            		  singlePage.getCategory().equals("TV Show") ) {	            	  		
	            	  FacebookPage facebookPage = new FacebookPage(page.getId(), page.getName().replaceAll(" ", "_"), singlePage.getCategory());
	            	  facebookPageList.add(facadeFbPage.saveFacebookPage(facebookPage));
	            	  
	              }
	          }
        }
        return facebookPageList;
        
		
	}
	

}
