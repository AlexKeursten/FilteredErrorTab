package filterPackage;

import java.util.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.*;

public class filter {
	static String currentBuild = "";
	static String regexString = "";
	static String redRegexString = "";
	static String orangeRegexString = "";
	static String keepRegexString = "";
	static String startBuildRegexString = "";
	static String endBuildRegexString = "";
	static Pattern linePattern;
	static Pattern redPattern;
	static Pattern orangePattern;
	static Pattern keepPattern;
	static Pattern startBuildPattern;
	static Pattern endBuildPattern;
	static Matcher matcher;
	static Matcher redMatcher;
	static Matcher orangeMatcher;
	static Matcher keepMatcher;
	static Matcher startBuildMatcher;
	static Matcher endBuildMatcher;
	
	static String buildString = "";
   	static String Build = "";
   	static String oldBuild = "";
   	static String okTitle = "";
   	static String badTitle = "";
   	static int uniqueID = 0;
   	static String Colour;
   	
	//these booleans are used to create the tree structures by determining when we start and finish a build
   	static boolean startBuild = false;
   	static boolean endBuild = false;
   	static boolean inBuild = false;
   	static boolean jarBuild = false;
   	static boolean badBuild = false;
	
   	static boolean keep;
    static boolean addColour = false;
    static boolean keepNext = false;

    
   	public static String filterLine(String line){
		String returnString = "";
		//because the symbols are in I think charset=ISO-8859-1 they come out strange and need to be changed
		line = line.replaceAll("<.*?>", "");
		line = line.replaceAll("Ã¢â¬Ë", "'");
		line = line.replaceAll("Ã¢â¬â¢", "'");
		line = line.replaceAll("&#092;", "\\\\");
		line = line.replaceAll("&#039;", "'");
		resetMatchers(line);
		String prevToken = "";
		String token = "";
	    keep = true;
	    addColour = false;
	    startBuild = false;
	    endBuild = false;
	    Colour = "636363";
		//if we find the pattern we don't want to keep the line.
	    while(matcher.find()){
	    	keep = false;
	    }
		//some lines that we want to see are excluded by the broad regex patterns in the excluding patterns
	    while(keepMatcher.find()){
	    	keep = true;
	    }
	    if (keep == true){
	    	//finding the start of builds (need this to form the tree structures)
	    	while(startBuildMatcher.find()){
	    		oldBuild = Build;
	    		Build = "";
	    		StringTokenizer st = new StringTokenizer(line);
       	     	while (st.hasMoreTokens()) {
       	     		prevToken = token;
       	     		token = st.nextToken();
       	     		//Here if where we get the name for the build
       	     		//If additions are made to the pattern.txt file for the //STARTBUILDPATTERN you may want to make additions here
       	     		if (prevToken.equals("Project:")){
       	     			Build = "Project: " + token.substring(0, token.lastIndexOf(","));
       	     		} else if (prevToken.equals("Testsuite:")){
       	     			Build = "Test suite: " +token;
       	     		} else if (token.length() > 9 && (token.substring(0,10).equals("'jar:file:"))){
       	     			Build = "JarBuild: " + line.substring(line.lastIndexOf("/")+1, line.lastIndexOf("'"));
       	     			jarBuild = true;
       	     		} 
       	     		if(Build.equals("")) {
       	     			Build = "No name";
       	     		}
       	     	}
       	     	//need a uniqueID for when a tree appears twice it behaves correctly.
       	     	uniqueID++;
       	     	startBuild = true;
	    	}
	    	if(inBuild){
	    		if(jarBuild){
	    			if(!line.contains("Errors")&&!line.contains("Line")){
	    				endBuild = true;
	    				inBuild = false;
	    				jarBuild = false;
	    			}
	    		}
    	    	while(endBuildMatcher.find()){
					endBuild = true;
					inBuild = false;
    	    	}
	    	}
	    	//We add colour by seeing if a linei matches the colours matchers
   	    	
   	    	while(orangeMatcher.find()){
   	    		if (inBuild){
   	    			badBuild = true;       	    			
   	    		}
	    		 	addColour = true;
    		 	Colour = "FFA500";
   	    	}
   	    	while(redMatcher.find()){
   	    		if (inBuild){
   	    			badBuild = true;       	    			
   	    		}
   	    		addColour = true;
    		 	Colour = "FF0000";
   	    	}
   	    	if (startBuild){
   	    		if (inBuild){

   	    			buildString += "</ul>\n"+
   	    			"</li>\n"+
   	    			"</ul>\n" +
   	    			"<script type=\"text/javascript\">\n"+
   	    			"ddtreemenu.createTree(\"" + oldBuild + Integer.toString(uniqueID-1) + "\", true\n";
       	    		if(badBuild){
       	    			buildString += "ddtreemenu.flatten('" + oldBuild + Integer.toString(uniqueID - 1) + "', 'expand')\n";
       	    			badBuild = false;
       	    			buildString = badTitle + buildString;
       	    		}  else {
       	    			buildString = okTitle + buildString;
       	    		}
       	    		buildString += "</script>\n";
   	    			returnString = buildString;
   	    			buildString = "";
       	    	}
   	    		okTitle = "<ul style=\"margin-bottom:0;margin-top:0;\" id=\"" + Build + Integer.toString(uniqueID) + "\" class=\"treeview\">\n"+
   	    		"<li><font color=636363>  " + Build + "</font>" +
   	    		"\n<ul>\n";
   	    		badTitle = "<ul style=\"margin-bottom:0;margin-top:0;\" id=\"" + Build + Integer.toString(uniqueID) + "\" class=\"treeview\">\n"+
   	    		"<li><font color=FF0000> " + Build + "</font>" +
   	    		"\n<ul>\n";
   	    		inBuild = true;
   	    		
   	    	}
			if (inBuild||endBuild){
				buildString += "<li>\n";
			}
	    	if (addColour){
	    		if(inBuild||startBuild||endBuild){
	    			buildString += "<B><FONT COLOR=" + Colour + ">";
	    		} else {
	    			returnString+= "<B><FONT COLOR=" + Colour + ">";
	    		}
	    	} else {
	    		if(inBuild||startBuild||endBuild){
	    			buildString += "<FONT COLOR=636363>";
	    		} else {
	    			returnString+= "<FONT COLOR=" + Colour + ">";
	    		}
	    	}
			if(inBuild||endBuild){
				buildString += line;
			} else {
				returnString += line;
			}
			if (inBuild||endBuild){
				buildString += "\n</li>\n";
			} else {
				returnString += "<BR>";
			}
    		if(addColour){
        		if(inBuild||startBuild||endBuild){
        			buildString += "</FONT></B>";
        		} else {
        			returnString += "</FONT></B>";
        		}
    		} else {
        		if(inBuild||startBuild||endBuild){
        			buildString += "</FONT>";
        		} else {
        			returnString += "</FONT>";
        		}
    		}
   	    	if (endBuild){
   	    		buildString += "</ul>\n"+
	    			"</li>\n"+
	    			"</ul>\n" +
	    			"<script type=\"text/javascript\">\n"+
	    			"ddtreemenu.createTree(\"" + Build + Integer.toString(uniqueID) + "\", true)\n\n";
   	    		if(badBuild){
   	    			buildString += "ddtreemenu.flatten('" + Build + Integer.toString(uniqueID) + "', 'expand')\n";
   	    			badBuild = false;
   	    			buildString = badTitle + buildString;
   	    		}  else {
   	    			buildString = okTitle + buildString;
   	    		}
   	    		buildString += "</script>\n";
   	    		returnString = buildString;
   	    		buildString = "";
   	    	}
		}
	    
	    return returnString;
   	}
   	
	public static void setRegexStrings(String regex, String redRegex, String orangeRegex, String keepRegex, String startBuildRegex, String endBuildRegex){
		regexString = regex;
		redRegexString = redRegex;
		orangeRegexString = orangeRegex;
		keepRegexString = keepRegex;
		startBuildRegexString = startBuildRegex;
		endBuildRegexString = endBuildRegex;
		linePattern = Pattern.compile(regexString);
		redPattern = Pattern.compile(redRegexString);
		orangePattern = Pattern.compile(orangeRegexString);
		keepPattern = Pattern.compile(keepRegexString);
		startBuildPattern = Pattern.compile(startBuildRegexString);
		endBuildPattern = Pattern.compile(endBuildRegexString);
	}
	private static void resetMatchers(String line){
		matcher = linePattern.matcher(line);
		redMatcher = redPattern.matcher(line);
		orangeMatcher = orangePattern.matcher(line);
		keepMatcher = keepPattern.matcher(line);
		startBuildMatcher = startBuildPattern.matcher(line);
		endBuildMatcher = endBuildPattern.matcher(line);
	}
}
