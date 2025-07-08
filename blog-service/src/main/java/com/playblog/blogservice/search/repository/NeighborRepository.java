package com.playblog.blogservice.search.repository;

import com.playblog.blogservice.common.entity.Neighbor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NeighborRepository extends JpaRepository<Neighbor, Long> {

    @Query("SELECT n.toUser.id FROM Neighbor n WHERE n.fromUser.id = :myUserId")
    List<Long> findFollowingUserIdsByUserId(@Param("myUserId") Long myUserId);
}
