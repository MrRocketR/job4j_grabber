package ru.job4j.quartz;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbitTemp {

    private static JobDataMap data = new JobDataMap();
    private static Connection connection;

    public static Properties load(String properties) {
        Properties config = new Properties();
        try (InputStream in = AlertRabbitTemp.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            config.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    public static void init() throws IOException, ClassNotFoundException, SQLException {

        try (InputStream in = AlertRabbitTemp.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            String driver = config.getProperty("driver");
            Class.forName(driver);
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
            data.put("connection", connection);
            data.put("rabbit.interval", config.getProperty("rabbit.interval"));
        }
    }

    public static void insertIntoDb(JobDataMap data, LocalDateTime localDateTime) {
        String sql = "INSERT INTO rabbit (created) values (?)";
        Timestamp timestampSQL = Timestamp.valueOf(localDateTime);
        try {
        Connection connection = (Connection) data.get("connection");
        PreparedStatement pS = connection.prepareStatement(sql);
        pS.setTimestamp(1, timestampSQL);
        pS.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException,
            ClassNotFoundException, SQLException {
        init();
        try {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            data.put("store", store);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(5)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            insertIntoDb(data, LocalDateTime.now());
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            store.add(System.currentTimeMillis());
        }
    }
}