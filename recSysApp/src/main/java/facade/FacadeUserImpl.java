package facade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import dataAccess.localDataset.UserGraphDB;
import dataAccess.localDataset.UserRepository;
import model.FacebookPage;
import model.User;
import utils.FastText;
import utils.PropertyFileReader;

public class FacadeUserImpl implements FacadeUser {
	
	public void saveUserfromFacebook(com.restfb.types.User facebookUser) {
		String anonymousID = "placeholder";
		try {
			File file = new File(getClass().getClassLoader().getResource("conf.properties").getFile());
			Path dataFile = Paths.get(file.getPath());
			anonymousID = PropertyFileReader.loadProperties(dataFile.toString()).get("userId");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(anonymousID == "placeholder") return;
		User user = new User(facebookUser.getId(), facebookUser.getFirstName(), facebookUser.getLastName());
		user.setId(anonymousID);
		UserRepository userRepo = new UserGraphDB();
		userRepo.saveUser(user);
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
