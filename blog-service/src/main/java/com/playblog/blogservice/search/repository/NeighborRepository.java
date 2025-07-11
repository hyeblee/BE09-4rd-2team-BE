package com.playblog.blogservice.search.repository;

import com.playblog.blogservice.search.entity.Neighbor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NeighborRepository extends JpaRepository<Neighbor, Long> {

    @Query("SELECT n.toUserInfo.id FROM Neighbor n WHERE n.fromUserInfo.id = :myUserInfoId")
    List<Long> findFollowingUserInfoIdsByUserInfoId(@Param("myUserInfoId") Long myUserInfoId);

}
