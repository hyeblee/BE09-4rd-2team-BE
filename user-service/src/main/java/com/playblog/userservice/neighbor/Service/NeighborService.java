package com.playblog.userservice.neighbor.Service;

import com.playblog.userservice.neighbor.Entity.Neighbor;
import com.playblog.userservice.neighbor.Entity.User;
import com.playblog.userservice.neighbor.Entity.UserInfo;
import com.playblog.userservice.neighbor.Repository.NeighborRepository;
import com.playblog.userservice.neighbor.Repository.UserRepository;
import com.playblog.userservice.neighbor.dto.NeighborDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NeighborService {

    private final NeighborRepository neighborRepository;
    private final UserRepository userRepository;


    public List<NeighborDto> getAllNeighborByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        UserInfo myInfo = user.getUserInfo();

        List<Neighbor> myFollowings = myInfo.getFollowing();

        List<Neighbor> myFollowers = myInfo.getFollowers();

        Map<Long, Neighbor> uniqueMap = new LinkedHashMap<>();


        // 팔로잉(내가 이웃 친구추가 한사람들) 은 이웃 목록에 무조건 넣음
        for (Neighbor n : myFollowings) {
            Long otherId = n.getToUserInfo().getId();
            uniqueMap.put(otherId, n);
        }

        // 팔로워중 서로이웃이 아닌 애들 맵에 추가
        for (Neighbor n : myFollowers) {
            if(n.isMutual()==false){
                uniqueMap.put(n.getToUserInfo().getId(), n);
            }
        }
        // 로그인 없을때 인증없이 맵객체를 리스트 DTO로 반환해서 전달
        return uniqueMap.values().stream()
                .map(NeighborDto::fromWithoutLogin)
                .collect(Collectors.toList());
    }
}
