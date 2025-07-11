package com.playblog.blogservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum SubTopic {
    BOOKS("문학.책", TopicType.ENTERTAIN),
    MOVIES("영화",  TopicType.ENTERTAIN),
    ARTS("미술.디자인", TopicType.ENTERTAIN),
    THOUGHTS("일상, 생각", TopicType.LIFE),
    MARRIAGE("육아.결혼", TopicType.LIFE),
    PETS("반려동물", TopicType.LIFE),
    GAMES("게임", TopicType.HOBBY),
    SPORTS("스포츠", TopicType.HOBBY),
    PHOTO("사진", TopicType.HOBBY),
    COMPUTER("IT.컴퓨터", TopicType.KNOWLEDGE),
    SOCIETY("사회.정치", TopicType.KNOWLEDGE),
    HEALTH("건강.의학", TopicType.KNOWLEDGE);

    private final String subtopicName;
    private final TopicType mainTopic;
}
