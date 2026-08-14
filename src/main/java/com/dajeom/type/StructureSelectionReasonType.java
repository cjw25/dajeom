package com.dajeom.type;

/**
 * 정밀화 5차-9.
 *
 * 특정 격 후보가:
 *
 * - 주격
 * - 보조격
 * - 보류
 * - 제외
 *
 * 역할을 받은 주요 근거.
 *
 * 사용자 출력용 문장 자체가 아니라
 * 엔진 내부 Explainability Evidence다.
 */
public enum StructureSelectionReasonType {

    EXACT_MONTH_SPECIAL(
            "월지 특수조건 정확 일치"
    ),

    STRICT_FOLLOW_STRUCTURE(
            "엄격한 종격 후보 조건 충족"
    ),

    MAIN_QI_AUTHORITY(
            "월령 정기 권위"
    ),

    MONTH_STEM_EXPOSURE(
            "월간 직접 투출"
    ),

    OTHER_STEM_EXPOSURE(
            "년간·시간 투출"
    ),

    ESTABLISHED_STRUCTURE(
            "격 성립 근거 강함"
    ),

    SUPPORTED_STRUCTURE(
            "격 성립 근거 있음"
    ),

    WEAK_STRUCTURE(
            "격 성립 근거 약함"
    ),

    BALANCE_ACCEPTABLE(
            "일간 강약과 수용 가능"
    ),

    BALANCE_BURDENED(
            "일간 강약상 부담"
    ),

    FORMATION_SUPPORT_PRESENT(
            "성격 요소 존재"
    ),

    STRONG_FORMATION_SUPPORT(
            "강한 성격 요소 존재"
    ),

    NO_MEANINGFUL_DAMAGE(
            "의미 있는 파격 요소 없음"
    ),

    MEANINGFUL_DAMAGE_PRESENT(
            "파격 요소 존재"
    ),

    STRONG_DAMAGE_PRESENT(
            "강한 파격 요소 존재"
    ),

    RELATIVELY_PURE(
            "격 구조가 비교적 선명"
    ),

    HIGHLY_MIXED(
            "격 구조 혼잡도가 높음"
    ),

    SPECIAL_REVIEW_PENDING(
            "특수 월령 구조 추가 검토 필요"
    ),

    INACTIVE_CANDIDATE(
            "현재 비활성 후보"
    );

    private final String koreanName;

    StructureSelectionReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }
}