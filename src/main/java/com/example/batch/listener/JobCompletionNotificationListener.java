package com.example.batch.listener;

import com.example.batch.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Job의 실행 전후에 후처리를 담당하는 리스너 클래스입니다.
 *
 * - @Component로 등록되어 자동으로 Spring Bean으로 관리됨
 * - JobExecutionListenerSupport를 상속받아 beforeJob, afterJob 오버라이딩 가능
 * - afterJob에서는 배치가 성공적으로 끝난 경우 DB에서 결과 데이터를 조회하고 로그로 출력
 */
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    // JdbcTemplate을 주입받아 DB 조회에 사용
    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Job 시작 전에 호출됨
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("::::::::::::::::::Spring Batch Job Start::::::::::::::::::");
        log.info("🔫 [BEFORE_JOB] Job 시작 전 처리: beforeJob 실행됨 (JobExecution 상태: {})", jobExecution.getStatus());
    }

    // Job이 끝난 후 호출됨
    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("🎉 [AFTER_JOB] Job 종료 후 처리: afterJob 실행됨 (JobExecution 상태: {})", jobExecution.getStatus());

        // Job이 정상적으로 완료된 경우
       /* if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("\uD83C\uDF89 [AFTER_JOB] 배치 작업 완료: people 테이블에서 결과 데이터 확인 시작");

            jdbcTemplate.query("SELECT first_name, last_name FROM people",
                    (rs, row) -> new Person(
                            rs.getString(1),
                            rs.getString(2))
            );
        }*/

        log.info("::::::::::::::::::Spring Batch Job End::::::::::::::::::");
    }
}
