package com.dajeom.analyzer;

import com.dajeom.model.DayMasterElementRelationAnalysis;
import com.dajeom.model.DayMasterForceEvidence;
import com.dajeom.model.DayMasterForceFormationAnalysis;
import com.dajeom.model.DayMasterSupportEvidence;
import com.dajeom.model.DayMasterSupportPowerAnalysis;
import com.dajeom.model.ElementPowerEvidence;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.rule.DayMasterSupportPowerRule;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 4차-5.
 *
 * 비겁과 인성의 실제 생조 세력을 계산한다.
 *
 * 중요한 원칙:
 *
 * 1.
 * 일간 자기 자신을
 * 비겁 지원으로 계산하지 않는다.
 *
 * 2.
 * 3차 최종 오행 세력을 다시 계산하지 않는다.
 *
 * 3.
 * 통근/투간/계절 세력을 별도로 다시 더하지 않는다.
 *
 * 해당 효과는 이미 3차 ElementPowerAnalysis에
 * 반영되어 있기 때문이다.
 *
 * 4.
 * 4-4의 득세 구조도
 * 이 단계에서 추가 점수로 더하지 않는다.
 *
 * 득세는 별도 구조 Evidence로 유지하고
 * 4-8 StrengthRule에서 함께 사용한다.
 */
@Component
public class DayMasterSupportPowerAnalyzer {

    private final DayMasterElementRelationAnalyzer
            dayMasterElementRelationAnalyzer;

    private final DayMasterForceFormationAnalyzer
            dayMasterForceFormationAnalyzer;

    private final DayMasterSupportPowerRule
            dayMasterSupportPowerRule;

    public DayMasterSupportPowerAnalyzer(

            DayMasterElementRelationAnalyzer
                    dayMasterElementRelationAnalyzer,

            DayMasterForceFormationAnalyzer
                    dayMasterForceFormationAnalyzer,

            DayMasterSupportPowerRule
                    dayMasterSupportPowerRule

    ) {

        this.dayMasterElementRelationAnalyzer =
                Objects.requireNonNull(
                        dayMasterElementRelationAnalyzer,
                        "DayMasterElementRelationAnalyzer가 필요합니다."
                );

        this.dayMasterForceFormationAnalyzer =
                Objects.requireNonNull(
                        dayMasterForceFormationAnalyzer,
                        "DayMasterForceFormationAnalyzer가 필요합니다."
                );

        this.dayMasterSupportPowerRule =
                Objects.requireNonNull(
                        dayMasterSupportPowerRule,
                        "DayMasterSupportPowerRule이 필요합니다."
                );
    }

    /**
     * 4-1 / 4-4 결과를 내부에서 계산한다.
     */
    public DayMasterSupportPowerAnalysis analyze(
            ElementStrengthAnalysis elementStrength
    ) {

        Objects.requireNonNull(
                elementStrength,
                "오행 세력 분석이 필요합니다."
        );

        DayMasterElementRelationAnalysis relations =
                dayMasterElementRelationAnalyzer
                        .analyze(
                                elementStrength
                        );

        DayMasterForceFormationAnalysis formation =
                dayMasterForceFormationAnalyzer
                        .analyze(
                                elementStrength,
                                relations
                        );

        return analyze(
                elementStrength,
                relations,
                formation
        );
    }

    /**
     * 이미 계산된 4-1 / 4-4 결과를 재사용한다.
     *
     * 향후 4차 통합 Engine에서
     * 이 오버로드를 사용한다.
     */
    public DayMasterSupportPowerAnalysis analyze(

            ElementStrengthAnalysis elementStrength,

            DayMasterElementRelationAnalysis relations,

            DayMasterForceFormationAnalysis formation

    ) {

        Objects.requireNonNull(
                elementStrength,
                "오행 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                relations,
                "일간 기준 오행 관계 분석이 필요합니다."
        );

        Objects.requireNonNull(
                formation,
                "득세 구조 분석이 필요합니다."
        );

        if (
                elementStrength.dayMaster()
                        != relations.dayMaster()
                        || elementStrength.dayMaster()
                        != formation.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "4차 분석 단계의 일간이 서로 일치하지 않습니다."
            );
        }

        FiveElement dayMasterElement =
                elementStrength
                        .dayMasterElement();

        FiveElement companionElement =
                relations
                        .companionElement();

        FiveElement resourceElement =
                relations
                        .resourceElement();

        /*
         * 비겁 오행은 반드시
         * 일간 오행과 같아야 한다.
         */
        if (
                companionElement
                        != dayMasterElement
        ) {

            throw new IllegalStateException(
                    "비겁 오행과 일간 오행이 일치하지 않습니다."
            );
        }

        /*
         * ------------------------------------------------
         * 비겁
         * ------------------------------------------------
         */

        ElementPowerEvidence companionPowerEvidence =
                requireElementPowerEvidence(
                        elementStrength,
                        companionElement
                );

        int companionRawBasePower =
                companionPowerEvidence
                        .rawBasePower();

        int companionFinalPower =
                companionPowerEvidence
                        .finalPower();

        /*
         * 일간 자신을 제외한:
         *
         * - 외부 천간 비겁
         * - 지장간 비겁
         *
         * raw 세력만 합산한다.
         *
         * DayMasterForceFormationAnalyzer가
         * 이미 일간 자신을 제외했기 때문에
         * 그대로 사용할 수 있다.
         */
        List<DayMasterForceEvidence>
                companionSources =
                formation
                        .evidences()
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::companion
                        )
                        .toList();

        int externalCompanionRawPower =
                companionSources
                        .stream()
                        .mapToInt(
                                DayMasterForceEvidence
                                        ::rawWeight
                        )
                        .sum();

        if (
                externalCompanionRawPower
                        > companionRawBasePower
        ) {

            throw new IllegalStateException(
                    "외부 비겁 raw 세력이 전체 비겁 raw 세력보다 큽니다."
            );
        }

        int companionCapacity =
                dayMasterSupportPowerRule
                        .calculateExternalCompanionCapacity(
                                companionFinalPower,
                                companionRawBasePower,
                                externalCompanionRawPower
                        );

        int companionSupportPower =
                dayMasterSupportPowerRule
                        .calculateCompanionSupportPower(
                                companionCapacity
                        );

        DayMasterSupportEvidence companionEvidence =
                new DayMasterSupportEvidence(
                        DayMasterElementRole.COMPANION,
                        companionElement,
                        companionRawBasePower,
                        externalCompanionRawPower,
                        companionFinalPower,
                        companionCapacity,
                        dayMasterSupportPowerRule
                                .companionSupportRate(),
                        companionSupportPower,
                        countVisibleSources(
                                companionSources
                        ),
                        countHiddenSources(
                                companionSources
                        ),
                        countExposedHiddenSources(
                                companionSources
                        )
                );

        /*
         * ------------------------------------------------
         * 인성
         * ------------------------------------------------
         */

        ElementPowerEvidence resourcePowerEvidence =
                requireElementPowerEvidence(
                        elementStrength,
                        resourceElement
                );

        int resourceRawBasePower =
                resourcePowerEvidence
                        .rawBasePower();

        int resourceFinalPower =
                resourcePowerEvidence
                        .finalPower();

        int dayMasterElementFinalPower =
                companionFinalPower;

        List<DayMasterForceEvidence>
                resourceSources =
                formation
                        .evidences()
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::resource
                        )
                        .toList();

        int resourceExternalRawPower =
                resourceSources
                        .stream()
                        .mapToInt(
                                DayMasterForceEvidence
                                        ::rawWeight
                        )
                        .sum();

        /*
         * 인성은 일간 자신과 동일한 오행이
         * 아니기 때문에 정상적인 경우:
         *
         * externalRaw == 전체 resource raw
         *
         * 여야 한다.
         */
        if (
                resourceExternalRawPower
                        != resourceRawBasePower
        ) {

            throw new IllegalStateException(
                    "인성 source raw 세력이 3차 인성 오행 raw 세력과 일치하지 않습니다."
            );
        }

        int resourceCapacity =
                dayMasterSupportPowerRule
                        .calculateResourceSupportCapacity(
                                resourceFinalPower,
                                dayMasterElementFinalPower
                        );

        int resourceSupportPower =
                dayMasterSupportPowerRule
                        .calculateResourceSupportPower(
                                resourceCapacity
                        );

        DayMasterSupportEvidence resourceEvidence =
                new DayMasterSupportEvidence(
                        DayMasterElementRole.RESOURCE,
                        resourceElement,
                        resourceRawBasePower,
                        resourceExternalRawPower,
                        resourceFinalPower,
                        resourceCapacity,
                        dayMasterSupportPowerRule
                                .resourceSupportRate(),
                        resourceSupportPower,
                        countVisibleSources(
                                resourceSources
                        ),
                        countHiddenSources(
                                resourceSources
                        ),
                        countExposedHiddenSources(
                                resourceSources
                        )
                );

        int totalSupportPower =
                companionSupportPower
                        + resourceSupportPower;

        int visibleSupportSourceCount =
                companionEvidence
                        .visibleSourceCount()

                        + resourceEvidence
                        .visibleSourceCount();

        int hiddenSupportSourceCount =
                companionEvidence
                        .hiddenSourceCount()

                        + resourceEvidence
                        .hiddenSourceCount();

        int exposedHiddenSupportSourceCount =
                companionEvidence
                        .exposedHiddenSourceCount()

                        + resourceEvidence
                        .exposedHiddenSourceCount();

        int supportSourceCount =
                visibleSupportSourceCount
                        + hiddenSupportSourceCount;

        return new DayMasterSupportPowerAnalysis(
                elementStrength.dayMaster(),
                dayMasterElement,
                companionEvidence,
                resourceEvidence,
                companionSupportPower,
                resourceSupportPower,
                totalSupportPower,
                supportSourceCount,
                visibleSupportSourceCount,
                hiddenSupportSourceCount,
                exposedHiddenSupportSourceCount,
                formation.level()
        );
    }

    private ElementPowerEvidence
    requireElementPowerEvidence(

            ElementStrengthAnalysis elementStrength,

            FiveElement element

    ) {

        return elementStrength
                .evidenceOf(
                        element
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "오행 최종 세력 Evidence를 찾을 수 없습니다: "
                                                + element
                                )
                );
    }

    private int countVisibleSources(
            List<DayMasterForceEvidence> sources
    ) {

        return (int) sources
                .stream()
                .filter(
                        DayMasterForceEvidence
                                ::visibleStemSource
                )
                .count();
    }

    private int countHiddenSources(
            List<DayMasterForceEvidence> sources
    ) {

        return (int) sources
                .stream()
                .filter(
                        DayMasterForceEvidence
                                ::hiddenStemSource
                )
                .count();
    }

    private int countExposedHiddenSources(
            List<DayMasterForceEvidence> sources
    ) {

        return (int) sources
                .stream()
                .filter(
                        DayMasterForceEvidence
                                ::exposedHiddenStem
                )
                .count();
    }
}