package facade;

import dataAccess.localDataset.FacebookPageGraphDB;
import dataAccess.localDataset.FacebookPageRepository;
import model.FacebookPage;

public class FacadeFacebookPageImpl implements FacadeFacebookPage {
	
	public FacebookPage saveFacebookPage(FacebookPage facebookPage) {
		
		FacebookPageRepository fbPageRepo = new FacebookPageGraphDB();
		fbPageRepo.saveFacebookPage(facebookPage);
		return facebookPage;
	}

}
