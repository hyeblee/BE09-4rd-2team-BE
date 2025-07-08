package com.playblog.userservice.neighbor.Repository;

import com.playblog.userservice.neighbor.Entity.Neighbor;
import com.playblog.userservice.neighbor.Entity.NeighborStatus;
import com.playblog.userservice.neighbor.Entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NeighborRepository extends JpaRepository<Neighbor,Long> {

    Optional<Neighbor> findByIdAndToUserInfo(Long id ,UserInfo toUserInfo);

    Optional<Neighbor> findByToUserInfoAndId(UserInfo toUserInfo,Long id);

    Optional<Neighbor> findByFromUserInfoAndToUserInfo(UserInfo id, UserInfo deleteUserId);

    List<Neighbor> findByFromUserInfoAndStatus(UserInfo me, NeighborStatus neighborStatus);

    List<Neighbor> findByToUserInfoAndStatus(UserInfo me, NeighborStatus neighborStatus);
}
