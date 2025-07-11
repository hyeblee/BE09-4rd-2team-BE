package com.playblog.userservice.neighbor.Controller;


import com.playblog.userservice.neighbor.Entity.Neighbor;
import com.playblog.userservice.neighbor.Entity.UserInfo;

import com.playblog.userservice.neighbor.Repository.UserInfoRepository;
import com.playblog.userservice.neighbor.Service.NeighborService;
import com.playblog.userservice.neighbor.dto.*;
import com.playblog.userservice.neighbor.mapper.NeighborDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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
    @Operation(summary = "내가 추가 이웃", description = "내가 이웃을 요청한 이웃 목록들을 보여줍니다.")
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
    @Operation(summary = "나를 추가한 이웃", description = "나에게 이웃을 요청한 이웃 목록들을 보여줍니다.")
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
    @Operation(summary = "내가 보낸 서로이웃",description = "내가 서로이웃 요청을 보낸 이웃 목록들을 보여줍니다.")
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
    @Operation(summary = "내가 받은 서로이웃",description = "나에게 서로이웃 요청을 보낸 이웃 목록들을 보여줍니다.")
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
    @Operation(summary = "이웃 해제",description = "해당 유저의 이웃 관계를 해제합니다.")
    @PatchMapping("/{deleteUserId}/reject")
    public ResponseEntity<Void> rejectNeighbor(
            @RequestHeader Long userId,
            @PathVariable Long deleteUserId
    ) {
        neighborService.rejectNeighbor(userId,deleteUserId);
        return ResponseEntity.noContent().build();
    }

    // 이웃 요청
    @Operation(summary = "이웃 신청",description = "서로 이웃이 아닐경우  그냥 이웃신청 하고 이웃일경우 서로이웃을 신청합니다.")
    @PatchMapping("/accept")
    public ResponseEntity<Void> insertNeighbor(
            @RequestHeader Long fromUserId,
            @RequestBody List<Long> insertUserId
    ){
        neighborService.acceptNeighbor(fromUserId,insertUserId);
        return ResponseEntity.noContent().build();
    }

    // 서로이웃 수락(단체)
    @Operation(summary = "서로이웃 단체수락",description = "목록에서 클릭한 유저들의 서로이웃 신청을 다 받습니다.")
    @PostMapping("/batch-accept")
    public ResponseEntity<Void> acceptMultipleNeighbors(
            @RequestHeader Long userId,
            @RequestBody List<Long> Ids
    ){
        neighborService.accpetMultipleNeighbors(userId,Ids);
        return ResponseEntity.noContent().build();
    }

    // 서로 이웃 거절(단체)
    @Operation(summary = "서로이웃 단체거절",description = "목록에서 클릭한 유저들의 서로이웃 신청을 다 거절합니다.")
    @PostMapping("/batch-rejected")
    public ResponseEntity<Void> rejectMultipleNeighbors(
            @RequestHeader Long userId,
            @RequestBody List<Long> Ids
    ){
        neighborService.rejectMultipleNeighbors(userId,Ids);
        return ResponseEntity.noContent().build();
    }
    // 이웃관계 삭제
    @Operation(summary = "이웃관계 완전 헤제",description = "해당 유저를 이웃에서 완전히 제거합니다")
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
