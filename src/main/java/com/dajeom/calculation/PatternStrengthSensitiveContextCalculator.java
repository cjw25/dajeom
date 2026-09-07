package com.dajeom.calculation;

import com.dajeom.domain.DayMasterStrengthStatus;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternStrengthSensitiveContextAnalysis;
import com.dajeom.domain.PatternStrengthSensitiveContextReason;
import com.dajeom.domain.PatternTenGodFoundationAnalysis;
import com.dajeom.domain.PatternTenGodFoundationProfile;
import com.dajeom.domain.PatternTenGodRouteAnalysis;
import com.dajeom.domain.PatternTenGodRouteType;
import com.dajeom.domain.PatternWealthResourceInteractionAnalysis;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;

/**
 * 일간 강약과 십성 foundation / route를 결합하여
 * 고차 격국 Context를 계산한다.
 *
 * =================================================
 *
 * 숫자식 없음.
 *
 * 예:
 *
 * 印 3개니까 강함
 * 食傷 2개니까 과다
 *
 * 같은 방식은 사용하지 않는다.
 *
 * 대신:
 *
 * - 일간이 실제 강한 쪽인가
 * - 印이 월령/통근/투출을 통해 강화되어 있는가
 * - 食傷이 투출 + 통근되어 실제 설기 구조가 있는가
 * - 財가 실제 印을 제어하고 있는가
 *
 * 를 조건식으로 판단한다.
 */
@Component
public class PatternStrengthSensitiveContextCalculator {

    public PatternStrengthSensitiveContextAnalysis analyze(

            DayMasterStrengthStatus strengthStatus,

            PatternTenGodFoundationProfile foundations,

            PatternTenGodRouteAnalysis routes,

            PatternWealthResourceInteractionAnalysis wealthResourceInteraction

    ) {

        Objects.requireNonNull(
                strengthStatus,
                "DayMasterStrengthStatus는 필수입니다."
        );

        Objects.requireNonNull(
                foundations,
                "PatternTenGodFoundationProfile은 필수입니다."
        );

        Objects.requireNonNull(
                routes,
                "PatternTenGodRouteAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                wealthResourceInteraction,
                "PatternWealthResourceInteractionAnalysis는 필수입니다."
        );

        validateDayMaster(
                foundations,
                routes,
                wealthResourceInteraction
        );

        EnumSet<PatternStrengthSensitiveContextReason> reasons =
                EnumSet.noneOf(
                        PatternStrengthSensitiveContextReason.class
                );

        boolean strongSide =
                strongSide(
                        strengthStatus
                );

        boolean weakSide =
                weakSide(
                        strengthStatus
                );

        if (strongSide) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .DAY_MASTER_STRONG_SIDE
            );

        } else if (weakSide) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .DAY_MASTER_WEAK_SIDE
            );

        } else {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .DAY_MASTER_NOT_CLEAR_ENOUGH_FOR_AUTOMATIC_DECISION
            );
        }

        /*
         * =================================================
         * 印 구조
         * =================================================
         */

        boolean resourceFoundation =
                foundations
                        .resourceFoundationStable();

        if (resourceFoundation) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .RESOURCE_FOUNDATION_PRESENT
            );
        }

        boolean resourceVisibleRootReinforcement =
                resourceHasVisibleRootReinforcement(
                        foundations
                );

        if (resourceVisibleRootReinforcement) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .RESOURCE_VISIBLE_ROOT_REINFORCEMENT_PRESENT
            );
        }

        boolean resourceMonthCommandWithVisibleReinforcement =
                resourceMonthCommandWithVisibleReinforcement(
                        foundations
                );

        if (resourceMonthCommandWithVisibleReinforcement) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .RESOURCE_MONTH_COMMAND_WITH_VISIBLE_REINFORCEMENT
            );
        }

        /*
         * "印 구조가 강화됐다"는 뜻.
         *
         * 단순 印 존재가 아니다.
         */
        boolean reinforcedResourceStructure =
                resourceFoundation
                        &&
                        (
                                resourceVisibleRootReinforcement

                                        ||

                                        resourceMonthCommandWithVisibleReinforcement
                        );

        if (reinforcedResourceStructure) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .RESOURCE_STRUCTURE_REINFORCED
            );
        }

        /*
         * =================================================
         * 1. 身強 + 印 구조 강화
         *
         * → excessiveResourceContext
         * =================================================
         *
         * 이 boolean은
         *
         * "印이 무조건 나쁘다"
         *
         * 가 아니다.
         *
         * 身印이 이미 충분한데
         * 印을 더 쓰는 route를 조심해야 하는
         * 구조적 Context다.
         */

        boolean excessiveResourceContext =
                strongSide
                        &&
                        reinforcedResourceStructure;

        if (excessiveResourceContext) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .STRONG_DAY_MASTER_WITH_REINFORCED_RESOURCE
            );

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .EXCESSIVE_RESOURCE_CONTEXT_CONFIRMED
            );
        }

        /*
         * =================================================
         * 2. 印多用財
         * =================================================
         *
         * 조건:
         *
         * - 身強
         * - 印 구조 강화
         * - 財 foundation 존재
         * - 財가 실제 印을 제어
         *
         * 여기서는 같은 財→印 관계라도
         * excessiveResourceContext에서는
         * "破印"이 아니라
         * "印을 적절히 덜어내는 후보"로 볼 수 있게 한다.
         */

        boolean wealthFoundation =
                foundations
                        .wealthFoundationStable();

        if (wealthFoundation) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .WEALTH_FOUNDATION_PRESENT
            );
        }

        boolean wealthDirectlyInteractsWithResource =
                wealthResourceInteraction
                        .resourcePatternConflictActive();

        if (wealthDirectlyInteractsWithResource) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .WEALTH_DIRECTLY_INTERACTS_WITH_RESOURCE
            );
        }

        boolean wealthModeratesExcessResource =
                excessiveResourceContext
                        &&
                        wealthFoundation
                        &&
                        wealthDirectlyInteractsWithResource;

        if (wealthModeratesExcessResource) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .WEALTH_MODERATES_EXCESS_RESOURCE
            );
        }

        /*
         * =================================================
         * 食傷 구조
         * =================================================
         */

        boolean outputFoundation =
                foundations.eatingGodFoundationStable()
                        ||
                        foundations.hurtingOfficerFoundationStable();

        if (outputFoundation) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .OUTPUT_FOUNDATION_PRESENT
            );
        }

        boolean outputVisibleRootReinforcement =
                outputHasVisibleRootReinforcement(
                        foundations
                );

        if (outputVisibleRootReinforcement) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .OUTPUT_VISIBLE_ROOT_REINFORCEMENT_PRESENT
            );
        }

        /*
         * =================================================
         * 3. 身弱 + 食傷 통근/투출
         *
         * → outputDrainWeakStructureActive
         * =================================================
         *
         * 食傷이 지장간에 하나 존재한다는 이유만으로
         * 설기 과다라고 하지 않는다.
         *
         * 실제 투출된 食傷이 통근된 경우만
         * 자동으로 이 Context를 올린다.
         */

        boolean outputDrainWeakStructureActive =
                weakSide
                        &&
                        outputFoundation
                        &&
                        outputVisibleRootReinforcement;

        if (outputDrainWeakStructureActive) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .WEAK_DAY_MASTER_WITH_REINFORCED_OUTPUT
            );

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .OUTPUT_DRAIN_WEAK_STRUCTURE
            );
        }

        /*
         * =================================================
         * 4. 傷官佩印인데 傷官이 너무 가벼움
         * =================================================
         *
         * 보수적으로 잡는다.
         *
         * 조건:
         *
         * - 日主 강한 쪽
         * - 傷官이 月令 MAIN
         * - 傷官이 별도로 투출되어 보강되지 않음
         * - 印 구조는 강화됨
         * - 印이 실제 月令 傷官을 제어하는 route 존재
         *
         * 즉:
         *
         * 身旺
         * +
         * 傷官은 월령에만 있고
         * +
         * 印이 강하게 들어와 傷官을 누르는 구조
         */

        PatternTenGodFoundationAnalysis hurtingOfficerFoundation =
                foundations.foundationOf(
                        MonthCommandPatternCandidateType
                                .HURTING_OFFICER
                );

        boolean hurtingOfficerIsMonthCommand =
                hurtingOfficerFoundation
                        .monthCommandMain();

        if (hurtingOfficerIsMonthCommand) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .HURTING_OFFICER_IS_MONTH_COMMAND
            );
        }

        boolean hurtingOfficerHasNoVisibleReinforcement =
                hurtingOfficerIsMonthCommand
                        &&
                        !hurtingOfficerFoundation
                                .visible();

        if (hurtingOfficerHasNoVisibleReinforcement) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .HURTING_OFFICER_HAS_NO_VISIBLE_REINFORCEMENT
            );
        }

        boolean resourceControlsHurtingOfficer =
                routes.hasRoute(
                        PatternTenGodRouteType
                                .HURTING_OFFICER_PATTERN_RESOURCE_CONTROLS_HURTING_OFFICER
                );

        if (resourceControlsHurtingOfficer) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .RESOURCE_CONTROLS_MONTH_COMMAND_HURTING_OFFICER
            );
        }

        boolean hurtingOfficerTooLightForResourceRoute =
                strongSide
                        &&
                        hurtingOfficerHasNoVisibleReinforcement
                        &&
                        reinforcedResourceStructure
                        &&
                        resourceControlsHurtingOfficer;

        if (hurtingOfficerTooLightForResourceRoute) {

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .STRONG_DAY_MASTER_WITH_LIGHT_HURTING_OFFICER
            );

            reasons.add(
                    PatternStrengthSensitiveContextReason
                            .HURTING_OFFICER_TOO_LIGHT_FOR_RESOURCE_ROUTE
            );
        }

        return new PatternStrengthSensitiveContextAnalysis(

                foundations.dayMaster(),

                strengthStatus,

                excessiveResourceContext,

                wealthModeratesExcessResource,

                outputDrainWeakStructureActive,

                hurtingOfficerTooLightForResourceRoute,

                reasons
        );
    }

    /*
     * =================================================
     * 印 구조 강화
     * =================================================
     */

    private boolean resourceHasVisibleRootReinforcement(
            PatternTenGodFoundationProfile foundations
    ) {

        PatternTenGodFoundationAnalysis direct =
                foundations.foundationOf(
                        MonthCommandPatternCandidateType
                                .DIRECT_RESOURCE
                );

        PatternTenGodFoundationAnalysis indirect =
                foundations.foundationOf(
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
                );

        return direct.rootedVisibleStemExists()
                ||
                indirect.rootedVisibleStemExists();
    }

    private boolean resourceMonthCommandWithVisibleReinforcement(
            PatternTenGodFoundationProfile foundations
    ) {

        PatternTenGodFoundationAnalysis direct =
                foundations.foundationOf(
                        MonthCommandPatternCandidateType
                                .DIRECT_RESOURCE
                );

        PatternTenGodFoundationAnalysis indirect =
                foundations.foundationOf(
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
                );

        /*
         * 正印月令 + 투출 印
         *
         * 또는
         *
         * 偏印月令 + 투출 印
         */
        boolean monthCommandResource =
                direct.monthCommandMain()
                        ||
                        indirect.monthCommandMain();

        boolean visibleResource =
                direct.visible()
                        ||
                        indirect.visible();

        return monthCommandResource
                &&
                visibleResource;
    }

    /*
     * =================================================
     * 食傷 구조 강화
     * =================================================
     */

    private boolean outputHasVisibleRootReinforcement(
            PatternTenGodFoundationProfile foundations
    ) {

        PatternTenGodFoundationAnalysis eatingGod =
                foundations.foundationOf(
                        MonthCommandPatternCandidateType
                                .EATING_GOD
                );

        PatternTenGodFoundationAnalysis hurtingOfficer =
                foundations.foundationOf(
                        MonthCommandPatternCandidateType
                                .HURTING_OFFICER
                );

        return eatingGod.rootedVisibleStemExists()
                ||
                hurtingOfficer.rootedVisibleStemExists();
    }

    /*
     * =================================================
     * 강약
     * =================================================
     */

    private boolean strongSide(
            DayMasterStrengthStatus status
    ) {

        return status
                == DayMasterStrengthStatus.STRONG_LEANING

                ||

                status
                        == DayMasterStrengthStatus.CLEARLY_STRONG;
    }

    private boolean weakSide(
            DayMasterStrengthStatus status
    ) {

        return status
                == DayMasterStrengthStatus.WEAK_LEANING

                ||

                status
                        == DayMasterStrengthStatus.CLEARLY_WEAK;
    }

    private void validateDayMaster(

            PatternTenGodFoundationProfile foundations,

            PatternTenGodRouteAnalysis routes,

            PatternWealthResourceInteractionAnalysis wealthResourceInteraction

    ) {

        if (
                foundations.dayMaster()
                        != routes.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "FoundationProfile과 RouteAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                foundations.dayMaster()
                        != wealthResourceInteraction.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "FoundationProfile과 WealthResourceInteraction의 일간이 일치하지 않습니다."
            );
        }
    }
}