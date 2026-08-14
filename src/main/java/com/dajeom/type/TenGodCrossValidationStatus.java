package com.dajeom.type;

/**
 * 정밀화 10차-9.
 *
 * 핵심 십성 Pattern의 교차검증 상태.
 *
 * =================================================
 *
 * SUPPORTED
 *
 * 10-8 Pattern이 다른 명리 분석과도
 * 비교적 일관되게 뒷받침되는 상태.
 *
 * =================================================
 *
 * QUALIFIED
 *
 * Pattern 자체는 유지되지만
 * 신강신약·격국·용신·합화 등에 따라
 * 조건부 해석이 필요한 상태.
 *
 * =================================================
 *
 * WEAKENED
 *
 * Pattern은 존재하지만
 * 합화·격국 불안정 등으로
 * 실제 작용력이 약화된 상태.
 *
 * =================================================
 *
 * CONFLICTED
 *
 * 서로 다른 분석축 사이에
 * 명확한 충돌이 존재하는 상태.
 *
 * =================================================
 *
 * 이 상태는 길흉 등급이 아니다.
 */
public enum TenGodCrossValidationStatus {

    SUPPORTED(
            "지지됨",
            4
    ),

    QUALIFIED(
            "조건부",
            3
    ),

    WEAKENED(
            "약화",
            2
    ),

    CONFLICTED(
            "충돌",
            1
    );

    private final String koreanName;

    private final int rank;

    TenGodCrossValidationStatus(
            String koreanName,
            int rank
    ) {

        this.koreanName =
                koreanName;

        this.rank =
                rank;
    }

    public String koreanName() {

        return koreanName;
    }

    public int rank() {

        return rank;
    }

    public boolean supported() {

        return this
                == SUPPORTED;
    }

    public boolean qualified() {

        return this
                == QUALIFIED;
    }

    public boolean weakened() {

        return this
                == WEAKENED;
    }

    public boolean conflicted() {

        return this
                == CONFLICTED;
    }

    public boolean requiresQualification() {

        return qualified()
                || weakened()
                || conflicted();
    }

    /**
     * Pattern 자체는 어떤 상태에서도 삭제하지 않는다.
     *
     * CONFLICTED도 "없음"이 아니라
     * "해석축 사이의 충돌이 있음"을 뜻한다.
     */
    public boolean patternRetained() {

        return true;
    }
}