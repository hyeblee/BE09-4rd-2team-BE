package com.playblog.blogservice.userInfo;

import com.playblog.blogservice.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    // 블로그명 검색
    @Query("SELECT u FROM UserInfo u WHERE u.blogTitle LIKE %:blogTitle% " +
            "OR u.profileIntro LIKE %:blogTitle%")
    List<UserInfo> findByBlogTitleOrProfileIntro(@Param("blogTitle") String blogTitle);

    // 별명.아이디 검색
    @Query("SELECT u FROM UserInfo u WHERE u.nickname LIKE %:nickname% " +
            "OR u.blogId LIKE %:nickname%")
    List<UserInfo> findByNicknameOrBlogId(String nickname);

    //     User 정보로 UserInfo 조회
    Optional<UserInfo> findByUser(User user);

    UserInfo findByNickname(String nickname);
}
