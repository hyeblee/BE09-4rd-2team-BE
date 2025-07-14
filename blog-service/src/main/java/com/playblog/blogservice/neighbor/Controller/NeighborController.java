package com.playblog.blogservice.neighbor.Controller;


import com.playblog.blogservice.neighbor.Entity.Neighbor;
import com.playblog.blogservice.neighbor.Service.NeighborService;
import com.playblog.blogservice.neighbor.dto.*;
import com.playblog.blogservice.neighbor.mapper.NeighborDtoMapper;
import com.playblog.blogservice.userInfo.UserInfo;
import com.playblog.blogservice.userInfo.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @RequestHeader("X-User-Id") Long userId
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
            @RequestHeader("X-User-Id") Long userId
    ) {
        List<Neighbor> neighbors = neighborService.getAddedToMeNeighbors(userId);

        List<Neighbor> reverseNeighbors = neighborService.getAddedForMeNeighbors(userId);
        List<MyAddedToMeNeighborDto> result = neighborDtoMapper.toMyReceivedDto(neighbors, reverseNeighbors);

        return ResponseEntity.ok(result);
    }


    // 내가 보낸 서로이웃
    @GetMapping("/my-following/sent-mutual")
    public ResponseEntity<List<SentMutualNeighborDto>> getSentMutualNeighbors(
            @RequestHeader("X-User-Id") Long userId
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
            @AuthenticationPrincipal Long userId
    ) {
        List<Neighbor> neighbors = neighborService.getReceivedMutualNeighbors(userId);
        List<ReceivedMutualNeighborDto> result = neighbors.stream()
                .map(neighborDtoMapper::toReceivedMutualDto)
                .toList();
        return ResponseEntity.ok(result);
    }



    // 이웃 요청(다수)
    @PatchMapping("/accept")
    public ResponseEntity<Void> insertNeighbors(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<Long> insertUserIds
    ){
        neighborService.acceptNeighborsStatus(userId,insertUserIds);
        return ResponseEntity.noContent().build();
    }
    // 이웃 요청(한명)
    @PatchMapping("/{insertUserId}/accept")
    public ResponseEntity<Void> insertNeighbor(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long insertUserId
    ){
        neighborService.acceptNeighbor(userId,insertUserId);
        return ResponseEntity.noContent().build();
    }


    // 서로이웃 수락(단체)
    @PostMapping("/batch-accept")
    public ResponseEntity<Void> acceptMultipleNeighbors(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<Long> Ids
    ){
        neighborService.accpetMultipleNeighbors(userId,Ids);
        return ResponseEntity.noContent().build();
    }

    // 서로 이웃 거절(단체)
    @PostMapping("/batch-rejected")
    public ResponseEntity<Void> rejectMultipleNeighbors(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<Long> Ids
    ){
        neighborService.rejectMultipleNeighbors(userId,Ids);
        return ResponseEntity.noContent().build();
    }
    // 이웃관계 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> rejectAllNeighbors(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<Long> deleteUserId
    ){
        neighborService.rejectAllRelationNeighbor(userId,deleteUserId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/batch-change")
    public ResponseEntity<Void> changeRelationNeighbors(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<Long> changeUserId
    ){
        neighborService.changeRelationNeighbor(userId,changeUserId);
        return ResponseEntity.noContent().build();
    }
    // 내가 보낸 신청 취소
    @PostMapping("/batch-cancel")
    public ResponseEntity<Void> cancelRequestNeighbors(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<Long> cancelUserIds
    ){
        neighborService.cancelRequestNeighbors(userId,cancelUserIds);
        return ResponseEntity.noContent().build();
    }

    // 유저 차단
    @PostMapping("/batch-block")
    public ResponseEntity<Void> blockNeighbors(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody List<Long> blockUserIds
            ){
        neighborService.blockNeighbors(userId,blockUserIds);
        return ResponseEntity.noContent().build();
    }
}
