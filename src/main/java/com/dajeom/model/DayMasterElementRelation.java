package com.dajeom.model;

import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.Objects;

/**
 * 일간과 특정 오행 사이의 관계.
 *
 * 예:
 *
 * 일간 丁火
 * 대상 木
 *
 * 木生火
 *
 * 따라서:
 *
 * role = RESOURCE
 * koreanName = 인성
 *
 * elementPower는 정밀화 3차에서
 * 이미 계산된 해당 오행의 최종 상대 세력이다.
 *
 * 이 모델에서는 아직:
 *
 * - 신강
 * - 신약
 * - 중화
 *
 * 를 판정하지 않는다.
 */
public record DayMasterElementRelation(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        FiveElement targetElement,

        DayMasterElementRole role,

        int elementPower,

        int shareBasisPoints

) {

    public DayMasterElementRelation {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                targetElement,
                "대상 오행이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "일간 기준 오행 역할이 필요합니다."
        );

        if (
                elementPower < 0
        ) {

            throw new IllegalArgumentException(
                    "오행 세력은 음수가 될 수 없습니다."
            );
        }

        if (
                shareBasisPoints < 0
                        || shareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "오행 비중은 0~10000bp 사이여야 합니다."
            );
        }
    }

    /**
     * 일간 자신과 같은 오행.
     */
    public boolean companion() {

        return role.companion();
    }

    /**
     * 일간을 생하는 인성 오행.
     */
    public boolean resource() {

        return role.resource();
    }

    /**
     * 일간이 생하는 식상 오행.
     */
    public boolean output() {

        return role.output();
    }

    /**
     * 일간이 극하는 재성 오행.
     */
    public boolean wealth() {

        return role.wealth();
    }

    /**
     * 일간을 극하는 관살 오행.
     */
    public boolean officer() {

        return role.officer();
    }

    /**
     * 비겁 또는 인성.
     *
     * 아직 이 값만으로
     * 신강을 판정하지 않는다.
     */
    public boolean supportive() {

        return role.supportive();
    }

    /**
     * 식상 또는 재성.
     */
    public boolean consuming() {

        return role.consuming();
    }

    /**
     * 관살.
     */
    public boolean controlling() {

        return role.controlling();
    }

    public double sharePercent() {

        return shareBasisPoints
                / 100.0;
    }

    public boolean exists() {

        return elementPower > 0;
    }
}