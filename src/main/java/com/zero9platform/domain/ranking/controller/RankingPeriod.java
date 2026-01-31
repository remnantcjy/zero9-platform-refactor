package com.zero9platform.domain.ranking.controller;

import lombok.Getter;

@Getter
public enum RankingPeriod {

    REALTIME, // 기본값 (메인, 상시 노출)
    DAILY,
    WEEKLY,
    MONTHLY
}
