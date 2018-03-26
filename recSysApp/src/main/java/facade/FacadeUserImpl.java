package facade;

import java.util.List;

import dataAccess.localDataset.UserGraphDB;
import dataAccess.localDataset.UserRepository;
import model.FacebookPage;
import model.User;

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

}
