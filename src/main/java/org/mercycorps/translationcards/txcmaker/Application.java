package org.mercycorps.translationcards.txcmaker;

import org.mercycorps.translationcards.txcmaker.auth.AuthUtils;
import org.mercycorps.translationcards.txcmaker.service.FileVerifier;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Application implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        AuthUtils authUtils = new AuthUtils();
        FileVerifier fileVerifier = new FileVerifier();

        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.setAttribute("authUtils", authUtils);
        servletContext.setAttribute("fileVerifier", fileVerifier);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
