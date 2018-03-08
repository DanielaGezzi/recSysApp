package facade;

import dataAccess.localDataset.UserGraphDB;
import dataAccess.localDataset.UserRepository;
import model.User;

public class FacadeUserImpl implements FacadeUser {
	
	public void saveUserfromFacebook(com.restfb.types.User facebookUser) {
		
		User user = new User(facebookUser.getId(), facebookUser.getFirstName(), facebookUser.getLastName());
		UserRepository userRepo = new UserGraphDB();
		userRepo.saveUser(user);
		
		
	}

}
