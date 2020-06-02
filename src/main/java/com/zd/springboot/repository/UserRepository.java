package com.zd.springboot.repository;

import com.zd.springboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Dinnes Zhang
 * @date
 *
 * 集成JpaRepository<T,ID>来完成对数据库的操作
 */
public interface UserRepository extends JpaRepository<User,Long> {
}
