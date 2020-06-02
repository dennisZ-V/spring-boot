package com.zd.springboot.entity;

import lombok.Data;

/**
 * @author Dinnes Zhang
 * @date
 */
@Data
public class Employee {
    private Long id;
    private String lastName;
    private String email;
    private Long gender;
    private Long dId;
}
