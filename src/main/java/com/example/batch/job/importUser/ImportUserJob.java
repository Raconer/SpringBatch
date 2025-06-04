package com.example.batch.job.importUser;

import com.example.batch.listener.JobCompletionNotificationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Batch 설정 클래스입니다.
 * <p>
 * - @Configuration: 이 클래스가 Spring 설정 클래스임을 명시
 * - @EnableBatchProcessing: Spring Batch의 기본 인프라(빌더 등) 자동 등록
 * <p>
 * 이 클래스에서는 하나의 Job(importUserJob)을 구성하며,
 * 해당 Job은 다음의 컴포넌트로 구성됩니다:
 * 1. JobCompletionNotificationListener: Job 시작/완료 시 동작 정의
 * 2. FlatFileItemReader: sample-data.csv에서 Person 데이터 읽기
 * 3. PersonItemProcessor: 이름을 대문자로 가공
 * 4. JdbcBatchItemWriter: people 테이블에 데이터 삽입
 * 5. Step: 위 Reader → Processor → Writer를 chunk 단위로 실행
 * 6. Job: 단일 Step으로 구성된 배치 작업 정의
 */
@Configuration
public class ImportUserJob {
    private static final Logger log = LoggerFactory.getLogger(ImportUserJob.class);

    private final JobBuilderFactory jobBuilderFactory;

    public ImportUserJob(JobBuilderFactory jobBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
    }

    /**
     * Batch 작업(Job)을 정의하는 importUserJob 빈을 생성합니다.
     * <p>
     * - "importUserJob"이라는 이름의 Job 생성
     * - 매 실행 시마다 고유한 ID를 생성하기 위해 RunIdIncrementer 사용
     * - 작업 완료 후 알림을 위한 JobCompletionNotificationListener 등록
     * - 단일 Step(flow)로 구성된 Job 정의
     */
    @Bean
    public Job job(JobCompletionNotificationListener listener,
                             Step step) {
        // 로그: Job 빈 생성 시작
        log.info("⚙️ [SETUP] Job 빈 생성: importUserJob 실행 흐름 구성 (RunId 증가, 리스너 등록, Step 연결)");

        return jobBuilderFactory.get("UserJob") // "importUserJob"이라는 이름으로 JobBuilder 생성
                .incrementer(new RunIdIncrementer())   // 실행할 때마다 다른 JobParameters를 만들기 위한 incrementer 설정
                .listener(listener)                    // Job 실행 전/후 동작을 정의한 리스너 등록
                .flow(step)                             // 하나의 Step을 Job에 등록
                .end()                                 // Job 정의 종료
                .build();                              // Job 인스턴스 생성
    }
}
