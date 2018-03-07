package utils;

import java.util.List;
import com.github.jfasttext.JFastText;

public class FastText {
	private JFastText jft = new JFastText();
	private String model = "src/main/resources/wiki.en.bin";
	    
		public FastText() {
			this.jft.loadModel(model);
		}
		
	    public List<Float> getVector(String word) {
			return jft.getVector(word);
			}

}
