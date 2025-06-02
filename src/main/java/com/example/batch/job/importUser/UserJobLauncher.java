package com.example.batch.job.importUser;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 구동 시 Job을 실행하는 클래스
 * - JobLauncher는 Job을 실제 실행해주는 역할
 * - JobExecution을 통해 상태 및 결과를 추적할 수 있음
 */
@Component
public class UserJobLauncher implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job importUserJob;

    public UserJobLauncher(JobLauncher jobLauncher, Job importUserJob) {
        this.jobLauncher = jobLauncher;
        this.importUserJob = importUserJob;
    }

    @Override
    public void run(String... args) throws Exception {
        // JobParameters를 설정 (중복 실행 방지용)
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis()) // 매 실행마다 고유값
                .toJobParameters();

        // Job 실행 및 JobExecution 반환
        JobExecution execution = jobLauncher.run(importUserJob, params);

        // 실행 결과 출력
        System.out.println("▶ Job 실행 완료");
        System.out.println("▶ Status: " + execution.getStatus()); // 예: COMPLETED, FAILED
        System.out.println("▶ ExitCode: " + execution.getExitStatus().getExitCode());
    }
}