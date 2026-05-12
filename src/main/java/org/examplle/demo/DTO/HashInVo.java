package org.examplle.demo.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class HashInVo implements Serializable {
    // Redis 저장시 직렬화 필요
    private String name;
    private String company;
    private int age;
}

