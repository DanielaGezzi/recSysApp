package dataAccess.localDataset;

import java.util.List;

import model.FacebookPage;
import model.User;

public interface UserRepository {
	
	void saveUser(User user);
	
	public User getUser(String userFbId);

	void saveUserLike(String userID, String facebookID);
	
	public List<FacebookPage> getUserLikes(String userID);

}
