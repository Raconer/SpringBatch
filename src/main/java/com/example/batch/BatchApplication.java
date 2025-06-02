package com.example.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 애플리케이션의 진입점입니다.
 * <p>
 * - @SpringBootApplication: 컴포넌트 스캔, 자동 설정, 설정 클래스 포함
 * - main(): 애플리케이션을 실행하고 종료 상태 코드를 명시적으로 반환
 * - SpringApplication.exit(): 배치 작업이 끝나면 애플리케이션 종료 처리
 */
@SpringBootApplication
public class BatchApplication {

    private static final Logger log = LoggerFactory.getLogger(BatchApplication.class);

    public static void main(String[] args) throws Exception {
        // 애플리케이션 실행 시작 로그
        long start = System.currentTimeMillis();
        log.info("▶ BatchApplication 시작");
        // Spring 애플리케이션 실행 및 종료 코드 획득
        int exitCode = SpringApplication.exit(
                SpringApplication.run(BatchApplication.class, args)
        );

        // 종료 시간 기록
        long end = System.currentTimeMillis();
        long elapsedMillis = end - start;
        double elapsedSeconds = elapsedMillis / 1000.0;

        log.info("■ BatchApplication 종료 (exitCode: {})", exitCode);
        log.info("⏱ 총 실행 시간: {}초 ({} ms)", String.format("%.2f", elapsedSeconds), elapsedMillis);

        // 실제 종료 수행
        System.exit(exitCode);
    }
}