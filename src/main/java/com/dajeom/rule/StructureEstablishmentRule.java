package com.dajeom.rule;

import com.dajeom.model.StructureExposurePriorityEvidence;
import com.dajeom.type.StructureEstablishmentLevel;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 5차-3.
 *
 * 월령 정기·중기·여기 각각의
 * 격 성립도를 판정한다.
 *
 * 중요한 원칙:
 *
 * 정기
 * 중기
 * 여기
 *
 * 를 같은 기준으로 판정하지 않는다.
 *
 * ================================================
 *
 * 정기
 *
 * 월령 본기이므로
 * 미투출 상태에서도 기본 권위를 가진다.
 *
 * ================================================
 *
 * 중기
 *
 * 실제 천간 투출 여부가 중요하다.
 *
 * ================================================
 *
 * 여기
 *
 * 가장 보조적인 월령 source이므로
 * 더 명확한 투출·세력 근거가 필요하다.
 *
 * ================================================
 *
 * 또한 이 Rule은 주격을 확정하지 않는다.
 */
@Component
public class StructureEstablishmentRule {

    /**
     * 정기 미투출 상태에서도
     * 해당 오행이 전체 오행 세력의
     * 15% 이상이면 기본 성립 근거를
     * 비교적 유지한다고 본다.
     */
    private static final int
            MAIN_QI_HIDDEN_SHARE_THRESHOLD =
            1_500;

    /**
     * 중기 월간 투출 후보가
     * ESTABLISHED까지 올라가기 위한
     * 최소 오행 비중.
     */
    private static final int
            MIDDLE_QI_MONTH_SHARE_THRESHOLD =
            1_000;

    /**
     * 중기 미투출 후보가
     * 완전 INACTIVE가 아닌 WEAK로
     * 남기 위한 최소 오행 비중.
     */
    private static final int
            MIDDLE_QI_HIDDEN_SHARE_THRESHOLD =
            1_500;

    /**
     * 여기 월간 투출 후보가
     * 반복적으로 천간에 표현되고
     * 실제 오행 세력까지 충분한 경우
     * ESTABLISHED까지 허용한다.
     */
    private static final int
            RESIDUAL_QI_STRONG_SHARE_THRESHOLD =
            1_500;

    /**
     * 여기의 타간 투출을
     * SUPPORTED로 올릴 때 필요한
     * 최소 오행 비중.
     */
    private static final int
            RESIDUAL_QI_OTHER_SHARE_THRESHOLD =
            1_200;

    public StructureEstablishmentLevel determineLevel(

            StructureExposurePriorityEvidence evidence,

            int elementShareBasisPoints

    ) {

        Objects.requireNonNull(
                evidence,
                "격 후보 투출 Evidence가 필요합니다."
        );

        if (
                elementShareBasisPoints < 0
                        || elementShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "오행 비중은 0~10000bp 사이여야 합니다."
            );
        }

        if (
                evidence.mainQi()
        ) {

            return determineMainQiLevel(
                    evidence,
                    elementShareBasisPoints
            );
        }

        if (
                evidence.middleQi()
        ) {

            return determineMiddleQiLevel(
                    evidence,
                    elementShareBasisPoints
            );
        }

        if (
                evidence.residualQi()
        ) {

            return determineResidualQiLevel(
                    evidence,
                    elementShareBasisPoints
            );
        }

        throw new IllegalStateException(
                "알 수 없는 월령 지장간 역할입니다."
        );
    }

    /**
     * 정기 판정.
     */
    private StructureEstablishmentLevel
    determineMainQiLevel(

            StructureExposurePriorityEvidence evidence,

            int elementShareBasisPoints

    ) {

        /*
         * 월령 정기가 월간에 직접 투출.
         *
         * 매우 명확한 격 성립 Evidence.
         */
        if (
                evidence.monthStemExposed()
        ) {

            return StructureEstablishmentLevel
                    .ESTABLISHED;
        }

        /*
         * 정기가 년간 또는 시간에라도
         * 실제 투출했다면 강한 후보로 유지.
         */
        if (
                evidence.otherStemExposed()
        ) {

            return StructureEstablishmentLevel
                    .ESTABLISHED;
        }

        /*
         * 정기는 투출하지 않아도
         * 월령 본기라는 권위 자체가 존재한다.
         *
         * 해당 오행이 원국에서도
         * 일정 세력 이상이면 SUPPORTED.
         */
        if (
                elementShareBasisPoints
                        >= MAIN_QI_HIDDEN_SHARE_THRESHOLD
        ) {

            return StructureEstablishmentLevel
                    .SUPPORTED;
        }

        /*
         * 오행 전체 세력까지 약하더라도
         * 정기이므로 완전히 INACTIVE로 버리지 않는다.
         */
        return StructureEstablishmentLevel
                .WEAK;
    }

    /**
     * 중기 판정.
     */
    private StructureEstablishmentLevel
    determineMiddleQiLevel(

            StructureExposurePriorityEvidence evidence,

            int elementShareBasisPoints

    ) {

        /*
         * 중기가 월간에 직접 투출.
         *
         * 실제 오행 세력도 일정 수준 이상이면
         * 강한 성립 후보.
         */
        if (
                evidence.monthStemExposed()
        ) {

            if (
                    elementShareBasisPoints
                            >= MIDDLE_QI_MONTH_SHARE_THRESHOLD
            ) {

                return StructureEstablishmentLevel
                        .ESTABLISHED;
            }

            return StructureEstablishmentLevel
                    .SUPPORTED;
        }

        /*
         * 년간 또는 시간에 투출.
         *
         * 월간보다는 한 단계 낮게 둔다.
         */
        if (
                evidence.otherStemExposed()
        ) {

            return StructureEstablishmentLevel
                    .SUPPORTED;
        }

        /*
         * 미투출 중기는
         * 실제 오행 세력이 충분할 때만
         * 약한 후보로 유지한다.
         */
        if (
                elementShareBasisPoints
                        >= MIDDLE_QI_HIDDEN_SHARE_THRESHOLD
        ) {

            return StructureEstablishmentLevel
                    .WEAK;
        }

        return StructureEstablishmentLevel
                .INACTIVE;
    }

    /**
     * 여기 판정.
     */
    private StructureEstablishmentLevel
    determineResidualQiLevel(

            StructureExposurePriorityEvidence evidence,

            int elementShareBasisPoints

    ) {

        /*
         * 여기가 월간에 직접 투출.
         */
        if (
                evidence.monthStemExposed()
        ) {

            /*
             * 월간 투출
             * +
             * 반복 투출
             * +
             * 실제 오행 세력도 충분
             *
             * 이면 여기라도 강한 후보까지
             * 올라갈 수 있다.
             */
            if (
                    evidence.repeatedExposure()

                            && elementShareBasisPoints
                            >= RESIDUAL_QI_STRONG_SHARE_THRESHOLD
            ) {

                return StructureEstablishmentLevel
                        .ESTABLISHED;
            }

            return StructureEstablishmentLevel
                    .SUPPORTED;
        }

        /*
         * 년간/시간 투출.
         *
         * 반복 표현 또는 실제 오행 세력이
         * 충분할 때만 SUPPORTED.
         */
        if (
                evidence.otherStemExposed()
        ) {

            if (
                    evidence.repeatedExposure()

                            || elementShareBasisPoints
                            >= RESIDUAL_QI_OTHER_SHARE_THRESHOLD
            ) {

                return StructureEstablishmentLevel
                        .SUPPORTED;
            }

            return StructureEstablishmentLevel
                    .WEAK;
        }

        /*
         * 여기 미투출은
         * 현재 단계에서는 비활성 후보로 둔다.
         */
        return StructureEstablishmentLevel
                .INACTIVE;
    }

    /**
     * 일반 내격 후보인지.
     *
     * 비견·겁재는 여기서 일반 팔격으로
     * 바로 처리하지 않는다.
     */
    public boolean isStandardInnerStructureEligible(
            StructureTenGodType tenGod
    ) {

        Objects.requireNonNull(
                tenGod,
                "십성이 필요합니다."
        );

        return !tenGod
                .companionGroup();
    }

    /**
     * 건록·양인 등
     * 별도 특수구조 검토가 필요한가.
     */
    public boolean requiresSpecialStructureReview(
            StructureTenGodType tenGod
    ) {

        Objects.requireNonNull(
                tenGod,
                "십성이 필요합니다."
        );

        return tenGod
                .companionGroup();
    }
}