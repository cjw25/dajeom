package com.dajeom.type;

/**
 * 정밀화 18차-1.
 *
 * 생활영역 분석에서 Evidence를 읽는 우선 계층.
 *
 * =================================================
 *
 * CORE_STRUCTURE
 *
 * 오행세력
 * 신강신약
 * 격국
 * 조후·병약·통관
 * 용신
 * 십성 복합구조
 *
 * 가장 먼저 본다.
 *
 * =================================================
 *
 * STRUCTURAL_CONTEXT
 *
 * 12운성
 * 궁위
 * 원국 관계
 * 합화/변환
 *
 * 핵심구조가 어느 위치와 관계에서
 * 작동하는지 보완한다.
 *
 * =================================================
 *
 * TEMPORAL_CONTEXT
 *
 * 대운
 * 세운
 * 월운
 *
 * 현재 시점에서 어떤 구조가 활성화되는지 본다.
 *
 * =================================================
 *
 * AUXILIARY
 *
 * 신살.
 *
 * 다른 구조적 Evidence를 보조한다.
 *
 * =================================================
 *
 * 이 Enum은 점수 가중치가 아니다.
 */
public enum LifeDomainEvidenceTier {

    CORE_STRUCTURE(
            "핵심 구조"
    ),

    STRUCTURAL_CONTEXT(
            "구조적 맥락"
    ),

    TEMPORAL_CONTEXT(
            "운 시점 맥락"
    ),

    AUXILIARY(
            "보조 근거"
    );

    private final String koreanName;

    LifeDomainEvidenceTier(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String koreanName() {
        return koreanName;
    }

    /**
     * ordinal 순서는 Evidence 해석 우선순위일 뿐
     * 수치 가중치가 아니다.
     */
    public boolean precedes(
            LifeDomainEvidenceTier other
    ) {

        return other != null
                && ordinal() < other.ordinal();
    }

    public boolean auxiliary() {
        return this == AUXILIARY;
    }
}