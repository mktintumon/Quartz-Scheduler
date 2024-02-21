package com.quartz.QuartzScheduler.jobs;

import java.nio.charset.StandardCharsets;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public class EmailJob extends QuartzJobBean{

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailProperties mailProperties;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();

        String recipientEmail = jobDataMap.getString("email");
        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");

        sendEmail(mailProperties.getUsername() , recipientEmail , body , subject);
    }

    private void sendEmail(String fromEmail , String toEmail , String body , String subject){
        try {
            MimeMessage message =  javaMailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(message , StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(body , true);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);

            javaMailSender.send(message);

        } catch (MessagingException me) {
            System.out.println(me);
        }
    }

}
