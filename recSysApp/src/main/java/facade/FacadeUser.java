package facade;

import java.util.List;

import model.FacebookPage;

public interface FacadeUser {

	void saveUserfromFacebook(com.restfb.types.User user);
	public void saveUserLikes(com.restfb.types.User facebookUser, List<FacebookPage> facebookPageList);
}
