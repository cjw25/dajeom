package com.dajeom.rule;

import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.ShinsalEvidence;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.ShinsalType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 학습·성장 영역의 문창귀인 보조 Rule.
 *
 * =================================================
 *
 * 핵심 원칙:
 *
 * 문창귀인은 AUXILIARY Evidence다.
 *
 * 따라서:
 *
 * 문창귀인 존재
 * -> 공부를 잘한다
 *
 * 문창귀인 존재
 * -> 시험에 합격한다
 *
 * 문창귀인 존재
 * -> 자격증을 취득한다
 *
 * 문창귀인 운
 * -> 학업 성과가 발생한다
 *
 * 같은 실제 결과를 생성하지 않는다.
 *
 * =================================================
 *
 * 이 Rule에서는 문창귀인을:
 *
 * - 배우고 이해하는 과정
 * - 정보를 정리하는 과정
 * - 생각을 언어·문서·표현으로 옮기는 과정
 * - 지식과 표현을 연결하는 과정
 *
 * 을 해석할 때 사용할 수 있는
 * 보조 Context로만 연결한다.
 *
 * =================================================
 *
 * 반드시 비보조 Evidence와 함께 사용한다.
 *
 * 핵심 Anchor:
 *
 * TEN_GOD_COMPOSITE
 *
 * 기존 Learning Rule이 이미:
 *
 * - 인성 계열
 * - 식상 계열
 * - 인성/식상 상호작용
 * - 교차검증 Pattern
 *
 * 을 판정하므로
 * 문창귀인이 그 구조를 대체하지 않는다.
 *
 * =================================================
 *
 * 운 신살의 경우:
 *
 * DECADE
 * -> DECADE_CONTEXT
 *
 * ANNUAL
 * -> ANNUAL_CONTEXT
 *
 * MONTHLY
 * -> MONTHLY_CONTEXT
 *
 * 를 해당 Bundle 안에서 추가 Anchor로 연결한다.
 *
 * =================================================
 *
 * 신살 자체는:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 *
 * 를 결정하지 않는다.
 *
 * 이 Rule의 상태는 항상:
 *
 * CONTEXTUAL
 *
 * 이다.
 *
 * =================================================
 *
 * DEFINITE / POSSIBLE은 17차 판정을 그대로 보존한다.
 *
 * POSSIBLE Evidence를 DEFINITE로 승격하지 않는다.
 */
@Component
public class LearningWenChangAuxiliaryRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "LEARNING_WEN_CHANG_AUXILIARY";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType.TEN_GOD_COMPOSITE,
                    LifeDomainEvidenceSourceType.DECADE_CONTEXT,
                    LifeDomainEvidenceSourceType.ANNUAL_CONTEXT,
                    LifeDomainEvidenceSourceType.MONTHLY_CONTEXT,
                    LifeDomainEvidenceSourceType.SHINSAL
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
                "LearningWenChangAuxiliaryRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.LEARNING_GROWTH
        ) {

            throw new IllegalArgumentException(
                    "LearningWenChangAuxiliaryRule은 LEARNING_GROWTH Bundle만 평가할 수 있습니다."
            );
        }

        List<LifeDomainEvidenceReference<?>>
                shinsalReferences =
                bundle.evidencesOf(
                        LifeDomainEvidenceSourceType.SHINSAL
                );

        if (
                shinsalReferences.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 핵심 Learning Anchor
         * =================================================
         *
         * 문창귀인 단독으로
         * 학습·성장 Match를 만들지 않는다.
         *
         * 10차 TenGodCompositeAnalysis가
         * 학습·흡수 / 표현·산출 구조의
         * 실제 핵심 근거다.
         */

        List<LifeDomainEvidenceReference<?>>
                coreAnchors =
                collectCoreAnchors(
                        bundle
                );

        if (
                coreAnchors.isEmpty()
        ) {

            return List.of();
        }

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 문창귀인 Evidence
         * =================================================
         */

        for (
                LifeDomainEvidenceReference<?> shinsalReference
                : shinsalReferences
        ) {

            ShinsalEvidence shinsalEvidence =
                    shinsalReference
                            .requireSource(
                                    ShinsalEvidence.class
                            );

            if (
                    shinsalEvidence.type()
                            != ShinsalType.WEN_CHANG_NOBLEMAN
            ) {

                continue;
            }

            /*
             * =================================================
             * 해당 운 시간층 Anchor
             * =================================================
             */

            List<LifeDomainEvidenceReference<?>>
                    temporalAnchors =
                    collectTemporalAnchors(
                            bundle,
                            shinsalEvidence
                    );

            List<LifeDomainEvidenceReference<?>>
                    usedEvidences =
                    new ArrayList<>();

            usedEvidences.addAll(
                    coreAnchors
            );

            usedEvidences.addAll(
                    temporalAnchors
            );

            usedEvidences.add(
                    shinsalReference
            );

            validateNonAuxiliaryAnchor(
                    usedEvidences
            );

            /*
             * =================================================
             * Reason
             * =================================================
             */

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "문창귀인 Evidence가 학습·성장 영역의 보조 Context로 연결되었습니다."
            );

            reasons.add(
                    "문창귀인은 기존 학습·흡수·정리·표현 구조를 대체하지 않고 해당 구조를 해석할 때 참고하는 보조 Evidence입니다."
            );

            reasons.add(
                    "신살 유형은 "
                            + shinsalEvidence
                            .type()
                            .koreanName()
                            + "입니다."
            );

            reasons.add(
                    "신살 Rule Code는 "
                            + shinsalEvidence.ruleCode()
                            + "입니다."
            );

            reasons.add(
                    "신살 Convention은 "
                            + shinsalEvidence.conventionId()
                            + "입니다."
            );

            reasons.add(
                    "신살 판정 Basis는 "
                            + shinsalEvidence
                            .basis()
                            .name()
                            + "입니다."
            );

            /*
             * =================================================
             * Certainty
             * =================================================
             */

            if (
                    shinsalEvidence.definite()
            ) {

                reasons.add(
                        "이 문창귀인 Evidence의 확실성은 DEFINITE입니다."
                );
            }

            if (
                    shinsalEvidence.possibleOnly()
            ) {

                reasons.add(
                        "이 문창귀인 Evidence의 확실성은 POSSIBLE이며 확정 Evidence로 승격하지 않습니다."
                );
            }

            /*
             * =================================================
             * 원국 / 운
             * =================================================
             */

            if (
                    shinsalEvidence.natalEvidence()
            ) {

                reasons.add(
                        "이 문창귀인 Evidence는 원국에서 검출되었습니다."
                );
            }

            if (
                    shinsalEvidence.luckEvidence()
            ) {

                reasons.add(
                        "이 문창귀인 Evidence는 현재 Timeline Segment에 적용되는 운 신살 Evidence입니다."
                );

                reasons.add(
                        "이 Evidence가 연결된 Timeline Segment 수는 "
                                + shinsalEvidence
                                .applicableSegmentCount()
                                + "개입니다."
                );
            }

            /*
             * =================================================
             * Layer
             * =================================================
             */

            if (
                    shinsalEvidence.decadeEvidence()
            ) {

                reasons.add(
                        "문창귀인 matchedPoint에 DECADE Layer가 포함되어 있습니다."
                );
            }

            if (
                    shinsalEvidence.annualEvidence()
            ) {

                reasons.add(
                        "문창귀인 matchedPoint에 ANNUAL Layer가 포함되어 있습니다."
                );
            }

            if (
                    shinsalEvidence.monthlyEvidence()
            ) {

                reasons.add(
                        "문창귀인 matchedPoint에 MONTHLY Layer가 포함되어 있습니다."
                );
            }

            /*
             * =================================================
             * 17차 원본 Reason 보존
             * =================================================
             */

            for (
                    String reason
                    : shinsalEvidence.reasons()
            ) {

                reasons.add(
                        "17차 문창귀인 근거: "
                                + reason
                );
            }

            /*
             * =================================================
             * 역할 제한
             * =================================================
             */

            reasons.add(
                    "문창귀인은 배우고 이해하고 정리하는 기능 또는 생각을 표현으로 옮기는 기능을 해석할 때 보조적으로 사용할 수 있습니다."
            );

            reasons.add(
                    "문창귀인 존재만으로 학습 능력·성적·시험 합격·자격 취득·문서 성과를 확정하지 않습니다."
            );

            reasons.add(
                    "실제 학습·성장 방향은 TEN_GOD_COMPOSITE 및 대운·세운·월운의 핵심 Context가 별도로 결정합니다."
            );

            reasons.add(
                    "문창귀인 자체로 기존 Learning Rule의 SUPPORTIVE, PRESSURE, MIXED 상태를 변경하지 않습니다."
            );

            /*
             * =================================================
             * Match
             * =================================================
             */

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            summaryOf(
                                    shinsalEvidence
                            ),
                            List.copyOf(
                                    usedEvidences
                            ),
                            List.copyOf(
                                    reasons
                            )
                    )
            );
        }

        return List.copyOf(
                matches
        );
    }

    /*
     * =================================================
     * Learning 핵심 Anchor
     * =================================================
     */

    private List<LifeDomainEvidenceReference<?>>
    collectCoreAnchors(
            LifeDomainEvidenceBundle bundle
    ) {

        List<LifeDomainEvidenceReference<?>>
                result =
                new ArrayList<>();

        addNonAuxiliaryReferences(
                result,
                bundle.evidencesOf(
                        LifeDomainEvidenceSourceType
                                .TEN_GOD_COMPOSITE
                )
        );

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 운 시간층 Anchor
     * =================================================
     *
     * 실제 ShinsalEvidence matched layer에
     * 대응하는 Context만 연결한다.
     */

    private List<LifeDomainEvidenceReference<?>>
    collectTemporalAnchors(

            LifeDomainEvidenceBundle bundle,

            ShinsalEvidence shinsalEvidence

    ) {

        List<LifeDomainEvidenceReference<?>>
                result =
                new ArrayList<>();

        if (
                shinsalEvidence.decadeEvidence()
        ) {

            addNonAuxiliaryReferences(
                    result,
                    bundle.evidencesOf(
                            LifeDomainEvidenceSourceType
                                    .DECADE_CONTEXT
                    )
            );
        }

        if (
                shinsalEvidence.annualEvidence()
        ) {

            addNonAuxiliaryReferences(
                    result,
                    bundle.evidencesOf(
                            LifeDomainEvidenceSourceType
                                    .ANNUAL_CONTEXT
                    )
            );
        }

        if (
                shinsalEvidence.monthlyEvidence()
        ) {

            addNonAuxiliaryReferences(
                    result,
                    bundle.evidencesOf(
                            LifeDomainEvidenceSourceType
                                    .MONTHLY_CONTEXT
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 비보조 Evidence만 추가
     * =================================================
     */

    private void addNonAuxiliaryReferences(

            List<LifeDomainEvidenceReference<?>> target,

            List<LifeDomainEvidenceReference<?>> candidates

    ) {

        for (
                LifeDomainEvidenceReference<?> candidate
                : candidates
        ) {

            Objects.requireNonNull(
                    candidate,
                    "LifeDomain Evidence Reference는 null일 수 없습니다."
            );

            if (
                    candidate.auxiliary()
            ) {

                continue;
            }

            target.add(
                    candidate
            );
        }
    }

    /*
     * =================================================
     * Auxiliary-only 방지
     * =================================================
     */

    private void validateNonAuxiliaryAnchor(
            List<LifeDomainEvidenceReference<?>> evidences
    ) {

        boolean nonAuxiliaryExists =
                evidences
                        .stream()
                        .anyMatch(
                                evidence ->
                                        !evidence.auxiliary()
                        );

        if (
                !nonAuxiliaryExists
        ) {

            throw new IllegalStateException(
                    "LearningWenChangAuxiliaryRule은 SHINSAL Evidence만으로 Match를 생성할 수 없습니다."
            );
        }
    }

    /*
     * =================================================
     * 내부 의미 Summary
     * =================================================
     *
     * 최종 사용자용 문장이 아니다.
     *
     * 나중에 AI Grounding에서:
     *
     * - 배우는 내용을 정리하는 과정
     * - 생각을 글이나 말로 표현하는 과정
     * - 준비한 내용을 실제로 꺼내 쓰는 과정
     *
     * 등의 일반 생활언어로 풀기 위한
     * 보조 의미만 제공한다.
     */

    private String summaryOf(
            ShinsalEvidence shinsalEvidence
    ) {

        if (
                shinsalEvidence.possibleOnly()
        ) {

            return "배우고 정리한 내용을 이해·표현으로 연결하는 맥락을 보조할 가능성이 있으나 확정 Context는 아닙니다.";
        }

        return "배우고 정리한 내용을 이해·표현으로 연결하는 맥락을 보조하는 근거가 존재합니다.";
    }
}