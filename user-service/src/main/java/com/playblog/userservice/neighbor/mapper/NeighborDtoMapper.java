package com.playblog.userservice.neighbor.mapper;


import com.playblog.userservice.neighbor.Entity.Neighbor;
import com.playblog.userservice.neighbor.Entity.UserInfo;
import com.playblog.userservice.neighbor.dto.ReceivedMutualNeighborDto;
import com.playblog.userservice.neighbor.dto.MyAddedForMeNeighborDto;
import com.playblog.userservice.neighbor.dto.MyAddedToMeNeighborDto;
import com.playblog.userservice.neighbor.dto.SentMutualNeighborDto;

public class NeighborDtoMapper {

    public static MyAddedForMeNeighborDto toMyAddedDto(Neighbor neighbor) {
        UserInfo user = neighbor.getToUserInfo();
        return new MyAddedForMeNeighborDto(
                user.getBlogTitle(),
                user.getNickname(),
//        최근 게시글        toUser.getUser().get
                neighbor.getFollowedAt()
        );
    }

    public static MyAddedToMeNeighborDto toMyReceivedDto(Neighbor neighbor) {
        UserInfo user = neighbor.getFromUserInfo();
        return new MyAddedToMeNeighborDto(
            user.getBlogTitle(),
            user.getNickname(),
            neighbor.getFollowedAt()
        );
    }

    public static ReceivedMutualNeighborDto toReceivedMutualDto(Neighbor neighbor) {
        UserInfo user = neighbor.getToUserInfo(); // 또는 fromUser 판단
        return new ReceivedMutualNeighborDto(
            user.getBlogId(),
            neighbor.getFollowedAt()
        );
    }

    public static SentMutualNeighborDto toSentMutualDto(Neighbor neighbor) {
        UserInfo user = neighbor.getToUserInfo(); // 또는 fromUser 판단
        return new SentMutualNeighborDto(
            user.getBlogId(),
            neighbor.getFollowedAt()
        );
    }
}
