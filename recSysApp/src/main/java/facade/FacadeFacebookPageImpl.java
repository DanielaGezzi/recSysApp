package facade;

import dataAccess.localDataset.FacebookPageGraphDB;
import dataAccess.localDataset.FacebookPageRepository;
import model.FacebookPage;
import utils.FastText;

public class FacadeFacebookPageImpl implements FacadeFacebookPage {
	
	public FacebookPage saveFacebookPage(FacebookPage facebookPage) {
		FastText fastText = FastText.getFastText();
		try {
		facebookPage.setVector(fastText.getVector(facebookPage.getName().replaceAll(" ", "_")));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		FacebookPageRepository fbPageRepo = new FacebookPageGraphDB();
		fbPageRepo.saveFacebookPage(facebookPage);
		//System.out.println(facebookPage);
		return facebookPage;
	}

}
