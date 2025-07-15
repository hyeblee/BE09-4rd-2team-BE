package com.playblog.blogservice.neighbor.Service;




import com.playblog.blogservice.neighbor.Entity.Neighbor;
import com.playblog.blogservice.neighbor.Entity.NeighborStatus;
import com.playblog.blogservice.neighbor.Repository.NeighborRepository;

import com.playblog.blogservice.userInfo.UserInfo;
import com.playblog.blogservice.userInfo.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.server.ResponseStatusException;

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
    @Transactional
    public void acceptNeighborsStatus(Long fromUserId, List<Long> toUserIds) {
        UserInfo fromUser = userInfoRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        for (Long toUserId : toUserIds) {
            if (fromUserId.equals(toUserId)) continue;

            try {
                UserInfo toUser = userInfoRepository.findById(toUserId)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다: " + toUserId));

                Optional<Neighbor> fromTo = neighborRepository.findByFromUserInfoAndToUserInfo(fromUser, toUser);
                Optional<Neighbor> toFrom = neighborRepository.findByFromUserInfoAndToUserInfo(toUser, fromUser);

                if (toFrom.isEmpty()) {
                    System.err.println("이웃 요청이 존재하지 않음: " + toUserId);
                    continue;
                }

                // 1. 요청을 ACCEPTED로 변경
                Neighbor origin = toFrom.get();
                if (origin.getStatus() == NeighborStatus.REQUESTED) {
                    origin.setStatus(NeighborStatus.ACCEPTED);
                    origin.setFollowedAt(LocalDate.now());
                    neighborRepository.save(origin);
                } else if (origin.getStatus() == NeighborStatus.ACCEPTED) {
                    System.out.println("이미 서로이웃 상태: " + toUserId);
                }

                // 2. 역방향이 존재하면 상태를 ACCEPTED로 갱신 or 없으면 새로 만듦
                if (fromTo.isPresent()) {
                    Neighbor reverse = fromTo.get();
                    if (reverse.getStatus() != NeighborStatus.ACCEPTED) {
                        reverse.setStatus(NeighborStatus.ACCEPTED);
                        reverse.setFollowedAt(LocalDate.now());
                        neighborRepository.save(reverse);
                    }
                } else {
                    Neighbor newNeighbor = Neighbor.builder()
                            .fromUserInfo(fromUser)
                            .toUserInfo(toUser)
                            .requestedAt(LocalDate.now())
                            .followedAt(LocalDate.now())
                            .status(NeighborStatus.ACCEPTED)
                            .build();
                    neighborRepository.save(newNeighbor);
                }

            } catch (Exception e) {
                System.err.println("이웃 수락 실패 (userId: " + toUserId + ") - " + e.getMessage());
            }
        }
    }



    // 내가 추가한 이웃 조회
    public List<Neighbor> getAddedForMeNeighbors(Long userId) {
        UserInfo me = userInfoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));

        return neighborRepository.findByFromUserInfoAndStatusIn(me, List.of(NeighborStatus.ACCEPTED, NeighborStatus.REQUESTED));
    }
    // 나를 추가한 이웃 조회
    public List<Neighbor> getAddedToMeNeighbors(Long userId) {
        UserInfo me = userInfoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));

        return neighborRepository.findByToUserInfoAndStatusIn(me, List.of(NeighborStatus.ACCEPTED, NeighborStatus.REQUESTED, NeighborStatus.REJECTED));
    }
    // 서로이웃 보낸 신청
    public List<Neighbor> getSentMutualNeighbors(Long userId) {
        UserInfo me = userInfoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));

        return neighborRepository.findByFromUserInfoAndStatus(me, REQUESTED);
    }
    // 서로이웃 받은 신청
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
    public void rejectAllRelationNeighbor(Long userId, List<Long> deleteUserId) {
        UserInfo me = userInfoRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        for(Long id : deleteUserId) {
            UserInfo other = userInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            Optional<Neighbor> relation  = neighborRepository.findByFromUserInfoAndToUserInfo(me,other);
            if(relation.isPresent()) {
                neighborRepository.delete(relation.get());
            }
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
    @Transactional
    public void acceptNeighbor(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "자기 자신에게 이웃 요청을 보낼 수 없습니다.");
        }

        UserInfo fromUser = userInfoRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("fromUser가 존재하지 않습니다."));
        UserInfo toUser = userInfoRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("toUser가 존재하지 않습니다."));

        Optional<Neighbor> forwardOpt = neighborRepository.findByFromUserInfoAndToUserInfo(fromUser, toUser);
        Optional<Neighbor> reverseOpt = neighborRepository.findByFromUserInfoAndToUserInfo(toUser, fromUser);

        if (forwardOpt.isPresent() && reverseOpt.isPresent()) {
            Neighbor forward = forwardOpt.get();
            Neighbor reverse = reverseOpt.get();
            if (forward.getStatus() == NeighborStatus.ACCEPTED && reverse.getStatus() == NeighborStatus.ACCEPTED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 서로이웃입니다.");
            }
        }

        forwardOpt.ifPresent(neighborRepository::delete);
        reverseOpt.ifPresent(neighborRepository::delete);

        // ✅ 삭제를 DB에 즉시 반영해서 중복 insert 방지
        neighborRepository.flush();

        Neighbor forward = Neighbor.builder()
                .fromUserInfo(fromUser)
                .toUserInfo(toUser)
                .status(NeighborStatus.ACCEPTED)
                .requestedAt(LocalDate.now())
                .followedAt(LocalDate.now())
                .build();

        Neighbor reverse = Neighbor.builder()
                .fromUserInfo(toUser)
                .toUserInfo(fromUser)
                .status(NeighborStatus.ACCEPTED)
                .requestedAt(LocalDate.now())
                .followedAt(LocalDate.now())
                .build();

        neighborRepository.save(forward);
        neighborRepository.save(reverse);
    }

    @Transactional
    public void cancelRequestNeighbors(Long userId, List<Long> cancelUserIds) {
        UserInfo me = userInfoRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        for(Long id : cancelUserIds) {
            UserInfo other = userInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            Optional<Neighbor> relation  = neighborRepository.findByFromUserInfoAndToUserInfo(me,other);
            if(relation.isPresent()) {
                neighborRepository.delete(relation.get());
            }
        }
    }

    @Transactional
    public void blockNeighbors(Long userId, List<Long> blockUserIds) {
        UserInfo me = userInfoRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        for (Long targetId : blockUserIds) {

            // 나 자신은 건너뜀 (옵션)
            if (userId.equals(targetId)) continue;

            UserInfo other = userInfoRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + targetId));

            // ③ 기존 관계 조회 (from = me, to = other)
            Optional<Neighbor> opt = neighborRepository.findByFromUserInfoAndToUserInfo(me, other);

            if (opt.isPresent()) {
                // ④ 이미 관계가 있으면 상태만 REMOVED 로 변경
                Neighbor relation = opt.get();
                relation.setStatus(NeighborStatus.REMOVED);
            } else {
                // ⑤ 없으면 새로운 관계를 만들어 REMOVED 로 저장
                Neighbor relation = Neighbor.builder()
                        .fromUserInfo(me)
                        .toUserInfo(other)
                        .status(NeighborStatus.REMOVED)
                        .build();
                neighborRepository.save(relation);
            }
        }
    }
    public List<Neighbor> getBlockedNeighbors(Long userId) {
        UserInfo me = userInfoRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return neighborRepository.findByFromUserInfoAndStatus(me, REMOVED);
    }
}


