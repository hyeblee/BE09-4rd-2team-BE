package com.playblog.blogservice.search.repository;

import com.playblog.blogservice.search.entity.TestUser;
import com.playblog.blogservice.search.entity.TestUserInfo;
import com.playblog.blogservice.user.User;
import com.playblog.blogservice.userInfo.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TestUserInfoRepository extends JpaRepository<TestUserInfo, Long> {
    // 블로그명 검색
    @Query("SELECT u FROM UserInfo u WHERE u.blogTitle LIKE %:blogKeyword% " +
            "OR u.profileIntro LIKE %:blogKeyword%")
    List<TestUserInfo> findByBlogTitleOrProfileIntro(@Param("blogKeyword") String blogKeyword);

    // 별명.아이디 검색
    @Query("SELECT u FROM UserInfo u WHERE u.nickname LIKE %:nickname% " +
            "OR u.blogId LIKE %:nickname%")
    List<TestUserInfo> findByNicknameOrBlogId(String nickname);

    //     User 정보로 UserInfo 조회
    Optional<TestUserInfo> findByUser(TestUser user);
}
