package com.example.batch.processor;

import com.example.batch.domain.person.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ItemProcessor 구현 클래스.
 *
 * - 입력받은 Person 객체의 firstName, lastName을 대문자로 변환하여 새 Person 객체로 반환
 * - Reader → Processor → Writer 흐름에서 "가공" 단계에 해당
 */
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);
    private final AtomicInteger count = new AtomicInteger(0); // 스레드 안전한 카운터

    /**
     * Person 객체를 받아 이름을 대문자로 변환한 새 Person 객체로 리턴합니다.
     *
     * @param person 입력 데이터
     * @return 변환된 Person 객체 (firstName, lastName 대문자)
     */
    @Override
    public Person process(final Person person) throws Exception {
        // 입력된 이름 정보를 대문자로 변환
        final String firstName = person.getFirstName().toUpperCase();
        final String lastName = person.getLastName().toUpperCase();

        // 변환된 결과를 새로운 Person 인스턴스로 생성
        final Person transPerson = new Person(firstName, lastName);

        int currentCount = count.incrementAndGet(); // 카운트 증가 및 반환

        log.info("🔄 [PROCESS:{}] ItemProcessor 실행: {} → {}", currentCount, person, transPerson);

        return transPerson;
    }
}
