package com.playblog.blogservice.postlike.repository;

import com.playblog.blogservice.postlike.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 게시글에 공감 확인
    Optional<PostLike> findByPostIdAndUser_Id(Long postId, Long userId);

    // 게시글에 공감 여부 확인
    boolean existsByPostIdAndUser_Id(Long postId, Long userId);

    // 게시글의 공감 수
    long countByPostId(Long postId);

    // 게시글에 공감한 사용자 목록 (최신순)
    List<PostLike> findByPostIdOrderByCreatedAtDesc(Long postId);

    // 게시글 공감 삭제
    void deleteByPostIdAndUser_Id(Long postId, Long userId);

    // (목록 생성용) 여러 게시글의 공감 수 한번에 조회
//    @Query("select pl.postId, count(pl) from PostLike pl where pl.postId in :postIds group by pl.postId")
//    List<Object[]> countByPostIds(@Param("postIds") List<Long> postIds);
//    Collection<Object> countLikesByPostIds(List<Long> postIds);
}