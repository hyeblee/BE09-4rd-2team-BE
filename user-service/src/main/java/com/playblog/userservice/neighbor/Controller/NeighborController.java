package com.playblog.userservice.neighbor.Controller;


import com.playblog.userservice.neighbor.Service.NeighborService;
import com.playblog.userservice.neighbor.dto.NeighborDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/neighbors")
@RequiredArgsConstructor
public class NeighborController {
    private final NeighborService neighborService;

    @GetMapping("/my-following")
    public ResponseEntity<List<NeighborDto>> getNeighbors(@RequestHeader Long userId) {
        List<NeighborDto> neighbors = neighborService.getAllNeighborByUserId(userId);
        return ResponseEntity.ok(neighbors);
    }
}
