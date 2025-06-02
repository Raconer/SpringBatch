# Spring Batch + MyBatis Example

Spring Batch와 MyBatis를 활용한 CSV → DB 배치 처리 예제입니다.
CSV 파일에서 사용자 정보를 읽고, 대문자로 가공한 후 데이터베이스에 저장합니다.

## 📁 프로젝트 구조

```
com.example.batch
├── config
│   └── BatchConfig.java                  # 배치 Job/Step 설정
├── entity
│   └── Person.java                       # 도메인 엔티티
├── listener
│   └── JobCompletionNotificationListener.java  # Job 실행 리스너
├── processor
│   └── PersonItemProcessor.java          # ItemProcessor 구현체
├── mappers
│   └── PersonMapper.java                 # MyBatis Mapper 인터페이스
├── log
│   └── CustomMyBatisLoggerImpl.java      # MyBatis SQL 로그 커스터마이징
├── resources
│   ├── application.yml                   # 공통 환경 설정
│   ├── application-local.yml             # Local 환경 설정
│   ├── application-test.yml              # Test 환경 설정
│   ├── schema-all.sql                    # DDL (실행 시 people 테이블 생성)
│   ├── sample-data.csv                   # CSV 입력 데이터
│   └── mybatis/mappers/PersonMapper.xml  # MyBatis SQL 매퍼
└── BatchApplication.java                 # 실행 메인 클래스
```

## 🔧 실행 환경 및 버전

* Java 17
* Spring Boot 2.7.13
* Gradle
* 주요 의존성:

```groovy
// Batch
implementation("org.springframework.boot:spring-boot-starter-batch")

// DB - MySQL
runtimeOnly("com.mysql:mysql-connector-j")

// MyBatis
implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:2.3.2")

// H2
runtimeOnly("com.h2database:h2")

// Lombok
compileOnly("org.projectlombok:lombok")
annotationProcessor("org.projectlombok:lombok")

// Test
testImplementation("org.springframework.boot:spring-boot-starter-test")
testImplementation("org.springframework.batch:spring-batch-test")
```

## 🌐 프로파일별 DB 설정

Spring Boot의 `spring.profiles.active` 설정에 따라 서로 다른 데이터베이스를 사용합니다:

* **local**: MySQL 데이터베이스를 사용하며, 실제 운영 환경에 가까운 테스트 시 사용
* **test**: H2 In-Memory 데이터베이스를 사용하여 빠른 테스트 가능 (애플리케이션 종료 시 데이터 삭제됨)

환경에 따라 `application-local.yml`, `application-test.yml` 또는 통합 `application.yml` 내에서 profile 조건 분기를 통해 설정을 나눌 수 있습니다.

## 💬 주요 구성 요약

* **FlatFileItemReader**: CSV 파일을 읽고 Person 객체로 매핑
* **ItemProcessor**: 이름을 대문자로 가공
* **MyBatisBatchItemWriter**: Mapper XML을 기반으로 DB 저장
* **JobListener**: 배치 Job 시작 및 종료 시 로그 출력
* **MyBatis Logger**: SQL 실행 로그에 커스텀 태그 출력

---

# Spring Batch란?

> 개발자가 정의한 작업을 한 번에 일괄 처리하는 애플리케이션.

## 참고

* [우아한 테크](https://www.youtube.com/watch?v=1xJU8HfBREY)
* [Hyeri.log](https://velog.io/@hiy7030/Spring-Spring-batch-Scheduler)
* [gil.log](https://velog.io/@gillog/Batch-Application-%EC%83%9D%EC%84%B1%ED%95%98%EA%B8%B0)

## 주요 적용 사례

* 매출 데이터를 이용한 일매출 집계
* 매우 큰 데이터를 활용한 보험급여 결정
* 트랜잭션 방식으로 포맷, 유효성 확인 및 처리가 필요한 내부 및 외부 시스템에서 수신한 정보를 기록 시스템으로 통합
* 등등…

## Batch 애플리케이션이 필요한 상황

1. 일정 주기로 실행해야 할 때
2. 실시간 처리가 어려운 대량의 데이터를 처리해야 할 때

> 이런 작업을 하나의 애플리케이션에서 수행하면 성능 저하를 유발할 수 있으니 배치 애플리케이션을 구현한다.

## Batch 애플리케이션의 조건

* **대용량 데이터**

    * 대량의 데이터를 가져오기, 전달하기, 계산하는 등의 처리를 할 수 있어야 한다.
* **자동화**

    * 심각한 문제 해결을 제외하고는 사용자 개입 없이 실행되어야 한다.
* **견고성**

    * 잘못된 데이터를 충돌/중단 없이 처리할 수 있어야 한다.
* **신뢰성**

    * 무엇이 잘못되었는지를 추적할 수 있어야 한다. (Logging, 알림)
* **성능**

    * 지정한 시간 안에 처리를 완료하거나 동시에 실행되는 다른 어플리케이션을 방해하지 않도록 수행되어야 한다.

## Batch 실행 순서

> 일반적으로 배치 작업은 JobLauncher를 통해 실행되며, Job 내에 여러 개의 Step이 순차적으로 실행됩니다. 각 Step은 Reader, Processor, Writer를 통해 데이터를 읽고 가공한 뒤 저장하거나 출력합니다. JobExecution을 통해 각 작업의 실행 상태를 모니터링할 수 있습니다.

0. **청크**
    * 청크 사이즈 : 1500개 → 1500개당 1개의 쓰레드
    * 병렬 처리 시 중요한 개념이다.
1. **Job**
    * 배치 작업의 가장 상위 단계인 Job을 정의합니다.
    * Job은 하나 이상의 Step으로 구성됩니다.
2. **Step**
    * Job 내에서 하나의 Step은 하나의 단위 작업을 나타냅니다. 각 Step은 아래의 세부 단계로 구성됩니다.
    * **Reader**: 데이터를 읽어오는 단계입니다. 예: 파일, DB 등
    * **Processor**: 데이터를 가공하거나 변환하는 단계입니다. (선택)
    * **Writer**: 데이터를 저장하거나 출력하는 단계입니다. 예: DB 저장
3. **JobLauncher**
    * Job을 실행하는 역할을 합니다.
    * 주로 스케줄러나 REST API를 통해 JobLauncher를 호출하여 배치 작업을 시작합니다.
4. **JobRepository**
    * 배치 작업의 실행 상태를 추적하고 저장하는 데 사용됩니다.
    * 이 정보는 장애 복구 및 모니터링에 유용합니다.
5. **JobExecution**
    * 각 Job의 실행에 대한 정보를 저장하며, 이를 통해 실행 결과를 확인할 수 있습니다.

