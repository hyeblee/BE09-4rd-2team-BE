package com.playblog.blogservice.search.entity;

import com.playblog.blogservice.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "test_neighbor")
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class Neighbor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 본인 (이웃을 추가한 사람)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 이웃 (추가된 대상)
    @ManyToOne
    @JoinColumn(name = "neighbor_id", nullable = false)
    private User neighbor;
}

