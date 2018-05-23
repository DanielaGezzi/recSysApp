package dataAccess.localDataset;

import java.util.List;

import model.FacebookPage;
import model.User;

public interface UserRepository {
	
	void saveUser(User user);
	
	User getUser(String userFbId);
	
	List<String> getUsersIds();

	void saveUserLike(String userID, String facebookID);
	
	List<FacebookPage> getUserLikes(String userID);

}
