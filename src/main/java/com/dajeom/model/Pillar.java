package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;

import java.util.Objects;

/**
 * 사주의 한 기둥을 나타내는 불변 Value Object.
 *
 * 예:
 *
 * 癸酉
 * 庚申
 * 丁丑
 * 甲辰
 *
 * 년주 / 월주 / 일주 / 시주가 공통으로 사용한다.
 *
 * =================================================
 * 정밀화 호환 정책
 * =================================================
 *
 * record component는 기존 호환성을 위해:
 *
 * stem
 * branch
 *
 * 를 유지한다.
 *
 * 따라서 기존 코드의:
 *
 * pillar.stem()
 * pillar.branch()
 *
 * 역시 계속 사용할 수 있다.
 *
 * =================================================
 *
 * 정밀화 계층에서는 의미가 더 분명한:
 *
 * pillar.heavenlyStem()
 * pillar.earthlyBranch()
 *
 * accessor를 사용한다.
 *
 * 두 방식은 같은 객체를 반환한다.
 *
 * 별도의 천간/지지를 새로 생성하지 않는다.
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
     * 정밀화용 명시적 천간 accessor.
     *
     * 기존 record accessor인 stem()과
     * 정확히 같은 객체를 반환한다.
     */
    public HeavenlyStem heavenlyStem() {

        return stem;
    }

    /**
     * 정밀화용 명시적 지지 accessor.
     *
     * 기존 record accessor인 branch()와
     * 정확히 같은 객체를 반환한다.
     */
    public EarthlyBranch earthlyBranch() {

        return branch;
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
     * 표시용.
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

    @Override
    public String toString() {

        return displayName();
    }
}