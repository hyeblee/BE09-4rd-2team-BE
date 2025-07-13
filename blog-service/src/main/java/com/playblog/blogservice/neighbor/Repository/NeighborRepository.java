package com.playblog.blogservice.neighbor.Repository;


import com.playblog.blogservice.neighbor.Entity.Neighbor;
import com.playblog.blogservice.neighbor.Entity.NeighborStatus;
import com.playblog.blogservice.userInfo.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NeighborRepository extends JpaRepository<Neighbor,Long> {

    Optional<Neighbor> findByIdAndToUserInfo(Long id , UserInfo toUserInfo);

    Optional<Neighbor> findByToUserInfoAndId(UserInfo toUserInfo,Long id);

    Optional<Neighbor> findByFromUserInfoAndToUserInfo(UserInfo id, UserInfo deleteUserId);

    List<Neighbor> findByToUserInfoAndStatus(UserInfo me, NeighborStatus neighborStatus);

    List<Neighbor> findAllByFromUserInfoInAndToUserInfo(List<UserInfo> fromUsers, UserInfo toUser);

    List<Neighbor> findByFromUserInfoAndStatus(UserInfo me, NeighborStatus neighborStatus);

    List<Neighbor> findByFromUserInfoAndStatusIn(UserInfo me, List<NeighborStatus> accepted);

    List<Neighbor> findByToUserInfoAndStatusIn(UserInfo me, List<NeighborStatus> accepted);

    List<Neighbor> findByFromUserInfoIdInAndToUserInfoIdAndStatus(List<Long> attr0, Long attr1, NeighborStatus status);

    List<Long> findFollowingUserIdsById(Long myUserId);

    Optional<Neighbor> findByFromUserInfoAndToUserInfoAndStatus(UserInfo me, UserInfo other, NeighborStatus neighborStatus);
}
