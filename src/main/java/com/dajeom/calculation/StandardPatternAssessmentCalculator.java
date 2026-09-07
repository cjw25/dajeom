package com.dajeom.calculation;

import com.dajeom.domain.DayMasterStrengthAnalysis;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.MonthCommandPatternSelectionAnalysis;
import com.dajeom.domain.MonthCommandPatternSelectionPath;
import com.dajeom.domain.PatternAssessment;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.StandardPatternAssessment;
import com.dajeom.domain.StandardPatternAssessmentPath;
import com.dajeom.domain.StandardPatternAssessmentResult;
import com.dajeom.domain.StandardPatternStructuralSignals;
import com.dajeom.rule.DirectOfficerPatternRule;
import com.dajeom.rule.EatingGodPatternRule;
import com.dajeom.rule.HurtingOfficerPatternRule;
import com.dajeom.rule.ResourcePatternRule;
import com.dajeom.rule.SevenKillingsPatternRule;
import com.dajeom.rule.WealthPatternRule;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 일반 8격의 통합 판정 Calculator.
 *
 * =================================================
 *
 * 역할:
 *
 * MonthCommandPatternSelectionRule이 만든
 * 선택 경로를 읽고,
 *
 * 실제 일반 8격 하나가 선택된 경우에만
 * 해당 전용 PatternRule을 실행한다.
 *
 * =================================================
 *
 * 실행:
 *
 * 正官
 * → DirectOfficerPatternRule
 *
 * 七殺
 * → SevenKillingsPatternRule
 *
 * 正財 / 偏財
 * → WealthPatternRule
 *
 * 正印 / 偏印
 * → ResourcePatternRule
 *
 * 食神
 * → EatingGodPatternRule
 *
 * 傷官
 * → HurtingOfficerPatternRule
 *
 * =================================================
 *
 * 실행하지 않음:
 *
 * - 격국 변화 검토 중
 * - 복수 투출 검토 중
 * - 建祿
 * - 陽刃
 * - 月劫
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 / 다수결 없음.
 */
@Component
public class StandardPatternAssessmentCalculator {

    private final DirectOfficerPatternRule
            directOfficerPatternRule;

    private final WealthPatternRule
            wealthPatternRule;

    private final ResourcePatternRule
            resourcePatternRule;

    private final EatingGodPatternRule
            eatingGodPatternRule;

    private final HurtingOfficerPatternRule
            hurtingOfficerPatternRule;

    private final SevenKillingsPatternRule
            sevenKillingsPatternRule;

    public StandardPatternAssessmentCalculator(

            DirectOfficerPatternRule
                    directOfficerPatternRule,

            WealthPatternRule
                    wealthPatternRule,

            ResourcePatternRule
                    resourcePatternRule,

            EatingGodPatternRule
                    eatingGodPatternRule,

            HurtingOfficerPatternRule
                    hurtingOfficerPatternRule,

            SevenKillingsPatternRule
                    sevenKillingsPatternRule

    ) {

        this.directOfficerPatternRule =
                Objects.requireNonNull(
                        directOfficerPatternRule,
                        "DirectOfficerPatternRule은 필수입니다."
                );

        this.wealthPatternRule =
                Objects.requireNonNull(
                        wealthPatternRule,
                        "WealthPatternRule은 필수입니다."
                );

        this.resourcePatternRule =
                Objects.requireNonNull(
                        resourcePatternRule,
                        "ResourcePatternRule은 필수입니다."
                );

        this.eatingGodPatternRule =
                Objects.requireNonNull(
                        eatingGodPatternRule,
                        "EatingGodPatternRule은 필수입니다."
                );

        this.hurtingOfficerPatternRule =
                Objects.requireNonNull(
                        hurtingOfficerPatternRule,
                        "HurtingOfficerPatternRule은 필수입니다."
                );

        this.sevenKillingsPatternRule =
                Objects.requireNonNull(
                        sevenKillingsPatternRule,
                        "SevenKillingsPatternRule은 필수입니다."
                );
    }

    public StandardPatternAssessmentResult assess(

            MonthCommandPatternSelectionAnalysis selection,

            PatternInteractionAnalysis interaction,

            DayMasterStrengthAnalysis strength,

            StandardPatternStructuralSignals structuralSignals

    ) {

        Objects.requireNonNull(
                selection,
                "월령 격국 선택 결과는 필수입니다."
        );

        Objects.requireNonNull(
                interaction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                strength,
                "일간 왕쇠 분석은 필수입니다."
        );

        Objects.requireNonNull(
                structuralSignals,
                "격국 구조 신호는 필수입니다."
        );

        validateCommon(
                selection,
                interaction,
                strength
        );

        /*
         * =================================================
         * 1. 격국 변화 후보 하나
         * =================================================
         *
         * 아직 어느 격을 쓸지 확정되지 않았으므로
         * 개별 PatternRule을 실행하지 않는다.
         */

        if (
                selection.path()
                        == MonthCommandPatternSelectionPath
                        .STANDARD_CHANGE_REVIEW
        ) {

            return new StandardPatternAssessmentResult(
                    selection.dayMaster(),
                    selection,
                    StandardPatternAssessmentPath
                            .PATTERN_CHANGE_REVIEW_REQUIRED,
                    Optional.empty()
            );
        }

        /*
         * =================================================
         * 2. 복수 투출
         * =================================================
         */

        if (
                selection.path()
                        == MonthCommandPatternSelectionPath
                        .STANDARD_MULTIPLE_CHANGE_REVIEW
        ) {

            return new StandardPatternAssessmentResult(
                    selection.dayMaster(),
                    selection,
                    StandardPatternAssessmentPath
                            .MULTIPLE_PATTERN_CHANGE_REVIEW_REQUIRED,
                    Optional.empty()
            );
        }

        /*
         * =================================================
         * 3. 建祿
         * =================================================
         */

        if (
                selection.path()
                        == MonthCommandPatternSelectionPath
                        .JIANLU_REVIEW
        ) {

            return new StandardPatternAssessmentResult(
                    selection.dayMaster(),
                    selection,
                    StandardPatternAssessmentPath
                            .JIANLU_REVIEW,
                    Optional.empty()
            );
        }

        /*
         * =================================================
         * 4. 陽刃
         * =================================================
         */

        if (
                selection.path()
                        == MonthCommandPatternSelectionPath
                        .YANG_BLADE_REVIEW
        ) {

            return new StandardPatternAssessmentResult(
                    selection.dayMaster(),
                    selection,
                    StandardPatternAssessmentPath
                            .YANG_BLADE_REVIEW,
                    Optional.empty()
            );
        }

        /*
         * =================================================
         * 5. 月劫
         * =================================================
         */

        if (
                selection.path()
                        == MonthCommandPatternSelectionPath
                        .YUEJIE_REVIEW
        ) {

            return new StandardPatternAssessmentResult(
                    selection.dayMaster(),
                    selection,
                    StandardPatternAssessmentPath
                            .YUEJIE_REVIEW,
                    Optional.empty()
            );
        }

        /*
         * =================================================
         * 6. 일반 8격 하나가 선택됨
         * =================================================
         */

        if (
                selection.path()
                        != MonthCommandPatternSelectionPath
                        .STANDARD_PRIMARY_REVIEW
        ) {

            throw new IllegalStateException(
                    "알 수 없는 월령 격국 선택 경로입니다: "
                            +
                            selection.path()
            );
        }

        MonthCommandPatternCandidate selectedPattern =
                selection
                        .provisionalStandardCandidate()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "STANDARD_PRIMARY_REVIEW인데 일반 8격 후보가 없습니다."
                                        )
                        );

        StandardPatternAssessment assessment =
                assessSelectedPattern(
                        selectedPattern,
                        interaction,
                        strength,
                        structuralSignals
                );

        return new StandardPatternAssessmentResult(
                selection.dayMaster(),
                selection,
                StandardPatternAssessmentPath
                        .ASSESSED,
                Optional.of(
                        assessment
                )
        );
    }

    private StandardPatternAssessment assessSelectedPattern(

            MonthCommandPatternCandidate selectedPattern,

            PatternInteractionAnalysis interaction,

            DayMasterStrengthAnalysis strength,

            StandardPatternStructuralSignals structuralSignals

    ) {

        MonthCommandPatternCandidateType type =
                selectedPattern
                        .candidateType();

        return switch (type) {

            /*
             * =================================================
             * 正官格
             * =================================================
             */

            case DIRECT_OFFICER -> {

                PatternAssessment<?> result =
                        directOfficerPatternRule.assess(
                                selectedPattern,
                                interaction,
                                structuralSignals
                                        .requireDirectOfficer()
                        );

                yield toStandardAssessment(
                        result
                );
            }

            /*
             * =================================================
             * 七殺格
             * =================================================
             */

            case SEVEN_KILLINGS -> {

                PatternAssessment<?> result =
                        sevenKillingsPatternRule.assess(
                                selectedPattern,
                                interaction,
                                strength,
                                structuralSignals
                                        .requireSevenKillings()
                        );

                yield toStandardAssessment(
                        result
                );
            }

            /*
             * =================================================
             * 正財 / 偏財
             * =================================================
             */

            case DIRECT_WEALTH,
                 INDIRECT_WEALTH -> {

                PatternAssessment<?> result =
                        wealthPatternRule.assess(
                                selectedPattern,
                                interaction,
                                strength,
                                structuralSignals
                                        .requireWealth()
                        );

                yield toStandardAssessment(
                        result
                );
            }

            /*
             * =================================================
             * 正印 / 偏印
             * =================================================
             */

            case DIRECT_RESOURCE,
                 INDIRECT_RESOURCE -> {

                PatternAssessment<?> result =
                        resourcePatternRule.assess(
                                selectedPattern,
                                interaction,
                                strength,
                                structuralSignals
                                        .requireResource()
                        );

                yield toStandardAssessment(
                        result
                );
            }

            /*
             * =================================================
             * 食神格
             * =================================================
             */

            case EATING_GOD -> {

                PatternAssessment<?> result =
                        eatingGodPatternRule.assess(
                                selectedPattern,
                                interaction,
                                strength,
                                structuralSignals
                                        .requireEatingGod()
                        );

                yield toStandardAssessment(
                        result
                );
            }

            /*
             * =================================================
             * 傷官格
             * =================================================
             */

            case HURTING_OFFICER -> {

                PatternAssessment<?> result =
                        hurtingOfficerPatternRule.assess(
                                selectedPattern,
                                interaction,
                                strength,
                                structuralSignals
                                        .requireHurtingOfficer()
                        );

                yield toStandardAssessment(
                        result
                );
            }

            /*
             * =================================================
             * 建祿 / 劫財月
             *
             * 이 switch까지 도달하면 안 된다.
             * =================================================
             */

            case MONTH_COMPANION,
                 MONTH_ROB_WEALTH ->

                    throw new IllegalStateException(
                            "建祿/陽刃/月劫 후보가 일반 8격 Rule로 전달되었습니다: "
                                    +
                                    type
                    );
        };
    }

    private StandardPatternAssessment toStandardAssessment(
            PatternAssessment<?> assessment
    ) {

        Objects.requireNonNull(
                assessment,
                "격국 Rule 결과는 필수입니다."
        );

        Set<String> reasonCodes =
                assessment
                        .reasons()
                        .stream()
                        .map(
                                reason ->
                                        ((Enum<?>) reason)
                                                .name()
                        )
                        .collect(
                                Collectors.toUnmodifiableSet()
                        );

        return new StandardPatternAssessment(
                assessment.dayMaster(),
                assessment.patternCandidate(),
                assessment.status(),
                reasonCodes
        );
    }

    private void validateCommon(

            MonthCommandPatternSelectionAnalysis selection,

            PatternInteractionAnalysis interaction,

            DayMasterStrengthAnalysis strength

    ) {

        if (
                selection.dayMaster()
                        != interaction.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "격국 선택 결과와 Interaction 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                selection.dayMaster()
                        != strength.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "격국 선택 결과와 왕쇠 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                !interaction
                        .patternSelection()
                        .equals(
                                selection
                        )
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis가 현재 격국 선택 결과를 기반으로 생성된 것이 아닙니다."
            );
        }

        if (
                strength
                        .requiresSpecialStructureReview()
        ) {

            throw new IllegalArgumentException(
                    "특수구조 판정이 끝나지 않은 상태에서 일반 격국을 판정할 수 없습니다."
            );
        }
    }
}