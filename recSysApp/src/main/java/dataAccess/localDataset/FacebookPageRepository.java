package dataAccess.localDataset;

import java.util.List;

import model.FacebookPage;

public interface FacebookPageRepository {
	
	void saveFacebookPage(FacebookPage fbPage);
	
	List<FacebookPage> getFacebookPageByUser(String userID);

}
