package com.zd.springboot.service.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Dinnes Zhang
 * @date
 */
@Service
public class ScheduleServiceImpl {

    /**
     *second, minute, hour, day of month, month, and day of week.
     * 0 * * * * MON-FRI
     */
    @Scheduled(cron = "0/3 * * * * *")
    public void hello(){
        System.out.println(LocalDateTime.now()+" hello.....");
    }
}
