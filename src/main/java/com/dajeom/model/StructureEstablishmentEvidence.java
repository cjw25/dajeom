package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.StructureEstablishmentLevel;
import com.dajeom.type.StructureTenGodType;

import java.util.Objects;

/**
 * 정밀화 5차-3.
 *
 * 하나의 월령 격 후보에 대한
 * 실제 성립도 Evidence.
 *
 * 포함 정보:
 *
 * - 정기/중기/여기
 * - 월령 source 권위
 * - 투출 우선순위
 * - 월간 투출 여부
 * - 타간 투출 여부
 * - 반복 투출 여부
 * - 월령 내부 원래 비중
 * - 해당 오행의 3차 최종 세력
 * - 전체 오행 중 해당 오행 비중
 * - 일반 내격 적용 가능 여부
 * - 특수 구조 검토 필요 여부
 *
 * 여기서 주격은 결정하지 않는다.
 */
public record StructureEstablishmentEvidence(

        StructureExposurePriorityEvidence
        exposureEvidence,

        FiveElement element,

        int elementFinalPower,

        int elementShareBasisPoints,

        boolean standardInnerStructureEligible,

        boolean specialStructureReviewRequired,

        StructureEstablishmentLevel level

) {

    public StructureEstablishmentEvidence {

        Objects.requireNonNull(
                exposureEvidence,
                "투출 우선순위 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                element,
                "격 후보의 오행이 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "격 성립도가 필요합니다."
        );

        if (
                elementFinalPower < 0
        ) {

            throw new IllegalArgumentException(
                    "오행 최종 세력은 음수가 될 수 없습니다."
            );
        }

        if (
                elementShareBasisPoints < 0
                        || elementShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "오행 비중은 0~10000bp 사이여야 합니다."
            );
        }

        if (
                standardInnerStructureEligible
                        && specialStructureReviewRequired
        ) {

            throw new IllegalArgumentException(
                    "일반 내격 후보와 특수 구조 검토 후보가 동시에 true일 수 없습니다."
            );
        }

        if (
                !standardInnerStructureEligible
                        && !specialStructureReviewRequired
        ) {

            throw new IllegalArgumentException(
                    "격 후보의 처리 방향이 지정되지 않았습니다."
            );
        }
    }

    public MonthStructureCandidate candidate() {

        return exposureEvidence
                .candidate();
    }

    public StructureTenGodType tenGod() {

        return candidate()
                .tenGod();
    }

    public boolean mainQi() {

        return candidate()
                .mainQi();
    }

    public boolean middleQi() {

        return candidate()
                .middleQi();
    }

    public boolean residualQi() {

        return candidate()
                .residualQi();
    }

    public boolean exposed() {

        return candidate()
                .exposed();
    }

    public boolean monthStemExposed() {

        return candidate()
                .exposedAtMonthStem();
    }

    public boolean otherStemExposed() {

        return candidate()
                .exposedAtYearStem()

                || candidate()
                .exposedAtHourStem();
    }

    public boolean repeatedExposure() {

        return candidate()
                .repeatedExposure();
    }

    public int hiddenStemWeight() {

        return candidate()
                .hiddenStemWeight();
    }

    public int sourceAuthorityRank() {

        return exposureEvidence
                .sourceAuthorityRank();
    }

    public int exposureRank() {

        return exposureEvidence
                .exposureRank();
    }

    public boolean established() {

        return level.established();
    }

    public boolean supported() {

        return level.supported();
    }

    public boolean weak() {

        return level.weak();
    }

    public boolean inactive() {

        return level.inactive();
    }

    public boolean activeCandidate() {

        return level.activeCandidate();
    }

    public boolean meaningfulCandidate() {

        return level.meaningfulCandidate();
    }

    /**
     * 일반 팔격 계열 후보로
     * 그대로 검토할 수 있는가.
     */
    public boolean standardStructureCandidate() {

        return standardInnerStructureEligible;
    }

    /**
     * 비견·겁재 월령처럼
     * 건록/양인 등 별도의 특수 구조 검토가
     * 필요한 후보인가.
     */
    public boolean specialStructureCandidate() {

        return specialStructureReviewRequired;
    }

    public double elementSharePercent() {

        return elementShareBasisPoints
                / 100.0;
    }
}