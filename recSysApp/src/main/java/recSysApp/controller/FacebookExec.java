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
import utils.FastText;

public class FacebookExec {
	
	private FacebookClient fbClient;
	
	public FacebookExec(String accessToken){
		this.fbClient = new DefaultFacebookClient(accessToken, Version.LATEST);
	}
	
	public User getFacebookUserInfo() {
		User user = fbClient.fetchObject("me", User.class, 
				Parameter.with("fields", "id,first_name,last_name"));
		return user;
		
	}

	public void saveFacebookUserInfo() {
    	FacadeUser facadeUser = new FacadeUserImpl();
		//get user info try catch
		User user = fbClient.fetchObject("me", User.class, 
				Parameter.with("fields", "id,first_name,last_name"));

		facadeUser.saveUserfromFacebook(user); //save user in local DB
		List<FacebookPage> facebookPageList = getFacebookUserLikes();
		facadeUser.saveUserLikes(user, facebookPageList); //save user <--> fbpage in localDB
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
	              //if(singlePage.getCategory().equals("Movie") | 
	            		  //singlePage.getCategory().equals("TV Show") ) {	            	  		
	            	  FacebookPage facebookPage = new FacebookPage(page.getId(), page.getName(), singlePage.getCategory());
	            	  facebookPageList.add(facadeFbPage.saveFacebookPage(facebookPage)); //save fbPage
	            	  
	              //}
	          }
        }
        return facebookPageList;
        
		
	}
	
	/*public List<FacebookPage> getFacebookUserLikesTest() {
	
	FastText fastText = FastText.getFastText();
	List<FacebookPage> facebookPageList = new ArrayList<FacebookPage>();	

	//get user likes info try catch
	Connection<Page> likes = fbClient.fetchConnection("me/likes", Page.class);
	
    for(List<Page> userLikes: likes) {
          for ( Page page : userLikes){		         
              Page singlePage = fbClient.fetchObject(page.getId(), Page.class, 
            		  Parameter.with("fields", "category"));
              //if(singlePage.getCategory().equals("Movie") | 
            		  //singlePage.getCategory().equals("TV Show") ) {	            	  		
            	  FacebookPage facebookPage = new FacebookPage(page.getId(), page.getName(), singlePage.getCategory());
            	  try {
          			facebookPage.setVector(fastText.getVector(facebookPage.getName().replaceAll(" ", "_")));
          			}catch(Exception e){
          				e.printStackTrace();
          			}
            	  facebookPageList.add(facebookPage); //save fbPage
            	  
             // }
          }
    }
    return facebookPageList;
    
	
}*/
	

}
