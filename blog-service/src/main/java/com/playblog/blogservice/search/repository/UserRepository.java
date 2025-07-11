package com.playblog.blogservice.search.repository;

import com.playblog.blogservice.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}