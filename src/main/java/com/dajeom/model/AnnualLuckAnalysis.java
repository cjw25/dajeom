package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TenGod;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 특정 연도의 세운 분석 결과.
 *
 * 포함:
 *
 * - 세운 간지
 * - 세운 시작/종료 입춘 시각
 * - 세운 천간의 십성
 * - 세운 지지 정기
 * - 세운 지지 정기의 십성
 *
 * 아직:
 *
 * - 원국과의 합충
 * - 대운과의 합충
 * - 용신과의 관계
 * - 재물/직업/연애 해석
 *
 * 등은 포함하지 않는다.
 *
 * 해당 분석은 15차의
 * LuckInteraction 계층에서 수행한다.
 */
public record AnnualLuckAnalysis(

        /**
         * 사용자가 요청한 세운 연도.
         */
        int targetYear,

        /**
         * 해당 세운의 기간 및 간지.
         */
        AnnualLuckPeriod annualLuck,

        /**
         * 세운 천간이
         * 원국 일간 기준으로 가지는 십성.
         */
        TenGod stemTenGod,

        /**
         * 세운 지지의 정기(본기).
         */
        HeavenlyStem branchMainStem,

        /**
         * 세운 지지 정기가
         * 원국 일간 기준으로 가지는 십성.
         */
        TenGod branchMainTenGod,

        String ruleVersion

) {

    public AnnualLuckAnalysis {

        Objects.requireNonNull(
                annualLuck,
                "세운 정보는 필수입니다."
        );

        Objects.requireNonNull(
                stemTenGod,
                "세운 천간 십성은 필수입니다."
        );

        Objects.requireNonNull(
                branchMainStem,
                "세운 지지 정기는 필수입니다."
        );

        Objects.requireNonNull(
                branchMainTenGod,
                "세운 지지 정기 십성은 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "Rule 버전은 필수입니다."
        );

        if (
                targetYear
                        != annualLuck.sajuYear()
        ) {

            throw new IllegalArgumentException(
                    "요청 연도와 세운 연도가 일치해야 합니다."
            );
        }
    }

    public Pillar pillar() {

        return annualLuck.pillar();
    }

    public String chineseName() {

        return annualLuck.chineseName();
    }

    public String koreanName() {

        return annualLuck.koreanName();
    }

    public ZonedDateTime startDateTime() {

        return annualLuck.startDateTime();
    }

    public ZonedDateTime endDateTime() {

        return annualLuck.endDateTime();
    }

    /**
     * 특정 시각이 이 세운에
     * 해당하는지 확인한다.
     */
    public boolean isActiveAt(
            ZonedDateTime dateTime
    ) {

        return annualLuck.contains(
                dateTime
        );
    }
}