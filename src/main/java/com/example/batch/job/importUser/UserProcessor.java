package com.example.batch.job.importUser;

import com.example.batch.processor.PersonItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UserProcessor {
    private static final Logger log = LoggerFactory.getLogger(UserProcessor.class);

    /**
     * Person 데이터를 처리하는 ItemProcessor 빈을 생성합니다.
     * <p>
     * - PersonItemProcessor는 입력된 Person 객체의 firstName, lastName을 대문자로 변환합니다
     * - Step 내에서 Reader → Processor → Writer 흐름 중 Processor에 해당합니다
     */
    @Bean
    public PersonItemProcessor processor() {
        // 로그: Processor 빈 생성 시작
        log.info("⚙️ [SETUP] PersonItemProcessor 빈 생성: Person 객체의 이름 정보를 대문자로 변환합니다.");

        return new PersonItemProcessor(); // 사용자 정의 Processor 인스턴스 생성
    }

}
