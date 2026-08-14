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
 * 학습·성장 영역의 화개 보조 Rule.
 *
 * =================================================
 *
 * 화개는 AUXILIARY Evidence다.
 *
 * 따라서:
 *
 * 화개 존재
 * -> 공부를 잘한다
 *
 * 화개 존재
 * -> 예술가가 된다
 *
 * 화개 존재
 * -> 종교적이다
 *
 * 화개 존재
 * -> 외롭다
 *
 * 화개 운
 * -> 혼자 지낸다
 *
 * 같은 결과를 생성하지 않는다.
 *
 * =================================================
 *
 * 이 Rule에서 화개는:
 *
 * - 한 주제를 오래 붙드는 몰입
 * - 혼자 정리하고 생각하는 시간
 * - 내면화
 * - 깊이 파고드는 탐구
 * - 외부 자극보다 자기 안에서 정리하는 과정
 *
 * 같은 학습·성장 맥락을
 * 보조하는 Evidence로만 사용한다.
 *
 * =================================================
 *
 * 핵심 Learning Anchor:
 *
 * TEN_GOD_COMPOSITE
 *
 * 기존 Learning Rule이 이미:
 *
 * - 인성 구조
 * - 식상 구조
 * - 학습·흡수
 * - 표현·산출
 * - 상호 제약
 * - 교차검증 Pattern
 *
 * 을 계산한다.
 *
 * 화개는 그 판단을 대체하지 않는다.
 *
 * =================================================
 *
 * 운 화개의 경우 matched layer에 따라:
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
 * 를 추가 Anchor로 연결한다.
 *
 * =================================================
 *
 * 상태:
 *
 * 이 Rule은 항상 CONTEXTUAL이다.
 *
 * 화개 자체로:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 *
 * 를 생성하지 않는다.
 *
 * =================================================
 *
 * DEFINITE / POSSIBLE은
 * 17차 ShinsalEvidence의 certainty를
 * 그대로 보존한다.
 */
@Component
public class LearningCanopyAuxiliaryRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "LEARNING_CANOPY_AUXILIARY";

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
                "LearningCanopyAuxiliaryRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.LEARNING_GROWTH
        ) {

            throw new IllegalArgumentException(
                    "LearningCanopyAuxiliaryRule은 LEARNING_GROWTH Bundle만 평가할 수 있습니다."
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
         * 화개 단독으로 Match를 만들지 않는다.
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
         * 화개 Evidence
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
                            != ShinsalType.CANOPY
            ) {

                continue;
            }

            /*
             * =================================================
             * 실제 운 Layer Anchor
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
             * Reasons
             * =================================================
             */

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "화개 Evidence가 학습·성장 영역의 보조 Context로 연결되었습니다."
            );

            reasons.add(
                    "화개는 기존 학습 구조를 대체하지 않고 몰입·내면화·탐구 맥락을 보조하는 Evidence로 사용합니다."
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
                        "이 화개 Evidence의 확실성은 DEFINITE입니다."
                );
            }

            if (
                    shinsalEvidence.possibleOnly()
            ) {

                reasons.add(
                        "이 화개 Evidence의 확실성은 POSSIBLE이며 확정 Evidence로 승격하지 않습니다."
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
                        "이 화개 Evidence는 원국에서 검출되었습니다."
                );
            }

            if (
                    shinsalEvidence.luckEvidence()
            ) {

                reasons.add(
                        "이 화개 Evidence는 운 Timeline에 적용되는 보조 Evidence입니다."
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
                        "화개 matchedPoint에 DECADE Layer가 포함되어 있습니다."
                );
            }

            if (
                    shinsalEvidence.annualEvidence()
            ) {

                reasons.add(
                        "화개 matchedPoint에 ANNUAL Layer가 포함되어 있습니다."
                );
            }

            if (
                    shinsalEvidence.monthlyEvidence()
            ) {

                reasons.add(
                        "화개 matchedPoint에 MONTHLY Layer가 포함되어 있습니다."
                );
            }

            /*
             * =================================================
             * 17차 원본 deterministic 근거
             * =================================================
             */

            for (
                    String reason
                    : shinsalEvidence.reasons()
            ) {

                reasons.add(
                        "17차 화개 근거: "
                                + reason
                );
            }

            /*
             * =================================================
             * 학습·성장 의미
             * =================================================
             */

            reasons.add(
                    "화개는 학습·성장 영역에서 한 주제를 깊게 파고들거나 혼자 정리하고 내면화하는 과정의 보조 맥락으로 사용합니다."
            );

            reasons.add(
                    "몰입과 혼자 정리하는 시간이 도움이 될 수 있다는 맥락과 실제 학습 성과가 발생한다는 것은 동일하지 않습니다."
            );

            reasons.add(
                    "화개가 존재하더라도 기존 Learning Evidence가 표현·실행의 부담을 나타내면 그 부담을 삭제하지 않습니다."
            );

            reasons.add(
                    "화개 존재만으로 예술적 재능·종교적 성향·학문적 성공·시험 합격·전문가가 되는 결과를 확정하지 않습니다."
            );

            reasons.add(
                    "학습·성장 영역의 실제 방향은 기존 Learning 핵심 Rule과 대운·세운·월운 Context가 별도로 결정합니다."
            );

            reasons.add(
                    "화개 자체로 기존 Learning Rule의 SUPPORTIVE, PRESSURE, MIXED 상태를 변경하지 않습니다."
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
     * 비보조 Reference만 추가
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
                    "LearningCanopyAuxiliaryRule은 SHINSAL Evidence만으로 Match를 생성할 수 없습니다."
            );
        }
    }

    /*
     * =================================================
     * 내부 의미 Summary
     * =================================================
     *
     * 최종 사용자용 점사 문장이 아니다.
     *
     * 이후 AI Grounding에서는:
     *
     * - 여러 가지를 얕게 보기보다 하나를 깊게 파는지
     * - 혼자 정리할 시간이 필요한지
     * - 외부 활동과 내부 정리의 균형이 어떤지
     * - 준비가 너무 길어져 실행이 늦지는 않는지
     *
     * 같은 일반 생활언어로 변환할 수 있다.
     */

    private String summaryOf(
            ShinsalEvidence shinsalEvidence
    ) {

        if (
                shinsalEvidence.possibleOnly()
        ) {

            return "한 주제를 깊게 탐구하고 혼자 정리·내면화하는 맥락을 보조할 가능성이 있으나 확정 Context는 아닙니다.";
        }

        return "한 주제를 깊게 탐구하고 혼자 정리·내면화하는 맥락을 보조하는 근거가 존재합니다.";
    }
}