package facade;

import java.util.List;

import model.FacebookPage;
import model.User;

public interface FacadeUser {

	void saveUserfromFacebook(com.restfb.types.User user);
	void saveUserLikes(com.restfb.types.User facebookUser, List<FacebookPage> facebookPageList);
	List<FacebookPage> getUserLikes(String fbUserId);
	User getUser(com.restfb.types.User user);
}
