package com.playblog.userservice.neighbor.Service;

import com.playblog.userservice.neighbor.Entity.Neighbor;
import com.playblog.userservice.neighbor.Entity.NeighborStatus;
import com.playblog.userservice.neighbor.Entity.User;
import com.playblog.userservice.neighbor.Entity.UserInfo;
import com.playblog.userservice.neighbor.Repository.NeighborRepository;
import com.playblog.userservice.neighbor.Repository.UserInfoRepository;
import com.playblog.userservice.neighbor.Repository.UserRepository;
import com.playblog.userservice.neighbor.dto.NeighborDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.playblog.userservice.neighbor.Entity.NeighborStatus.*;

@Service
@RequiredArgsConstructor
public class NeighborService {

    private final NeighborRepository neighborRepository;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;


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
                Neighbor reverseNeighborRelation = reverse.orElse(null);
                neighborRepository.save(neighbor);
            }
            default -> {
                throw new IllegalStateException("이미 처리된 이웃 상태입니다: " + neighbor.getStatus());
            }
        }
    }

    public NeighborDto addNeighbor(Long fromUserId,Long toUserId) {
        UserInfo toUser = userInfoRepository.findById(toUserId).orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));
        UserInfo fromUser = userInfoRepository.findById(fromUserId).orElseThrow(() -> new RuntimeException("User not found"));

        if (fromUser.getId().equals(toUser.getId())) {
            throw new RuntimeException("자기 자신에게 이웃 요청을 보낼 수 없습니다");
        }
        Optional<Neighbor> existing = neighborRepository.findByFromUserInfoAndToUserInfo(fromUser,toUser);
        if(existing.isPresent()) {
            throw new RuntimeException("이미 요청을 보냈습니다");
        }
        if(existing.isPresent() && existing.get().getStatus() == REMOVED) {
            throw new RuntimeException("기존에 차단한 이웃입니다.");
        }
        Optional<Neighbor> reverse = neighborRepository.findByFromUserInfoAndToUserInfo(toUser,fromUser);
        NeighborStatus status = reverse.map( n -> n.getStatus() == REQUESTED || n.getStatus()==ACCEPTED || n.getStatus() == REJECTED ? ACCEPTED : REJECTED).orElse(REQUESTED);

        Neighbor neighbor = Neighbor.builder()
                .fromUserInfo(fromUser)
                .toUserInfo(toUser)
                .followedAt(status == ACCEPTED ? LocalDateTime.now() : null)
                .requestedAt(LocalDateTime.now())
                .status(status)
                .build();
        Neighbor saved = neighborRepository.save(neighbor);

        // 상대가 먼저 팔로우한 상태라면 상대방의 상태도 ACCEPTED로 바꿔줌 → 진짜 서로이웃 상태
        if (status == ACCEPTED && reverse.isPresent() && reverse.get().getStatus() == REQUESTED) {
            Neighbor other = reverse.get();
            other.setStatus(ACCEPTED);
            other.setFollowedAt(LocalDateTime.now());
            neighborRepository.save(other);
        }
        return NeighborDto.fromWithoutLogin(saved);
    }

}
