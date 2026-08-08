package com.dajeom.type;

/**
 * 명리 분석 결과의 신뢰 수준.
 *
 * HIGH:
 * 현재 구현된 조건 안에서 근거가 충분함
 *
 * MEDIUM:
 * 주요 근거는 있으나 추가 분석 여지가 있음
 *
 * LOW:
 * 출생시간 미상 또는 미구현 분석 요소 때문에
 * 확정적으로 사용하기 어려움
 */
public enum AnalysisConfidence {

    HIGH(
            "높음"
    ),

    MEDIUM(
            "보통"
    ),

    LOW(
            "낮음"
    );

    private final String koreanName;

    AnalysisConfidence(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}