package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;

import java.util.Objects;

/**
 * 사주의 한 기둥을 나타낸다.
 *
 * 천간 1개와 지지 1개로 구성된다.
 */
public record Pillar(
        HeavenlyStem stem,
        EarthlyBranch branch
) {

    public Pillar {
        Objects.requireNonNull(
                stem,
                "천간은 필수입니다."
        );

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );
    }

    public String chineseName() {
        return stem.getChineseCharacter()
                + branch.getChineseCharacter();
    }

    public String koreanName() {
        return stem.getKoreanName()
                + branch.getKoreanName();
    }
}