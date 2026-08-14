package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.model.AnnualDecadeLuckInteraction;
import com.dajeom.model.AnnualDecadeLuckRelationReference;
import com.dajeom.model.AnnualDecadeLuckTransformationReference;
import com.dajeom.model.AnnualLuckContextSignal;
import com.dajeom.model.AnnualLuckNatalRelationAnalysis;
import com.dajeom.model.AnnualLuckNatalRelationReference;
import com.dajeom.model.AnnualLuckNatalTransformationReference;
import com.dajeom.model.AnnualLuckTenGodAnalysis;
import com.dajeom.model.AnnualLuckTenGodEvidence;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.model.UsefulGodAnalysis;
import com.dajeom.rule.EarthlyBranchDirectionalCombinationRule;
import com.dajeom.rule.EarthlyBranchThreeCombinationRule;
import com.dajeom.type.AnnualLuckContextAxis;
import com.dajeom.type.AnnualLuckContextStatus;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchDirectionalCombinationType;
import com.dajeom.type.EarthlyBranchThreeCombinationType;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 14차-6.
 *
 * 세운 Context Signal 생성기.
 *
 * =================================================
 *
 * annualOnly:
 *
 * 5차 격국
 * 7차 용신
 * 10차 십성 복합구조
 * 12차 궁위
 * 14-3 세운 십성
 * 14-4 원국 × 세운
 *
 * 을 교차한다.
 *
 * =================================================
 *
 * withDecade:
 *
 * 14-5 대운 × 세운 Interaction
 * 13-6 해당 대운 Context
 * 원국 + 대운 + 세운 3층 관계
 *
 * 를 추가한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 하나의 종합 점수를 만들지 않는다.
 *
 * 관계 존재와 실제 합화를 구분한다.
 *
 * 특히 원국 + 대운 + 세운으로
 * 삼합/방합이 완성되었다고 해서
 * 여기서 즉시 activated=true로 승격시키지 않는다.
 *
 * 14-6은 Context 계층이므로
 * 3층 완전조합 존재는 별도 후보 Evidence로 보존한다.
 */
@Component
public class AnnualLuckContextSignalAnalyzer {

    private final EarthlyBranchThreeCombinationRule
            earthlyBranchThreeCombinationRule;

    private final EarthlyBranchDirectionalCombinationRule
            earthlyBranchDirectionalCombinationRule;

    public AnnualLuckContextSignalAnalyzer(

            EarthlyBranchThreeCombinationRule
                    earthlyBranchThreeCombinationRule,

            EarthlyBranchDirectionalCombinationRule
                    earthlyBranchDirectionalCombinationRule

    ) {

        this.earthlyBranchThreeCombinationRule =
                Objects.requireNonNull(
                        earthlyBranchThreeCombinationRule,
                        "EarthlyBranchThreeCombinationRule이 필요합니다."
                );

        this.earthlyBranchDirectionalCombinationRule =
                Objects.requireNonNull(
                        earthlyBranchDirectionalCombinationRule,
                        "EarthlyBranchDirectionalCombinationRule이 필요합니다."
                );
    }

    /**
     * =================================================
     * 세운 공통 Context
     * =================================================
     */
    public List<AnnualLuckContextSignal> analyzeAnnual(

            StructureAnalysis structureAnalysis,

            UsefulGodAnalysis usefulGodAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            AnnualLuckTenGodAnalysis annualTenGodAnalysis,

            AnnualLuckNatalRelationAnalysis natalRelationAnalysis

    ) {

        validateAnnualInputs(
                structureAnalysis,
                usefulGodAnalysis,
                tenGodCompositeAnalysis,
                palaceAnalysis,
                annualTenGodAnalysis,
                natalRelationAnalysis
        );

        List<AnnualLuckContextSignal> signals =
                new ArrayList<>();

        addStructureSignals(
                structureAnalysis,
                signals
        );

        addUsefulGodSignals(
                usefulGodAnalysis,
                annualTenGodAnalysis,
                signals
        );

        addTenGodSignals(
                tenGodCompositeAnalysis,
                annualTenGodAnalysis,
                signals
        );

        addPalaceSignals(
                natalRelationAnalysis,
                signals
        );

        addNatalRelationSignals(
                natalRelationAnalysis,
                signals
        );

        addNatalTransformationSignals(
                natalRelationAnalysis,
                signals
        );

        return sortSignals(
                signals
        );
    }

    /**
     * =================================================
     * 특정 대운 Segment Context
     * =================================================
     */
    public List<AnnualLuckContextSignal> analyzeWithDecade(

            PalaceAnalysis palaceAnalysis,

            AnnualLuckTenGodAnalysis annualTenGodAnalysis,

            AnnualLuckNatalRelationAnalysis natalRelationAnalysis,

            AnnualDecadeLuckInteraction decadeInteraction,

            DecadeLuckPeriodContextAnalysis decadeContext

    ) {

        Objects.requireNonNull(
                palaceAnalysis,
                "14-6 Segment 분석에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualTenGodAnalysis,
                "14-6 Segment 분석에는 AnnualLuckTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "14-6 Segment 분석에는 AnnualLuckNatalRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeInteraction,
                "14-6 Segment 분석에는 AnnualDecadeLuckInteraction이 필요합니다."
        );

        Objects.requireNonNull(
                decadeContext,
                "14-6 Segment 분석에는 DecadeLuckPeriodContextAnalysis가 필요합니다."
        );

        if (
                annualTenGodAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "세운 십성과 원국 Palace의 일간이 일치하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "세운 원국 관계와 Palace의 일간이 일치하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.natalAnalysis()
                        != palaceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "14-4 관계 분석이 현재 PalaceAnalysis를 참조하지 않습니다."
            );
        }

        if (
                annualTenGodAnalysis.period()
                        != natalRelationAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "14-3과 14-4가 서로 다른 세운 Period를 참조합니다."
            );
        }

        if (
                decadeInteraction.annualPeriod()
                        != annualTenGodAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "14-5 Interaction이 현재 세운 Period를 참조하지 않습니다."
            );
        }

        if (
                decadeContext.period()
                        != decadeInteraction.decadePeriod()
        ) {

            throw new IllegalArgumentException(
                    "13-6 대운 Context와 14-5 Interaction의 대운 Period가 일치하지 않습니다."
            );
        }

        if (
                decadeContext.dayMaster()
                        != annualTenGodAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "13-6 대운 Context와 14차 세운의 일간이 일치하지 않습니다."
            );
        }

        List<AnnualLuckContextSignal> signals =
                new ArrayList<>();

        /*
         * =================================================
         * 13-6 Context 연결 자체를 명시적으로 보존.
         * =================================================
         *
         * 이 Signal은 길흉을 뜻하지 않는다.
         *
         * 현재 세운 Segment가
         * 정확히 어떤 13-6 대운 Context 위에서
         * 해석되는지를 기록한다.
         */

        signals.add(
                signal(
                        AnnualLuckContextAxis
                                .DECADE_INTERACTION,

                        AnnualLuckContextStatus
                                .QUALIFIED,

                        "DECADE_CONTEXT_LINK_"
                                + decadeInteraction
                                .decadeSequenceNumber(),

                        "현재 세운 구간에 해당 대운 Context가 연결됨",

                        Set.of(),

                        Set.of(),

                        List.of(
                                "14-5 대운-세운 Interaction의 대운 순번은 "
                                        + decadeInteraction
                                        .decadeSequenceNumber()
                                        + "입니다.",

                                "13-6 DecadeLuckPeriodContextAnalysis가 동일 DecadeLuckPeriod를 참조합니다."
                        )
                )
        );

        addDecadeInteractionSignals(
                decadeInteraction,
                signals
        );

        addDecadeTransformationSignals(
                decadeInteraction,
                signals
        );

        addThreeLayerSignals(
                palaceAnalysis,
                annualTenGodAnalysis,
                decadeInteraction,
                signals
        );

        return sortSignals(
                signals
        );
    }

    /*
     * =================================================
     * 5차 격국
     * =================================================
     *
     * 세운 십성과 격국 후보를 연결하는
     * 공식 Mapping이 별도로 존재하지 않으므로
     * 임의로 STRUCTURE_SUPPORT /
     * STRUCTURE_PRESSURE를 만들지 않는다.
     *
     * 현재 원국 격국 Context를 보존한다.
     */

    private void addStructureSignals(

            StructureAnalysis structureAnalysis,

            List<AnnualLuckContextSignal> signals

    ) {

        AnnualLuckContextStatus status =
                structureAnalysis
                        .requiresSpecialReview()

                        ? AnnualLuckContextStatus
                        .QUALIFIED

                        : AnnualLuckContextStatus
                        .QUALIFIED;

        signals.add(
                signal(
                        AnnualLuckContextAxis
                                .STRUCTURE,

                        status,

                        "NATAL_STRUCTURE_CONTEXT",

                        "원국 격국 Context를 세운 해석의 기준으로 유지",

                        Set.of(),

                        Set.of(),

                        List.of(
                                "원국 5차 주격 후보는 "
                                        + structureAnalysis
                                        .primaryNameKorean()
                                        + "입니다.",

                                "현재 격국 판정 상태는 "
                                        + structureAnalysis
                                        .statusKorean()
                                        + "입니다.",

                                structureAnalysis
                                        .requiresSpecialReview()

                                        ? "특수격 또는 추가 검토 가능성이 있어 세운만으로 격국 상태를 단정하지 않습니다."

                                        : "세운 유입만으로 원국 격국 자체를 재판정하지 않습니다."
                        )
                )
        );
    }

    /*
     * =================================================
     * 7차 용신
     * =================================================
     */

    private void addUsefulGodSignals(

            UsefulGodAnalysis usefulGodAnalysis,

            AnnualLuckTenGodAnalysis annualTenGodAnalysis,

            List<AnnualLuckContextSignal> signals

    ) {

        FiveElement stemElement =
                annualTenGodAnalysis
                        .period()
                        .stem()
                        .getElement();

        FiveElement branchElement =
                annualTenGodAnalysis
                        .period()
                        .branch()
                        .getElement();

        addUsefulGodElementSignals(
                "VISIBLE_STEM",
                "세운 천간",
                stemElement,
                usefulGodAnalysis,
                signals
        );

        addUsefulGodElementSignals(
                "EARTHLY_BRANCH",
                "세운 지지",
                branchElement,
                usefulGodAnalysis,
                signals
        );
    }

    private void addUsefulGodElementSignals(

            String sourceCode,

            String sourceName,

            FiveElement element,

            UsefulGodAnalysis usefulGodAnalysis,

            List<AnnualLuckContextSignal> signals

    ) {

        List<FiveElement> usefulElements =
                usefulGodAnalysis
                        .relationAnalysis()
                        .usefulGodElements();

        List<FiveElement> favorableElements =
                usefulGodAnalysis
                        .relationAnalysis()
                        .favorableGodElements();

        List<FiveElement> unfavorableElements =
                usefulGodAnalysis
                        .relationAnalysis()
                        .unfavorableGodElements();

        if (
                usefulElements.contains(
                        element
                )
        ) {

            signals.add(
                    signal(
                            AnnualLuckContextAxis
                                    .USEFUL_GOD,

                            AnnualLuckContextStatus
                                    .SUPPORTED,

                            "PRIMARY_USEFUL_ELEMENT_ENTERED_"
                                    + sourceCode
                                    + "_"
                                    + element.name(),

                            sourceName
                                    + "에서 용신 계열 오행이 유입됨",

                            Set.of(),

                            Set.of(),

                            List.of(
                                    sourceName
                                            + "의 대표 오행은 "
                                            + element
                                            .getKoreanName()
                                            + "입니다.",

                                    "7차 용신 관계 분석에서 "
                                            + element
                                            .getKoreanName()
                                            + "이 용신 계열에 포함되어 있습니다."
                            )
                    )
            );
        }

        if (
                favorableElements.contains(
                        element
                )
        ) {

            signals.add(
                    signal(
                            AnnualLuckContextAxis
                                    .USEFUL_GOD,

                            AnnualLuckContextStatus
                                    .SUPPORTED,

                            "FAVORABLE_ELEMENT_ENTERED_"
                                    + sourceCode
                                    + "_"
                                    + element.name(),

                            sourceName
                                    + "에서 희신 계열 오행이 유입됨",

                            Set.of(),

                            Set.of(),

                            List.of(
                                    sourceName
                                            + "의 대표 오행은 "
                                            + element
                                            .getKoreanName()
                                            + "입니다.",

                                    "7차 용신 관계 분석에서 해당 오행이 희신 계열에 포함되어 있습니다."
                            )
                    )
            );
        }

        if (
                unfavorableElements.contains(
                        element
                )
        ) {

            signals.add(
                    signal(
                            AnnualLuckContextAxis
                                    .USEFUL_GOD,

                            AnnualLuckContextStatus
                                    .CONFLICTED,

                            "UNFAVORABLE_ELEMENT_ENTERED_"
                                    + sourceCode
                                    + "_"
                                    + element.name(),

                            sourceName
                                    + "에서 기피 오행 계열이 유입됨",

                            Set.of(),

                            Set.of(),

                            List.of(
                                    sourceName
                                            + "의 대표 오행은 "
                                            + element
                                            .getKoreanName()
                                            + "입니다.",

                                    "7차 용신 관계 분석에서 해당 오행이 기피 계열에 포함되어 있습니다.",

                                    "용신·희신·기피 목록의 중첩 가능성을 제거하지 않고 각각의 Evidence를 그대로 보존합니다."
                            )
                    )
            );
        }

        if (
                !usefulElements.contains(
                        element
                )
                        && !favorableElements.contains(
                        element
                )
                        && !unfavorableElements.contains(
                        element
                )
        ) {

            signals.add(
                    signal(
                            AnnualLuckContextAxis
                                    .USEFUL_GOD,

                            AnnualLuckContextStatus
                                    .QUALIFIED,

                            "NEUTRAL_USEFUL_GOD_RELATION_"
                                    + sourceCode
                                    + "_"
                                    + element.name(),

                            sourceName
                                    + " 오행이 용신 관계의 직접 분류에 포함되지 않음",

                            Set.of(),

                            Set.of(),

                            List.of(
                                    sourceName
                                            + "의 대표 오행은 "
                                            + element
                                            .getKoreanName()
                                            + "입니다.",

                                    "현재 7차 용신·희신·기피 목록의 직접 분류에는 포함되지 않습니다."
                            )
                    )
            );
        }
    }

    /*
     * =================================================
     * 10차 십성 복합구조
     * =================================================
     */

    private void addTenGodSignals(

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            AnnualLuckTenGodAnalysis annualTenGodAnalysis,

            List<AnnualLuckContextSignal> signals

    ) {

        for (
                AnnualLuckTenGodEvidence evidence
                : annualTenGodAnalysis.evidences()
        ) {

            StructureTenGodType tenGodType =
                    evidence.tenGodType();

            String sourceCode =
                    evidence
                            .sourceType()
                            .name();

            if (
                    tenGodCompositeAnalysis
                            .present(
                                    tenGodType
                            )
            ) {

                signals.add(
                        signal(
                                AnnualLuckContextAxis
                                        .TEN_GOD,

                                AnnualLuckContextStatus
                                        .SUPPORTED,

                                "NATAL_TEN_GOD_REINFORCED_"
                                        + sourceCode
                                        + "_"
                                        + tenGodType.name(),

                                "세운에서 원국에 이미 존재하는 십성이 다시 유입됨",

                                Set.of(),

                                Set.of(
                                        tenGodType
                                ),

                                List.of(
                                        "세운 "
                                                + sourceCode
                                                + "의 십성은 "
                                                + tenGodType
                                                .koreanName()
                                                + "입니다.",

                                        "10차 원국 십성 분포에도 동일 십성이 존재합니다.",

                                        "존재 강화 Evidence이며 그 자체를 길흉 점수로 환산하지 않습니다."
                                )
                        )
                );

            } else {

                signals.add(
                        signal(
                                AnnualLuckContextAxis
                                        .TEN_GOD,

                                AnnualLuckContextStatus
                                        .QUALIFIED,

                                "NEW_TEN_GOD_FACTOR_ENTERED_"
                                        + sourceCode
                                        + "_"
                                        + tenGodType.name(),

                                "원국에서 부재했던 십성 요인이 세운에서 유입됨",

                                Set.of(),

                                Set.of(
                                        tenGodType
                                ),

                                List.of(
                                        "세운 "
                                                + sourceCode
                                                + "의 십성은 "
                                                + tenGodType
                                                .koreanName()
                                                + "입니다.",

                                        "10차 원국 십성 분포에서는 동일 십성이 직접 존재하지 않습니다.",

                                        "새 유입 요인이므로 조건부 Context로 보존합니다."
                                )
                        )
                );
            }
        }
    }

    /*
     * =================================================
     * 12차 Palace
     * =================================================
     */

    private void addPalaceSignals(

            AnnualLuckNatalRelationAnalysis natalRelationAnalysis,

            List<AnnualLuckContextSignal> signals

    ) {

        Set<PalacePosition> touched =
                new LinkedHashSet<>();

        for (
                AnnualLuckNatalRelationReference relation
                : natalRelationAnalysis.relations()
        ) {

            touched.addAll(
                    relation.natalPositions()
            );
        }

        for (
                AnnualLuckNatalTransformationReference transformation
                : natalRelationAnalysis.transformations()
        ) {

            touched.addAll(
                    transformation.natalPositions()
            );
        }

        for (
                PalacePosition position
                : touched
        ) {

            signals.add(
                    signal(
                            AnnualLuckContextAxis
                                    .PALACE,

                            AnnualLuckContextStatus
                                    .QUALIFIED,

                            "CORE_PALACE_CONTACT_"
                                    + position.name(),

                            "세운이 원국 "
                                    + position.name()
                                    + " 궁위와 직접 접촉",

                            Set.of(
                                    position
                            ),

                            Set.of(),

                            List.of(
                                    "14-4 원국 × 세운 관계 또는 합화 Context에 "
                                            + position.name()
                                            + " 궁위가 포함되어 있습니다.",

                                    "궁위 접촉 자체를 가족·직업·사건의 확정 결과로 변환하지 않습니다."
                            )
                    )
            );
        }
    }

    /*
     * =================================================
     * 14-4 원국 × 세운 관계
     * =================================================
     */

    private void addNatalRelationSignals(

            AnnualLuckNatalRelationAnalysis natalRelationAnalysis,

            List<AnnualLuckContextSignal> signals

    ) {

        for (
                AnnualLuckNatalRelationReference relation
                : natalRelationAnalysis.relations()
        ) {

            AnnualLuckContextStatus status =
                    relationStatus(
                            relation.relationCode()
                    );

            signals.add(
                    signal(
                            AnnualLuckContextAxis
                                    .NATAL_RELATION,

                            status,

                            "NATAL_"
                                    + relation
                                    .layer()
                                    .name()
                                    + "_"
                                    + sanitize(
                                    relation
                                            .relationCode()
                            )
                                    + "_"
                                    + positionKey(
                                    relation
                                            .natalPositions()
                            ),

                            "원국과 세운 사이에 "
                                    + relation.relationName()
                                    + " 관계가 성립",

                            relation.natalPositions(),

                            Set.of(),

                            List.of(
                                    "14-4 관계 Layer는 "
                                            + relation
                                            .layer()
                                            .name()
                                            + "입니다.",

                                    "관계 Code는 "
                                            + relation
                                            .relationCode()
                                            + "입니다.",

                                    "동일 글자 사이의 중첩 관계는 삭제하지 않고 각각 보존합니다."
                            )
                    )
            );
        }
    }

    /*
     * =================================================
     * 14-4 원국 × 세운 합화
     * =================================================
     */

    private void addNatalTransformationSignals(

            AnnualLuckNatalRelationAnalysis natalRelationAnalysis,

            List<AnnualLuckContextSignal> signals

    ) {

        for (
                AnnualLuckNatalTransformationReference transformation
                : natalRelationAnalysis.transformations()
        ) {

            String baseCode =
                    "NATAL_TRANSFORMATION_"
                            + sanitize(
                            transformation
                                    .transformationCode()
                    )
                            + "_"
                            + positionKey(
                            transformation
                                    .natalPositions()
                    );

            signals.add(
                    signal(
                            AnnualLuckContextAxis
                                    .TRANSFORMATION,

                            transformation.activated()
                                    ? AnnualLuckContextStatus.SUPPORTED
                                    : AnnualLuckContextStatus.QUALIFIED,

                            baseCode
                                    + "_ACTIVATION",

                            transformation.activated()
                                    ? "원국 × 세운 합화의 목표 오행 활성 조건이 성립"
                                    : "원국 × 세운 결합 Context는 있으나 실제 합화 활성은 확정되지 않음",

                            transformation.natalPositions(),

                            Set.of(),

                            List.of(
                                    "14-4 Transformation은 "
                                            + transformation
                                            .transformationName()
                                            + "입니다.",

                                    transformation.hasTargetElement()
                                            ? "목표 오행은 "
                                            + transformation
                                            .targetElementName()
                                            + "입니다."
                                            : "별도 목표 오행이 지정되지 않았습니다.",

                                    "activated = "
                                            + transformation.activated()
                            )
                    )
            );

            if (
                    transformation.originalFunctionReduced()
            ) {

                signals.add(
                        signal(
                                AnnualLuckContextAxis
                                        .TRANSFORMATION,

                                AnnualLuckContextStatus
                                        .WEAKENED,

                                baseCode
                                        + "_ORIGINAL_FUNCTION_REDUCED",

                                "원국 × 세운 합화 Context에서 원래 기능 감소가 확인됨",

                                transformation.natalPositions(),

                                Set.of(),

                                List.of(
                                        "14-4 Transformation의 originalFunctionReduced가 true입니다.",

                                        "목표 오행 활성 여부와 원기능 감소 여부를 서로 다른 Evidence로 보존합니다."
                                )
                        )
                );
            }
        }
    }

    /*
     * =================================================
     * 14-5 대운 × 세운 관계
     * =================================================
     */

    private void addDecadeInteractionSignals(

            AnnualDecadeLuckInteraction interaction,

            List<AnnualLuckContextSignal> signals

    ) {

        for (
                AnnualDecadeLuckRelationReference relation
                : interaction.relations()
        ) {

            AnnualLuckContextStatus status =
                    relationStatus(
                            relation.relationCode()
                    );

            signals.add(
                    signal(
                            AnnualLuckContextAxis
                                    .DECADE_INTERACTION,

                            status,

                            "DECADE_"
                                    + interaction
                                    .decadeSequenceNumber()
                                    + "_"
                                    + relation
                                    .layer()
                                    .name()
                                    + "_"
                                    + sanitize(
                                    relation
                                            .relationCode()
                            ),

                            "대운과 세운 사이에 "
                                    + relation
                                    .relationName()
                                    + " 관계가 성립",

                            Set.of(),

                            Set.of(),

                            List.of(
                                    "대운 순번은 "
                                            + interaction
                                            .decadeSequenceNumber()
                                            + "입니다.",

                                    "14-5 관계 Code는 "
                                            + relation
                                            .relationCode()
                                            + "입니다.",

                                    interaction.timingUncertain()
                                            ? "이 대운 Segment는 교운시점 불확실성을 포함합니다."
                                            : "이 대운 Segment의 시간 적용범위는 14-5 Overlap으로 관리됩니다."
                            )
                    )
            );
        }
    }

    /*
     * =================================================
     * 14-5 대운 × 세운 합화
     * =================================================
     */

    private void addDecadeTransformationSignals(

            AnnualDecadeLuckInteraction interaction,

            List<AnnualLuckContextSignal> signals

    ) {

        for (
                AnnualDecadeLuckTransformationReference transformation
                : interaction.transformations()
        ) {

            String baseCode =
                    "DECADE_"
                            + interaction
                            .decadeSequenceNumber()
                            + "_TRANSFORMATION_"
                            + sanitize(
                            transformation
                                    .transformationCode()
                    );

            signals.add(
                    signal(
                            AnnualLuckContextAxis
                                    .TRANSFORMATION,

                            transformation.activated()
                                    ? AnnualLuckContextStatus.SUPPORTED
                                    : AnnualLuckContextStatus.QUALIFIED,

                            baseCode
                                    + "_ACTIVATION",

                            transformation.activated()
                                    ? "대운 × 세운 합화의 목표 오행 활성 조건이 성립"
                                    : "대운 × 세운 결합은 있으나 실제 합화 활성은 확정되지 않음",

                            Set.of(),

                            Set.of(),

                            List.of(
                                    "14-5 Transformation은 "
                                            + transformation
                                            .transformationName()
                                            + "입니다.",

                                    transformation.hasTargetElement()
                                            ? "목표 오행은 "
                                            + transformation
                                            .targetElementName()
                                            + "입니다."
                                            : "별도 목표 오행이 지정되지 않았습니다.",

                                    "activated = "
                                            + transformation.activated()
                            )
                    )
            );

            if (
                    transformation.originalFunctionReduced()
            ) {

                signals.add(
                        signal(
                                AnnualLuckContextAxis
                                        .TRANSFORMATION,

                                AnnualLuckContextStatus
                                        .WEAKENED,

                                baseCode
                                        + "_ORIGINAL_FUNCTION_REDUCED",

                                "대운 × 세운 합화 Context에서 원래 기능 감소가 확인됨",

                                Set.of(),

                                Set.of(),

                                List.of(
                                        "14-5 Transformation의 originalFunctionReduced가 true입니다.",

                                        "합화 활성과 원기능 감소를 하나의 상태로 합치지 않습니다."
                                )
                        )
                );
            }
        }
    }

    /*
     * =================================================
     * 원국 + 대운 + 세운 3층 관계
     * =================================================
     *
     * 세운/대운은 외부 시간축이고
     * 원국 Palace 하나만 위치를 갖는다.
     *
     * 완전 삼합 / 완전 방합 / 삼형을
     * 확인한다.
     *
     * 완전 삼합·방합은 관계 완성 사실과
     * 실제 합화 활성 여부를 분리한다.
     */

    private void addThreeLayerSignals(

            PalaceAnalysis palaceAnalysis,

            AnnualLuckTenGodAnalysis annualTenGodAnalysis,

            AnnualDecadeLuckInteraction interaction,

            List<AnnualLuckContextSignal> signals

    ) {

        EarthlyBranch annualBranch =
                annualTenGodAnalysis
                        .period()
                        .branch();

        EarthlyBranch decadeBranch =
                interaction
                        .decadePeriod()
                        .pillar()
                        .branch();

        for (
                PalacePosition position
                : palaceAnalysis.knownPositions()
        ) {

            EarthlyBranch natalBranch =
                    palaceAnalysis
                            .positionAnalysis()
                            .branchEvidenceOf(
                                    position
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "원국 Palace 지지 Evidence가 없습니다: "
                                                            + position
                                            )
                            )
                            .branch();

            addThreeLayerThreeCombination(
                    position,
                    natalBranch,
                    decadeBranch,
                    annualBranch,
                    interaction,
                    signals
            );

            addThreeLayerDirectionalCombination(
                    position,
                    natalBranch,
                    decadeBranch,
                    annualBranch,
                    interaction,
                    signals
            );

            if (
                    BranchRelationTable
                            .isThreePunishment(
                                    natalBranch,
                                    decadeBranch,
                                    annualBranch
                            )
            ) {

                signals.add(
                        signal(
                                AnnualLuckContextAxis
                                        .THREE_LAYER_RELATION,

                                AnnualLuckContextStatus
                                        .CONFLICTED,

                                "THREE_LAYER_PUNISHMENT_"
                                        + interaction
                                        .decadeSequenceNumber()
                                        + "_"
                                        + position.name(),

                                "원국·대운·세운 3층에서 삼형 구조가 완성됨",

                                Set.of(
                                        position
                                ),

                                Set.of(),

                                List.of(
                                        "원국 "
                                                + position.name()
                                                + " 지지, 현재 대운 지지, 세운 지지가 함께 삼형 구조를 구성합니다.",

                                        "이 Evidence는 관계 충돌을 의미하며 사건 발생 확률이나 길흉 점수가 아닙니다."
                                )
                        )
                );
            }
        }
    }

    private void addThreeLayerThreeCombination(

            PalacePosition position,

            EarthlyBranch natalBranch,

            EarthlyBranch decadeBranch,

            EarthlyBranch annualBranch,

            AnnualDecadeLuckInteraction interaction,

            List<AnnualLuckContextSignal> signals

    ) {

        earthlyBranchThreeCombinationRule
                .findCompleteCombinationType(
                        natalBranch,
                        decadeBranch,
                        annualBranch
                )
                .ifPresent(
                        type -> {

                            String baseCode =
                                    "THREE_LAYER_THREE_COMBINATION_"
                                            + interaction
                                            .decadeSequenceNumber()
                                            + "_"
                                            + position.name()
                                            + "_"
                                            + type.name();

                            signals.add(
                                    signal(
                                            AnnualLuckContextAxis
                                                    .THREE_LAYER_RELATION,

                                            AnnualLuckContextStatus
                                                    .QUALIFIED,

                                            baseCode,

                                            "원국·대운·세운 3층에서 삼합이 완성됨",

                                            Set.of(
                                                    position
                                            ),

                                            Set.of(),

                                            threeCombinationReasons(
                                                    position,
                                                    type
                                            )
                                    )
                            );

                            /*
                             * 완전 삼합 존재와 실제 합화는 분리.
                             *
                             * 14-6 Context 모델에는
                             * 별도의 activated boolean을 저장하지 않으므로
                             * 실제 활성로 승격하지 않고
                             * 합화 후보 Context로 보존한다.
                             */

                            signals.add(
                                    signal(
                                            AnnualLuckContextAxis
                                                    .TRANSFORMATION,

                                            AnnualLuckContextStatus
                                                    .QUALIFIED,

                                            baseCode
                                                    + "_TRANSFORMATION_CANDIDATE",

                                            "3층 삼합 완성에 따른 합화 후보 Context",

                                            Set.of(
                                                    position
                                            ),

                                            Set.of(),

                                            List.of(
                                                    "원국 한 지지 + 대운 지지 + 세운 지지로 완전 삼합이 성립합니다.",

                                                    "완전 조합 존재와 실제 목표 오행 활성은 동일한 개념이 아닙니다.",

                                                    "14-6 Context 단계에서는 9차 전체 경쟁·방해 통합 결과 없이 activated=true를 추정하지 않습니다."
                                            )
                                    )
                            );
                        }
                );
    }

    private void addThreeLayerDirectionalCombination(

            PalacePosition position,

            EarthlyBranch natalBranch,

            EarthlyBranch decadeBranch,

            EarthlyBranch annualBranch,

            AnnualDecadeLuckInteraction interaction,

            List<AnnualLuckContextSignal> signals

    ) {

        earthlyBranchDirectionalCombinationRule
                .findCompleteCombinationType(
                        natalBranch,
                        decadeBranch,
                        annualBranch
                )
                .ifPresent(
                        type -> {

                            String baseCode =
                                    "THREE_LAYER_DIRECTIONAL_COMBINATION_"
                                            + interaction
                                            .decadeSequenceNumber()
                                            + "_"
                                            + position.name()
                                            + "_"
                                            + type.name();

                            signals.add(
                                    signal(
                                            AnnualLuckContextAxis
                                                    .THREE_LAYER_RELATION,

                                            AnnualLuckContextStatus
                                                    .QUALIFIED,

                                            baseCode,

                                            "원국·대운·세운 3층에서 방합이 완성됨",

                                            Set.of(
                                                    position
                                            ),

                                            Set.of(),

                                            directionalCombinationReasons(
                                                    position,
                                                    type
                                            )
                                    )
                            );

                            signals.add(
                                    signal(
                                            AnnualLuckContextAxis
                                                    .TRANSFORMATION,

                                            AnnualLuckContextStatus
                                                    .QUALIFIED,

                                            baseCode
                                                    + "_TRANSFORMATION_CANDIDATE",

                                            "3층 방합 완성에 따른 합화 후보 Context",

                                            Set.of(
                                                    position
                                            ),

                                            Set.of(),

                                            List.of(
                                                    "원국 한 지지 + 대운 지지 + 세운 지지로 완전 방합이 성립합니다.",

                                                    "방합 관계 완성과 실제 목표 오행 활성 상태를 분리합니다.",

                                                    "14-6 Context 단계에서는 실제 activated 상태를 임의 생성하지 않습니다."
                                            )
                                    )
                            );
                        }
                );
    }

    private List<String> threeCombinationReasons(

            PalacePosition position,

            EarthlyBranchThreeCombinationType type

    ) {

        return List.of(
                "원국 "
                        + position.name()
                        + " 궁위 지지와 대운·세운 지지가 함께 완전 삼합을 구성합니다.",

                "삼합 Type은 "
                        + type.name()
                        + "입니다.",

                "원국 Palace 위치는 보존하고 대운·세운에는 가짜 Palace 위치를 부여하지 않습니다."
        );
    }

    private List<String> directionalCombinationReasons(

            PalacePosition position,

            EarthlyBranchDirectionalCombinationType type

    ) {

        return List.of(
                "원국 "
                        + position.name()
                        + " 궁위 지지와 대운·세운 지지가 함께 완전 방합을 구성합니다.",

                "방합 Type은 "
                        + type.name()
                        + "입니다.",

                "원국 Palace 위치는 보존하고 대운·세운에는 가짜 Palace 위치를 부여하지 않습니다."
        );
    }

    /*
     * =================================================
     * 입력 정합성
     * =================================================
     */

    private void validateAnnualInputs(

            StructureAnalysis structureAnalysis,

            UsefulGodAnalysis usefulGodAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            AnnualLuckTenGodAnalysis annualTenGodAnalysis,

            AnnualLuckNatalRelationAnalysis natalRelationAnalysis

    ) {

        Objects.requireNonNull(
                structureAnalysis,
                "14-6에는 StructureAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                usefulGodAnalysis,
                "14-6에는 UsefulGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "14-6에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "14-6에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualTenGodAnalysis,
                "14-6에는 AnnualLuckTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "14-6에는 AnnualLuckNatalRelationAnalysis가 필요합니다."
        );

        if (
                annualTenGodAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "14-3 세운 십성과 12차 Palace의 일간이 일치하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "14-4 원국 관계와 12차 Palace의 일간이 일치하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 복합구조와 12차 Palace의 일간이 일치하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.natalAnalysis()
                        != palaceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "14-4 관계 분석이 현재 PalaceAnalysis를 참조하지 않습니다."
            );
        }

        if (
                annualTenGodAnalysis.period()
                        != natalRelationAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "14-3과 14-4가 서로 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        if (
                tenGodCompositeAnalysis.structureAnalysis()
                        != structureAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 복합구조가 현재 5차 StructureAnalysis를 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis.usefulGodAnalysis()
                        != usefulGodAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 복합구조가 현재 7차 UsefulGodAnalysis를 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis.completeChart()
                        != palaceAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "10차와 12차의 원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * Relation -> Context Status
     * =================================================
     *
     * 길흉 판정이 아니다.
     *
     * 충/형/파/해/극처럼
     * 구조적 충돌성이 있는 관계만
     * CONFLICTED로 표시한다.
     *
     * 합·부분조합 등은 QUALIFIED.
     */

    private AnnualLuckContextStatus relationStatus(
            String relationCode
    ) {

        String code =
                relationCode == null
                        ? ""
                        : relationCode
                        .toUpperCase(
                                Locale.ROOT
                        );

        if (
                code.contains(
                        "CLASH"
                )
                        || code.contains(
                        "PUNISHMENT"
                )
                        || code.contains(
                        "HARM"
                )
                        || code.contains(
                        "BREAK"
                )
                        || code.contains(
                        "CONTROL"
                )
        ) {

            return AnnualLuckContextStatus
                    .CONFLICTED;
        }

        return AnnualLuckContextStatus
                .QUALIFIED;
    }

    /*
     * =================================================
     * Signal 생성
     * =================================================
     */

    private AnnualLuckContextSignal signal(

            AnnualLuckContextAxis axis,

            AnnualLuckContextStatus status,

            String code,

            String summary,

            Set<PalacePosition> palacePositions,

            Set<StructureTenGodType> tenGodTypes,

            List<String> reasons

    ) {

        return new AnnualLuckContextSignal(
                axis,
                status,
                code,
                summary,
                palacePositions,
                tenGodTypes,
                reasons
        );
    }

    private List<AnnualLuckContextSignal> sortSignals(
            List<AnnualLuckContextSignal> signals
    ) {

        signals.sort(
                Comparator
                        .comparingInt(
                                (
                                        AnnualLuckContextSignal signal
                                ) ->
                                        signal.axis()
                                                .ordinal()
                        )
                        .thenComparingInt(
                                signal ->
                                        signal.status()
                                                .ordinal()
                        )
                        .thenComparing(
                                AnnualLuckContextSignal
                                        ::code
                        )
        );

        return List.copyOf(
                signals
        );
    }

    private String positionKey(
            Set<PalacePosition> positions
    ) {

        return positions
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                PalacePosition::order
                        )
                )
                .map(
                        Enum::name
                )
                .reduce(
                        (
                                first,
                                second
                        ) ->
                                first
                                        + "_"
                                        + second
                )
                .orElse(
                        "NONE"
                );
    }

    private String sanitize(
            String value
    ) {

        if (
                value == null
                        || value.isBlank()
        ) {

            return "UNKNOWN";
        }

        return value
                .trim()
                .toUpperCase(
                        Locale.ROOT
                )
                .replaceAll(
                        "[^A-Z0-9_]+",
                        "_"
                );
    }
}