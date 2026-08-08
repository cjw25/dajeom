package com.dajeom.model;

import com.dajeom.type.PillarPosition;
import com.dajeom.type.TenGod;

import java.util.List;
import java.util.Objects;

/**
 * 사주 한 기둥의 기초 분석 결과.
 *
 * 포함 내용:
 *
 * - 기둥 위치
 * - 간지
 * - 천간 십성
 * - 지장간
 * - 지장간별 십성
 *
 * 아직 합충형파해,
 * 신강신약 등의 판단은 포함하지 않는다.
 */
public record PillarAnalysis(

        PillarPosition position,

        Pillar pillar,

        TenGod stemTenGod,

        List<HiddenStemAnalysis> hiddenStems

) {

    public PillarAnalysis {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        Objects.requireNonNull(
                pillar,
                "기둥은 필수입니다."
        );

        Objects.requireNonNull(
                stemTenGod,
                "천간 십성은 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStems,
                "지장간 분석 결과는 필수입니다."
        );

        hiddenStems =
                List.copyOf(
                        hiddenStems
                );
    }

    public String chineseName() {

        return pillar.chineseName();
    }

    public String koreanName() {

        return pillar.koreanName();
    }
}