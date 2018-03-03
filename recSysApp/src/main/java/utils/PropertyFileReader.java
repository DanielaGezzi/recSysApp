package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyFileReader {
	
	public static Map<String, String> loadProperties(String fileName) throws IOException{
			
			Properties props = new Properties();
	        FileInputStream fis = new FileInputStream(fileName);
	        
	        //loading properties from properties file
	        props.load(fis);
	        	
			//reading property
		    Map<String, String> prop = new HashMap<String, String>();
		    		    
		    if(props.containsKey("appId"))
	        	prop.put("appId", props.getProperty("appId"));	
		    
		    if(props.containsKey("appSecret"))
	        	prop.put("appSecret", props.getProperty("appSecret"));
		        
		    return prop;
		    
	}
		    
}
