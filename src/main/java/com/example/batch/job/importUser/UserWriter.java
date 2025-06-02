package com.example.batch.job.importUser;

import com.example.batch.domain.person.model.Person;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class UserWriter {
    private static final Logger log = LoggerFactory.getLogger(UserWriter.class);

    /**
     * JdbcBatchItemWriter<Person> 빈을 생성하여 Spring Batch에서 Person 데이터를 people 테이블에 일괄 삽입할 수 있도록 설정합니다.
     * <p>
     * - BeanPropertyItemSqlParameterSourceProvider를 사용해 Person의 필드를 SQL 파라미터에 자동 매핑
     * - 지정된 SQL 구문으로 INSERT 실행
     * - @EnableBatchProcessing으로 생성된 DataSource를 주입받아 사용
     * <p>
     * 이 Writer는 Chunk 기반 Step에서 ItemWriter로 사용됩니다.
     */
    @Bean
    public MyBatisBatchItemWriter<Person> writer(SqlSessionFactory sqlSessionFactory) {
        log.info("[SETUP] JdbcBatchItemWriter<Person> 빈 등록: Person 데이터를 people 테이블에 배치 삽입합니다.");
        return new MyBatisBatchItemWriterBuilder<Person>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.example.batch.domain.person.mapper.PersonMapper.insert")
                .build(); // MyBatisBatchItemWriter<Person> 인스턴스 생성
    }
}
