package com.dajeom.rule;

import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.PalaceContextEvidence;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalacePositionContextType;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 학습·성장 영역의 HOUR Palace Context Rule.
 *
 * =================================================
 *
 * Source:
 *
 * 12차 PalaceAnalysis
 *
 * =================================================
 *
 * 공식 위치 Context:
 *
 * HOUR
 * =
 * EXTENSION_LATER_DEVELOPMENT
 * =
 * 확장·후반 전개
 *
 * =================================================
 *
 * 이 Rule에서는 HOUR 자체를
 * 학습·성장 영역으로 기계적으로 연결하지 않는다.
 *
 * HOUR 궁위에 실제로:
 *
 * 인성 계열
 *
 * - DIRECT_RESOURCE
 * - INDIRECT_RESOURCE
 *
 * 또는
 *
 * 식상 계열
 *
 * - EATING_GOD
 * - HURTING_OFFICER
 *
 * Evidence가 존재할 때만
 * LEARNING_GROWTH Context를 생성한다.
 *
 * =================================================
 *
 * 의미:
 *
 * 인성 계열:
 *
 * - 배우고 받아들이는 기능
 * - 이해하고 정리하는 기능
 * - 지식과 경험을 축적하는 기능
 *
 * 이 확장·후반 전개 위치에 관여한다.
 *
 * -------------------------------------------------
 *
 * 식상 계열:
 *
 * - 표현하는 기능
 * - 실행하는 기능
 * - 배운 것을 결과물로 꺼내는 기능
 *
 * 이 확장·후반 전개 위치에 관여한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * HOUR
 * -> 미래
 *
 * HOUR 인성
 * -> 나중에 공부한다
 *
 * HOUR 식상
 * -> 창작자로 성공한다
 *
 * HOUR 문창
 * -> 시험 합격
 *
 * HOUR 합
 * -> 새로운 일을 시작한다
 *
 * HOUR 충
 * -> 진로가 바뀐다
 *
 * 같은 사건을 생성하지 않는다.
 *
 * =================================================
 *
 * 시간 미상:
 *
 * HOUR Palace는 절대 추정하지 않는다.
 *
 * PalaceAnalysis.hourUnknown() == true
 *
 * 또는
 *
 * hourContext().isEmpty()
 *
 * 이면 Match를 생성하지 않는다.
 *
 * =================================================
 *
 * 12운성:
 *
 * 해당 HOUR 위치의 12운성은
 * 위치 Context Provenance로만 보존한다.
 *
 * 장생/제왕을 SUPPORTIVE,
 * 병/사/묘/절을 PRESSURE로
 * 변환하지 않는다.
 *
 * =================================================
 *
 * 원국 관계:
 *
 * HOUR에 관계가 존재하더라도
 * 합 = 지원
 * 충 = 부담
 *
 * 으로 단순 판정하지 않는다.
 *
 * =================================================
 *
 * 합화:
 *
 * 합화 Context 존재
 * !=
 * 실제 activated
 * !=
 * 원래 기능 감소
 *
 * 를 분리한다.
 *
 * =================================================
 *
 * 상태:
 *
 * 항상 CONTEXTUAL.
 *
 * 이 Rule은 학습능력의 우열이나
 * 미래 성공 여부를 판정하는 Rule이 아니라
 * 학습·표현 기능이
 * 확장·후반 전개 위치에 존재한다는
 * 구조 Context를 보존한다.
 */
@Component
public class LearningExtensionPalaceContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "LEARNING_EXTENSION_PALACE_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType.PALACE
            );

    private static final Set<StructureTenGodType>
            RESOURCE_TYPES =
            Set.of(
                    StructureTenGodType.DIRECT_RESOURCE,
                    StructureTenGodType.INDIRECT_RESOURCE
            );

    private static final Set<StructureTenGodType>
            OUTPUT_TYPES =
            Set.of(
                    StructureTenGodType.EATING_GOD,
                    StructureTenGodType.HURTING_OFFICER
            );

    @Override
    public LifeDomain domain() {

        return LifeDomain.LEARNING_GROWTH;
    }

    @Override
    public String code() {

        return RULE_CODE;
    }

    @Override
    public Set<LifeDomainEvidenceSourceType>
    supportedSources() {

        return SUPPORTED_SOURCES;
    }

    @Override
    public List<LifeDomainRuleMatch> evaluate(
            LifeDomainEvidenceBundle bundle
    ) {

        Objects.requireNonNull(
                bundle,
                "LearningExtensionPalaceContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.LEARNING_GROWTH
        ) {

            throw new IllegalArgumentException(
                    "LearningExtensionPalaceContextRule은 LEARNING_GROWTH Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                palaceReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType.PALACE
                );

        if (
                palaceReferenceOptional.isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                palaceReference =
                palaceReferenceOptional
                        .orElseThrow();

        PalaceAnalysis palaceAnalysis =
                palaceReference
                        .requireSource(
                                PalaceAnalysis.class
                        );

        /*
         * =================================================
         * 출생시간 미상
         * =================================================
         *
         * 가짜 HOUR Palace를 만들지 않는다.
         */

        if (
                palaceAnalysis
                        .hourUnknown()
        ) {

            return List.of();
        }

        Optional<PalaceContextEvidence>
                hourContextOptional =
                palaceAnalysis
                        .hourContext();

        if (
                hourContextOptional.isEmpty()
        ) {

            throw new IllegalStateException(
                    "completeChart=true인데 HOUR PalaceContextEvidence가 존재하지 않습니다."
            );
        }

        PalaceContextEvidence hourContext =
                hourContextOptional
                        .orElseThrow();

        validateHourContext(
                hourContext
        );

        /*
         * =================================================
         * 학습·성장 관련 십성
         * =================================================
         *
         * HOUR라는 이유만으로
         * LEARNING_GROWTH Match를 만들지 않는다.
         */

        List<StructureTenGodType> resourceTypes =
                collectPresentTypes(
                        palaceAnalysis,
                        RESOURCE_TYPES
                );

        List<StructureTenGodType> outputTypes =
                collectPresentTypes(
                        palaceAnalysis,
                        OUTPUT_TYPES
                );

        if (
                resourceTypes.isEmpty()
                        && outputTypes.isEmpty()
        ) {

            return List.of();
        }

        boolean resourcePresent =
                !resourceTypes.isEmpty();

        boolean outputPresent =
                !outputTypes.isEmpty();

        List<String> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 위치 Identity
         * =================================================
         */

        reasons.add(
                "12차 PalaceAnalysis의 HOUR 궁위를 학습·성장 영역의 확장·후반 전개 Context로 사용합니다."
        );

        reasons.add(
                "HOUR 궁위의 공식 위치 Context는 "
                        + hourContext
                        .positionContext()
                        .koreanName()
                        + "입니다."
        );

        reasons.add(
                "HOUR 궁위를 자녀·노년·특정 미래 사건으로 기계적으로 고정하지 않고 현재 원국에서 앞으로 뻗어나가는 확장·후반 전개 위치로 사용합니다."
        );

        /*
         * =================================================
         * Pillar
         * =================================================
         */

        reasons.add(
                "HOUR 궁위 천간은 "
                        + hourContext.stem()
                        + "입니다."
        );

        reasons.add(
                "HOUR 궁위 지지는 "
                        + hourContext.branch()
                        + "입니다."
        );

        /*
         * =================================================
         * 인성 축
         * =================================================
         */

        if (
                resourcePresent
        ) {

            reasons.add(
                    "HOUR 확장·후반 전개 위치에 인성 계열 Evidence가 존재합니다: "
                            + joinTypes(
                            resourceTypes
                    )
            );

            reasons.add(
                    "배우고 이해하고 정리·축적하는 기능이 확장·후반 전개 위치에 관여합니다."
            );
        }

        /*
         * =================================================
         * 식상 축
         * =================================================
         */

        if (
                outputPresent
        ) {

            reasons.add(
                    "HOUR 확장·후반 전개 위치에 식상 계열 Evidence가 존재합니다: "
                            + joinTypes(
                            outputTypes
                    )
            );

            reasons.add(
                    "생각이나 준비한 내용을 표현·실행·산출하는 기능이 확장·후반 전개 위치에 관여합니다."
            );
        }

        /*
         * =================================================
         * 인성 + 식상 공존
         * =================================================
         *
         * 공존 자체를 조화 또는 충돌로 판정하지 않는다.
         */

        if (
                resourcePresent
                        && outputPresent
        ) {

            reasons.add(
                    "HOUR 위치에 학습·흡수 기능과 표현·산출 기능이 함께 존재하므로 준비와 실행 두 축을 모두 보존합니다."
            );

            reasons.add(
                    "인성과 식상이 같은 위치에 존재한다는 사실만으로 두 기능의 조화 또는 충돌을 확정하지 않습니다."
            );
        }

        /*
         * =================================================
         * Visible / Hidden
         * =================================================
         */

        hourContext
                .visibleTenGod()
                .ifPresent(
                        ignored ->
                                reasons.add(
                                        "HOUR 궁위에는 visible TenGod Evidence가 존재합니다."
                                )
                );

        if (
                !hourContext
                        .hiddenTenGods()
                        .isEmpty()
        ) {

            reasons.add(
                    "HOUR 궁위에는 "
                            + hourContext
                            .hiddenTenGods()
                            .size()
                            + "개의 hidden TenGod Evidence가 존재합니다."
            );
        }

        reasons.add(
                "visible 또는 hidden 여부는 기능의 위치 Provenance로 보존하며 그 자체를 성과의 우열로 변환하지 않습니다."
        );

        /*
         * =================================================
         * 12운성
         * =================================================
         */

        reasons.add(
                "HOUR 궁위의 12운성은 "
                        + hourContext
                        .twelveLifeStage()
                        .name()
                        + "이며 확장·후반 전개 위치 Context의 일부로만 보존합니다."
        );

        reasons.add(
                "12운성 단계만으로 학습력·재능·성과 또는 미래 길흉을 판정하지 않습니다."
        );

        /*
         * =================================================
         * 관계
         * =================================================
         */

        if (
                hourContext
                        .hasStemRelations()
        ) {

            reasons.add(
                    "HOUR 궁위에는 "
                            + hourContext
                            .stemRelations()
                            .size()
                            + "개의 천간 관계 Reference가 연결되어 있습니다."
            );
        }

        if (
                hourContext
                        .hasBranchRelations()
        ) {

            reasons.add(
                    "HOUR 궁위에는 "
                            + hourContext
                            .branchRelations()
                            .size()
                            + "개의 지지 관계 Reference가 연결되어 있습니다."
            );
        }

        if (
                hourContext.hasStemRelations()
                        || hourContext.hasBranchRelations()
        ) {

            reasons.add(
                    "HOUR 위치의 원국 관계 존재 자체를 SUPPORTIVE 또는 PRESSURE로 변환하지 않으며 실제 관계 종류와 중첩은 기존 Relation Evidence를 따릅니다."
            );
        }

        /*
         * =================================================
         * Transformation Context 존재
         * =================================================
         */

        if (
                hourContext
                        .hasTransformations()
        ) {

            reasons.add(
                    "HOUR 궁위에는 "
                            + hourContext
                            .transformations()
                            .size()
                            + "개의 Transformation Context Reference가 연결되어 있습니다."
            );
        }

        /*
         * =================================================
         * 실제 활성
         * =================================================
         */

        if (
                hourContext
                        .hasActivatedTransformation()
        ) {

            reasons.add(
                    "HOUR 궁위의 Transformation Context 중 실제 activated 상태가 존재합니다."
            );

        } else if (
                hourContext
                        .hasTransformations()
        ) {

            reasons.add(
                    "HOUR 궁위에 Transformation Context는 존재하지만 실제 activated 상태가 확인된 것은 아닙니다."
            );
        }

        /*
         * =================================================
         * 원기능 감소
         * =================================================
         */

        if (
                hourContext
                        .hasReducedOriginalFunction()
        ) {

            reasons.add(
                    "HOUR 궁위의 Transformation Context 중 원래 기능 감소가 명시된 Evidence가 존재합니다."
            );

        } else if (
                hourContext
                        .hasTransformations()
        ) {

            reasons.add(
                    "HOUR 궁위에 Transformation Context가 존재하더라도 원래 기능 감소가 자동으로 성립하는 것은 아닙니다."
            );
        }

        reasons.add(
                "결합 존재, Transformation 후보, 실제 활성, 원기능 감소를 서로 동일한 상태로 취급하지 않습니다."
        );

        /*
         * =================================================
         * 의미 제한
         * =================================================
         */

        reasons.add(
                "HOUR에 학습·표현 관련 십성이 존재한다는 것은 해당 기능이 확장·후반 전개 위치에 관여한다는 뜻이며 미래 학업·자격·창작·직업 성과를 예언하지 않습니다."
        );

        reasons.add(
                "실제 학습·흡수 및 표현·산출 구조의 강약과 상호작용은 LearningResourceOutputRule과 LearningCrossValidatedPatternRule이 별도로 담당합니다."
        );

        reasons.add(
                "대운·세운·월운에서 학습·성장 기능이 어떻게 움직이는지는 각각의 Learning temporal Rule이 별도로 판단합니다."
        );

        /*
         * =================================================
         * Match
         * =================================================
         *
         * 위치 Context이므로 방향성 길흉을 만들지 않는다.
         */

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.LEARNING_GROWTH,
                        RULE_CODE,
                        LifeDomainSignalStatus.CONTEXTUAL,
                        summaryOf(
                                hourContext,
                                resourcePresent,
                                outputPresent
                        ),
                        List.of(
                                palaceReference
                        ),
                        List.copyOf(
                                reasons
                        )
                )
        );
    }

    /*
     * =================================================
     * HOUR Context 정합성
     * =================================================
     */

    private void validateHourContext(
            PalaceContextEvidence context
    ) {

        Objects.requireNonNull(
                context,
                "HOUR PalaceContextEvidence가 필요합니다."
        );

        if (
                context.position()
                        != PalacePosition.HOUR
        ) {

            throw new IllegalStateException(
                    "hourContext의 PalacePosition이 HOUR가 아닙니다: "
                            + context.position()
            );
        }

        if (
                context.positionContext()
                        != PalacePositionContextType
                        .EXTENSION_LATER_DEVELOPMENT
        ) {

            throw new IllegalStateException(
                    "HOUR Palace의 공식 Context는 EXTENSION_LATER_DEVELOPMENT여야 합니다: "
                            + context.positionContext()
            );
        }

        if (
                !context
                        .positionContext()
                        .extensionLaterDevelopment()
        ) {

            throw new IllegalStateException(
                    "HOUR Palace Context가 extensionLaterDevelopment 상태가 아닙니다."
            );
        }
    }

    /*
     * =================================================
     * HOUR 위치에 실제 존재하는
     * 특정 십성 타입 수집
     * =================================================
     */

    private List<StructureTenGodType>
    collectPresentTypes(

            PalaceAnalysis palaceAnalysis,

            Set<StructureTenGodType> candidates

    ) {

        List<StructureTenGodType> result =
                new ArrayList<>();

        for (
                StructureTenGodType type
                : candidates
        ) {

            if (
                    palaceAnalysis
                            .containsTenGod(
                                    PalacePosition.HOUR,
                                    type
                            )
            ) {

                result.add(
                        type
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Debug / Provenance 이름
     * =================================================
     *
     * 최종 사용자 노출용 문장이 아니다.
     */

    private String joinTypes(
            List<StructureTenGodType> types
    ) {

        return String.join(
                ", ",
                types
                        .stream()
                        .map(
                                Enum::name
                        )
                        .toList()
        );
    }

    /*
     * =================================================
     * 내부 Semantic Summary
     * =================================================
     */

    private String summaryOf(

            PalaceContextEvidence hourContext,

            boolean resourcePresent,

            boolean outputPresent

    ) {

        String functionalContext;

        if (
                resourcePresent
                        && outputPresent
        ) {

            functionalContext =
                    "배우고 정리하는 기능과 표현·산출하는 기능";

        } else if (
                resourcePresent
        ) {

            functionalContext =
                    "배우고 이해하고 정리·축적하는 기능";

        } else {

            functionalContext =
                    "생각과 준비를 표현·실행·산출로 연결하는 기능";
        }

        if (
                hourContext
                        .hasReducedOriginalFunction()
        ) {

            return "확장·후반 전개 위치에서 "
                    + functionalContext
                    + "이 관여하며 Transformation에 따른 원기능 감소 Evidence도 존재하지만 이를 미래 성과의 좋고 나쁨으로 단순화하지 않습니다.";
        }

        if (
                hourContext
                        .hasActivatedTransformation()
        ) {

            return "확장·후반 전개 위치에서 "
                    + functionalContext
                    + "이 관여하고 실제 활성된 기능변화 Context가 함께 존재합니다.";
        }

        if (
                hourContext.hasStemRelations()
                        || hourContext.hasBranchRelations()
        ) {

            return "확장·후반 전개 위치에서 "
                    + functionalContext
                    + "이 원국의 다른 위치와 관계망을 이루며 작동합니다.";
        }

        return "확장·후반 전개 위치에서 "
                + functionalContext
                + "이 학습·성장 Context로 관여합니다.";
    }

    /*
     * =================================================
     * 단일 PALACE Evidence
     * =================================================
     */

    private Optional<LifeDomainEvidenceReference<?>>
    optionalSingleEvidence(

            LifeDomainEvidenceBundle bundle,

            LifeDomainEvidenceSourceType sourceType

    ) {

        List<LifeDomainEvidenceReference<?>>
                evidences =
                bundle.evidencesOf(
                        sourceType
                );

        if (
                evidences.isEmpty()
        ) {

            return Optional.empty();
        }

        if (
                evidences.size()
                        != 1
        ) {

            throw new IllegalStateException(
                    "LearningExtensionPalaceContextRule에는 최대 하나의 "
                            + sourceType
                            + " Evidence가 허용됩니다. actual="
                            + evidences.size()
            );
        }

        return Optional.of(
                evidences.get(
                        0
                )
        );
    }
}