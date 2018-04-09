package dataAccess.localDataset;

import java.util.List;

import model.FacebookPage;

public interface FacebookPageRepository {
	
	void saveFacebookPage(FacebookPage fbPage);
	
	public List<FacebookPage> getFacebookPageByUser(String userID);

}
