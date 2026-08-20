package com.dajeom.type;

/**
 * 하나의 세운 Functional 기준선에서
 * 대운 Segment 결합 상태를 나타낸다.
 *
 * =================================================
 *
 * DECADE_SEGMENT_UNAVAILABLE
 *
 * 대운 Sequence / Segment 결합을
 * 사용할 수 없는 상태.
 *
 * 그러나 Annual Functional 자체가
 * 사용할 수 없다는 뜻은 아니다.
 *
 * =================================================
 *
 * ANNUAL_ONLY
 *
 * 14-5 분석은 가능하지만
 * 해당 AnnualLuckPeriod와 실제 겹치는
 * DecadeLuckPeriod가 없는 상태.
 *
 * 예:
 *
 * - 첫 대운 시작 전
 * - 생성된 대운 범위 이후
 *
 * =================================================
 *
 * SINGLE_DECADE_SEGMENT
 *
 * 하나의 대운 Segment와 결합.
 *
 * =================================================
 *
 * MULTIPLE_DECADE_SEGMENTS
 *
 * 세운 기간 중 교운 또는
 * 교운시점 불확실성으로
 * 둘 이상의 대운 Segment와 결합.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 상태는 길흉이나 강도를 의미하지 않는다.
 *
 * 단지 세운 Functional 결과가
 * 어떤 시간 Segment 구조를 갖는지 나타낸다.
 */
public enum AnnualFunctionalBaselineStatus {

    DECADE_SEGMENT_UNAVAILABLE,

    ANNUAL_ONLY,

    SINGLE_DECADE_SEGMENT,

    MULTIPLE_DECADE_SEGMENTS;

    public boolean decadeSegmentAvailable() {

        return this != DECADE_SEGMENT_UNAVAILABLE;
    }

    public boolean annualOnly() {

        return this == ANNUAL_ONLY;
    }

    public boolean hasDecadeSegment() {

        return this == SINGLE_DECADE_SEGMENT
                || this == MULTIPLE_DECADE_SEGMENTS;
    }

    public boolean multipleDecadeSegments() {

        return this == MULTIPLE_DECADE_SEGMENTS;
    }

    public static AnnualFunctionalBaselineStatus of(

            boolean decadeSegmentAvailable,

            int segmentCount

    ) {

        if (
                segmentCount < 0
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Segment 수는 음수일 수 없습니다."
            );
        }

        if (
                !decadeSegmentAvailable
        ) {

            if (
                    segmentCount != 0
            ) {

                throw new IllegalArgumentException(
                        "대운 Segment 분석 불가 상태에서는 Segment가 존재할 수 없습니다."
                );
            }

            return DECADE_SEGMENT_UNAVAILABLE;
        }

        if (
                segmentCount == 0
        ) {

            return ANNUAL_ONLY;
        }

        if (
                segmentCount == 1
        ) {

            return SINGLE_DECADE_SEGMENT;
        }

        return MULTIPLE_DECADE_SEGMENTS;
    }
}