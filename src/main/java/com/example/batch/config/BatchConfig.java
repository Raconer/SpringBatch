package com.example.batch.config;

import com.example.batch.entity.Person;
import com.example.batch.listener.JobCompletionNotificationListener;
import com.example.batch.processor.PersonItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);
    public JobBuilderFactory jobBuilderFactory;
    public StepBuilderFactory stepBuilderFactory;

    public BatchConfig(JobBuilderFactory jobBuilderFactory,
                       StepBuilderFactory stepBuilderFactory){
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    /*
     * 이것은 JDBC 대상을 대상으로 하며 @EnableBatchProcessing에 의해 생성된 데이터 소스의 복사본을 자동으로 가져옵니다.
     * 여기에는 Java bean 특성에 의해 구동되는 단일 Person을 삽입하는 데 필요한 SQL문이 포함됩니다.
     */
    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource){
        log.info("1. @Bean -> JdbcBatchItemWriter<Person> writer(DataSource dataSource)");
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(
                        new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }

    // 결국에 따지면 이게 1등이다.
    // Writer를 받기위해 Writer가 먼저 실행된다.
    @Bean
    public Step step(JdbcBatchItemWriter<Person> writer){
        log.info("2. @Bean -> Step step(JdbcBatchItemWriter<Person> writer)");
        return stepBuilderFactory.get("step")
                .<Person, Person> chunk(10)
                .writer(writer)
                .processor(processor())
                .reader(reader())
                .build();
    }

    /*
     * sample-data.csv라는 파일을 찾고 Person로 변환하기에 충분한 정보가 있는 각 라인 항목을 구문 분석합니다.
     */
    @Bean
    public FlatFileItemReader<Person> reader() {
        log.info("3.  @Bean -> FlatFileItemReader<Person> reader()");
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
                    {
                        setTargetType(Person.class);
                    }
                }).build();
    }

    /*
     * 데이터를 대문자로 변환하기 위해 이전에 정의한 PersonItemProcessor의 인스턴스를 만듭니다.
     */
    @Bean
    public PersonItemProcessor processor(){
        log.info("4. @Bean -> PersonItemProcessor processor()");
        return new PersonItemProcessor();
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step){
        log.info("5. @Bean -> Job importUserJob(JobCompletionNotificationListener listener, Step step)");
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step)
                .end()
                .build();
    }
}
