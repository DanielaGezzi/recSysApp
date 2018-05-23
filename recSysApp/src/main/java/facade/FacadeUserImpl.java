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
			if(userRepo.getUser(facebookUser.getId()) == null) {
				long id = 500000;
				List<String> ids = userRepo.getUsersIds();
				if(!ids.isEmpty()) {
					id = Long.parseLong(ids.get(ids.size() - 1)) + 1;
				}
				user.setId(String.valueOf(id));
				userRepo.saveUser(user);
			}
	}
	
	public User getUser(com.restfb.types.User facebookUser) {
		UserRepository userRepo = new UserGraphDB();
		return userRepo.getUser(facebookUser.getId());
		
	}
	
	
	public void saveUserLikes(com.restfb.types.User facebookUser, List<FacebookPage> facebookPageList) {
		UserRepository userRepo = new UserGraphDB();
		for(FacebookPage fbpage: facebookPageList) {
			userRepo.saveUserLike(facebookUser.getId(), fbpage.getFacebookID());
		}
	}
	
	public List<FacebookPage> getUserLikes(String fbUserId){
		List<FacebookPage> userLikes = new ArrayList<FacebookPage>();
		
		UserRepository userRepo = new UserGraphDB();
		userLikes = userRepo.getUserLikes(fbUserId);
		
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
