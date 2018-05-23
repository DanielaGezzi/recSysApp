package recSysApp.controller;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import utils.LensKitRecommender;

@WebListener
public class ContextListener implements ServletContextListener {
	
	@Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Starting up!");
        LensKitRecommender.getLensKitRecommender();
    }

}
