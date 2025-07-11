package com.playblog.blogservice.neighbor.Entity;

public enum NeighborStatus {
    /* 서로 이웃 요청 받기 전 */
    REQUESTED,
    /* 서로 이웃 거절 */
    REJECTED,
    /* 서로 이웃 */
    ACCEPTED,
    /* 차단한 이웃 */
    REMOVED
}
