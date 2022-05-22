package ru.job4j.quartz.example;

import org.quartz.*;

/**
 * HelloJob - простая работа для печати указанного контента
 *
 * Created by zhuyiquan90 on 2018/8/18.
 */
public class HelloJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // JobDetail
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        // JobDataMap
        JobDataMap dataMap = jobDetail.getJobDataMap();
        String content = dataMap.getString("CONTENT");
        System.out.println(content);
    }
}