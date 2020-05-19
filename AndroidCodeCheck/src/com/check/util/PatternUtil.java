package com.check.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtil {

	public static void main(String[] args) {
		boolean matchAllNumber = matchAllNumber("8634");
		System.out.println(matchAllNumber);
	}
	
	public static String getStyleName(String line) {
		Pattern compile = Pattern.compile("<style name=\"(.*?)\"");
		Matcher matcher = compile.matcher(line);
		while (matcher.find()) {
			String group = matcher.group(1);
			return group;
		}
		return "";
	}
	
	public static boolean matchAllNumber(String line) {
		Pattern compile = Pattern.compile("^(\\d)*$");
		Matcher matcher = compile.matcher(line);
		return matcher.matches();
	}

}
