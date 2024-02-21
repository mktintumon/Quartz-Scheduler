package com.quartz.QuartzScheduler.controller;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.quartz.QuartzScheduler.jobs.EmailJob;
import com.quartz.QuartzScheduler.payload.EmailRequest;
import com.quartz.QuartzScheduler.payload.EmailResponse;


@RestController
public class EmailController {

    @Autowired
    private Scheduler scheduler;

    @PostMapping("/schedule/mail")
    public ResponseEntity<EmailResponse> scheduleEmail(@RequestBody EmailRequest emailRequest){
        try {
            ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getLocalDateTime() , emailRequest.getTimeZone());
            if(dateTime.isBefore(ZonedDateTime.now())){
                EmailResponse emailResponse = new EmailResponse(false , "Date Time must be before current Time");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(emailResponse);
            }

            JobDetail jobDetail = buildJobDetail(emailRequest);
            Trigger trigger = buildTrigger(jobDetail, dateTime);

            scheduler.scheduleJob(jobDetail, trigger);

            EmailResponse emailResponse = new EmailResponse(true, jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email sent successfully!!!");

            return ResponseEntity.ok(emailResponse);
            
        } catch (SchedulerException se) {
            System.out.println("Error occured : " + se);
            EmailResponse emailResponse = new EmailResponse(false, "Error while scheduling Mail");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
        }
    }


    private JobDetail buildJobDetail(EmailRequest emailRequest){
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email" , emailRequest.getEmail());
        jobDataMap.put("subject" , emailRequest.getSubject());
        jobDataMap.put("body" , emailRequest.getBody());

        return JobBuilder.newJob(EmailJob.class)
                        .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                        .withDescription("Send Email Job")
                        .usingJobData(jobDataMap)
                        .storeDurably()
                        .build();
    }

    private Trigger buildTrigger(JobDetail jobDetail , ZonedDateTime starTime){
        return TriggerBuilder.newTrigger()
                            .forJob(jobDetail)
                            .withIdentity(jobDetail.getKey().getName() , "email-triggers")
                            .withDescription("Send Email triggers")
                            .startAt(Date.from(starTime.toInstant()))
                            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(2).repeatForever())
                            //.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                            .build();
    }


    @GetMapping("/checking")
    public String checking(){
        return "checking successful";
    }
}
