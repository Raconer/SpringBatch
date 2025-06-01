package com.example.batch.config;

import com.example.batch.entity.Person;
import com.example.batch.listener.JobCompletionNotificationListener;
import com.example.batch.processor.PersonItemProcessor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

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
@EnableBatchProcessing
public class BatchConfig {
    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);
    public JobBuilderFactory jobBuilderFactory;
    public StepBuilderFactory stepBuilderFactory;

    public BatchConfig(JobBuilderFactory jobBuilderFactory,
                       StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

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
                .statementId("com.example.batch.mappers.PersonMapper.insert")
                .build(); // MyBatisBatchItemWriter<Person> 인스턴스 생성
    }

    /**
     * Spring Batch의 Step을 정의하는 메서드입니다.
     * <p>
     * - 이름이 "step"인 Step을 생성하며, chunk 기반 처리 방식 사용
     * - 1개의 chunk 단위로 10개의 Person 항목을 처리함
     * - Reader → Processor → Writer 순서로 실행
     * - 주입받은 JdbcBatchItemWriter<Person>를 통해 DB에 쓰기 수행
     * <p>
     * 이 Step은 Job의 구성 요소로 사용됩니다.
     */
    @Bean
    public Step step(MyBatisBatchItemWriter<Person> writer) {
        log.info("⚙️ [SETUP] Step 빈 생성: Person 데이터를 읽고 가공한 뒤 DB에 10개 단위로 일괄 삽입합니다.");
        return stepBuilderFactory.get("Person Insertion Step")
                .<Person, Person>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

    /**
     * CSV 파일로부터 Person 데이터를 읽어오는 FlatFileItemReader 빈을 생성합니다.
     * <p>
     * - ClassPath의 sample-data.csv 파일을 읽습니다
     * - CSV 파일의 각 행을 Person 객체로 매핑합니다
     * - 각 필드는 firstName, lastName 순서로 구성되어 있어야 합니다
     * <p>
     * 이 Reader는 Chunk 기반 Step에서 입력 데이터 소스로 사용됩니다.
     */
    @Bean
    public FlatFileItemReader<Person> reader() {
        // 로그: FlatFileItemReader 생성 시작
        log.info("⚙️ [SETUP] FlatFileItemReader<Person> 빈 생성: sample-data.csv에서 Person 데이터를 읽어옵니다.");

        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")                           // Reader 이름 설정
                .resource(new ClassPathResource("sample-data.csv")) // classpath의 CSV 파일 지정
                .delimited()                                        // 구분자(,) 기반 파싱 설정
                .names("firstName", "lastName")       // CSV 헤더 또는 순서에 따라 필드 이름 지정
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
                    {
                        setTargetType(Person.class);                // 각 라인을 Person 객체로 변환
                    }
                })
                .build();                                           // Reader 인스턴스 생성
    }

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

    /**
     * Batch 작업(Job)을 정의하는 importUserJob 빈을 생성합니다.
     * <p>
     * - "importUserJob"이라는 이름의 Job 생성
     * - 매 실행 시마다 고유한 ID를 생성하기 위해 RunIdIncrementer 사용
     * - 작업 완료 후 알림을 위한 JobCompletionNotificationListener 등록
     * - 단일 Step(flow)로 구성된 Job 정의
     */
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step) {
        // 로그: Job 빈 생성 시작
        log.info("⚙️ [SETUP] Job 빈 생성: importUserJob 실행 흐름 구성 (RunId 증가, 리스너 등록, Step 연결)");

        return jobBuilderFactory.get("importUserJob") // "importUserJob"이라는 이름으로 JobBuilder 생성
                .incrementer(new RunIdIncrementer())   // 실행할 때마다 다른 JobParameters를 만들기 위한 incrementer 설정
                .listener(listener)                    // Job 실행 전/후 동작을 정의한 리스너 등록
                .flow(step)                            // 하나의 Step을 Job에 등록
                .end()                                 // Job 정의 종료
                .build();                              // Job 인스턴스 생성
    }
}
