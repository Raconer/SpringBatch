package com.example.batch.domain.person.mapper;

import com.example.batch.domain.person.model.Person;
import org.apache.ibatis.annotations.Mapper;

@Mapper
interface PersonMapper {
    void insert(Person person); // people 테이블에 한 명 삽입
}
