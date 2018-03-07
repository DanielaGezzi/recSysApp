package facade;

import model.FacebookPage;
import utils.FastText;

public class FacadeFacebookPageImpl implements FacadeFacebookPage {
	
	public FacebookPage saveFacebookPage(FacebookPage facebookPage) {
		FastText fastText = new FastText();
		try {
		facebookPage.setVector(fastText.getVector(facebookPage.getName()));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//save local...
		
		return facebookPage;
	}

}
