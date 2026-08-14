package com.dajeom.type;

/**
 * 정밀화 5차-2.
 *
 * 월령 지장간의 투출 형태를
 * 구조적인 우선순위로 구분한다.
 *
 * 주의:
 *
 * 이 우선순위는
 * "격국 최종 우선순위"가 아니다.
 *
 * 오직 월령 지장간이
 * 천간으로 얼마나 명확하게 드러났는지에 대한
 * Exposure Priority다.
 *
 * 실제 격국 성립 여부는
 * 정밀화 5차-3 이후에서 판정한다.
 */
public enum StructureExposurePriority {

    /**
     * 월령 정기가 월간으로 직접 투출.
     *
     * 월령 본기와 월간이 정확히 연결되는
     * 가장 직접적인 투출 형태.
     */
    MAIN_QI_MONTH_STEM(
            "정기 월간 투출",
            900,
            true,
            true
    ),

    /**
     * 월령 정기가 년간 또는 시간으로 투출.
     */
    MAIN_QI_OTHER_STEM(
            "정기 타간 투출",
            800,
            true,
            true
    ),

    /**
     * 월령 중기가 월간으로 직접 투출.
     */
    MIDDLE_QI_MONTH_STEM(
            "중기 월간 투출",
            700,
            false,
            true
    ),

    /**
     * 월령 여기가 월간으로 직접 투출.
     */
    RESIDUAL_QI_MONTH_STEM(
            "여기 월간 투출",
            650,
            false,
            true
    ),

    /**
     * 월령 중기가 년간 또는 시간으로 투출.
     */
    MIDDLE_QI_OTHER_STEM(
            "중기 타간 투출",
            600,
            false,
            true
    ),

    /**
     * 월령 여기가 년간 또는 시간으로 투출.
     */
    RESIDUAL_QI_OTHER_STEM(
            "여기 타간 투출",
            550,
            false,
            true
    ),

    /**
     * 월령 정기이지만 천간으로 투출하지 않음.
     *
     * 투출 우선순위만 보면 투간 후보보다 아래지만,
     * 월령 본기의 권위 자체는 사라지지 않는다.
     */
    MAIN_QI_HIDDEN(
            "정기 미투출",
            400,
            true,
            false
    ),

    /**
     * 월령 중기 미투출.
     */
    MIDDLE_QI_HIDDEN(
            "중기 미투출",
            300,
            false,
            false
    ),

    /**
     * 월령 여기 미투출.
     */
    RESIDUAL_QI_HIDDEN(
            "여기 미투출",
            200,
            false,
            false
    );

    private final String koreanName;

    private final int rank;

    private final boolean mainQi;

    private final boolean exposed;

    StructureExposurePriority(
            String koreanName,
            int rank,
            boolean mainQi,
            boolean exposed
    ) {

        this.koreanName =
                koreanName;

        this.rank =
                rank;

        this.mainQi =
                mainQi;

        this.exposed =
                exposed;
    }

    public String koreanName() {

        return koreanName;
    }

    /**
     * 큰 값일수록
     * 투출 근거가 직접적이다.
     *
     * 확률이나 세력 점수가 아니다.
     */
    public int rank() {

        return rank;
    }

    public boolean mainQi() {

        return mainQi;
    }

    public boolean exposed() {

        return exposed;
    }

    public boolean monthStemExposure() {

        return this == MAIN_QI_MONTH_STEM
                || this == MIDDLE_QI_MONTH_STEM
                || this == RESIDUAL_QI_MONTH_STEM;
    }

    public boolean otherStemExposure() {

        return this == MAIN_QI_OTHER_STEM
                || this == MIDDLE_QI_OTHER_STEM
                || this == RESIDUAL_QI_OTHER_STEM;
    }

    public boolean hiddenOnly() {

        return !exposed;
    }

    public boolean secondaryQi() {

        return !mainQi;
    }
}