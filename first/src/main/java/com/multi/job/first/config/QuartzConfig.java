package com.multi.job.first.config;

import com.multi.job.first.PrintJob;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.*;

import javax.sql.DataSource;
import java.sql.Date;
import java.text.ParseException;
import java.time.Instant;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private QuartzProperties quartzProperties;

    @Autowired
    private DataSource dataSource;

    @Bean
    public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setQuartzProperties(getProperties());
        schedulerFactory.setDataSource(dataSource);
        schedulerFactory.setJobFactory(springBeanJobFactory());
        schedulerFactory.setJobDetails(job);
        schedulerFactory.setTriggers(trigger);
        return schedulerFactory;
    }

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());
        return properties;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public JobDetailFactoryBean jobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(PrintJob.class);
        jobDetailFactory.setDescription("Invoke Sample Job service...");
        jobDetailFactory.setDurability(true);
        jobDetailFactory.setName("payment-job");
        jobDetailFactory.setGroup("payment");
        return jobDetailFactory;
    }

//    @Bean
//    public SimpleTriggerFactoryBean trigger(JobDetail job) {
//        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
//        trigger.setJobDetail(job);
//        trigger.setRepeatInterval(5000);
//        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
//        trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_SMART_POLICY );
//        trigger.setGroup("payment");
//        trigger.setName("payment-trigger");
//        return trigger;
//    }

    @Bean
    public CronTrigger trigger(JobDetail jobDetail) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setName("payment-trigger");
        factoryBean.setGroup("payment");
        factoryBean.setStartTime(Date.from(Instant.now()));
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression("0 0/5 4-00 ? * SUN,MON,TUE,WED,THU,FRI *");
        factoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        try {
            factoryBean.afterPropertiesSet();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return factoryBean.getObject();
    }

}
