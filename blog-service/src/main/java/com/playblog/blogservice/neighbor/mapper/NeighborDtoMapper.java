package com.playblog.blogservice.neighbor.mapper;

import com.playblog.blogservice.neighbor.Entity.Neighbor;
import com.playblog.blogservice.neighbor.Entity.NeighborStatus;
import com.playblog.blogservice.neighbor.dto.*;
import com.playblog.blogservice.userInfo.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.playblog.blogservice.neighbor.Entity.NeighborStatus.ACCEPTED;

@Component
@RequiredArgsConstructor
public class NeighborDtoMapper {

    public MyAddedForMeNeighborDto toMyAddedDto(Neighbor neighbor, Neighbor reverse) {
        UserInfo other = neighbor.getToUserInfo();
        boolean isMutual = neighbor.getStatus() == ACCEPTED &&
                reverse != null &&
                reverse.getStatus() == ACCEPTED;

        return new MyAddedForMeNeighborDto(
                other.getId(),
                other.getNickname(),
                other.getProfileIntro(),
                neighbor.getRequestedAt(),
                neighbor.getStatus().name(),
                isMutual
        );
    }

    public List<MyAddedToMeNeighborDto> toMyReceivedDto(List<Neighbor> neighbors, List<Neighbor> reverseNeighbors) {
        Map<Long, Neighbor> reverseMap = reverseNeighbors.stream()
                .collect(Collectors.toMap(
                        n -> n.getToUserInfo().getId(), // 내가 이웃 추가한 대상 ID
                        n -> n,
                        (existing, duplicate) -> existing
                ));

        return neighbors.stream()
                .map(n -> {
                    Long fromUserId = n.getFromUserInfo().getId();
                    Neighbor reverse = reverseMap.get(fromUserId);

                    boolean isMutual = n.getStatus() == NeighborStatus.ACCEPTED &&
                            reverse != null &&
                            reverse.getStatus() == NeighborStatus.ACCEPTED;

                    return new MyAddedToMeNeighborDto(
                            fromUserId,
                            n.getFromUserInfo().getNickname(),
                            n.getFromUserInfo().getProfileIntro(),
                            n.getRequestedAt(),
                            n.getStatus().name(),
                            isMutual
                    );
                })
                .toList();
    }

    public ReceivedMutualNeighborDto toReceivedMutualDto(Neighbor neighbor) {
        UserInfo user = neighbor.getFromUserInfo();
        return new ReceivedMutualNeighborDto(
                user.getId(),
                user.getNickname(),
                neighbor.getRequestedAt()
        );
    }

    public SentMutualNeighborDto toSentMutualDto(Neighbor neighbor) {
        UserInfo user = neighbor.getToUserInfo();
        return new SentMutualNeighborDto(
                user.getId(),
                user.getNickname(),
                neighbor.getRequestedAt()
        );
    }

    public MyAddedForLoginModalNeighborDto toMyAddedLoginDto(Neighbor neighbor) {
        UserInfo toUser = neighbor.getToUserInfo();
        return new MyAddedForLoginModalNeighborDto(
                toUser.getId(),
                toUser.getBlogId(),
                toUser.getProfileImageUrl()
        );
    }

    public BlockedForMeNeighborDto blockedForMeNeighborDto(Neighbor neighbor){
        UserInfo toUser = neighbor.getToUserInfo();
        return new BlockedForMeNeighborDto(
                toUser.getId(),
                toUser.getNickname(),
                toUser.getBlogId()
        );
    }
    public LoginUserDto toLoginUserDto(UserInfo loginUser) {
        return new LoginUserDto(
                loginUser.getId(),
                loginUser.getNickname(),
                loginUser.getProfileIntro()
        );
    }

    public insertPopup toInsertPopup(UserInfo addUser) {
        return new insertPopup(
                addUser.getId(),
                addUser.getNickname()
        );
    }
}
