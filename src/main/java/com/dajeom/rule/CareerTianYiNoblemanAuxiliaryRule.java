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
 * 직업·사회 영역의 천을귀인 보조 Rule.
 *
 * =================================================
 *
 * 천을귀인은 AUXILIARY Evidence다.
 *
 * 따라서:
 *
 * 천을귀인 존재
 * -> 귀인이 나타난다
 *
 * 천을귀인 존재
 * -> 상사가 도와준다
 *
 * 천을귀인 운
 * -> 취업한다
 *
 * 천을귀인 운
 * -> 승진한다
 *
 * 천을귀인 운
 * -> 문제가 해결된다
 *
 * 같은 실제 사건을 생성하지 않는다.
 *
 * =================================================
 *
 * 이 Rule에서 천을귀인은:
 *
 * - 혼자 해결하지 않아도 되는 여지
 * - 외부 지원을 활용할 수 있는 맥락
 * - 사람·조직·제도를 통한 중재 가능성
 * - 조언이나 협업 자원을 활용하는 맥락
 * - 직업적 문제를 완충할 수 있는 보조 조건
 *
 * 을 해석하기 위한 Auxiliary Evidence다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 천을귀인 자체가
 * 직업 구조를 SUPPORTIVE로 바꾸지 않는다.
 *
 * 예:
 *
 * 원국 Career PRESSURE
 * +
 * 천을귀인
 *
 * !=
 *
 * Career SUPPORTIVE
 *
 * 기존 부담 구조는 그대로 유지한다.
 *
 * 천을귀인은:
 *
 * "부담이 존재하지만
 *  혼자 감당하는 방식만 있는 것은 아니다"
 *
 * 같은 추가 해석 근거로만 사용할 수 있다.
 *
 * =================================================
 *
 * 핵심 직업 Anchor:
 *
 * TEN_GOD_COMPOSITE
 *
 * 기존 Career Rule에서 이미:
 *
 * - 역할과 책임
 * - 관성 구조
 * - 압박
 * - 식상 제어
 * - 인성 중재
 * - 재성 지원
 * - 교차검증 Pattern
 *
 * 을 계산한다.
 *
 * 천을귀인은 이를 다시 계산하거나
 * 대체하지 않는다.
 *
 * =================================================
 *
 * 운 신살인 경우:
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
 * matched layer에 해당하는
 * 시간층 Evidence를 함께 연결한다.
 *
 * =================================================
 *
 * 상태:
 *
 * 이 Rule은 항상 CONTEXTUAL이다.
 *
 * 신살 자체로:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 *
 * 를 생성하지 않는다.
 *
 * =================================================
 *
 * DEFINITE / POSSIBLE:
 *
 * 17차 ShinsalEvidence의 certainty를
 * 그대로 보존한다.
 *
 * POSSIBLE을 DEFINITE로 승격하지 않는다.
 *
 * =================================================
 *
 * 최종 사용자 풀이에서는:
 *
 * "천을귀인이 있습니다"
 *
 * 라는 명리용어 노출보다:
 *
 * - 혼자 해결하려 하지 않는 것이 중요하다
 * - 도움을 요청할 여지가 있다
 * - 사람이나 조직의 지원을 활용하는 것이 중요하다
 * - 문제가 생기면 중재자나 경험자의 조언을 쓰는 편이 낫다
 *
 * 같은 생활언어로 변환할 수 있도록
 * deterministic 근거를 보존한다.
 */
@Component
public class CareerTianYiNoblemanAuxiliaryRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "CAREER_TIAN_YI_NOBLEMAN_AUXILIARY";

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

        return LifeDomain.CAREER;
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
                "CareerTianYiNoblemanAuxiliaryRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerTianYiNoblemanAuxiliaryRule은 CAREER Bundle만 평가할 수 있습니다."
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
         * 핵심 Career Anchor
         * =================================================
         *
         * 천을귀인 단독으로
         * Career Match를 생성하지 않는다.
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
         * 천을귀인 Evidence
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
                            != ShinsalType.TIAN_YI_NOBLEMAN
            ) {

                continue;
            }

            /*
             * =================================================
             * 실제 시간층 Anchor
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
                    "천을귀인 Evidence가 직업·사회 영역의 보조 Context로 연결되었습니다."
            );

            reasons.add(
                    "천을귀인은 기존 직업 구조의 방향을 바꾸지 않고 외부 지원·중재·협업 자원을 해석하기 위한 보조 Evidence로 사용합니다."
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
                        "이 천을귀인 Evidence의 확실성은 DEFINITE입니다."
                );
            }

            if (
                    shinsalEvidence.possibleOnly()
            ) {

                reasons.add(
                        "이 천을귀인 Evidence의 확실성은 POSSIBLE이며 확정 Evidence로 승격하지 않습니다."
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
                        "이 천을귀인 Evidence는 원국에서 검출되었습니다."
                );
            }

            if (
                    shinsalEvidence.luckEvidence()
            ) {

                reasons.add(
                        "이 천을귀인 Evidence는 운 Timeline에 적용되는 보조 Evidence입니다."
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
                        "천을귀인 matchedPoint에 DECADE Layer가 포함되어 있습니다."
                );
            }

            if (
                    shinsalEvidence.annualEvidence()
            ) {

                reasons.add(
                        "천을귀인 matchedPoint에 ANNUAL Layer가 포함되어 있습니다."
                );
            }

            if (
                    shinsalEvidence.monthlyEvidence()
            ) {

                reasons.add(
                        "천을귀인 matchedPoint에 MONTHLY Layer가 포함되어 있습니다."
                );
            }

            /*
             * =================================================
             * 17차 원본 Evidence 보존
             * =================================================
             */

            for (
                    String reason
                    : shinsalEvidence.reasons()
            ) {

                reasons.add(
                        "17차 천을귀인 근거: "
                                + reason
                );
            }

            /*
             * =================================================
             * 직업·사회 의미
             * =================================================
             */

            reasons.add(
                    "천을귀인은 직업·사회 문제에서 사람·조직·조언·협업·중재 같은 외부 자원을 활용할 수 있는 맥락을 보조적으로 해석하는 데 사용합니다."
            );

            reasons.add(
                    "외부 지원 맥락이 존재한다는 것과 실제 특정 인물이 나타나 도움을 준다는 것은 동일하지 않습니다."
            );

            reasons.add(
                    "직업영역에 부담 Evidence가 존재하더라도 천을귀인만으로 그 부담을 삭제하거나 SUPPORTIVE 상태로 변경하지 않습니다."
            );

            reasons.add(
                    "천을귀인 존재만으로 취업·승진·합격·문제 해결·인맥 획득 같은 실제 사건을 확정하지 않습니다."
            );

            reasons.add(
                    "직업·사회 영역의 실제 방향은 기존 Career 핵심 Rule과 대운·세운·월운 Context가 별도로 결정합니다."
            );

            /*
             * =================================================
             * Match
             * =================================================
             */

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.CAREER,
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
     * Career 핵심 Anchor
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
     * 신살이 실제로 검출된 Layer에 대응하는
     * Context만 추가한다.
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
     * Auxiliary-only Match 방지
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
                    "CareerTianYiNoblemanAuxiliaryRule은 SHINSAL Evidence만으로 Match를 생성할 수 없습니다."
            );
        }
    }

    /*
     * =================================================
     * 내부 의미 Summary
     * =================================================
     *
     * 최종 사용자 문장이 아니다.
     *
     * 이후 Grounding/AI에서는:
     *
     * - 혼자 해결하는 방식만 고집할 필요가 없는지
     * - 경험자의 조언을 활용할 여지가 있는지
     * - 사람이나 조직의 지원을 활용하는 것이 중요한지
     * - 갈등 상황에서 중재를 쓰는 편이 나은지
     *
     * 같은 일반 생활언어로 변환할 수 있다.
     */

    private String summaryOf(
            ShinsalEvidence shinsalEvidence
    ) {

        if (
                shinsalEvidence.possibleOnly()
        ) {

            return "직업·사회 문제를 혼자 처리하기보다 외부의 도움·조언·협업 자원을 활용할 수 있는 맥락을 보조할 가능성이 있으나 확정 Context는 아닙니다.";
        }

        return "직업·사회 문제에서 외부의 도움·조언·협업 자원을 활용할 수 있는 맥락을 보조하는 근거가 존재합니다.";
    }
}