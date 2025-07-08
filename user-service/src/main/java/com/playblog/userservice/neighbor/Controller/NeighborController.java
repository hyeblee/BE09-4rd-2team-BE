package com.playblog.userservice.neighbor.Controller;


import com.playblog.userservice.neighbor.Entity.User;
import com.playblog.userservice.neighbor.Entity.UserInfo;
import com.playblog.userservice.neighbor.Service.NeighborService;
import com.playblog.userservice.neighbor.dto.NeighborDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/neighbors")
@RequiredArgsConstructor
public class NeighborController {
    private final NeighborService neighborService;

    // 전체 이웃 조회
    @GetMapping("/my-following")
    public ResponseEntity<List<NeighborDto>> getNeighbors(@RequestHeader Long userId) {
        List<NeighborDto> neighbors = neighborService.getAllNeighborByUserId(userId);
        return ResponseEntity.ok(neighbors);
    }

    // 해당 이웃 해제
    @PatchMapping("/{deleteUserId}/reject")
    public ResponseEntity<Void> rejectNeighbor(
            @RequestHeader Long userId,
            @PathVariable Long deleteUserId
    ) {
        neighborService.rejectNeighbor(userId,deleteUserId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{insertUserId}/add")
    public ResponseEntity<Void> insertNeighbor(
            @RequestHeader Long fromUserId,
            @PathVariable Long insertUserId
    ){
        neighborService.addNeighbor(fromUserId,insertUserId);
        return ResponseEntity.noContent().build();
    }
}
