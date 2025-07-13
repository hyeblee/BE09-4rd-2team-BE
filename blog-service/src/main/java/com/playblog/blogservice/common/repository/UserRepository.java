package com.playblog.blogservice.common.repository;

import com.playblog.blogservice.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("commonUserRepository")
public interface UserRepository extends JpaRepository<User, Long> {
}
