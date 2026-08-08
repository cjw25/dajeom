package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;

import java.util.Objects;

/**
 * 사주의 한 기둥을 나타낸다.
 *
 * 예:
 * 癸酉
 * 庚申
 * 丁丑
 * 甲辰
 *
 * 년주, 월주, 일주, 시주가 모두
 * 이 모델을 공통으로 사용한다.
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

    /**
     * 한자 간지.
     *
     * 예:
     * 癸酉
     */
    public String chineseName() {

        return stem.getChineseCharacter()
                + branch.getChineseCharacter();
    }

    /**
     * 한글 간지.
     *
     * 예:
     * 계유
     */
    public String koreanName() {

        return stem.getKoreanName()
                + branch.getKoreanName();
    }

    /**
     * 보기 편한 형태.
     *
     * 예:
     * 계유(癸酉)
     */
    public String displayName() {

        return koreanName()
                + "("
                + chineseName()
                + ")";
    }
}