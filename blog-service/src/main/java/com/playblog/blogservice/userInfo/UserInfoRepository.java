package com.playblog.blogservice.userInfo;

import com.playblog.blogservice.search.entity.TestUserInfo;
import com.playblog.blogservice.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

}
