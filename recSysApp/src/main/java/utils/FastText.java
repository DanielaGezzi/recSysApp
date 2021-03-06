package utils;

import java.io.File;
import java.util.List;

import com.github.jfasttext.JFastText;

public class FastText {
	private static FastText instance = null;
	private static JFastText jft = null;
	    
		private FastText() {
			File file = new File(getClass().getClassLoader().getResource("wiki.en.bin").getFile());
			FastText.jft = new JFastText();
			FastText.jft.loadModel(file.getAbsolutePath());
		}
		
		public static synchronized FastText getFastText() {
			if (instance == null) {
				instance = new FastText();
			}
			return instance;
		}
		
	    public List<Float> getVector(String word) {
			return jft.getVector(word);
			}

}
