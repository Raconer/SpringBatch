package com.example.batch.mappers;

import com.example.batch.entity.Person;
import org.apache.ibatis.annotations.Mapper;

@Mapper
interface PersonMapper {
    void insert(Person person); // people 테이블에 한 명 삽입
}
