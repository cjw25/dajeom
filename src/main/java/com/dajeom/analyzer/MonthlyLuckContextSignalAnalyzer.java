package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckDecadeContextAnalysis;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
import com.dajeom.model.MonthlyAnnualLuckRelationAnalysis;
import com.dajeom.model.MonthlyAnnualLuckRelationReference;
import com.dajeom.model.MonthlyAnnualLuckTransformationReference;
import com.dajeom.model.MonthlyDecadeLuckInteraction;
import com.dajeom.model.MonthlyDecadeLuckRelationReference;
import com.dajeom.model.MonthlyDecadeLuckTransformationReference;
import com.dajeom.model.MonthlyLuckContextSignal;
import com.dajeom.model.MonthlyLuckNatalRelationAnalysis;
import com.dajeom.model.MonthlyLuckNatalRelationReference;
import com.dajeom.model.MonthlyLuckNatalTransformationReference;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.MonthlyLuckTenGodAnalysis;
import com.dajeom.model.MonthlyLuckTenGodEvidence;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.model.UsefulGodAnalysis;
import com.dajeom.rule.EarthlyBranchDirectionalCombinationRule;
import com.dajeom.rule.EarthlyBranchThreeCombinationRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchDirectionalCombinationType;
import com.dajeom.type.EarthlyBranchThreeCombinationType;
import com.dajeom.type.FiveElement;
import com.dajeom.type.MonthlyLuckContextAxis;
import com.dajeom.type.MonthlyLuckContextStatus;
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
 * 정밀화 15차-7.
 *
 * 월운 Context Signal 생성기.
 *
 * =================================================
 *
 * 월운 공통 Context:
 *
 * - 5차 격국
 * - 7차 용신
 * - 10차 십성 복합구조
 * - 12차 궁위
 * - 14차 세운 Context 연결
 * - 15-3 월운 십성
 * - 15-4 원국 × 월운
 * - 15-6 세운 × 월운
 * - 원국 + 세운 + 월운 3층 관계
 *
 * =================================================
 *
 * 대운 Segment Context:
 *
 * - 13-6 해당 대운 Context
 * - 14-6 해당 세운·대운 Context
 * - 15-5 대운 × 월운
 * - 원국 + 대운 + 월운 3층 관계
 * - 대운 + 세운 + 월운 3층 관계
 * - 네 시간층에서 복수 3층 구조가 동시에
 *   존재하는 4층 중첩 Context
 *
 * =================================================
 *
 * 중요:
 *
 * 1.
 * 하나의 종합 점수를 만들지 않는다.
 *
 * 2.
 * 관계 존재와 실제 Transformation 활성은
 * 분리한다.
 *
 * 3.
 * 완전 삼합/방합 존재만으로
 * activated=true를 생성하지 않는다.
 *
 * 4.
 * 원국 PalacePosition은 원국에만 유지한다.
 *
 * 대운 / 세운 / 월운에는
 * 가짜 YEAR / MONTH / DAY / HOUR 위치를
 * 부여하지 않는다.
 *
 * 5.
 * 출생시간 미상일 경우
 * PalaceAnalysis.knownPositions()만 사용한다.
 *
 * 6.
 * 4층 관계는 새로운 명리 관계 공식을
 * 만드는 것이 아니다.
 *
 * 기존 3층 관계 Evidence가 같은 실제 시간 Segment에서
 * 여러 층에 걸쳐 중첩된 사실만 별도 Context로 보존한다.
 */
@Component
public class MonthlyLuckContextSignalAnalyzer {

    private final EarthlyBranchThreeCombinationRule
            earthlyBranchThreeCombinationRule;

    private final EarthlyBranchDirectionalCombinationRule
            earthlyBranchDirectionalCombinationRule;

    public MonthlyLuckContextSignalAnalyzer(

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
     * 월운 전체 공통 Context
     * =================================================
     */
    public List<MonthlyLuckContextSignal> analyzeMonthly(

            StructureAnalysis structureAnalysis,

            UsefulGodAnalysis usefulGodAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

            MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

            MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis

    ) {

        validateMonthlyInputs(
                structureAnalysis,
                usefulGodAnalysis,
                tenGodCompositeAnalysis,
                palaceAnalysis,
                annualLuckAnalysis,
                monthlyTenGodAnalysis,
                natalRelationAnalysis,
                annualRelationAnalysis
        );

        List<MonthlyLuckContextSignal> signals =
                new ArrayList<>();

        addStructureSignals(
                structureAnalysis,
                signals
        );

        addUsefulGodSignals(
                usefulGodAnalysis,
                monthlyTenGodAnalysis,
                signals
        );

        addTenGodSignals(
                tenGodCompositeAnalysis,
                monthlyTenGodAnalysis,
                signals
        );

        addAnnualContextLink(
                annualLuckAnalysis,
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

        addAnnualInteractionSignals(
                annualRelationAnalysis,
                signals
        );

        addAnnualTransformationSignals(
                annualRelationAnalysis,
                signals
        );

        addNatalAnnualMonthlySignals(
                palaceAnalysis,
                annualLuckAnalysis,
                monthlyTenGodAnalysis.period(),
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
    public List<MonthlyLuckContextSignal> analyzeWithDecade(

            PalaceAnalysis palaceAnalysis,

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

            MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

            MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis,

            MonthlyDecadeLuckInteraction decadeInteraction,

            DecadeLuckPeriodContextAnalysis decadeContext,

            AnnualLuckDecadeContextAnalysis annualDecadeContext

    ) {

        validateSegmentInputs(
                palaceAnalysis,
                annualLuckAnalysis,
                monthlyTenGodAnalysis,
                natalRelationAnalysis,
                annualRelationAnalysis,
                decadeInteraction,
                decadeContext,
                annualDecadeContext
        );

        List<MonthlyLuckContextSignal> signals =
                new ArrayList<>();

        addDecadeContextLink(
                decadeInteraction,
                decadeContext,
                annualDecadeContext,
                signals
        );

        addDecadeInteractionSignals(
                decadeInteraction,
                signals
        );

        addDecadeTransformationSignals(
                decadeInteraction,
                signals
        );

        addNatalDecadeMonthlySignals(
                palaceAnalysis,
                monthlyTenGodAnalysis.period(),
                decadeInteraction,
                signals
        );

        addDecadeAnnualMonthlySignals(
                annualLuckAnalysis,
                monthlyTenGodAnalysis.period(),
                decadeInteraction,
                signals
        );

        addFourLayerOverlapSignals(
                palaceAnalysis,
                annualLuckAnalysis,
                monthlyTenGodAnalysis.period(),
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
     * 월운 하나로 원국 격국을 재판정하지 않는다.
     */
    private void addStructureSignals(

            StructureAnalysis structureAnalysis,

            List<MonthlyLuckContextSignal> signals

    ) {

        signals.add(
                signal(
                        MonthlyLuckContextAxis
                                .STRUCTURE,

                        MonthlyLuckContextStatus
                                .QUALIFIED,

                        "NATAL_STRUCTURE_CONTEXT",

                        "원국 격국 Context를 월운 해석의 기준으로 유지",

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

                                        ? "특수격 또는 추가 검토 가능성이 있어 월운 유입만으로 원국 격국을 확정 변경하지 않습니다."

                                        : "월운 유입만으로 원국 격국 자체를 재판정하지 않습니다."
                        )
                )
        );
    }

    /*
     * =================================================
     * 7차 용신·희신·기피
     * =================================================
     */
    private void addUsefulGodSignals(

            UsefulGodAnalysis usefulGodAnalysis,

            MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

            List<MonthlyLuckContextSignal> signals

    ) {

        MonthlyLuckPeriod period =
                monthlyTenGodAnalysis.period();

        addUsefulGodElementSignals(
                "VISIBLE_STEM",
                "월운 천간",
                period
                        .stem()
                        .getElement(),
                usefulGodAnalysis,
                signals
        );

        addUsefulGodElementSignals(
                "EARTHLY_BRANCH",
                "월운 지지",
                period
                        .branch()
                        .getElement(),
                usefulGodAnalysis,
                signals
        );
    }

    private void addUsefulGodElementSignals(

            String sourceCode,

            String sourceName,

            FiveElement element,

            UsefulGodAnalysis usefulGodAnalysis,

            List<MonthlyLuckContextSignal> signals

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
                            MonthlyLuckContextAxis
                                    .USEFUL_GOD,

                            MonthlyLuckContextStatus
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

                                    "7차 용신 관계 분석에서 해당 오행이 용신 계열에 포함되어 있습니다."
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
                            MonthlyLuckContextAxis
                                    .USEFUL_GOD,

                            MonthlyLuckContextStatus
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
                            MonthlyLuckContextAxis
                                    .USEFUL_GOD,

                            MonthlyLuckContextStatus
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

                                    "용신·희신·기피 후보가 중첩되는 경우 한쪽으로 제거하지 않고 각각의 Evidence를 보존합니다."
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
                            MonthlyLuckContextAxis
                                    .USEFUL_GOD,

                            MonthlyLuckContextStatus
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
     * 10차 원국 십성 × 15-3 월운 십성
     * =================================================
     */
    private void addTenGodSignals(

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

            List<MonthlyLuckContextSignal> signals

    ) {

        for (
                MonthlyLuckTenGodEvidence evidence
                : monthlyTenGodAnalysis.evidences()
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
                                MonthlyLuckContextAxis
                                        .TEN_GOD,

                                MonthlyLuckContextStatus
                                        .SUPPORTED,

                                "NATAL_TEN_GOD_REINFORCED_"
                                        + sourceCode
                                        + "_"
                                        + tenGodType.name(),

                                "월운에서 원국에 이미 존재하는 십성이 다시 유입됨",

                                Set.of(),

                                Set.of(
                                        tenGodType
                                ),

                                List.of(
                                        "15-3 월운 "
                                                + sourceCode
                                                + " 십성은 "
                                                + tenGodType
                                                .koreanName()
                                                + "입니다.",

                                        "10차 원국 십성 복합구조에도 동일 십성이 존재합니다.",

                                        "존재 강화 Evidence이며 길흉 점수로 환산하지 않습니다."
                                )
                        )
                );

            } else {

                signals.add(
                        signal(
                                MonthlyLuckContextAxis
                                        .TEN_GOD,

                                MonthlyLuckContextStatus
                                        .QUALIFIED,

                                "NEW_TEN_GOD_FACTOR_ENTERED_"
                                        + sourceCode
                                        + "_"
                                        + tenGodType.name(),

                                "원국에서 직접 존재하지 않았던 십성 요인이 월운에서 유입됨",

                                Set.of(),

                                Set.of(
                                        tenGodType
                                ),

                                List.of(
                                        "15-3 월운 "
                                                + sourceCode
                                                + " 십성은 "
                                                + tenGodType
                                                .koreanName()
                                                + "입니다.",

                                        "10차 원국 십성 복합구조에서는 동일 십성이 직접 존재하지 않습니다.",

                                        "새 유입 요인을 조건부 Context로 보존합니다."
                                )
                        )
                );
            }
        }
    }

    /*
     * =================================================
     * 14차 세운 Context 연결
     * =================================================
     *
     * 14차 Signal을 월운 Signal로 복사하지 않는다.
     *
     * 현재 월운이 동일 AnnualLuckAnalysis 위에서
     * 분석되고 있다는 연결만 보존한다.
     */
    private void addAnnualContextLink(

            AnnualLuckAnalysis annualLuckAnalysis,

            List<MonthlyLuckContextSignal> signals

    ) {

        signals.add(
                signal(
                        MonthlyLuckContextAxis
                                .ANNUAL_INTERACTION,

                        MonthlyLuckContextStatus
                                .QUALIFIED,

                        "ANNUAL_CONTEXT_LINK",

                        "현재 월운이 동일 세운 Context 위에서 해석됨",

                        Set.of(),

                        Set.of(),

                        List.of(
                                "현재 월운의 AnnualLuckPeriod는 "
                                        + annualLuckAnalysis
                                        .period()
                                        .targetYear()
                                        + " 명리연도입니다.",

                                "14차 세운 공통 Context Signal 수는 "
                                        + annualLuckAnalysis
                                        .annualContextSignals()
                                        .size()
                                        + "개입니다.",

                                "14차 Signal을 월운 Signal로 복제하지 않고 상위 시간층 Context로 유지합니다."
                        )
                )
        );
    }

    /*
     * =================================================
     * 12차 Palace 접촉
     * =================================================
     */
    private void addPalaceSignals(

            MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

            List<MonthlyLuckContextSignal> signals

    ) {

        Set<PalacePosition> touched =
                new LinkedHashSet<>();

        for (
                MonthlyLuckNatalRelationReference relation
                : natalRelationAnalysis.relations()
        ) {

            touched.addAll(
                    relation.natalPositions()
            );
        }

        for (
                MonthlyLuckNatalTransformationReference transformation
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
                            MonthlyLuckContextAxis
                                    .PALACE,

                            MonthlyLuckContextStatus
                                    .QUALIFIED,

                            "CORE_PALACE_CONTACT_"
                                    + position.name(),

                            "월운이 원국 "
                                    + position.name()
                                    + " 궁위와 직접 접촉",

                            Set.of(
                                    position
                            ),

                            Set.of(),

                            List.of(
                                    "15-4 원국 × 월운 관계 또는 Transformation Context에 "
                                            + position.name()
                                            + " 궁위가 포함되어 있습니다.",

                                    "궁위 접촉을 구체적인 가족·직업·관계 사건 발생으로 변환하지 않습니다."
                            )
                    )
            );
        }
    }

    /*
     * =================================================
     * 15-4 원국 × 월운 관계
     * =================================================
     */
    private void addNatalRelationSignals(

            MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

            List<MonthlyLuckContextSignal> signals

    ) {

        for (
                MonthlyLuckNatalRelationReference relation
                : natalRelationAnalysis.relations()
        ) {

            signals.add(
                    signal(
                            MonthlyLuckContextAxis
                                    .NATAL_RELATION,

                            relationStatus(
                                    relation.relationCode()
                            ),

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

                            "원국과 월운 사이에 "
                                    + relation.relationName()
                                    + " 관계가 성립",

                            relation.natalPositions(),

                            Set.of(),

                            List.of(
                                    "15-4 관계 Layer는 "
                                            + relation
                                            .layer()
                                            .name()
                                            + "입니다.",

                                    "관계 Code는 "
                                            + relation
                                            .relationCode()
                                            + "입니다.",

                                    "같은 글자 조합에 중첩 관계가 존재하면 하나로 축약하지 않습니다."
                            )
                    )
            );
        }
    }

    /*
     * =================================================
     * 15-4 Transformation
     * =================================================
     */
    private void addNatalTransformationSignals(

            MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

            List<MonthlyLuckContextSignal> signals

    ) {

        for (
                MonthlyLuckNatalTransformationReference transformation
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
                            MonthlyLuckContextAxis
                                    .TRANSFORMATION,

                            transformation.activated()
                                    ? MonthlyLuckContextStatus.SUPPORTED
                                    : MonthlyLuckContextStatus.QUALIFIED,

                            baseCode
                                    + "_ACTIVATION",

                            transformation.activated()
                                    ? "원국 × 월운 Transformation의 목표 오행 활성 조건이 성립"
                                    : "원국 × 월운 결합 Context는 있으나 실제 활성은 확정되지 않음",

                            transformation.natalPositions(),

                            Set.of(),

                            List.of(
                                    "15-4 Transformation은 "
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
                                MonthlyLuckContextAxis
                                        .TRANSFORMATION,

                                MonthlyLuckContextStatus
                                        .WEAKENED,

                                baseCode
                                        + "_ORIGINAL_FUNCTION_REDUCED",

                                "원국 × 월운 Transformation에서 원래 기능 감소가 확인됨",

                                transformation.natalPositions(),

                                Set.of(),

                                List.of(
                                        "15-4 Transformation의 originalFunctionReduced가 true입니다.",

                                        "목표 오행 활성과 원기능 감소를 서로 다른 Evidence로 보존합니다."
                                )
                        )
                );
            }
        }
    }

    /*
     * =================================================
     * 15-6 세운 × 월운 관계
     * =================================================
     */
    private void addAnnualInteractionSignals(

            MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis,

            List<MonthlyLuckContextSignal> signals

    ) {

        for (
                MonthlyAnnualLuckRelationReference relation
                : annualRelationAnalysis.relations()
        ) {

            signals.add(
                    signal(
                            MonthlyLuckContextAxis
                                    .ANNUAL_INTERACTION,

                            relationStatus(
                                    relation.relationCode()
                            ),

                            "ANNUAL_"
                                    + relation
                                    .layer()
                                    .name()
                                    + "_"
                                    + sanitize(
                                    relation
                                            .relationCode()
                            ),

                            "세운과 월운 사이에 "
                                    + relation.relationName()
                                    + " 관계가 성립",

                            Set.of(),

                            Set.of(),

                            List.of(
                                    "15-6 관계 Layer는 "
                                            + relation
                                            .layer()
                                            .name()
                                            + "입니다.",

                                    "관계 Code는 "
                                            + relation
                                            .relationCode()
                                            + "입니다.",

                                    "세운과 월운은 모두 외부 시간축이므로 원국 Palace 위치를 부여하지 않습니다."
                            )
                    )
            );
        }
    }

    /*
     * =================================================
     * 15-6 세운 × 월운 Transformation
     * =================================================
     */
    private void addAnnualTransformationSignals(

            MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis,

            List<MonthlyLuckContextSignal> signals

    ) {

        for (
                MonthlyAnnualLuckTransformationReference transformation
                : annualRelationAnalysis.transformations()
        ) {

            String baseCode =
                    "ANNUAL_TRANSFORMATION_"
                            + sanitize(
                            transformation
                                    .transformationCode()
                    );

            signals.add(
                    signal(
                            MonthlyLuckContextAxis
                                    .TRANSFORMATION,

                            transformation.activated()
                                    ? MonthlyLuckContextStatus.SUPPORTED
                                    : MonthlyLuckContextStatus.QUALIFIED,

                            baseCode
                                    + "_ACTIVATION",

                            transformation.activated()
                                    ? "세운 × 월운 Transformation의 목표 오행 활성 조건이 성립"
                                    : "세운 × 월운 결합은 있으나 실제 활성은 확정되지 않음",

                            Set.of(),

                            Set.of(),

                            List.of(
                                    "15-6 Transformation은 "
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
                                MonthlyLuckContextAxis
                                        .TRANSFORMATION,

                                MonthlyLuckContextStatus
                                        .WEAKENED,

                                baseCode
                                        + "_ORIGINAL_FUNCTION_REDUCED",

                                "세운 × 월운 Transformation에서 원래 기능 감소가 확인됨",

                                Set.of(),

                                Set.of(),

                                List.of(
                                        "15-6 Transformation의 originalFunctionReduced가 true입니다.",

                                        "Transformation 활성과 원기능 감소를 하나의 상태로 합치지 않습니다."
                                )
                        )
                );
            }
        }
    }

    /*
     * =================================================
     * 원국 + 세운 + 월운
     * =================================================
     */
    private void addNatalAnnualMonthlySignals(

            PalaceAnalysis palaceAnalysis,

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckPeriod monthlyPeriod,

            List<MonthlyLuckContextSignal> signals

    ) {

        EarthlyBranch annualBranch =
                annualLuckAnalysis
                        .period()
                        .branch();

        EarthlyBranch monthlyBranch =
                monthlyPeriod.branch();

        for (
                PalacePosition position
                : palaceAnalysis.knownPositions()
        ) {

            EarthlyBranch natalBranch =
                    natalBranch(
                            palaceAnalysis,
                            position
                    );

            addThreeLayerSignals(
                    "NATAL_ANNUAL_MONTHLY_"
                            + position.name(),

                    "원국·세운·월운",

                    Set.of(
                            position
                    ),

                    natalBranch,
                    annualBranch,
                    monthlyBranch,

                    signals
            );
        }
    }

    /*
     * =================================================
     * 13/14 Context 연결
     * =================================================
     */
    private void addDecadeContextLink(

            MonthlyDecadeLuckInteraction decadeInteraction,

            DecadeLuckPeriodContextAnalysis decadeContext,

            AnnualLuckDecadeContextAnalysis annualDecadeContext,

            List<MonthlyLuckContextSignal> signals

    ) {

        int sequenceNumber =
                decadeInteraction
                        .decadeSequenceNumber();

        signals.add(
                signal(
                        MonthlyLuckContextAxis
                                .DECADE_INTERACTION,

                        MonthlyLuckContextStatus
                                .QUALIFIED,

                        "DECADE_CONTEXT_LINK_"
                                + sequenceNumber,

                        "현재 월운 Segment에 동일 대운·세운 Context가 연결됨",

                        Set.of(),

                        Set.of(),

                        List.of(
                                "15-5 월운 Interaction의 대운 순번은 "
                                        + sequenceNumber
                                        + "입니다.",

                                "13-6 DecadeLuckPeriodContextAnalysis가 동일 DecadeLuckPeriod를 참조합니다.",

                                "14-6 AnnualLuckDecadeContextAnalysis도 동일 DecadeLuckPeriod를 참조합니다.",

                                decadeInteraction.timingUncertain()
                                        ? "현재 월운 Segment는 교운시점 불확실성을 포함합니다."
                                        : "현재 대운 Segment의 시간범위는 15-5 Overlap에서 관리됩니다."
                        )
                )
        );

        /*
         * 실제 Identity는 Analyzer/Factory에서 검증하며
         * 여기서는 Context 연결 Evidence만 생성한다.
         */
        Objects.requireNonNull(
                decadeContext
        );

        Objects.requireNonNull(
                annualDecadeContext
        );
    }

    /*
     * =================================================
     * 15-5 대운 × 월운 관계
     * =================================================
     */
    private void addDecadeInteractionSignals(

            MonthlyDecadeLuckInteraction interaction,

            List<MonthlyLuckContextSignal> signals

    ) {

        for (
                MonthlyDecadeLuckRelationReference relation
                : interaction.relations()
        ) {

            signals.add(
                    signal(
                            MonthlyLuckContextAxis
                                    .DECADE_INTERACTION,

                            relationStatus(
                                    relation.relationCode()
                            ),

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

                            "대운과 월운 사이에 "
                                    + relation.relationName()
                                    + " 관계가 성립",

                            Set.of(),

                            Set.of(),

                            List.of(
                                    "대운 순번은 "
                                            + interaction
                                            .decadeSequenceNumber()
                                            + "입니다.",

                                    "15-5 관계 Code는 "
                                            + relation
                                            .relationCode()
                                            + "입니다.",

                                    interaction.timingUncertain()
                                            ? "이 Evidence가 속한 대운 Segment는 교운시점 불확실성을 포함합니다."
                                            : "이 Evidence의 적용 시간은 15-5 Overlap으로 관리됩니다."
                            )
                    )
            );
        }
    }

    /*
     * =================================================
     * 15-5 대운 × 월운 Transformation
     * =================================================
     */
    private void addDecadeTransformationSignals(

            MonthlyDecadeLuckInteraction interaction,

            List<MonthlyLuckContextSignal> signals

    ) {

        for (
                MonthlyDecadeLuckTransformationReference transformation
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
                            MonthlyLuckContextAxis
                                    .TRANSFORMATION,

                            transformation.activated()
                                    ? MonthlyLuckContextStatus.SUPPORTED
                                    : MonthlyLuckContextStatus.QUALIFIED,

                            baseCode
                                    + "_ACTIVATION",

                            transformation.activated()
                                    ? "대운 × 월운 Transformation의 목표 오행 활성 조건이 성립"
                                    : "대운 × 월운 결합은 있으나 실제 활성은 확정되지 않음",

                            Set.of(),

                            Set.of(),

                            List.of(
                                    "15-5 Transformation은 "
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
                                MonthlyLuckContextAxis
                                        .TRANSFORMATION,

                                MonthlyLuckContextStatus
                                        .WEAKENED,

                                baseCode
                                        + "_ORIGINAL_FUNCTION_REDUCED",

                                "대운 × 월운 Transformation에서 원래 기능 감소가 확인됨",

                                Set.of(),

                                Set.of(),

                                List.of(
                                        "15-5 Transformation의 originalFunctionReduced가 true입니다.",

                                        "Transformation 활성과 원기능 감소를 서로 다른 Evidence로 유지합니다."
                                )
                        )
                );
            }
        }
    }

    /*
     * =================================================
     * 원국 + 대운 + 월운
     * =================================================
     */
    private void addNatalDecadeMonthlySignals(

            PalaceAnalysis palaceAnalysis,

            MonthlyLuckPeriod monthlyPeriod,

            MonthlyDecadeLuckInteraction decadeInteraction,

            List<MonthlyLuckContextSignal> signals

    ) {

        EarthlyBranch decadeBranch =
                decadeInteraction
                        .decadePeriod()
                        .pillar()
                        .branch();

        EarthlyBranch monthlyBranch =
                monthlyPeriod.branch();

        for (
                PalacePosition position
                : palaceAnalysis.knownPositions()
        ) {

            addThreeLayerSignals(
                    "NATAL_DECADE_MONTHLY_"
                            + decadeInteraction
                            .decadeSequenceNumber()
                            + "_"
                            + position.name(),

                    "원국·대운·월운",

                    Set.of(
                            position
                    ),

                    natalBranch(
                            palaceAnalysis,
                            position
                    ),

                    decadeBranch,

                    monthlyBranch,

                    signals
            );
        }
    }

    /*
     * =================================================
     * 대운 + 세운 + 월운
     * =================================================
     *
     * 세 글자 모두 외부 운축.
     *
     * PalacePosition은 비어 있어야 한다.
     */
    private void addDecadeAnnualMonthlySignals(

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckPeriod monthlyPeriod,

            MonthlyDecadeLuckInteraction decadeInteraction,

            List<MonthlyLuckContextSignal> signals

    ) {

        addThreeLayerSignals(
                "DECADE_ANNUAL_MONTHLY_"
                        + decadeInteraction
                        .decadeSequenceNumber(),

                "대운·세운·월운",

                Set.of(),

                decadeInteraction
                        .decadePeriod()
                        .pillar()
                        .branch(),

                annualLuckAnalysis
                        .period()
                        .branch(),

                monthlyPeriod.branch(),

                signals
        );
    }

    /**
     * =================================================
     * 공통 3층 관계
     * =================================================
     *
     * 완전 삼합
     * 완전 방합
     * 삼형
     *
     * 을 확인한다.
     *
     * 관계 완성 != 실제 합화 활성.
     */
    private void addThreeLayerSignals(

            String codePrefix,

            String layerName,

            Set<PalacePosition> palacePositions,

            EarthlyBranch first,

            EarthlyBranch second,

            EarthlyBranch third,

            List<MonthlyLuckContextSignal> signals

    ) {

        earthlyBranchThreeCombinationRule
                .findCompleteCombinationType(
                        first,
                        second,
                        third
                )
                .ifPresent(
                        type -> {

                            String baseCode =
                                    codePrefix
                                            + "_THREE_COMBINATION_"
                                            + type.name();

                            signals.add(
                                    signal(
                                            MonthlyLuckContextAxis
                                                    .THREE_LAYER_RELATION,

                                            MonthlyLuckContextStatus
                                                    .QUALIFIED,

                                            baseCode,

                                            layerName
                                                    + " 3층에서 삼합이 완성됨",

                                            palacePositions,

                                            Set.of(),

                                            List.of(
                                                    layerName
                                                            + "의 세 지지가 완전 삼합을 구성합니다.",

                                                    "삼합 Type은 "
                                                            + type.name()
                                                            + "입니다.",

                                                    "완전 삼합 존재 자체를 실제 합화 활성로 간주하지 않습니다."
                                            )
                                    )
                            );

                            signals.add(
                                    signal(
                                            MonthlyLuckContextAxis
                                                    .TRANSFORMATION,

                                            MonthlyLuckContextStatus
                                                    .QUALIFIED,

                                            baseCode
                                                    + "_TRANSFORMATION_CANDIDATE",

                                            layerName
                                                    + " 삼합 완성에 따른 Transformation 후보 Context",

                                            palacePositions,

                                            Set.of(),

                                            List.of(
                                                    "세 지지의 완전 삼합 관계는 확인되었습니다.",

                                                    "관계 완성과 목표 오행의 실제 활성은 서로 다른 판정입니다.",

                                                    "15-7 Context 단계에서는 별도의 9차 전체 경쟁·방해 통합 없이 activated=true를 생성하지 않습니다."
                                            )
                                    )
                            );
                        }
                );

        earthlyBranchDirectionalCombinationRule
                .findCompleteCombinationType(
                        first,
                        second,
                        third
                )
                .ifPresent(
                        type -> {

                            String baseCode =
                                    codePrefix
                                            + "_DIRECTIONAL_COMBINATION_"
                                            + type.name();

                            signals.add(
                                    signal(
                                            MonthlyLuckContextAxis
                                                    .THREE_LAYER_RELATION,

                                            MonthlyLuckContextStatus
                                                    .QUALIFIED,

                                            baseCode,

                                            layerName
                                                    + " 3층에서 방합이 완성됨",

                                            palacePositions,

                                            Set.of(),

                                            List.of(
                                                    layerName
                                                            + "의 세 지지가 완전 방합을 구성합니다.",

                                                    "방합 Type은 "
                                                            + type.name()
                                                            + "입니다.",

                                                    "완전 방합 존재와 실제 Transformation 활성은 분리합니다."
                                            )
                                    )
                            );

                            signals.add(
                                    signal(
                                            MonthlyLuckContextAxis
                                                    .TRANSFORMATION,

                                            MonthlyLuckContextStatus
                                                    .QUALIFIED,

                                            baseCode
                                                    + "_TRANSFORMATION_CANDIDATE",

                                            layerName
                                                    + " 방합 완성에 따른 Transformation 후보 Context",

                                            palacePositions,

                                            Set.of(),

                                            List.of(
                                                    "세 지지의 완전 방합 관계는 확인되었습니다.",

                                                    "관계 완성 사실만으로 실제 목표 오행 활성 상태를 추정하지 않습니다.",

                                                    "외부 운축에는 원국 PalacePosition을 부여하지 않습니다."
                                            )
                                    )
                            );
                        }
                );

        if (
                BranchRelationTable
                        .isThreePunishment(
                                first,
                                second,
                                third
                        )
        ) {

            signals.add(
                    signal(
                            MonthlyLuckContextAxis
                                    .THREE_LAYER_RELATION,

                            MonthlyLuckContextStatus
                                    .CONFLICTED,

                            codePrefix
                                    + "_THREE_PUNISHMENT",

                            layerName
                                    + " 3층에서 삼형 구조가 완성됨",

                            palacePositions,

                            Set.of(),

                            List.of(
                                    layerName
                                            + "의 세 지지가 삼형 구조를 구성합니다.",

                                    "CONFLICTED는 구조적 충돌 Evidence를 뜻하며 길흉 점수나 사건 발생 확률을 뜻하지 않습니다."
                            )
                    )
            );
        }
    }

    /*
     * =================================================
     * 4층 중첩 Context
     * =================================================
     *
     * 새로운 4글자 합/형 공식을 만들지 않는다.
     *
     * 동일 원국 Palace를 기준으로:
     *
     * NATAL + DECADE + ANNUAL
     * NATAL + DECADE + MONTHLY
     * NATAL + ANNUAL + MONTHLY
     * DECADE + ANNUAL + MONTHLY
     *
     * 네 개의 3층 조합 중
     * 완전 삼합 / 완전 방합 / 삼형 Evidence가
     * 2개 이상 동시에 존재할 때만
     * FOUR_LAYER_RELATION으로 보존한다.
     */
    private void addFourLayerOverlapSignals(

            PalaceAnalysis palaceAnalysis,

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckPeriod monthlyPeriod,

            MonthlyDecadeLuckInteraction decadeInteraction,

            List<MonthlyLuckContextSignal> signals

    ) {

        EarthlyBranch decadeBranch =
                decadeInteraction
                        .decadePeriod()
                        .pillar()
                        .branch();

        EarthlyBranch annualBranch =
                annualLuckAnalysis
                        .period()
                        .branch();

        EarthlyBranch monthlyBranch =
                monthlyPeriod.branch();

        for (
                PalacePosition position
                : palaceAnalysis.knownPositions()
        ) {

            EarthlyBranch natalBranch =
                    natalBranch(
                            palaceAnalysis,
                            position
                    );

            List<ThreeLayerDescriptor> descriptors =
                    new ArrayList<>();

            descriptors.addAll(
                    threeLayerDescriptors(
                            "NATAL_DECADE_ANNUAL",
                            natalBranch,
                            decadeBranch,
                            annualBranch
                    )
            );

            descriptors.addAll(
                    threeLayerDescriptors(
                            "NATAL_DECADE_MONTHLY",
                            natalBranch,
                            decadeBranch,
                            monthlyBranch
                    )
            );

            descriptors.addAll(
                    threeLayerDescriptors(
                            "NATAL_ANNUAL_MONTHLY",
                            natalBranch,
                            annualBranch,
                            monthlyBranch
                    )
            );

            descriptors.addAll(
                    threeLayerDescriptors(
                            "DECADE_ANNUAL_MONTHLY",
                            decadeBranch,
                            annualBranch,
                            monthlyBranch
                    )
            );

            if (
                    descriptors.size() < 2
            ) {

                continue;
            }

            boolean conflicted =
                    descriptors
                            .stream()
                            .anyMatch(
                                    ThreeLayerDescriptor
                                            ::conflicted
                            );

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "동일 월운·대운 Segment에서 "
                            + descriptors.size()
                            + "개의 완전 3층 관계 Evidence가 동시에 존재합니다."
            );

            for (
                    ThreeLayerDescriptor descriptor
                    : descriptors
            ) {

                reasons.add(
                        descriptor.layerCode()
                                + " / "
                                + descriptor.relationCode()
                );
            }

            reasons.add(
                    "FOUR_LAYER_RELATION은 새로운 4글자 합화 공식이 아니라 여러 기존 3층 Evidence의 동시 중첩 Context입니다."
            );

            signals.add(
                    signal(
                            MonthlyLuckContextAxis
                                    .FOUR_LAYER_RELATION,

                            conflicted
                                    ? MonthlyLuckContextStatus.CONFLICTED
                                    : MonthlyLuckContextStatus.QUALIFIED,

                            "FOUR_LAYER_OVERLAP_"
                                    + decadeInteraction
                                    .decadeSequenceNumber()
                                    + "_"
                                    + position.name(),

                            "원국·대운·세운·월운 4층에서 복수의 3층 관계가 동시에 중첩됨",

                            Set.of(
                                    position
                            ),

                            Set.of(),

                            reasons
                    )
            );
        }
    }

    private List<ThreeLayerDescriptor>
    threeLayerDescriptors(

            String layerCode,

            EarthlyBranch first,

            EarthlyBranch second,

            EarthlyBranch third

    ) {

        List<ThreeLayerDescriptor> result =
                new ArrayList<>();

        earthlyBranchThreeCombinationRule
                .findCompleteCombinationType(
                        first,
                        second,
                        third
                )
                .ifPresent(
                        type ->
                                result.add(
                                        new ThreeLayerDescriptor(
                                                layerCode,
                                                "THREE_COMBINATION_"
                                                        + type.name(),
                                                false
                                        )
                                )
                );

        earthlyBranchDirectionalCombinationRule
                .findCompleteCombinationType(
                        first,
                        second,
                        third
                )
                .ifPresent(
                        type ->
                                result.add(
                                        new ThreeLayerDescriptor(
                                                layerCode,
                                                "DIRECTIONAL_COMBINATION_"
                                                        + type.name(),
                                                false
                                        )
                                )
                );

        if (
                BranchRelationTable
                        .isThreePunishment(
                                first,
                                second,
                                third
                        )
        ) {

            result.add(
                    new ThreeLayerDescriptor(
                            layerCode,
                            "THREE_PUNISHMENT",
                            true
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 입력 검증 - 월운 공통
     * =================================================
     */
    private void validateMonthlyInputs(

            StructureAnalysis structureAnalysis,

            UsefulGodAnalysis usefulGodAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

            MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

            MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis

    ) {

        Objects.requireNonNull(
                structureAnalysis,
                "15-7에는 StructureAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                usefulGodAnalysis,
                "15-7에는 UsefulGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "15-7에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "15-7에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualLuckAnalysis,
                "15-7에는 AnnualLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyTenGodAnalysis,
                "15-7에는 MonthlyLuckTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "15-7에는 MonthlyLuckNatalRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualRelationAnalysis,
                "15-7에는 MonthlyAnnualLuckRelationAnalysis가 필요합니다."
        );

        MonthlyLuckPeriod period =
                monthlyTenGodAnalysis.period();

        if (
                monthlyTenGodAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "15-3 월운 십성과 12차 Palace의 일간이 일치하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "15-4 원국 관계와 12차 Palace의 일간이 일치하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10차와 12차의 일간이 일치하지 않습니다."
            );
        }

        if (
                annualLuckAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "14차와 12차의 일간이 일치하지 않습니다."
            );
        }

        if (
                annualLuckAnalysis.natalAnalysis()
                        != palaceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "14차 AnnualLuckAnalysis가 현재 PalaceAnalysis를 참조하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.natalAnalysis()
                        != palaceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "15-4 관계 분석이 현재 PalaceAnalysis를 참조하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.period()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "15-3과 15-4가 서로 다른 MonthlyLuckPeriod를 참조합니다."
            );
        }

        if (
                annualRelationAnalysis.monthlyPeriod()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "15-6이 현재 MonthlyLuckPeriod를 참조하지 않습니다."
            );
        }

        if (
                period.annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "현재 월운이 다른 AnnualLuckAnalysis에 속합니다."
            );
        }

        if (
                annualRelationAnalysis.annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "15-6이 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        if (
                tenGodCompositeAnalysis
                        .structureAnalysis()
                        != structureAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10차가 현재 5차 StructureAnalysis를 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis
                        .usefulGodAnalysis()
                        != usefulGodAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10차가 현재 7차 UsefulGodAnalysis를 참조하지 않습니다."
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
     * 입력 검증 - 대운 Segment
     * =================================================
     */
    private void validateSegmentInputs(

            PalaceAnalysis palaceAnalysis,

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

            MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

            MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis,

            MonthlyDecadeLuckInteraction decadeInteraction,

            DecadeLuckPeriodContextAnalysis decadeContext,

            AnnualLuckDecadeContextAnalysis annualDecadeContext

    ) {

        Objects.requireNonNull(
                palaceAnalysis,
                "15-7 Segment에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualLuckAnalysis,
                "15-7 Segment에는 AnnualLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyTenGodAnalysis,
                "15-7 Segment에는 MonthlyLuckTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "15-7 Segment에는 MonthlyLuckNatalRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualRelationAnalysis,
                "15-7 Segment에는 MonthlyAnnualLuckRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeInteraction,
                "15-7 Segment에는 MonthlyDecadeLuckInteraction이 필요합니다."
        );

        Objects.requireNonNull(
                decadeContext,
                "15-7 Segment에는 DecadeLuckPeriodContextAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualDecadeContext,
                "15-7 Segment에는 AnnualLuckDecadeContextAnalysis가 필요합니다."
        );

        MonthlyLuckPeriod period =
                monthlyTenGodAnalysis.period();

        if (
                annualLuckAnalysis.natalAnalysis()
                        != palaceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "14차와 15-7 Segment가 서로 다른 PalaceAnalysis를 참조합니다."
            );
        }

        if (
                natalRelationAnalysis.natalAnalysis()
                        != palaceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "15-4가 현재 PalaceAnalysis를 참조하지 않습니다."
            );
        }

        if (
                monthlyTenGodAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()

                        || natalRelationAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()

                        || annualLuckAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "15-7 Segment의 일간이 일치하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.period()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "15-3과 15-4의 MonthlyLuckPeriod가 다릅니다."
            );
        }

        if (
                annualRelationAnalysis.monthlyPeriod()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "15-6이 현재 MonthlyLuckPeriod를 참조하지 않습니다."
            );
        }

        if (
                decadeInteraction.monthlyPeriod()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "15-5 Interaction이 현재 MonthlyLuckPeriod를 참조하지 않습니다."
            );
        }

        if (
                period.annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "현재 월운이 현재 AnnualLuckAnalysis에 속하지 않습니다."
            );
        }

        if (
                annualRelationAnalysis.annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "15-6이 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        if (
                decadeContext.period()
                        != decadeInteraction.decadePeriod()
        ) {

            throw new IllegalArgumentException(
                    "13-6 대운 Context와 15-5 Interaction의 대운 Period가 다릅니다."
            );
        }

        if (
                decadeContext.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "13-6 대운 Context와 월운 Context의 일간이 다릅니다."
            );
        }

        if (
                annualDecadeContext
                        .interaction()
                        .decadePeriod()
                        != decadeInteraction.decadePeriod()
        ) {

            throw new IllegalArgumentException(
                    "14-6 세운 Segment와 15-5 월운 Segment의 대운이 다릅니다."
            );
        }

        if (
                annualDecadeContext
                        .decadeContext()
                        != decadeContext
        ) {

            throw new IllegalArgumentException(
                    "14-6 세운 Segment가 현재 13-6 대운 Context를 참조하지 않습니다."
            );
        }

        if (
                annualDecadeContext
                        .annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "14-6 세운 Segment가 현재 AnnualLuckPeriod를 참조하지 않습니다."
            );
        }

        if (
                annualDecadeContext.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "14-6 세운 Segment와 월운 Context의 일간이 다릅니다."
            );
        }
    }

    /*
     * =================================================
     * 원국 지지
     * =================================================
     */
    private EarthlyBranch natalBranch(

            PalaceAnalysis palaceAnalysis,

            PalacePosition position

    ) {

        return palaceAnalysis
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
    }

    /*
     * =================================================
     * Relation -> Context Status
     * =================================================
     *
     * 길흉 판정이 아니다.
     */
    private MonthlyLuckContextStatus relationStatus(
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

            return MonthlyLuckContextStatus
                    .CONFLICTED;
        }

        return MonthlyLuckContextStatus
                .QUALIFIED;
    }

    /*
     * =================================================
     * Signal
     * =================================================
     */
    private MonthlyLuckContextSignal signal(

            MonthlyLuckContextAxis axis,

            MonthlyLuckContextStatus status,

            String code,

            String summary,

            Set<PalacePosition> palacePositions,

            Set<StructureTenGodType> tenGodTypes,

            List<String> reasons

    ) {

        return new MonthlyLuckContextSignal(
                axis,
                status,
                code,
                summary,
                palacePositions,
                tenGodTypes,
                reasons
        );
    }

    private List<MonthlyLuckContextSignal> sortSignals(
            List<MonthlyLuckContextSignal> signals
    ) {

        signals.sort(
                Comparator
                        .comparingInt(
                                (
                                        MonthlyLuckContextSignal signal
                                ) ->
                                        signal
                                                .axis()
                                                .ordinal()
                        )
                        .thenComparingInt(
                                signal ->
                                        signal
                                                .status()
                                                .ordinal()
                        )
                        .thenComparing(
                                MonthlyLuckContextSignal
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

    private record ThreeLayerDescriptor(

            String layerCode,

            String relationCode,

            boolean conflicted

    ) {

        private ThreeLayerDescriptor {

            Objects.requireNonNull(
                    layerCode
            );

            Objects.requireNonNull(
                    relationCode
            );
        }
    }
}