package ru.job4j.quartz.example;


import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * HelloQuartz - простой планировщик Quartz
 * <p>
 * Created by zhuyiquan90 on 2018/8/18.
 */
public class HelloQuartz {

    public static void main(String[] args) {

        try {
            // Получить экземпляр планировщика из фабрики планировщиков
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            scheduler.start();
            /**
             * Повторно используйте HelloJob для достижения различных примеров
             */
            // Регистрируем jobDetail1, печатаем «Hello Quartz!», Выполняем каждые 5 секунд
            JobDetail jobDetail1 = newJob(HelloJob.class).withIdentity("job1", "group").build();
            jobDetail1.getJobDataMap().put("CONTENT", "Hello Quartz!");
            Trigger trigger1 = newTrigger().withIdentity("trigger1", "group").startNow()
                    .withSchedule(simpleSchedule().withIntervalInSeconds(5)
                            .withRepeatCount(0)).build();
            scheduler.scheduleJob(jobDetail1, trigger1);

            // Регистрируем jobDetail2, печатаем текущее системное время, выполняем каждые 10 секунд
            JobDetail jobDetail2 = newJob(HelloJob.class).withIdentity("job2", "group").build();
            jobDetail2.getJobDataMap().put("CONTENT", String.valueOf(System.currentTimeMillis()));
            Trigger trigger2 = newTrigger().withIdentity("trigger2", "group")
                    .startNow().withSchedule(simpleSchedule()
                            .withIntervalInSeconds(10).repeatForever()).build();
            scheduler.scheduleJob(jobDetail2, trigger2);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}