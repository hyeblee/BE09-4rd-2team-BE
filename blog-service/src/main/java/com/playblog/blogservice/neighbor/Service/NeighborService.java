package com.playblog.blogservice.neighbor.Service;




import com.playblog.blogservice.neighbor.Entity.Neighbor;
import com.playblog.blogservice.neighbor.Entity.NeighborStatus;
import com.playblog.blogservice.neighbor.Repository.NeighborRepository;
import com.playblog.blogservice.neighbor.dto.NeighborDto;
import com.playblog.blogservice.userInfo.UserInfo;
import com.playblog.blogservice.userInfo.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.playblog.blogservice.neighbor.Entity.NeighborStatus.*;
import static javax.management.Query.in;

@Service
@RequiredArgsConstructor
public class NeighborService {

    private final NeighborRepository neighborRepository;
    private final UserInfoRepository userInfoRepository;

    /* 전체 이웃 조회
    public List<NeighborDto> getAllNeighborByUserId(Long id) {
        UserInfo myInfo = userInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        List<Neighbor> myFollowings = myInfo.getFollowing();

        List<Neighbor> myFollowers = myInfo.getFollowers();

        Map<Long, Neighbor> uniqueMap = new LinkedHashMap<>();


        // 팔로잉(내가 이웃 친구추가 한사람들) 은 이웃 목록에 무조건 넣음
        for (Neighbor n : myFollowers) {
            Long otherId = n.getFromUserInfo().getId();
            if (!otherId.equals(myInfo.getId())) {
                uniqueMap.put(otherId, n);
            }
        }

        // 팔로워중 서로이웃이 아닌 애들 맵에 추가
        for (Neighbor n : myFollowings) {
            Long otherId = n.getToUserInfo().getId();
            if (!otherId.equals(myInfo.getId()) && !uniqueMap.containsKey(otherId)) {
                uniqueMap.put(otherId, n);
            }
        }
        // 로그인 없을때 인증없이 맵객체를 리스트 DTO로 반환해서 전달
        return uniqueMap.values().stream()
                .map(NeighborDto::fromWithoutLogin)
                .collect(Collectors.toList());
    }

     */

    // 이웃 해제
    @Transactional
    public void rejectNeighbor(Long id,Long deleteUserId) {
        UserInfo myInfo = userInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        UserInfo deleteUser = userInfoRepository.findById(deleteUserId).orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Neighbor> optional = neighborRepository.findByFromUserInfoAndToUserInfo(deleteUser,myInfo);

        if (optional.isEmpty()) {
            // 혹시 내가 신청했는데 그걸 취소하려는 상황일 수도 있음
            optional = neighborRepository.findByFromUserInfoAndToUserInfo(myInfo, deleteUser);
        }

        Neighbor neighbor = optional
                .orElseThrow(() -> new RuntimeException("이웃 관계가 존재하지 않습니다."));

        switch (neighbor.getStatus()) {
            case REQUESTED -> {
                neighbor.setStatus(REJECTED);
                neighborRepository.save(neighbor);
            }
            case ACCEPTED -> {
                Optional<Neighbor> reverse = neighborRepository.findByFromUserInfoAndToUserInfo(myInfo,deleteUser);
                neighbor.setStatus(REJECTED);
                neighborRepository.save(neighbor);
            }
            default -> {
                throw new IllegalStateException("이미 처리된 이웃 상태입니다: " + neighbor.getStatus());
            }
        }
    }
    // 이웃 신청
    public List<NeighborDto> acceptNeighbor(Long fromUserId, List<Long> toUserIds) {
        UserInfo fromUser = userInfoRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        List<NeighborDto> results = new ArrayList<>();

        for (Long toUserId : toUserIds) {
            if (fromUserId.equals(toUserId)) {
                // 자기 자신에게 요청 → 건너뜀
                continue;
            }

            try {
                UserInfo toUser = userInfoRepository.findById(toUserId)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다: " + toUserId));

                Optional<Neighbor> existing = neighborRepository.findByFromUserInfoAndToUserInfo(fromUser, toUser);
                if (existing.isPresent()) {
                    Neighbor ex = existing.get();
                    switch (ex.getStatus()) {
                        case REJECTED:
                            neighborRepository.delete(ex); // 삭제 후 새로 생성
                        case REMOVED:
                            continue; // 차단된 경우 → 건너뜀
                        default:
                            continue; // 이미 존재하는 경우 → 건너뜀
                    }
                }

                Optional<Neighbor> reverse = neighborRepository.findByFromUserInfoAndToUserInfo(toUser, fromUser);
                NeighborStatus status = reverse.map(n -> n.getStatus() == ACCEPTED ? ACCEPTED : REQUESTED)
                        .orElse(REQUESTED);

                Neighbor neighbor = Neighbor.builder()
                        .fromUserInfo(fromUser)
                        .toUserInfo(toUser)
                        .requestedAt(LocalDate.now())
                        .followedAt(status == ACCEPTED ? LocalDate.now() : null)
                        .status(status)
                        .build();

                Neighbor saved = neighborRepository.save(neighbor);

                // 상대방도 요청했었다면 → 서로이웃으로 갱신
                if (status == ACCEPTED && reverse.isPresent() && reverse.get().getStatus() == REQUESTED) {
                    Neighbor other = reverse.get();
                    other.setStatus(ACCEPTED);
                    other.setFollowedAt(LocalDate.now());
                    neighborRepository.save(other);
                }

                results.add(NeighborDto.from(saved, reverse.orElse(null)));

            } catch (Exception e) {
                // 개별 예외는 로그 남기고 계속 진행
                System.err.println("이웃 추가 실패 (userId: " + toUserId + ") - " + e.getMessage());
            }
        }

        return results;
    }


    // 내가 추가한 이웃 조회
    public List<Neighbor> getAddedForMeNeighbors(Long userId) {
        UserInfo me = userInfoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));

        return neighborRepository.findByFromUserInfoAndStatusIn(me,   List.of(NeighborStatus.ACCEPTED, NeighborStatus.REQUESTED));
    }
    // 나를 추가한 이웃 조회
    public List<Neighbor> getAddedToMeNeighbors(Long userId) {
        UserInfo me = userInfoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));

        return neighborRepository.findByToUserInfoAndStatusIn(me, List.of(NeighborStatus.ACCEPTED, NeighborStatus.REQUESTED));
    }
    // 서로이웃 받은 신청
    public List<Neighbor> getSentMutualNeighbors(Long userId) {
        UserInfo me = userInfoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));

        return neighborRepository.findByFromUserInfoAndStatus(me, REQUESTED);
    }
    // 서로이웃 보낸 신청
    public List<Neighbor> getReceivedMutualNeighbors(Long userId) {
        UserInfo me = userInfoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));

        return neighborRepository.findByToUserInfoAndStatus(me, REQUESTED);
    }
    // 여러 신청 일괄 수락
    @Transactional
    public void accpetMultipleNeighbors(Long userId, List<Long> ids) {
        UserInfo me = userInfoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));
        for(Long id : ids) {
            UserInfo other = userInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            Optional<Neighbor> reverse = neighborRepository.findByFromUserInfoAndToUserInfo(me,other);
            Optional<Neighbor> relation = neighborRepository.findByFromUserInfoAndToUserInfo(other,me);
            if(relation.isPresent()) {
                if(relation.get().getStatus() == REQUESTED){
                    relation.get().setStatus(ACCEPTED);
                }
            }
            else{
                throw new RuntimeException("신청받은 요청이 없습니다.");
            }

            if (reverse.isPresent()) {
                Neighbor back = reverse.get();
                back.setStatus(NeighborStatus.ACCEPTED);
                back.setFollowedAt(LocalDate.now());
            }
        }
    }
    // 여러 이웃 신청 거절
    @Transactional
    public void rejectMultipleNeighbors(Long userId, List<Long> ids) {
        UserInfo me = userInfoRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        for(Long id : ids) {
            UserInfo other = userInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            Optional<Neighbor> relation  = neighborRepository.findByFromUserInfoAndToUserInfo(other,me);
            if(relation.get().getStatus() == REQUESTED){
                relation.get().setStatus(REJECTED);
            }

        }

    }
    @Transactional
    public void rejectAllRelationNeighbor(Long userId, Long deleteUserId) {
        UserInfo me = userInfoRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserInfo other = userInfoRepository.findById(deleteUserId).orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Neighbor> relation  = neighborRepository.findByFromUserInfoAndToUserInfo(me,other);
        if(relation.isPresent()) {
            neighborRepository.delete(relation.get());
        }
    }
    @Transactional
    public void changeRelationNeighbor(Long userId, List<Long> changeUserId) {
        UserInfo me = userInfoRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        for(Long id : changeUserId) {
            UserInfo other = userInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            Optional<Neighbor> relation  = neighborRepository.findByFromUserInfoAndToUserInfo(other,me);
            if(relation.get().getStatus() == ACCEPTED){
                relation.get().setStatus(REJECTED);
            }
        }
    }

    public List<Neighbor> getReverseNeighbors(List<UserInfo> fromUsers, Long toUserId) {
        UserInfo toUser = userInfoRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));
        return neighborRepository.findAllByFromUserInfoInAndToUserInfo(fromUsers, toUser);
    }

    public Map<Long, Neighbor> getReverseNeighborMap(List<Neighbor> neighbors, Long userId) {
        List<Long> fromUserIds = neighbors.stream()
                .map(n -> n.getFromUserInfo().getId())
                .distinct()
                .toList();

        List<Neighbor> reverseList = neighborRepository.findByFromUserInfoIdInAndToUserInfoIdAndStatus(
                fromUserIds,
                userId,
                NeighborStatus.ACCEPTED
        );

        return reverseList.stream()
                .collect(Collectors.toMap(
                        n -> n.getFromUserInfo().getId(),
                        n -> n,
                        (existing, duplicate) -> existing
                ));
    }
}

