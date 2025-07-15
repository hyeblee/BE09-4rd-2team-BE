package com.playblog.blogservice.post.dto;

import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.common.entity.TopicType;
import com.playblog.blogservice.post.entity.Post;
import com.playblog.blogservice.post.entity.PostPolicy;
import com.playblog.blogservice.post.entity.PostVisibility;
import com.playblog.blogservice.user.User;
import com.playblog.blogservice.userInfo.UserInfo;
import lombok.*;

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

    /* Update에 들어갈 추가 Dto */
    private TopicType mainTopic;
    private SubTopic subTopic;
    private String thumbnailImageUrl;

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

// 백에서 DTO 호출시 보이거나 동작하지 않는건 갖고 오지 않는다.
// 엔터티는 DB용일뿐 그이상 그이하도 아니다.

    // DTO

    // Post 엔티티가 policy를 가지고 있지 않은 단방향 구조라면, DTO 생성 시점에 서비스 레이어에서
    // PostPolicy를 별도로 조회해서 함께 넘겨줘야 합니다. 즉, from(...) 시그니처를 이렇게 바꿔주세요.
    public static PostResponseDto from(
            Post post,
            PostPolicy policy,      // 추가
            UserInfo userInfo,
            Long likeCount,
            Boolean isLiked
    ) {
        return PostResponseDto.builder()
                /* 게시글 기본 */
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .visibility(post.getVisibility())

                /* 정책 필드 */
                .allowComment(policy != null ? policy.getAllowComment() : false)
                .allowLike   (policy != null ? policy.getAllowLike()    : false)
                .allowSearch (policy != null ? policy.getAllowSearch()  : false)

                /* 작성자 정보 */
                .blogTitle(userInfo != null ? userInfo.getBlogTitle() : "임시 블로그")
                .nickname (userInfo != null ? userInfo.getNickname()  : "임시 닉네임")
                .profileImageUrl(userInfo != null ? userInfo.getProfileImageUrl() : null)

                /* 좋아요 */
                .likeCount(likeCount  != null ? likeCount  : 0L)
                .isLiked  (isLiked    != null ? isLiked    : false)

                /* 주제·썸네일 */
                .mainTopic(post.getMainTopic())
                .subTopic (post.getSubTopic())
                .thumbnailImageUrl(post.getThumbnailImageUrl())

                .build();

    }

    /* 정책포함 상세조회용 Dto 빌더 */
    public PostResponseDto toResponse(Post post, PostPolicy policy, User user) {
        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .visibility(policy.getPost().getVisibility()) // ← 여기 추가
                .allowComment(policy.getAllowComment())
                .allowLike(policy.getAllowLike())
                .allowSearch(policy.getAllowSearch())
                .blogTitle(user.getUserInfo().getBlogTitle())
                .nickname(user.getUserInfo().getNickname())
                .profileImageUrl(user.getUserInfo().getProfileImageUrl())
                .build();
    }

}