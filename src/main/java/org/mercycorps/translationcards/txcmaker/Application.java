package org.mercycorps.translationcards.txcmaker;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.gson.Gson;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.service.*;
import org.mercycorps.translationcards.txcmaker.task.TxcPortingUtility;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;

public class Application implements ServletContextListener {

    public static final String TASK_QUEUE_NAME = "queue-txc-building";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        AuthUtils authUtils = new AuthUtils();
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        Queue taskQueue = QueueFactory.getQueue(TASK_QUEUE_NAME);
        ServletContext servletContext = servletContextEvent.getServletContext();
        LanguageService languageService = getLanguageService(servletContext);
        Gson gson = new Gson();
        TxcPortingUtility txcPortingUtility = new TxcPortingUtility(languageService, gson);
        DriveService driveService = new DriveService(txcPortingUtility);
        FileVerifier fileVerifier = new FileVerifier(txcPortingUtility);

        servletContext.setAttribute("authUtils", authUtils);
        servletContext.setAttribute("fileVerifier", fileVerifier);
        servletContext.setAttribute("channelService", channelService);
        servletContext.setAttribute("taskQueue", taskQueue);
        servletContext.setAttribute("driveService", driveService);
        servletContext.setAttribute("languageService", languageService);
        servletContext.setAttribute("txcPortingUtility", txcPortingUtility);
    }

    private LanguageService getLanguageService(ServletContext servletContext) {
        InputStream inputStream = servletContext.getResourceAsStream("/language_codes.json");
        LanguagesImportUtility languagesImportUtility = new LanguagesImportUtility(inputStream);
        try {
            inputStream.close();
        } catch(IOException e) {
            //do something
        }
        return new LanguageService(languagesImportUtility);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
