package com.example.batch.job.importUser;

import com.example.batch.config.BatchConfig;
import com.example.batch.domain.person.model.Person;
import com.example.batch.processor.PersonItemProcessor;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportUserStep {
    private static final Logger log = LoggerFactory.getLogger(ImportUserStep.class);

    private final StepBuilderFactory stepBuilderFactory;
    private final FlatFileItemReader<Person> reader;
    private final PersonItemProcessor processor;

    public ImportUserStep(StepBuilderFactory stepBuilderFactory,
                       FlatFileItemReader<Person> reader,
                       PersonItemProcessor processor) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.reader = reader;
        this.processor = processor;
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
        return stepBuilderFactory.get("importUserStep")
                .<Person, Person>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
