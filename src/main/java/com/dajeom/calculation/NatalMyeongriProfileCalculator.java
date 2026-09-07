package com.dajeom.calculation;

import com.dajeom.domain.DayMasterStrengthDecision;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.NatalMyeongriProfile;
import com.dajeom.domain.NatalPatternResolution;
import com.dajeom.domain.PatternBranchCombinationAnalysis;
import com.dajeom.domain.PatternBranchDisruptionAnalysis;
import com.dajeom.domain.PatternBranchDisruptionResolutionAnalysis;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternMonthCommandDisruptionResolutionAnalysis;
import com.dajeom.domain.PatternOfficerKillingsPurificationAnalysis;
import com.dajeom.domain.PatternStemCombinationActivationAnalysis;
import com.dajeom.domain.PatternStemCombinationPreservationAnalysis;
import com.dajeom.domain.PatternStrengthSensitiveContextAnalysis;
import com.dajeom.domain.PatternTenGodFoundationProfile;
import com.dajeom.domain.PatternTenGodRouteAnalysis;
import com.dajeom.domain.PatternWealthResourceInteractionAnalysis;
import com.dajeom.domain.PatternYangBladeAnalysis;
import com.dajeom.domain.PatternYangBladeKillingCombinationAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 지금까지 계산된 명리 구조를
 * AI 해석 직전의 NatalMyeongriProfile로 조립한다.
 *
 * =================================================
 *
 * 이 Calculator에서는:
 *
 * - 신강약을 다시 계산하지 않는다.
 * - 격국을 다시 계산하지 않는다.
 * - 합충을 다시 계산하지 않는다.
 * - 십성을 다시 계산하지 않는다.
 *
 * 각 전문 Calculator/Rule의 결과를
 * 한곳에 모으기만 한다.
 *
 * =================================================
 *
 * 계산 책임:
 *
 * Java
 *
 * =================================================
 *
 * 표현 책임:
 *
 * 이후 LifeClaim + AI prose
 */
@Component
public class NatalMyeongriProfileCalculator {

    public NatalMyeongriProfile build(

            PatternInteractionAnalysis patternInteraction,

            DayMasterStrengthDecision strengthDecision,

            NatalPatternResolution patternResolution,

            PatternTenGodFoundationProfile foundations,

            PatternTenGodRouteAnalysis routes,

            PatternStemCombinationActivationAnalysis
                    stemCombinationActivation,

            PatternStemCombinationPreservationAnalysis
                    stemCombinationPreservation,

            PatternBranchDisruptionAnalysis branchDisruptions,

            PatternBranchCombinationAnalysis branchCombinations,

            PatternBranchDisruptionResolutionAnalysis
                    branchDisruptionResolutions,

            PatternMonthCommandDisruptionResolutionAnalysis
                    monthCommandDisruptionResolution,

            PatternWealthResourceInteractionAnalysis
                    wealthResourceInteraction,

            PatternStrengthSensitiveContextAnalysis
                    strengthSensitiveContext,

            PatternOfficerKillingsPurificationAnalysis
                    officerKillingsPurification,

            PatternYangBladeAnalysis yangBlade,

            PatternYangBladeKillingCombinationAnalysis
                    yangBladeKillingCombination

    ) {

        Objects.requireNonNull(
                patternInteraction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                strengthDecision,
                "DayMasterStrengthDecision은 필수입니다."
        );

        Objects.requireNonNull(
                patternResolution,
                "NatalPatternResolution은 필수입니다."
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
                stemCombinationActivation,
                "PatternStemCombinationActivationAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                stemCombinationPreservation,
                "PatternStemCombinationPreservationAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                branchDisruptions,
                "PatternBranchDisruptionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                branchCombinations,
                "PatternBranchCombinationAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                branchDisruptionResolutions,
                "PatternBranchDisruptionResolutionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                monthCommandDisruptionResolution,
                "PatternMonthCommandDisruptionResolutionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                wealthResourceInteraction,
                "PatternWealthResourceInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                strengthSensitiveContext,
                "PatternStrengthSensitiveContextAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                officerKillingsPurification,
                "PatternOfficerKillingsPurificationAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                yangBlade,
                "PatternYangBladeAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                yangBladeKillingCombination,
                "PatternYangBladeKillingCombinationAnalysis는 필수입니다."
        );

        HeavenlyStem dayMaster =
                patternInteraction
                        .dayMaster();

        /*
         * 실제 세부 일관성 검증은
         * NatalMyeongriProfile 생성자에서 한다.
         *
         * Calculator가 중복해서 같은 판정을
         * 다시 하지 않는다.
         */
        return new NatalMyeongriProfile(

                dayMaster,

                strengthDecision,

                patternResolution,

                patternInteraction,

                foundations,

                routes,

                stemCombinationActivation,

                stemCombinationPreservation,

                branchDisruptions,

                branchCombinations,

                branchDisruptionResolutions,

                monthCommandDisruptionResolution,

                wealthResourceInteraction,

                strengthSensitiveContext,

                officerKillingsPurification,

                yangBlade,

                yangBladeKillingCombination
        );
    }
}