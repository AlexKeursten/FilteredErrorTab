<%@ page import="java.io.*,
                 java.util.StringTokenizer,
                 java.util.regex.Matcher,
                 java.util.regex.Pattern" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<HTML>
<head>
<%@ page language="java" import="filterPackage.*" %>
    <jsp:useBean id="pathPrefix" scope="request" type="java.lang.String"/>
    <script type="text/javascript" src="${pathPrefix}simpletreemenu.js">
        /***********************************************
         * Simple Tree Menu- © Dynamic Drive DHTML code library (www.dynamicdrive.com)
         * This notice MUST stay intact for legal use
         * Visit Dynamic Drive at http://www.dynamicdrive.com/ for full source code
         ***********************************************/
    </script>
    <link rel="stylesheet" type="text/css" href="${pathPrefix}simpletree.css"/>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    
    <title>Log File</title>
</head>
<BODY>
<span style="font-family: Monospace; font-size: 12px; ">
    <jsp:useBean id="myLog" scope="request" type="java.lang.String"/>
    <jsp:useBean id="pattern" scope="request" type="java.lang.String"/>
    <%
        boolean keep;
        boolean addColour;
        String line;
        String prevToken;
        String token;
        String regexString = "";
        String redRegexString = "";
        String orangeRegexString = "";
        String keepRegexString = "";
        String startBuildRegexString = "";
        String endBuildRegexString = "";
        int readStage = 0;

        final BufferedReader patternRead = new BufferedReader(new StringReader(pattern));
        try {
            while ((line = patternRead.readLine()) != null) {
                if (line.length() >= 2 && line.substring(0, 2).equals("##")) {
                    //the line read in is a comment
                } else {
                    //this is determines when to go to the next reading stage
                    //the initial stage 0 is for the patterns that are used to remove lines
                    //having any of these lines twice in the file would cause some errors.
                    if (line.equals("//REDPATTERN")) {
                        readStage = 1;
                    } else if (line.equals("//ORANGEPATTERN")) {
                        readStage = 2;
                    } else if (line.equals("//KEEPPATTERN")) {
                        readStage = 3;
                    } else if (line.equals("//STARTBUILDPATTERN")) {
                        readStage = 4;
                    } else if (line.equals("//ENDBUILDPATTERN")) {
                        readStage = 5;
                    } else {
                        switch (readStage) {
                            case 0:
                                regexString += line;
                                break;
                            case 1:
                                redRegexString += line;
                                break;
                            case 2:
                                orangeRegexString += line;
                                break;
                            case 3:
                                keepRegexString += line;
                                break;
                            case 4:
                                startBuildRegexString += line;
                                break;
                            case 5:
                                endBuildRegexString += line;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        } finally {
            patternRead.close();
        }
    	filter.setRegexStrings(regexString, redRegexString, orangeRegexString, keepRegexString, startBuildRegexString, endBuildRegexString);
    	//Reading in the log
    	InputStream is = new ByteArrayInputStream(myLog.getBytes());
    	BufferedReader read = new BufferedReader(new InputStreamReader(is));
    	//FileReader testFile = new FileReader(testPath);	
    	//BufferedReader testRead = new BufferedReader(testFile);

    	while((line = read.readLine()) != null ) {
    		out.write(filter.filterLine(line));
    		if(!line.equals("")){
    			out.write("\n");
    		}
    	}
    %>
</span>
</BODY>
</HTML>