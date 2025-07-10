package com.playblog.blogservice.search.repository;

import com.playblog.blogservice.search.entity.TestNeighbor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NeighborRepository extends JpaRepository<TestNeighbor, Long> {

    @Query("SELECT n.neighbor.id FROM TestNeighbor n WHERE n.user.id = :myUserId")
    List<Long> findFollowingUserIdsByUserId(@Param("myUserId") Long myUserId);
}
