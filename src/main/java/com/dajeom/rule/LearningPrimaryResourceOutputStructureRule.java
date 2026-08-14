package com.dajeom.rule;

import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
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
 * 학습·성장 영역의
 * 인성/식상 주격 격국 Context Rule.
 *
 * =================================================
 *
 * Source:
 *
 * 5차 StructureAnalysis
 *
 * =================================================
 *
 * 현재 직접 판정 대상:
 *
 * 인성 계열:
 *
 * - DIRECT_RESOURCE
 * - INDIRECT_RESOURCE
 *
 * 식상 계열:
 *
 * - EATING_GOD
 * - HURTING_OFFICER
 *
 * =================================================
 *
 * 인성 계열 주격:
 *
 * 원국 전체 구조에서:
 *
 * - 배우기
 * - 이해하기
 * - 받아들이기
 * - 정리하기
 * - 축적하기
 * - 준비하기
 *
 * 같은 기능이
 * 중심축으로 관여하는 구조 Evidence.
 *
 * =================================================
 *
 * 식상 계열 주격:
 *
 * 원국 전체 구조에서:
 *
 * - 표현하기
 * - 설명하기
 * - 만들어내기
 * - 실행하기
 * - 결과물로 꺼내기
 *
 * 같은 기능이
 * 중심축으로 관여하는 구조 Evidence.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * 인성격
 * -> 공부를 잘한다
 *
 * 인성격
 * -> 시험에 합격한다
 *
 * 편인격
 * -> 천재다
 *
 * 식신격
 * -> 예술적 재능이 있다
 *
 * 상관격
 * -> 말을 잘한다
 *
 * 식상격
 * -> 창업에 성공한다
 *
 * 같은 능력·성과·사건을
 * 직접 생성하지 않는다.
 *
 * =================================================
 *
 * 기존 Learning Rule과의 차이:
 *
 * LearningResourceOutputRule:
 *
 * TenGodCompositeAnalysis를 통해
 * 실제 인성/식상 분포와 발달,
 * 일간 수용력,
 * 인성-식상 간 제약 등을 본다.
 *
 * LearningCrossValidatedPatternRule:
 *
 * 인성/식상 및 관성/재성과의
 * 복합 Pattern 교차검증을 본다.
 *
 * 이 Rule:
 *
 * 5차 격국에서 실제 주격이
 * 인성 또는 식상으로 선택되었는지와
 * 그 중심 구조의:
 *
 * - 성립
 * - 부담
 * - 손상
 * - 혼잡
 *
 * 을 STRUCTURE Evidence로 보존한다.
 *
 * =================================================
 *
 * 상태:
 *
 * SUPPORTIVE
 *
 * 학습 능력이 좋다는 뜻이 아니다.
 *
 * 해당 학습/표현 중심축이
 * 비교적 성립되어 있다는 뜻이다.
 *
 * -------------------------------------------------
 *
 * PRESSURE
 *
 * 공부를 못한다거나
 * 표현력이 나쁘다는 뜻이 아니다.
 *
 * 중심 구조에 부담 또는
 * 강한 손상이 존재한다는 뜻이다.
 *
 * -------------------------------------------------
 *
 * MIXED
 *
 * 중심축 성립 근거와
 * 부담·손상·혼잡 근거가
 * 동시에 존재한다.
 *
 * -------------------------------------------------
 *
 * CONTEXTUAL
 *
 * 인성/식상이 주격으로 관여하지만
 * 방향을 단순 확정하기 어려운
 * 구조 Context다.
 *
 * =================================================
 *
 * 보조격:
 *
 * StructureSelectionCandidate의
 * 타입 API를 추측하지 않는다.
 *
 * 따라서:
 *
 * - 보조격 존재 여부
 * - 보조격 이름
 *
 * 만 Provenance로 남긴다.
 *
 * =================================================
 *
 * 시간 미상:
 *
 * completeChart=false를 그대로 보존한다.
 */
@Component
public class LearningPrimaryResourceOutputStructureRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType.STRUCTURE
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
                "LearningPrimaryResourceOutputStructureRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.LEARNING_GROWTH
        ) {

            throw new IllegalArgumentException(
                    "LearningPrimaryResourceOutputStructureRule은 LEARNING_GROWTH Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                structureReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType.STRUCTURE
                );

        if (
                structureReferenceOptional.isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                structureReference =
                structureReferenceOptional
                        .orElseThrow();

        StructureAnalysis structureAnalysis =
                structureReference
                        .requireSource(
                                StructureAnalysis.class
                        );

        /*
         * =================================================
         * 특수격을 인성/식상 일반격으로
         * 임의 환원하지 않는다.
         * =================================================
         */

        if (
                !structureAnalysis
                        .primaryIsStandard()
        ) {

            return List.of();
        }

        Optional<StructureTenGodType>
                primaryTypeOptional =
                structureAnalysis
                        .primaryStandardTenGod();

        if (
                primaryTypeOptional.isEmpty()
        ) {

            throw new IllegalStateException(
                    "primaryIsStandard=true인데 primaryStandardTenGod이 존재하지 않습니다."
            );
        }

        StructureTenGodType primaryType =
                primaryTypeOptional
                        .orElseThrow();

        boolean resourcePrimary =
                RESOURCE_TYPES.contains(
                        primaryType
                );

        boolean outputPrimary =
                OUTPUT_TYPES.contains(
                        primaryType
                );

        if (
                !resourcePrimary
                        && !outputPrimary
        ) {

            return List.of();
        }

        LifeDomainSignalStatus status =
                resolveStatus(
                        structureAnalysis
                );

        List<String> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 기본 Identity
         * =================================================
         */

        reasons.add(
                "5차 격국 분석에서 주격은 "
                        + structureAnalysis
                        .primaryNameKorean()
                        + "입니다."
        );

        reasons.add(
                "주격 일반격 십성 유형은 "
                        + primaryType.name()
                        + "입니다."
        );

        reasons.add(
                "최종 격 상태는 "
                        + structureAnalysis
                        .statusKorean()
                        + "입니다."
        );

        reasons.add(
                "최종 격 상태 설명: "
                        + structureAnalysis
                        .statusDescription()
        );

        reasons.add(
                "격국 분석 기준 일간은 "
                        + structureAnalysis
                        .dayMaster()
                        + ", 월지는 "
                        + structureAnalysis
                        .monthBranch()
                        + "입니다."
        );

        /*
         * =================================================
         * 기능 축
         * =================================================
         */

        if (
                resourcePrimary
        ) {

            reasons.add(
                    "인성 계열이 주격이므로 배우고 이해하고 받아들인 내용을 정리·축적하는 기능이 원국의 중심 구조에 관여합니다."
            );
        }

        if (
                outputPrimary
        ) {

            reasons.add(
                    "식상 계열이 주격이므로 생각이나 준비한 내용을 표현·실행·산출하는 기능이 원국의 중심 구조에 관여합니다."
            );
        }

        /*
         * =================================================
         * 성립
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryEstablished()
        ) {

            reasons.add(
                    "현재 주격은 성립 계열 상태로 판정되었습니다."
            );

        } else {

            reasons.add(
                    "현재 주격이 선택되어 있으나 성립 계열 상태로 확정된 것은 아닙니다."
            );
        }

        /*
         * =================================================
         * 균형 부담
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryBurdened()
        ) {

            reasons.add(
                    "현재 주격은 일간 강약 및 균형 관점에서 부담 조건을 동반합니다."
            );
        }

        /*
         * =================================================
         * 손상
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryDamaged()
        ) {

            reasons.add(
                    "현재 주격에 의미 있는 손상 Evidence가 존재합니다."
            );
        }

        if (
                structureAnalysis
                        .primaryStronglyDamaged()
        ) {

            reasons.add(
                    "현재 주격에 강한 손상 Evidence가 존재합니다."
            );
        }

        /*
         * =================================================
         * 순도 / 혼잡
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryRelativelyPure()
        ) {

            reasons.add(
                    "현재 주격은 상대적으로 순도가 확보된 구조입니다."
            );
        }

        if (
                structureAnalysis
                        .primaryHighlyMixed()
        ) {

            reasons.add(
                    "현재 주격에는 높은 혼잡도 Evidence가 존재합니다."
            );
        }

        /*
         * =================================================
         * 보조격
         * =================================================
         *
         * 이름은 Provenance로만 사용한다.
         * 문자열을 파싱해서 종류를 역추론하지 않는다.
         */

        if (
                structureAnalysis
                        .hasSecondaryStructure()
        ) {

            reasons.add(
                    "주격 외에 보조격 후보가 존재합니다."
            );

            if (
                    !structureAnalysis
                            .secondaryNamesKorean()
                            .isEmpty()
            ) {

                reasons.add(
                        "보조격 후보: "
                                + String.join(
                                ", ",
                                structureAnalysis
                                        .secondaryNamesKorean()
                        )
                );
            }
        }

        /*
         * =================================================
         * 특수격 후보 / 추가 검토
         * =================================================
         */

        if (
                structureAnalysis
                        .hasSpecialStructureCandidate()
        ) {

            reasons.add(
                    "현재 일반격 주격과 별개로 특수격 후보가 존재합니다."
            );
        }

        if (
                structureAnalysis
                        .requiresSpecialReview()
        ) {

            reasons.add(
                    "특수구조 추가 검토가 필요하므로 현재 일반격 주격을 절대적인 단일 구조로 확정하지 않습니다."
            );
        }

        /*
         * =================================================
         * 완전성
         * =================================================
         */

        if (
                !structureAnalysis
                        .completeChart()
        ) {

            reasons.add(
                    "원국이 completeChart=false이므로 시간 미상 등에서 발생하는 구조 불확실성을 그대로 보존합니다."
            );
        }

        /*
         * =================================================
         * 생활영역 의미 제한
         * =================================================
         */

        if (
                resourcePrimary
        ) {

            reasons.add(
                    "인성 주격은 학습·이해·정리 기능이 중심축에 관여한다는 뜻이며 학업 성적·시험 합격·자격 취득 같은 결과를 보장하지 않습니다."
            );
        }

        if (
                outputPrimary
        ) {

            reasons.add(
                    "식상 주격은 표현·실행·산출 기능이 중심축에 관여한다는 뜻이며 표현 능력·창작 성공·사업 성공 같은 결과를 보장하지 않습니다."
            );
        }

        reasons.add(
                "격의 성립·부담·손상·혼잡 상태는 학습이나 재능의 우열이 아니라 해당 기능이 원국 중심 구조에서 작동하는 안정성·부담·복합성을 설명하는 Context입니다."
        );

        reasons.add(
                "실제 인성·식상 발달도와 일간 수용력, 인성-식상 상호작용은 별도의 LearningResourceOutputRule이 담당합니다."
        );

        reasons.add(
                "인성·식상 관련 복합 Pattern의 교차검증은 LearningCrossValidatedPatternRule이 담당하며 이 Rule은 5차 주격 정보를 추가 Context로만 보존합니다."
        );

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.LEARNING_GROWTH,
                        RULE_CODE,
                        status,
                        summaryOf(
                                primaryType,
                                structureAnalysis,
                                status
                        ),
                        List.of(
                                structureReference
                        ),
                        List.copyOf(
                                reasons
                        )
                )
        );
    }

    /*
     * =================================================
     * STRUCTURE -> 상태
     * =================================================
     *
     * 학업 길흉이나 능력 등급이 아니다.
     *
     * 해당 주격 중심축이
     * 어떤 구조 상태로 존재하는지만 표현한다.
     */

    private LifeDomainSignalStatus resolveStatus(
            StructureAnalysis analysis
    ) {

        Objects.requireNonNull(
                analysis,
                "StructureAnalysis가 필요합니다."
        );

        boolean established =
                analysis.primaryEstablished();

        boolean burdened =
                analysis.primaryBurdened();

        boolean damaged =
                analysis.primaryDamaged();

        boolean stronglyDamaged =
                analysis.primaryStronglyDamaged();

        boolean highlyMixed =
                analysis.primaryHighlyMixed();

        /*
         * =================================================
         * 강한 손상
         * =================================================
         */

        if (
                stronglyDamaged
        ) {

            return established
                    ? LifeDomainSignalStatus.MIXED
                    : LifeDomainSignalStatus.PRESSURE;
        }

        /*
         * =================================================
         * 손상 / 높은 혼잡
         * =================================================
         */

        if (
                damaged
                        || highlyMixed
        ) {

            return LifeDomainSignalStatus.MIXED;
        }

        /*
         * =================================================
         * 균형 부담
         * =================================================
         */

        if (
                burdened
        ) {

            return established
                    ? LifeDomainSignalStatus.MIXED
                    : LifeDomainSignalStatus.PRESSURE;
        }

        /*
         * =================================================
         * 성립
         * =================================================
         */

        if (
                established
        ) {

            return LifeDomainSignalStatus.SUPPORTIVE;
        }

        return LifeDomainSignalStatus.CONTEXTUAL;
    }

    /*
     * =================================================
     * 내부 의미 Summary
     * =================================================
     *
     * 사용자 최종 점사 문장이 아니다.
     */

    private String summaryOf(

            StructureTenGodType primaryType,

            StructureAnalysis structureAnalysis,

            LifeDomainSignalStatus status

    ) {

        String center =
                switch (
                        primaryType
                        ) {

                    case DIRECT_RESOURCE ->
                            "배운 내용을 안정적으로 받아들이고 정리·축적하는 기능";

                    case INDIRECT_RESOURCE ->
                            "정보를 선택적으로 받아들이고 자기 방식으로 해석·내면화하는 기능";

                    case EATING_GOD ->
                            "준비한 내용을 비교적 자연스럽게 표현하고 결과물로 만들어내는 기능";

                    case HURTING_OFFICER ->
                            "생각을 적극적으로 표현하고 기존 방식과 다른 결과물로 꺼내는 기능";

                    default ->
                            "배움과 표현을 연결하는 기능";
                };

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "원국의 중심 구조에서 "
                            + center
                            + "이 비교적 성립된 축으로 관여합니다.";

            case PRESSURE ->
                    "원국의 중심 구조에서 "
                            + center
                            + "이 관여하지만 부담 또는 손상 조건이 두드러집니다.";

            case MIXED ->
                    "원국의 중심 구조에서 "
                            + center
                            + "이 중요한 축으로 관여하면서 성립 근거와 부담·손상·혼잡 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "원국의 중심 구조에서 "
                            + center
                            + "이 관여하지만 학습·성장 방향을 단독으로 확정할 수준으로 단순화하지 않습니다.";
        };
    }

    /*
     * =================================================
     * 단일 STRUCTURE Evidence
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
                    "LearningPrimaryResourceOutputStructureRule에는 최대 하나의 "
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