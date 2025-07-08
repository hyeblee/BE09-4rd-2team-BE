package com.playblog.blogservice.search.repository;


import com.playblog.blogservice.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // 블로그명 검색
    @Query("SELECT u FROM User u WHERE u.userInfo.blogTitle LIKE %:blogKeyword% " +
            "OR u.userInfo.profileIntro LIKE %:blogKeyword%")
    List<User> findByBlogTitleOrProfileIntro(@Param("blogKeyword") String blogKeyword);

    // 별명.아이디 검색
    @Query("SELECT u FROM User u WHERE u.userInfo.nickname LIKE %:keyword% " +
            "OR u.userInfo.blogId LIKE %:keyword%")
    List<User> findByNicknameOrBlogId(String nickname);
}
