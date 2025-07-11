package com.playblog.blogservice.neighbor.Controller;


import com.playblog.blogservice.neighbor.Entity.Neighbor;
import com.playblog.blogservice.neighbor.Service.NeighborService;
import com.playblog.blogservice.neighbor.dto.*;
import com.playblog.blogservice.neighbor.mapper.NeighborDtoMapper;
import com.playblog.blogservice.userInfo.UserInfo;
import com.playblog.blogservice.userInfo.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/neighbors")
@RequiredArgsConstructor
public class NeighborController {
    private final NeighborService neighborService;
    private final NeighborDtoMapper neighborDtoMapper;
    private final UserInfoRepository userInfoRepository;


    // 내가 요청한 이웃(내가 추가)
    @GetMapping("/my-following/added")
    public ResponseEntity<List<MyAddedForMeNeighborDto>> getMyAddedNeighbors(
            @RequestHeader("userId") Long userId
    ) {
        List<Neighbor> neighbors = neighborService.getAddedForMeNeighbors(userId);

        // reverse 관계들 미리 조회해서 Map으로 만듦
        List<UserInfo> toUsers = neighbors.stream()
                .map(Neighbor::getToUserInfo)
                .toList();

        List<Neighbor> reverseList = neighborService.getReverseNeighbors(toUsers, userId);
        Map<Long, Neighbor> reverseMap = reverseList.stream()
                .collect(Collectors.toMap(n -> n.getFromUserInfo().getId(), n -> n));

        List<MyAddedForMeNeighborDto> result = neighbors.stream()
                .map(n -> neighborDtoMapper.toMyAddedDto(n, reverseMap.get(n.getToUserInfo().getId())))
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-following/received")
    public ResponseEntity<List<MyAddedToMeNeighborDto>> getMyReceivedNeighbors(
            @RequestHeader Long userId
    ) {
        List<Neighbor> neighbors = neighborService.getAddedToMeNeighbors(userId);
        for(Neighbor n : neighbors) {
            System.out.println("여기에요=============" + n.getFromUserInfo().getId());
        }
        List<Neighbor> reverseNeighbors = neighborService.getAddedForMeNeighbors(userId);
        List<MyAddedToMeNeighborDto> result = neighborDtoMapper.toMyReceivedDto(neighbors, reverseNeighbors);

        return ResponseEntity.ok(result);
    }


    // 내가 보낸 서로이웃
    @GetMapping("/my-following/sent-mutual")
    public ResponseEntity<List<SentMutualNeighborDto>> getSentMutualNeighbors(
            @RequestHeader Long userId
    ) {
        List<Neighbor> neighbors = neighborService.getSentMutualNeighbors(userId);
        List<SentMutualNeighborDto> result = neighbors.stream()
                .map(neighborDtoMapper::toSentMutualDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    // 내가 받은 서로이웃
    @GetMapping("/my-following/received-mutual")
    public ResponseEntity<List<ReceivedMutualNeighborDto>> getReceivedMutualNeighbors(
            @RequestHeader Long userId
    ) {
        List<Neighbor> neighbors = neighborService.getReceivedMutualNeighbors(userId);
        List<ReceivedMutualNeighborDto> result = neighbors.stream()
                .map(neighborDtoMapper::toReceivedMutualDto)
                .toList();
        return ResponseEntity.ok(result);
    }




    // 이웃 해제
    public ResponseEntity<Void> rejectNeighbor(
            @RequestHeader Long userId,
            @PathVariable Long deleteUserId
    ) {
        neighborService.rejectNeighbor(userId,deleteUserId);
        return ResponseEntity.noContent().build();
    }

    // 이웃 요청
    @PatchMapping("/accept")
    public ResponseEntity<Void> insertNeighbor(
            @RequestHeader Long fromUserId,
            @RequestBody List<Long> insertUserId
    ){
        neighborService.acceptNeighbor(fromUserId,insertUserId);
        return ResponseEntity.noContent().build();
    }

    // 서로이웃 수락(단체)
    @PostMapping("/batch-accept")
    public ResponseEntity<Void> acceptMultipleNeighbors(
            @RequestHeader Long userId,
            @RequestBody List<Long> Ids
    ){
        neighborService.accpetMultipleNeighbors(userId,Ids);
        return ResponseEntity.noContent().build();
    }

    // 서로 이웃 거절(단체)
    @PostMapping("/batch-rejected")
    public ResponseEntity<Void> rejectMultipleNeighbors(
            @RequestHeader Long userId,
            @RequestBody List<Long> Ids
    ){
        neighborService.rejectMultipleNeighbors(userId,Ids);
        return ResponseEntity.noContent().build();
    }
    // 이웃관계 삭제
    @DeleteMapping("/{deleteUserId}/delete")
    public ResponseEntity<Void> rejectAllNeighbors(
            @RequestHeader Long userId,
            @PathVariable Long deleteUserId
    ){
        neighborService.rejectAllRelationNeighbor(userId,deleteUserId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/batch-change")
    public ResponseEntity<Void> changeRelationNeighbors(
            @RequestHeader Long userId,
            @RequestBody List<Long> changeUserId
    ){
        neighborService.changeRelationNeighbor(userId,changeUserId);
        return ResponseEntity.noContent().build();
    }
}
