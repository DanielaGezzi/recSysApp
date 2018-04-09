package facade;

import java.util.ArrayList;
import java.util.List;

import dataAccess.localDataset.UserGraphDB;
import dataAccess.localDataset.UserRepository;
import model.FacebookPage;
import model.User;
import utils.FastText;

public class FacadeUserImpl implements FacadeUser {
	
	public void saveUserfromFacebook(com.restfb.types.User facebookUser) {
		
		User user = new User(facebookUser.getId(), facebookUser.getFirstName(), facebookUser.getLastName());
		UserRepository userRepo = new UserGraphDB();
		userRepo.saveUser(user);
	}
	
	public void saveUserLikes(com.restfb.types.User facebookUser, List<FacebookPage> facebookPageList) {
		UserRepository userRepo = new UserGraphDB();
		for(FacebookPage fbpage: facebookPageList) {
			userRepo.saveUserLike(facebookUser.getId(), fbpage.getFacebookID());
		}
	}
	
	public List<FacebookPage> getUserLikes(com.restfb.types.User facebookUser){
		List<FacebookPage> userLikes = new ArrayList<FacebookPage>();
		
		UserRepository userRepo = new UserGraphDB();
		userLikes = userRepo.getUserLikes(facebookUser.getId());
		
		FastText fastText = FastText.getFastText();
		for(FacebookPage fbPage: userLikes) {
			try {
				fbPage.setVector(fastText.getVector(fbPage.getName().replaceAll(" ", "_")));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return userLikes;
		
	}

}
