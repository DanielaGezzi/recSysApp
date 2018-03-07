package utils;

import java.io.File;
import java.util.List;

import com.github.jfasttext.JFastText;

public class FastText {
	private static FastText instance = null;
	private JFastText jft = new JFastText();
	private final String model = "D:/Documenti/workspace_test/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/recSysApp/WEB-INF/classes/wiki.en.bin";
	    
		private FastText() {
			//File file = new File(getClass().getClassLoader().getResource("wiki.en.bin").getFile());
			//String modelPath = file.getAbsolutePath();
			//System.out.println(modelPath);
			this.jft.loadModel(model);
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
