package com.example.batch.job.importUser;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Job 실행 전후 상태를 로그로 출력하는 리스너
 * - JobExecution 객체를 통해 실행 시간, 상태 등을 확인할 수 있음
 */
@Component
public class UserJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("🟡 Job 시작: " + jobExecution.getJobInstance().getJobName());
        System.out.println("🕒 Start Time: " + jobExecution.getStartTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("✅ Job 종료 상태: " + jobExecution.getStatus());
        System.out.println("🕙 End Time: " + jobExecution.getEndTime());
        System.out.println("📄 Exit Message: " + jobExecution.getExitStatus().getExitDescription());
    }
}