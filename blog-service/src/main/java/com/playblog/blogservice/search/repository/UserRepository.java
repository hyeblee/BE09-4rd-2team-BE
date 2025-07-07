package com.playblog.blogservice.search.repository;


import com.playblog.blogservice.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserInfo_BlogTitleContaining(String blogTitle);
    
}
