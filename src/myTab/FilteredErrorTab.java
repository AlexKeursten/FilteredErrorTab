package myTab;

//import jetbrains.buildServer.agent.plugins.beans.PluginDescriptor;

import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.buildLog.LogMessage;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.ViewLogTab;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

public class FilteredErrorTab extends ViewLogTab {
    private static final Logger logger = Logger.getLogger(FilteredErrorTab.class);
    private final PluginDescriptor myDescriptor;

    public FilteredErrorTab(final WebControllerManager manager,
                            final SBuildServer server,
                            final PluginDescriptor descriptor) {
        super("Error Build Log", "errorLogTab", manager, server);
        myDescriptor = descriptor;
        final String myPath = myDescriptor.getPluginResourcesPath("logResults.jsp");
        setIncludeUrl(myPath);
        setPlaceId(PlaceId.BUILD_RESULTS_TAB);
        register();
    }

    @SuppressWarnings("unchecked")
    protected void fillModel(final Map<String, Object> model,
                             final HttpServletRequest request,
                             @Nullable final SBuild build) {
        //These are all the files that are needed and it is easier to get the path right now
        final Format formatter = new SimpleDateFormat("HH:mm:ss");

        final StringBuilder myLog = new StringBuilder();
        if (build != null) {
            final Iterator<LogMessage> myIterator = build.getBuildLog().getMessagesIterator();
            while (myIterator.hasNext()) {
                final LogMessage line = myIterator.next();
                myLog.append("[")
                        .append(formatter.format(line.getTimestamp()))
                        .append("] ")
                        .append(line.getText())
                        .append("\n<br>");
            }
        }
        model.put("myLog", myLog.toString());
        try {
            final String teamCityConfig = System.getenv("TEAMCITY_DATA_PATH");
            final File pattern = new File(teamCityConfig, "pattern.txt");
            model.put("pattern", FileUtils.readFileToString(pattern));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        model.put("pathPrefix", myDescriptor.getPluginResourcesPath());

        logger.error("Filter log model: " + model);
    }
}
