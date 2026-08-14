package com.dajeom.type;

/**
 * 정밀화 5차-3.
 *
 * 월령에서 추출된 하나의 격 후보가
 * 현재 원국 구조에서 어느 정도
 * 성립 근거를 가지고 있는지를 나타낸다.
 *
 * 주의:
 *
 * 이 단계는 최종 격국 확정이 아니다.
 *
 * ESTABLISHED
 * 격 후보의 성립 근거가 매우 명확함.
 *
 * SUPPORTED
 * 격 후보가 실제로 작동할 근거가 있음.
 *
 * WEAK
 * 후보 자체는 존재하지만
 * 현재 원국에서 표현 근거가 약함.
 *
 * INACTIVE
 * 월령 내부에는 존재하지만
 * 현재 격 후보로 적극적으로 사용하기 어려움.
 */
public enum StructureEstablishmentLevel {

    ESTABLISHED(
            "성립 근거 강함",
            4
    ),

    SUPPORTED(
            "성립 가능",
            3
    ),

    WEAK(
            "약한 후보",
            2
    ),

    INACTIVE(
            "비활성 후보",
            1
    );

    private final String koreanName;

    /**
     * 정렬용 순위.
     *
     * 확률이나 세력 점수가 아니다.
     */
    private final int rank;

    StructureEstablishmentLevel(
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

    public boolean established() {

        return this == ESTABLISHED;
    }

    public boolean supported() {

        return this == SUPPORTED;
    }

    public boolean weak() {

        return this == WEAK;
    }

    public boolean inactive() {

        return this == INACTIVE;
    }

    /**
     * 이후 격국 분석 단계에서
     * 실제 후보군으로 계속 검토할 가치가 있는가.
     */
    public boolean activeCandidate() {

        return this == ESTABLISHED
                || this == SUPPORTED
                || this == WEAK;
    }

    /**
     * 성립 근거가 비교적 충분한 후보.
     */
    public boolean meaningfulCandidate() {

        return this == ESTABLISHED
                || this == SUPPORTED;
    }
}