package com.example.batch.job.importUser;

import com.example.batch.domain.person.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class UserReader {
    private static final Logger log = LoggerFactory.getLogger(UserReader.class);

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
}
