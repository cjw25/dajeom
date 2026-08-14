package com.dajeom.analyzer;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.ExtremeStrengthStructureAnalysis;
import com.dajeom.model.SpecialStructureAnalysis;
import com.dajeom.model.SpecialStructureEvidence;
import com.dajeom.model.StructurePurityAnalysis;
import com.dajeom.rule.SpecialStructureRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FollowWeakDirection;
import com.dajeom.type.SpecialStructureType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-8.
 *
 * 일반 내격과 분리해서:
 *
 * - 건록
 * - 양인
 * - 기타 비겁 월령
 * - 종강
 * - 종약 계열
 *
 * 후보를 연결한다.
 *
 * 중요한 원칙:
 *
 * 1.
 * 건록/양인은 월지를 정확히 비교한다.
 *
 * 2.
 * 겁재라고 해서 무조건 양인으로
 *    변환하지 않는다.
 *
 * 3.
 * 극강이라고 해서 무조건 종강으로
 *    변환하지 않는다.
 *
 * 4.
 * 극약이라고 해서 무조건
 *    종재·종관살·종아로 변환하지 않는다.
 *
 * 5.
 * 종격 후보는 반드시 4-7의
 *    ExtremeStrengthStructureAnalysis 결과를
 *    그대로 사용한다.
 *
 * 6.
 * 일반 내격과 특수격 후보가
 *    동시에 존재할 수 있다.
 *
 * 최종 선택은 5-9의 책임이다.
 */
@Component
public class SpecialStructureAnalyzer {

    private final StructurePurityAnalyzer
            structurePurityAnalyzer;

    private final SpecialStructureRule
            specialStructureRule;

    public SpecialStructureAnalyzer(

            StructurePurityAnalyzer
                    structurePurityAnalyzer,

            SpecialStructureRule
                    specialStructureRule

    ) {

        this.structurePurityAnalyzer =
                Objects.requireNonNull(
                        structurePurityAnalyzer,
                        "StructurePurityAnalyzer가 필요합니다."
                );

        this.specialStructureRule =
                Objects.requireNonNull(
                        specialStructureRule,
                        "SpecialStructureRule이 필요합니다."
                );
    }

    /**
     * 5-1 ~ 5-7을 내부에서 실행한 뒤
     * 5-8을 수행한다.
     */
    public SpecialStructureAnalysis analyze(
            DayMasterStrengthAnalysis strength
    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        StructurePurityAnalysis purity =
                structurePurityAnalyzer
                        .analyze(
                                strength
                        );

        return analyze(
                strength,
                purity
        );
    }

    /**
     * 이미 계산된 5-7 결과를 재사용한다.
     *
     * 향후 5차 전체 Engine에서는
     * 이 오버로드를 사용한다.
     */
    public SpecialStructureAnalysis analyze(

            DayMasterStrengthAnalysis strength,

            StructurePurityAnalysis purity

    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                purity,
                "격 순도 분석 결과가 필요합니다."
        );

        validateConsistency(
                strength,
                purity
        );

        EarthlyBranch monthBranch =
                purity.monthBranch();

        boolean mainQiSpecialDeferred =
                purity
                        .mainQiEvidence()
                        .specialStructureDeferred();

        int meaningfulStandardCandidateCount =
                purity
                        .meaningfulStandardCandidateCount();

        List<SpecialStructureEvidence> evidences =
                new ArrayList<>();

        /*
         * ================================================
         * 1.
         * 건록 후보
         * ================================================
         */
        boolean jianLuCandidate =
                specialStructureRule
                        .isJianLuMonth(
                                strength.dayMaster(),
                                monthBranch
                        );

        if (
                jianLuCandidate
        ) {

            evidences.add(
                    createMonthStructureEvidence(
                            SpecialStructureType
                                    .JIAN_LU_CANDIDATE,
                            strength,
                            monthBranch,
                            mainQiSpecialDeferred,
                            meaningfulStandardCandidateCount
                    )
            );
        }

        /*
         * ================================================
         * 2.
         * 양인 후보
         * ================================================
         *
         * 현재 다점 기본 정책에서는
         * 양간만 적용한다.
         */
        boolean yangRenCandidate =
                specialStructureRule
                        .isYangRenMonth(
                                strength.dayMaster(),
                                monthBranch
                        );

        if (
                yangRenCandidate
        ) {

            evidences.add(
                    createMonthStructureEvidence(
                            SpecialStructureType
                                    .YANG_REN_CANDIDATE,
                            strength,
                            monthBranch,
                            mainQiSpecialDeferred,
                            meaningfulStandardCandidateCount
                    )
            );
        }

        /*
         * ================================================
         * 3.
         * 기타 비겁 월령
         * ================================================
         *
         * 5-3에서 일반 내격 적용을 보류했지만
         * 건록/양인에 정확히 해당하지 않는 경우.
         *
         * 이 후보를 그냥 버리지 않는다.
         */
        boolean companionMonthReview =
                specialStructureRule
                        .requiresCompanionMonthReview(
                                mainQiSpecialDeferred,
                                jianLuCandidate,
                                yangRenCandidate
                        );

        if (
                companionMonthReview
        ) {

            evidences.add(
                    createCompanionMonthReviewEvidence(
                            strength,
                            monthBranch,
                            meaningfulStandardCandidateCount
                    )
            );
        }

        /*
         * ================================================
         * 4.
         * 종격 후보
         * ================================================
         *
         * 4-7 결과만 상속한다.
         */
        ExtremeStrengthStructureAnalysis extreme =
                strength
                        .extremeStructureAnalysis();

        Optional<SpecialStructureType>
                followCandidateType =
                specialStructureRule
                        .determineFollowCandidateType(
                                extreme
                        );

        followCandidateType.ifPresent(
                type ->
                        evidences.add(
                                createFollowStructureEvidence(
                                        type,
                                        strength,
                                        monthBranch,
                                        mainQiSpecialDeferred,
                                        meaningfulStandardCandidateCount,
                                        extreme
                                                .followWeakDirection()
                                )
                        )
        );

        return new SpecialStructureAnalysis(
                strength.dayMaster(),
                monthBranch,
                mainQiSpecialDeferred,
                meaningfulStandardCandidateCount,
                evidences
        );
    }

    /**
     * 건록/양인.
     *
     * 월지와 일간의 정확한 위치 대응으로
     * 생성되는 후보.
     */
    private SpecialStructureEvidence
    createMonthStructureEvidence(

            SpecialStructureType type,

            DayMasterStrengthAnalysis strength,

            EarthlyBranch monthBranch,

            boolean mainQiSpecialDeferred,

            int meaningfulStandardCandidateCount

    ) {

        if (
                !type.jianLu()
                        && !type.yangRen()
        ) {

            throw new IllegalArgumentException(
                    "정확한 월지 특수구조는 건록 또는 양인이어야 합니다."
            );
        }

        return new SpecialStructureEvidence(
                type,
                strength.dayMaster(),
                monthBranch,
                strength.level(),
                strength.strengthBasisPoints(),
                true,
                false,
                mainQiSpecialDeferred,
                meaningfulStandardCandidateCount,
                FollowWeakDirection.NONE
        );
    }

    /**
     * 일반 내격에서 보류됐지만
     * 현재 정책의 건록/양인에 직접 들어가지 않는
     * 비겁 월령.
     */
    private SpecialStructureEvidence
    createCompanionMonthReviewEvidence(

            DayMasterStrengthAnalysis strength,

            EarthlyBranch monthBranch,

            int meaningfulStandardCandidateCount

    ) {

        return new SpecialStructureEvidence(
                SpecialStructureType
                        .COMPANION_MONTH_REVIEW,
                strength.dayMaster(),
                monthBranch,
                strength.level(),
                strength.strengthBasisPoints(),
                false,
                false,
                true,
                meaningfulStandardCandidateCount,
                FollowWeakDirection.NONE
        );
    }

    /**
     * 4-7에서 상속받은 종격 후보.
     */
    private SpecialStructureEvidence
    createFollowStructureEvidence(

            SpecialStructureType type,

            DayMasterStrengthAnalysis strength,

            EarthlyBranch monthBranch,

            boolean mainQiSpecialDeferred,

            int meaningfulStandardCandidateCount,

            FollowWeakDirection followWeakDirection

    ) {

        if (
                !type.followStructure()
        ) {

            throw new IllegalArgumentException(
                    "종격 계열 타입이 아닙니다: "
                            + type
            );
        }

        FollowWeakDirection direction =
                type.followStrong()
                        ? FollowWeakDirection.NONE
                        : followWeakDirection;

        return new SpecialStructureEvidence(
                type,
                strength.dayMaster(),
                monthBranch,
                strength.level(),
                strength.strengthBasisPoints(),
                false,
                true,
                mainQiSpecialDeferred,
                meaningfulStandardCandidateCount,
                direction
        );
    }

    private void validateConsistency(

            DayMasterStrengthAnalysis strength,

            StructurePurityAnalysis purity

    ) {

        if (
                strength.dayMaster()
                        != purity.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "신강·신약 분석과 격 순도 분석의 일간이 일치하지 않습니다."
            );
        }

        EarthlyBranch strengthMonthBranch =
                strength
                        .elementStrengthAnalysis()
                        .basePowerAnalysis()
                        .monthCommandAnalysis()
                        .monthBranch();

        if (
                strengthMonthBranch
                        != purity.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "신강·신약 분석과 격 순도 분석의 월지가 일치하지 않습니다."
            );
        }
    }
}