package org.mercycorps.translationcards.txcmaker.task;


import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class VerifyDeckTask extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        String channelKey = req.getParameter("sessionId");
        channelService.sendMessage(new ChannelMessage(channelKey, "this will be a deck serialized into JSON"));
    }
}
