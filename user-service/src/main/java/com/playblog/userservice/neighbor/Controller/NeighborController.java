package com.playblog.userservice.neighbor.Controller;


import com.playblog.userservice.neighbor.Entity.Neighbor;
import com.playblog.userservice.neighbor.Entity.User;
import com.playblog.userservice.neighbor.Entity.UserInfo;
import com.playblog.userservice.neighbor.Service.NeighborService;
import com.playblog.userservice.neighbor.dto.*;
import com.playblog.userservice.neighbor.mapper.NeighborDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/neighbors")
@RequiredArgsConstructor
public class NeighborController {
    private final NeighborService neighborService;

    // 내가 요청한 이웃(내가 추가)
    @GetMapping("/my-following/added")
    public ResponseEntity<List<MyAddedForMeNeighborDto>> getMyAddedNeighbors(
            @RequestHeader Long userId
    ) {
        List<Neighbor> neighbors = neighborService.getAddedForMeNeighbors(userId);
        List<MyAddedForMeNeighborDto> result = neighbors.stream()
                .map(NeighborDtoMapper::toMyAddedDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    // 나에게 요청한 이웃 (나를 추가)
    @GetMapping("/my-following/received")
    public ResponseEntity<List<MyAddedToMeNeighborDto>> getMyReceivedNeighbors(
            @RequestHeader Long userId
    ) {
        List<Neighbor> neighbors = neighborService.getAddedToMeNeighbors(userId);
        List<MyAddedToMeNeighborDto> result = neighbors.stream()
                .map(NeighborDtoMapper::toMyReceivedDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    // 내가 보낸 서로이웃
    @GetMapping("/my-following/sent-mutual")
    public ResponseEntity<List<SentMutualNeighborDto>> getSentMutualNeighbors(
            @RequestHeader Long userId
    ) {
        List<Neighbor> neighbors = neighborService.getSentMutualNeighbors(userId);
        List<SentMutualNeighborDto> result = neighbors.stream()
                .map(NeighborDtoMapper::toSentMutualDto)
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
                .map(NeighborDtoMapper::toReceivedMutualDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    // 이웃 해제
    @PatchMapping("/{deleteUserId}/reject")
    public ResponseEntity<Void> rejectNeighbor(
            @RequestHeader Long userId,
            @PathVariable Long deleteUserId
    ) {
        neighborService.rejectNeighbor(userId,deleteUserId);
        return ResponseEntity.noContent().build();
    }

    // 이웃 요청
    @PatchMapping("/{toUserId}/accept")
    public ResponseEntity<Void> insertNeighbor(
            @RequestHeader Long fromUserId,
            @PathVariable Long insertUserId
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

}
