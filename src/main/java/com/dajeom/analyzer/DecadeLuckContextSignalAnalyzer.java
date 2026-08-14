package com.dajeom.analyzer;

import com.dajeom.model.DecadeLuckContextResolution;
import com.dajeom.model.DecadeLuckContextSignal;
import com.dajeom.model.DecadeLuckNatalRelationReference;
import com.dajeom.model.DecadeLuckNatalTransformationReference;
import com.dajeom.model.DecadeLuckPeriodNatalRelationAnalysis;
import com.dajeom.model.DecadeLuckPeriodTenGodAnalysis;
import com.dajeom.model.DecadeLuckTenGodEvidence;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.model.UsefulGodAnalysis;
import com.dajeom.type.DecadeLuckContextAxis;
import com.dajeom.type.DecadeLuckContextStatus;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 13차-6.
 *
 * 하나의 대운 Period에 대해:
 *
 * 5차 격국
 * 7차 용신
 * 10차 십성 복합구조
 * 12차 궁위
 * 13-4 대운 십성
 * 13-5 원국 관계/합화
 *
 * 를 교차하여 deterministic Context Signal을 생성한다.
 *
 * =================================================
 *
 * 원칙
 *
 * 1.
 * 길흉 총점을 만들지 않는다.
 *
 * 2.
 * 서로 다른 Axis의 상태를 합치지 않는다.
 *
 * 3.
 * 관계 존재만으로 합화 성립을 추측하지 않는다.
 *
 * 4.
 * 격국 Support/Pressure를
 * 공개되지 않은 Candidate API를 추측하여 만들지 않는다.
 *
 * 5.
 * 5차 격국은 현재 격 상태 Context 자체를 보존한다.
 *
 * 6.
 * 7차 용신은 대운 천간/지지의 실제 오행 유입과 비교한다.
 *
 * 7.
 * 10차 십성은 대운에서 유입된 십성이
 * 원국에도 존재하는지 직접 비교한다.
 *
 * 8.
 * 13-5 Relation/Transformation은
 * 이미 계산된 Reference를 그대로 사용한다.
 */
@Component
public class DecadeLuckContextSignalAnalyzer {

    public DecadeLuckContextResolution analyze(

            StructureAnalysis structureAnalysis,

            UsefulGodAnalysis usefulGodAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckPeriodTenGodAnalysis luckTenGodAnalysis,

            DecadeLuckPeriodNatalRelationAnalysis natalRelationAnalysis

    ) {

        validateInputs(
                structureAnalysis,
                usefulGodAnalysis,
                tenGodCompositeAnalysis,
                palaceAnalysis,
                luckTenGodAnalysis,
                natalRelationAnalysis
        );

        List<DecadeLuckContextSignal> signals =
                new ArrayList<>();

        addStructureSignals(
                structureAnalysis,
                signals
        );

        addUsefulGodSignals(
                usefulGodAnalysis,
                luckTenGodAnalysis,
                signals
        );

        addTenGodSignals(
                tenGodCompositeAnalysis,
                luckTenGodAnalysis,
                signals
        );

        addPalaceSignals(
                natalRelationAnalysis,
                signals
        );

        addRelationSignals(
                natalRelationAnalysis,
                signals
        );

        addTransformationSignals(
                natalRelationAnalysis,
                signals
        );

        signals.sort(
                Comparator
                        .comparingInt(
                                (
                                        DecadeLuckContextSignal signal
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
                                DecadeLuckContextSignal
                                        ::code
                        )
        );

        return new DecadeLuckContextResolution(
                luckTenGodAnalysis.period(),
                signals
        );
    }

    /*
     * =================================================
     * 입력 정합성
     * =================================================
     */

    private void validateInputs(

            StructureAnalysis structureAnalysis,

            UsefulGodAnalysis usefulGodAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis,

            DecadeLuckPeriodTenGodAnalysis luckTenGodAnalysis,

            DecadeLuckPeriodNatalRelationAnalysis natalRelationAnalysis

    ) {

        Objects.requireNonNull(
                structureAnalysis,
                "5차 StructureAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                usefulGodAnalysis,
                "7차 UsefulGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "10차 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "12차 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                luckTenGodAnalysis,
                "13-4 대운 십성 분석이 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "13-5 대운-원국 관계 분석이 필요합니다."
        );

        if (
                luckTenGodAnalysis.period()
                        != natalRelationAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "13-4와 13-5가 서로 다른 대운 Period를 참조합니다."
            );
        }

        if (
                natalRelationAnalysis.natalAnalysis()
                        != palaceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "13-5 분석이 현재 12차 PalaceAnalysis를 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis.structureAnalysis()
                        != structureAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 복합분석이 현재 5차 격국 분석을 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis.usefulGodAnalysis()
                        != usefulGodAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 복합분석이 현재 7차 용신 분석을 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()

                        || luckTenGodAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()

                        || natalRelationAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "5/7/10/12/13차 분석의 일간이 서로 일치하지 않습니다."
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
     * 5차 격국
     * =================================================
     */

    private void addStructureSignals(

            StructureAnalysis structureAnalysis,

            List<DecadeLuckContextSignal> signals

    ) {

        if (
                structureAnalysis.requiresSpecialReview()
        ) {

            signals.add(
                    new DecadeLuckContextSignal(
                            DecadeLuckContextAxis.STRUCTURE,
                            DecadeLuckContextStatus.QUALIFIED,
                            "STRUCTURE_SPECIAL_REVIEW_REQUIRED",
                            "원국 격국은 특수격 또는 추가 검토가 필요한 상태다.",
                            Set.of(),
                            Set.of(),
                            List.of(
                                    "주격: "
                                            + structureAnalysis
                                            .primaryNameKorean(),
                                    "격 상태: "
                                            + structureAnalysis
                                            .statusKorean(),
                                    "5차 StructureAnalysis가 특수격 재검토 필요 상태를 보존한다."
                            )
                    )
            );

            return;
        }

        signals.add(
                new DecadeLuckContextSignal(
                        DecadeLuckContextAxis.STRUCTURE,
                        DecadeLuckContextStatus.QUALIFIED,
                        "PRIMARY_STRUCTURE_CONTEXT",
                        "현재 대운 Context는 원국의 주격 상태와 함께 해석해야 한다.",
                        Set.of(),
                        Set.of(),
                        List.of(
                                "주격: "
                                        + structureAnalysis
                                        .primaryNameKorean(),
                                "격 상태: "
                                        + structureAnalysis
                                        .statusKorean(),
                                "대운이 원국 격국을 자동으로 변경한다고 판정하지 않는다."
                        )
                )
        );
    }

    /*
     * =================================================
     * 7차 용신·희신·기신
     * =================================================
     */

    private void addUsefulGodSignals(

            UsefulGodAnalysis usefulGodAnalysis,

            DecadeLuckPeriodTenGodAnalysis luckTenGodAnalysis,

            List<DecadeLuckContextSignal> signals

    ) {

        Set<FiveElement> enteredElements =
                new LinkedHashSet<>();

        enteredElements.add(
                luckTenGodAnalysis
                        .luckPillar()
                        .stem()
                        .getElement()
        );

        enteredElements.add(
                luckTenGodAnalysis
                        .luckPillar()
                        .branch()
                        .getElement()
        );

        /*
         * 실제 UsefulGodRelationAnalysis API는
         * Set이 아니라 List<FiveElement>를 반환한다.
         *
         * 중복 여부 판정에는 contains()만 필요하므로
         * 원본 반환 타입을 그대로 유지한다.
         */
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

        for (
                FiveElement element
                : enteredElements
        ) {

            boolean classified =
                    false;

            if (
                    usefulElements.contains(
                            element
                    )
            ) {

                classified =
                        true;

                signals.add(
                        new DecadeLuckContextSignal(
                                DecadeLuckContextAxis.USEFUL_GOD,
                                DecadeLuckContextStatus.SUPPORTED,
                                "PRIMARY_USEFUL_ELEMENT_ENTERED_"
                                        + element.name(),
                                element.getKoreanName()
                                        + " 오행이 대운에 유입되며 원국 용신과 일치한다.",
                                Set.of(),
                                Set.of(),
                                List.of(
                                        enteredElementReason(
                                                luckTenGodAnalysis,
                                                element
                                        ),
                                        "7차 UsefulGodRelationAnalysis의 용신 원소에 포함된다."
                                )
                        )
                );
            }

            if (
                    favorableElements.contains(
                            element
                    )
            ) {

                classified =
                        true;

                signals.add(
                        new DecadeLuckContextSignal(
                                DecadeLuckContextAxis.USEFUL_GOD,
                                DecadeLuckContextStatus.SUPPORTED,
                                "FAVORABLE_ELEMENT_ENTERED_"
                                        + element.name(),
                                element.getKoreanName()
                                        + " 오행이 대운에 유입되며 원국 희신과 일치한다.",
                                Set.of(),
                                Set.of(),
                                List.of(
                                        enteredElementReason(
                                                luckTenGodAnalysis,
                                                element
                                        ),
                                        "7차 UsefulGodRelationAnalysis의 희신 원소에 포함된다."
                                )
                        )
                );
            }

            if (
                    unfavorableElements.contains(
                            element
                    )
            ) {

                classified =
                        true;

                signals.add(
                        new DecadeLuckContextSignal(
                                DecadeLuckContextAxis.USEFUL_GOD,
                                DecadeLuckContextStatus.CONFLICTED,
                                "UNFAVORABLE_ELEMENT_ENTERED_"
                                        + element.name(),
                                element.getKoreanName()
                                        + " 오행이 대운에 유입되며 원국 기신 관계와 겹친다.",
                                Set.of(),
                                Set.of(),
                                List.of(
                                        enteredElementReason(
                                                luckTenGodAnalysis,
                                                element
                                        ),
                                        "7차 UsefulGodRelationAnalysis의 기신 원소에 포함된다.",
                                        "기신 유입을 곧바로 단일 길흉 점수로 변환하지 않는다."
                                )
                        )
                );
            }

            if (
                    !classified
            ) {

                signals.add(
                        new DecadeLuckContextSignal(
                                DecadeLuckContextAxis.USEFUL_GOD,
                                DecadeLuckContextStatus.QUALIFIED,
                                "UNCLASSIFIED_ELEMENT_ENTERED_"
                                        + element.name(),
                                element.getKoreanName()
                                        + " 오행이 대운에 유입되지만 용·희·기신 중 하나로 단순 확정하지 않는다.",
                                Set.of(),
                                Set.of(),
                                List.of(
                                        enteredElementReason(
                                                luckTenGodAnalysis,
                                                element
                                        ),
                                        "현재 7차 최종 용신·희신·기신 목록에서 직접 일치하지 않는다."
                                )
                        )
                );
            }
        }
    }

    private String enteredElementReason(

            DecadeLuckPeriodTenGodAnalysis analysis,

            FiveElement element

    ) {

        boolean stemElement =
                analysis
                        .luckPillar()
                        .stem()
                        .getElement()
                        == element;

        boolean branchElement =
                analysis
                        .luckPillar()
                        .branch()
                        .getElement()
                        == element;

        if (
                stemElement
                        && branchElement
        ) {

            return "대운 천간과 지지의 대표 오행이 모두 "
                    + element.getKoreanName()
                    + "이다.";
        }

        if (
                stemElement
        ) {

            return "대운 천간의 오행이 "
                    + element.getKoreanName()
                    + "이다.";
        }

        return "대운 지지의 대표 오행이 "
                + element.getKoreanName()
                + "이다.";
    }

    /*
     * =================================================
     * 10차 십성
     * =================================================
     */

    private void addTenGodSignals(

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            DecadeLuckPeriodTenGodAnalysis luckTenGodAnalysis,

            List<DecadeLuckContextSignal> signals

    ) {

        EnumSet<StructureTenGodType> enteredTenGodTypes =
                EnumSet.noneOf(
                        StructureTenGodType.class
                );

        for (
                DecadeLuckTenGodEvidence evidence
                : luckTenGodAnalysis.evidences()
        ) {

            enteredTenGodTypes.add(
                    evidence.tenGodType()
            );
        }

        for (
                StructureTenGodType tenGodType
                : enteredTenGodTypes
        ) {

            boolean presentInNatal =
                    tenGodCompositeAnalysis
                            .present(
                                    tenGodType
                            );

            int natalShareBasisPoints =
                    tenGodCompositeAnalysis
                            .shareBasisPointsOf(
                                    tenGodType
                            );

            if (
                    presentInNatal
            ) {

                signals.add(
                        new DecadeLuckContextSignal(
                                DecadeLuckContextAxis.TEN_GOD,
                                DecadeLuckContextStatus.SUPPORTED,
                                "NATAL_TEN_GOD_REINFORCED_"
                                        + tenGodType.name(),
                                tenGodType.koreanName()
                                        + "이 대운에 다시 등장하여 원국의 기존 십성 구조와 중첩된다.",
                                Set.of(),
                                Set.of(
                                        tenGodType
                                ),
                                List.of(
                                        "13-4 대운 십성에 "
                                                + tenGodType.koreanName()
                                                + "이 존재한다.",
                                        "10차 원국 십성 분포에도 동일 십성이 존재한다.",
                                        "원국 내 해당 십성 비율: "
                                                + natalShareBasisPoints
                                                + "bp"
                                )
                        )
                );

            } else {

                signals.add(
                        new DecadeLuckContextSignal(
                                DecadeLuckContextAxis.TEN_GOD,
                                DecadeLuckContextStatus.QUALIFIED,
                                "NEW_TEN_GOD_FACTOR_ENTERED_"
                                        + tenGodType.name(),
                                tenGodType.koreanName()
                                        + "이 대운에서 유입되지만 원국 십성 분포에서는 직접 존재하지 않는다.",
                                Set.of(),
                                Set.of(
                                        tenGodType
                                ),
                                List.of(
                                        "13-4 대운 십성에 "
                                                + tenGodType.koreanName()
                                                + "이 존재한다.",
                                        "10차 원국 십성 분포에서는 동일 십성이 직접 검출되지 않았다.",
                                        "새 십성 유입을 자동으로 강화 또는 약화로 단정하지 않는다."
                                )
                        )
                );
            }
        }
    }

    /*
     * =================================================
     * 12차 궁위
     * =================================================
     */

    private void addPalaceSignals(

            DecadeLuckPeriodNatalRelationAnalysis natalRelationAnalysis,

            List<DecadeLuckContextSignal> signals

    ) {

        EnumSet<PalacePosition> contactedPositions =
                EnumSet.noneOf(
                        PalacePosition.class
                );

        for (
                DecadeLuckNatalRelationReference relation
                : natalRelationAnalysis.relations()
        ) {

            contactedPositions.addAll(
                    relation.natalPositions()
            );
        }

        for (
                DecadeLuckNatalTransformationReference transformation
                : natalRelationAnalysis.transformations()
        ) {

            contactedPositions.addAll(
                    transformation.natalPositions()
            );
        }

        for (
                PalacePosition position
                : contactedPositions
        ) {

            int relationCount =
                    natalRelationAnalysis
                            .relationsOf(
                                    position
                            )
                            .size();

            int transformationCount =
                    natalRelationAnalysis
                            .transformationsOf(
                                    position
                            )
                            .size();

            signals.add(
                    new DecadeLuckContextSignal(
                            DecadeLuckContextAxis.PALACE,
                            DecadeLuckContextStatus.QUALIFIED,
                            "NATAL_PALACE_CONTACT_"
                                    + position.name(),
                            "대운이 원국 "
                                    + position.name()
                                    + " 궁위와 구조적으로 접촉한다.",
                            Set.of(
                                    position
                            ),
                            Set.of(),
                            List.of(
                                    "13-5 관계 Reference 수: "
                                            + relationCount,
                                    "13-5 합화 Reference 수: "
                                            + transformationCount,
                                    "궁위 접촉 자체를 길흉으로 단정하지 않는다."
                            )
                    )
            );
        }
    }

    /*
     * =================================================
     * 13-5 관계
     * =================================================
     */

    private void addRelationSignals(

            DecadeLuckPeriodNatalRelationAnalysis natalRelationAnalysis,

            List<DecadeLuckContextSignal> signals

    ) {

        for (
                DecadeLuckNatalRelationReference relation
                : natalRelationAnalysis.relations()
        ) {

            String positionKey =
                    positionKey(
                            relation.natalPositions()
                    );

            signals.add(
                    new DecadeLuckContextSignal(
                            DecadeLuckContextAxis.RELATION,
                            DecadeLuckContextStatus.QUALIFIED,
                            "NATAL_RELATION_"
                                    + relation.layer().name()
                                    + "_"
                                    + relation.relationCode()
                                    + "_"
                                    + positionKey,
                            "대운과 원국 사이에 "
                                    + relation.relationName()
                                    + " 관계가 활성된다.",
                            relation.natalPositions(),
                            Set.of(),
                            List.of(
                                    "관계 Layer: "
                                            + relation
                                            .layer()
                                            .koreanName(),
                                    "관계 Code: "
                                            + relation.relationCode(),
                                    "관계 궁위: "
                                            + positionKey,
                                    "관계 존재와 합화 성립은 별도 판단한다."
                            )
                    )
            );
        }
    }

    /*
     * =================================================
     * 13-5 합화·기능변화
     * =================================================
     */

    private void addTransformationSignals(

            DecadeLuckPeriodNatalRelationAnalysis natalRelationAnalysis,

            List<DecadeLuckContextSignal> signals

    ) {

        for (
                DecadeLuckNatalTransformationReference transformation
                : natalRelationAnalysis.transformations()
        ) {

            String positionKey =
                    positionKey(
                            transformation.natalPositions()
                    );

            if (
                    transformation.activated()
            ) {

                signals.add(
                        new DecadeLuckContextSignal(
                                DecadeLuckContextAxis.TRANSFORMATION,
                                DecadeLuckContextStatus.SUPPORTED,
                                "TRANSFORMATION_ACTIVATED_"
                                        + transformation.transformationCode()
                                        + "_"
                                        + positionKey,
                                transformation.transformationName()
                                        + "의 목표 오행 작용이 활성 상태다.",
                                transformation.natalPositions(),
                                Set.of(),
                                List.of(
                                        "13-5 합화 활성 판정: true",
                                        "목표 오행: "
                                                + transformation
                                                .targetElementName(),
                                        "단순 합 존재가 아니라 9차 판정축을 거친 결과다."
                                )
                        )
                );

            } else {

                signals.add(
                        new DecadeLuckContextSignal(
                                DecadeLuckContextAxis.TRANSFORMATION,
                                DecadeLuckContextStatus.QUALIFIED,
                                "TRANSFORMATION_NOT_ACTIVATED_"
                                        + transformation.transformationCode()
                                        + "_"
                                        + positionKey,
                                transformation.transformationName()
                                        + " 관계는 있으나 목표 오행 활성으로 확정되지 않는다.",
                                transformation.natalPositions(),
                                Set.of(),
                                List.of(
                                        "13-5 합화 활성 판정: false",
                                        "목표 오행 후보: "
                                                + transformation
                                                .targetElementName(),
                                        "합 관계 존재를 실제 합화로 승격하지 않는다."
                                )
                        )
                );
            }

            if (
                    transformation.originalFunctionReduced()
            ) {

                signals.add(
                        new DecadeLuckContextSignal(
                                DecadeLuckContextAxis.TRANSFORMATION,
                                DecadeLuckContextStatus.WEAKENED,
                                "ORIGINAL_FUNCTION_REDUCED_"
                                        + transformation.transformationCode()
                                        + "_"
                                        + positionKey,
                                transformation.transformationName()
                                        + "에 참여한 원국 기능의 감소 Evidence가 존재한다.",
                                transformation.natalPositions(),
                                Set.of(),
                                List.of(
                                        "13-5 originalFunctionReduced = true",
                                        "목표 오행: "
                                                + transformation
                                                .targetElementName(),
                                        "원래 기능을 삭제하지 않고 감소 상태만 별도 보존한다."
                                )
                        )
                );
            }
        }
    }

    /*
     * =================================================
     * 공통
     * =================================================
     */

    private String positionKey(
            Set<PalacePosition> positions
    ) {

        if (
                positions == null
                        || positions.isEmpty()
        ) {

            return "NONE";
        }

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
}