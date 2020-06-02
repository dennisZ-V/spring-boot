package com.zd.springboot.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Dinnes Zhang
 * @date
 */
@Data
public class Employee implements Serializable {
    private Long id;
    private String lastName;
    private String email;
    private Long gender;
    private Long dId;
}
