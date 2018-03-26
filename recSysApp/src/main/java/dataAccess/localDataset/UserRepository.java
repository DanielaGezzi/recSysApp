package dataAccess.localDataset;

import model.User;

public interface UserRepository {
	
	void saveUser(User user);

	void saveUserLike(String userID, String facebookID);
	
	

}
