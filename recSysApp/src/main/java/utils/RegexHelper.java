package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHelper {
	
	public static List<String> getRegexString(String regex, String input) {
		
		List<String> outputList = new ArrayList<String>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		
		while(m.find()) {
			outputList.add(m.group());
		}
		return outputList;
	}

}
