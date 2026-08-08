package com.dajeom.model;

import com.dajeom.type.TenGod;

import java.util.Objects;

/**
 * 하나의 지장간에 대한 기초 분석 결과.
 *
 * 예:
 *
 * 申
 *
 * 戊 -> 상관
 * 壬 -> 정관
 * 庚 -> 정재
 *
 * 와 같이 일간을 기준으로
 * 각 지장간의 십성을 함께 보관한다.
 */
public record HiddenStemAnalysis(

        HiddenStem hiddenStem,

        TenGod tenGod

) {

    public HiddenStemAnalysis {

        Objects.requireNonNull(
                hiddenStem,
                "지장간은 필수입니다."
        );

        Objects.requireNonNull(
                tenGod,
                "지장간 십성은 필수입니다."
        );
    }

    public String stemKoreanName() {

        return hiddenStem
                .stem()
                .getKoreanName();
    }

    public String stemChineseName() {

        return hiddenStem
                .stem()
                .getChineseCharacter();
    }

    public String roleKoreanName() {

        return hiddenStem
                .role()
                .getKoreanName();
    }

    public String tenGodKoreanName() {

        return tenGod
                .getKoreanName();
    }
}