package facade;

import model.FacebookPage;
import utils.FastText;

public class FacadeFacebookPageImpl implements FacadeFacebookPage {
	
	public FacebookPage saveFacebookPage(FacebookPage facebookPage) {
		FastText fastText = FastText.getFastText();
		try {
		facebookPage.setVector(fastText.getVector(facebookPage.getName()));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//save local...
		System.out.println(facebookPage);
		return facebookPage;
	}

}
