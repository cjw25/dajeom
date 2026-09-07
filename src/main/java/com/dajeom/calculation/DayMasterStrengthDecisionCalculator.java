package com.dajeom.calculation;

import com.dajeom.domain.DayMasterFollowingStructureAnalysis;
import com.dajeom.domain.DayMasterForceStatusAnalysis;
import com.dajeom.domain.DayMasterGroundStatusAnalysis;
import com.dajeom.domain.DayMasterSeasonAnalysis;
import com.dajeom.domain.DayMasterSpecialStructureCandidateAnalysis;
import com.dajeom.domain.DayMasterSpecialStructureCandidateType;
import com.dajeom.domain.DayMasterStemTransformationAssessment;
import com.dajeom.domain.DayMasterStrengthAnalysis;
import com.dajeom.domain.DayMasterStrengthDecision;
import com.dajeom.domain.DayMasterStrengthDecisionPath;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandDayMasterAnalysis;
import com.dajeom.rule.DayMasterFollowingStructureRule;
import com.dajeom.rule.DayMasterStrengthRule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 일간의 특수구조와 일반 왕쇠를
 * 실제 명리 순서대로 결정한다.
 *
 * =================================================
 *
 * 최종 실행 순서:
 *
 * 1. 真化
 *
 * 2. 실제 종격 확정
 *
 * 3. 종격 후보였으나 불성립
 *    → 일반 왕쇠로 복귀
 *
 * 4. 假化 / 爭合 / 不化
 *    → 일반 왕쇠 + 해당 Context 보존
 *
 * 5. 순수 일반 왕쇠
 *
 * =================================================
 *
 * 후보만 있다는 이유로
 * 일반 왕쇠를 중단하지 않는다.
 *
 * 실제 종격이 성립했을 때만 중단한다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 퍼센트 / 다수결 없음.
 */
@Component
public class DayMasterStrengthDecisionCalculator {

    private final DayMasterStrengthRule
            dayMasterStrengthRule;

    private final DayMasterFollowingStructureRule
            dayMasterFollowingStructureRule;

    public DayMasterStrengthDecisionCalculator(

            DayMasterStrengthRule
                    dayMasterStrengthRule,

            DayMasterFollowingStructureRule
                    dayMasterFollowingStructureRule

    ) {

        this.dayMasterStrengthRule =
                Objects.requireNonNull(
                        dayMasterStrengthRule,
                        "DayMasterStrengthRule은 필수입니다."
                );

        this.dayMasterFollowingStructureRule =
                Objects.requireNonNull(
                        dayMasterFollowingStructureRule,
                        "DayMasterFollowingStructureRule은 필수입니다."
                );
    }

    public DayMasterStrengthDecision analyze(

            HeavenlyStem dayMaster,

            DayMasterSeasonAnalysis season,

            MonthCommandDayMasterAnalysis monthCommand,

            DayMasterGroundStatusAnalysis ground,

            DayMasterForceStatusAnalysis force,

            DayMasterSpecialStructureCandidateAnalysis
                    specialStructureCandidates,

            List<DayMasterStemTransformationAssessment>
                    stemTransformations

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                season,
                "월령 분석은 필수입니다."
        );

        Objects.requireNonNull(
                monthCommand,
                "월령-일간 관계 분석은 필수입니다."
        );

        Objects.requireNonNull(
                ground,
                "득지 분석은 필수입니다."
        );

        Objects.requireNonNull(
                force,
                "득세 분석은 필수입니다."
        );

        Objects.requireNonNull(
                specialStructureCandidates,
                "특수구조 후보 분석은 필수입니다."
        );

        List<DayMasterStemTransformationAssessment>
                safeTransformations =
                stemTransformations == null
                        ? List.of()
                        : List.copyOf(
                        stemTransformations
                );

        validateDayMaster(
                dayMaster,
                season,
                monthCommand,
                ground,
                force,
                specialStructureCandidates,
                safeTransformations
        );

        /*
         * =================================================
         * 1. 真化 최우선
         * =================================================
         */

        List<DayMasterStemTransformationAssessment>
                trueTransformations =
                safeTransformations
                        .stream()
                        .filter(
                                DayMasterStemTransformationAssessment
                                        ::trueTransformation
                        )
                        .toList();

        if (
                trueTransformations.size()
                        > 1
        ) {

            throw new IllegalStateException(
                    "동일 일간에서 복수의 真化가 동시에 성립했습니다. "
                            +
                            "쟁합 또는 천간오합 계산을 확인해야 합니다."
            );
        }

        if (
                trueTransformations.size()
                        == 1
        ) {

            return new DayMasterStrengthDecision(
                    dayMaster,
                    DayMasterStrengthDecisionPath
                            .TRUE_TRANSFORMATION,
                    Optional.empty(),
                    Optional.empty(),
                    specialStructureCandidates
                            .candidates(),
                    safeTransformations
            );
        }

        /*
         * =================================================
         * 2. 실제 종격 후보가 존재하는가
         * =================================================
         *
         * GENERAL_STRENGTH_REVIEW만 있다면
         * 종격 Rule을 굳이 실행하지 않는다.
         */
        boolean hasFollowingCandidate =
                specialStructureCandidates
                        .candidates()
                        .stream()
                        .anyMatch(
                                candidate ->
                                        candidate
                                                != DayMasterSpecialStructureCandidateType
                                                .GENERAL_STRENGTH_REVIEW
                        );

        Optional<DayMasterFollowingStructureAnalysis>
                followingAnalysis =
                Optional.empty();

        if (
                hasFollowingCandidate
        ) {

            DayMasterFollowingStructureAnalysis following =
                    dayMasterFollowingStructureRule.assess(
                            dayMaster,
                            season,
                            monthCommand,
                            ground,
                            force,
                            specialStructureCandidates
                    );

            followingAnalysis =
                    Optional.of(
                            following
                    );

            /*
             * =============================================
             * 실제 종격 성립
             * =============================================
             *
             * 후보가 아니라 Rule을 통과한 결과만
             * 일반 왕쇠보다 우선한다.
             */
            if (
                    following.established()
            ) {

                return new DayMasterStrengthDecision(
                        dayMaster,
                        DayMasterStrengthDecisionPath
                                .FOLLOWING_STRUCTURE_ESTABLISHED,
                        Optional.empty(),
                        followingAnalysis,
                        specialStructureCandidates
                                .candidates(),
                        safeTransformations
                );
            }

            /*
             * 여기까지 왔다는 것:
             *
             * 종격 후보는 있었으나
             * 엄격한 실제 Rule에서 不從.
             *
             * 따라서 일반 왕쇠로 복귀한다.
             */
        }

        /*
         * =================================================
         * 3. 일반 왕쇠
         * =================================================
         *
         * 중요:
         *
         * DayMasterStrengthRule 내부에서는
         * 이제 특수구조 후보만 보고
         * SPECIAL_STRUCTURE_REVIEW로 멈추지 않는다.
         *
         * 실제 특수구조의 우선판단은
         * 이 DecisionCalculator가 담당한다.
         */
        DayMasterStrengthAnalysis ordinaryStrength =
                dayMasterStrengthRule.assess(
                        dayMaster,
                        season,
                        ground,
                        force,
                        specialStructureCandidates
                );

        /*
         * =================================================
         * 4. 특수 Context를 보존한 일반 왕쇠
         * =================================================
         *
         * 다음 중 하나라도 있으면:
         *
         * - 종격 후보였지만 실제 不從
         * - 假化
         * - 爭合
         * - 不化
         *
         * 일반 왕쇠 결과와 함께
         * 특수 Context를 보존한다.
         */

        boolean rejectedFollowing =
                followingAnalysis
                        .map(
                                following ->
                                        !following
                                                .established()
                        )
                        .orElse(
                                false
                        );

        boolean hasStemTransformationContext =
                !safeTransformations
                        .isEmpty();

        if (
                rejectedFollowing
                        ||
                        hasStemTransformationContext
        ) {

            return new DayMasterStrengthDecision(
                    dayMaster,
                    DayMasterStrengthDecisionPath
                            .GENERAL_STRENGTH_WITH_SPECIAL_CONTEXT,
                    Optional.of(
                            ordinaryStrength
                    ),
                    followingAnalysis,
                    specialStructureCandidates
                            .candidates(),
                    safeTransformations
            );
        }

        /*
         * =================================================
         * 5. 순수 일반 왕쇠
         * =================================================
         */

        return new DayMasterStrengthDecision(
                dayMaster,
                DayMasterStrengthDecisionPath
                        .GENERAL_STRENGTH,
                Optional.of(
                        ordinaryStrength
                ),
                Optional.empty(),
                specialStructureCandidates
                        .candidates(),
                List.of()
        );
    }

    private void validateDayMaster(

            HeavenlyStem dayMaster,

            DayMasterSeasonAnalysis season,

            MonthCommandDayMasterAnalysis monthCommand,

            DayMasterGroundStatusAnalysis ground,

            DayMasterForceStatusAnalysis force,

            DayMasterSpecialStructureCandidateAnalysis
                    specialStructureCandidates,

            List<DayMasterStemTransformationAssessment>
                    stemTransformations

    ) {

        if (
                season.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "월령 계절 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                monthCommand.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "월령-일간 관계 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                ground.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "득지 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                force.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "득세 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                specialStructureCandidates
                        .dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "특수구조 후보 분석의 일간이 일치하지 않습니다."
            );
        }

        for (
                DayMasterStemTransformationAssessment transformation
                : stemTransformations
        ) {

            Objects.requireNonNull(
                    transformation,
                    "천간 화기 판정 목록에 null이 포함될 수 없습니다."
            );

            if (
                    transformation.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "천간 화기 판정의 일간이 일치하지 않습니다."
                );
            }
        }
    }
}