package org.mercycorps.translationcards.txcmaker;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.service.DeckService;
import org.mercycorps.translationcards.txcmaker.service.FileVerifier;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Application implements ServletContextListener {

    public static final String TASK_QUEUE_NAME = "queue-txc-building";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        AuthUtils authUtils = new AuthUtils();
        FileVerifier fileVerifier = new FileVerifier();
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        Queue taskQueue = QueueFactory.getQueue(TASK_QUEUE_NAME);
        DeckService deckService = new DeckService(channelService, taskQueue);

        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.setAttribute("authUtils", authUtils);
        servletContext.setAttribute("fileVerifier", fileVerifier);
        servletContext.setAttribute("channelService", channelService);
        servletContext.setAttribute("taskQueue", taskQueue);
        servletContext.setAttribute("deckService", deckService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
