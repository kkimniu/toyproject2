package com.roommate.common.config;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Collections;

public class JdbcCleanupListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // No startup work required.
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Throwable ignore) {

        }
        ClassLoader c1 = Thread.currentThread().getContextClassLoader();
        for (Driver d : Collections.list(DriverManager.getDrivers())) {
            try {
                if (d.getClass().getClassLoader() == c1) {
                    DriverManager.deregisterDriver(d);
                }
            } catch (Exception ignore) {

            }
        }
    }
}
