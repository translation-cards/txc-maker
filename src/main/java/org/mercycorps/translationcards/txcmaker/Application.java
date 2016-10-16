package org.mercycorps.translationcards.txcmaker;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import org.mercycorps.translationcards.txcmaker.language.LanguagesImportUtility;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static final String TASK_QUEUE_NAME = "queue-txc-building";

    @Bean
    public ChannelService channelService() {
        return ChannelServiceFactory.getChannelService();
    }

    @Bean
    public Queue queue() {
        return QueueFactory.getQueue(TASK_QUEUE_NAME);
    }

    @Bean
    public LanguagesImportUtility languagesImportUtility(ServletContext servletContext) {
        InputStream inputStream = servletContext.getResourceAsStream("/language_codes.json");
        LanguagesImportUtility languagesImportUtility = new LanguagesImportUtility(inputStream);
        try {
            inputStream.close();
        } catch(IOException e) {
            //do something
        }
        return languagesImportUtility;
    }

    @Bean
    public GcsService gcsService() {
        return GcsServiceFactory.createGcsService();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
