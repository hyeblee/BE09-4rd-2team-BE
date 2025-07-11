package com.playblog.blogservice.postservice.post.dto;

import com.playblog.blogservice.postservice.post.entity.Post;
import com.playblog.blogservice.postservice.post.entity.PostPolicy;
import com.playblog.blogservice.postservice.post.entity.PostVisibility;
import com.playblog.blogservice.userInfo.UserInfo;
import jakarta.persistence.JoinColumn;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    /* 게시글 정보 */
    private Long postId;                // 게시글 아이디
    private String title;               // 제목
    private String content;             // 내용
    private PostVisibility visibility;  // 발행 설정
    private Boolean allowComment;       // 댓글 공개 여부
    private Boolean allowLike;          // 공감 허용
    private Boolean allowSearch;        // 검색 허용

    /* 작성자 정보 */
    private String blogTitle;          // 블로그 타이틀
    private String nickname;           // 유저 닉네임
    private String profileImageUrl;    // 프로필 사진
    //    private String profileIntro; // 프로필 소개글

    /* 좋아요 정보 */
    private Long likeCount;            // 댓글 수
    private Boolean isLiked;           // 댓글 공감 수

    /* 댓글 리스트 */
//    private List<CommentResponse> comments; // 댓글 리스트
    /*
    * DB에서는 post ↔ comment 연관관계가 @OneToMany로 연결돼 있을 겁니다.
    * 서비스에서 게시글을 가져올 때 post.getComments() 로 댓글 리스트를 가져옴.
    * 그걸 CommentResponse라는 안전한 응답 DTO로 변환해서 리스트로 포함.

    private Long commentId;            // 댓글 사용자 아이디
    private String commentNickname;    // 댓글 사용자
    private String comment;            // 댓글 내용
    private Boolean isSecret;          // 개인 비밀 댓글 허용 여부
    private LocalDateTime commentCreatedAt; // 댓글 작성 일시
    * */

    /* 정책 */
    @JoinColumn(name = "policy_id")
    private PostPolicy postPolicy;

// 백에서 DTO 호출시 보이거나 동작하지 않는건 갖고 오지 않는다.
// 엔터티는 DB용일뿐 그이상 그이하도 아니다.
//

    public static PostResponseDto from(Post post) {
        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .visibility(post.getVisibility())
                .allowComment(post.getPostPolicy() != null ? post.getPostPolicy().getAllowComment() : null)
                .allowLike(post.getPostPolicy() != null ? post.getPostPolicy().getAllowLike() : null)
                .allowSearch(post.getPostPolicy() != null ? post.getPostPolicy().getAllowSearch() : null)
                .blogTitle("임시 블로그")
                .nickname("임시 닉네임")
                .profileImageUrl(null)
                .likeCount(0L)
                .isLiked(null)
                .build();
    }


//    Null값 포스트
//    public static PostResponseDto from(Post post, UserInfo userInfo, /* List<CommentResponse> comments, */ Long likeCount, Boolean isLiked) {
//        return PostResponseDto.builder()
//                .postId(post.getId())
//                .title(post.getTitle())
//                .content(post.getContent())
//                .visibility(post.getVisibility())
//                .allowComment(post.getPostPolicy() != null ? post.getPostPolicy().getAllowComment() : null)
//                .allowLike(post.getPostPolicy() != null ? post.getPostPolicy().getAllowLike() : null)
//                .allowSearch(post.getPostPolicy() != null ? post.getPostPolicy().getAllowSearch() : null)
//                .blogTitle(userInfo.getBlogTitle())
//                .nickname(userInfo.getNickname())
//                .profileImageUrl(userInfo.getProfileImageUrl())
//                .likeCount(likeCount)
//                .isLiked(isLiked)
////                .comments(comments)
//                .build();
//    }

}