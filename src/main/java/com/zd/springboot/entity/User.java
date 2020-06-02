package com.zd.springboot.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Dinnes Zhang
 * @date
 *
 * @Entity  告诉JPA这是一个实体类
 * @Table   指定和哪个数据表对应
 */
@Entity
@Data
@Table(name = "tbl_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_name")
    private String lastName;

    @Column
    private String email;
}
