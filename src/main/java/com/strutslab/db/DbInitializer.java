package com.strutslab.db;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Statement;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

public class DbInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = MyBatisUtil.getSqlSessionFactory().openSession().getConnection()) {
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setAutoCommit(true);
            runner.setStopOnError(false);

            try (Reader schemaReader = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("db/schema.sql"), "UTF-8")) {
                runner.runScript(schemaReader);
            }

            try (Reader seedReader = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("db/seed.sql"), "UTF-8")) {
                runner.runScript(seedReader);
            }

            System.out.println("[StrutsLab] Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("[StrutsLab] Database init failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
