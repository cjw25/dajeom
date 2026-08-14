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
 * 관계영역의 도화 신살 보조 Rule.
 *
 * =================================================
 *
 * 핵심 원칙:
 *
 * 신살은 AUXILIARY Evidence다.
 *
 * 따라서:
 *
 * 도화 존재
 * -> 연애운 좋음
 *
 * 도화 존재
 * -> 이성에게 인기 많음 확정
 *
 * 도화 운
 * -> 새로운 사람 등장
 *
 * 도화 운
 * -> 연애 시작
 *
 * 도화 운
 * -> 결혼
 *
 * 으로 판단하지 않는다.
 *
 * =================================================
 *
 * 도화는 관계영역에서:
 *
 * - 사람에게 드러나는 정도
 * - 대인 접점
 * - 관계 관심
 * - 타인과의 상호작용이 두드러지는 맥락
 *
 * 을 보조하는 Evidence로만 사용한다.
 *
 * =================================================
 *
 * 반드시 비보조 Evidence와 결합한다.
 *
 * 원국 구조:
 *
 * - PALACE
 * - NATAL_RELATION
 * - TRANSFORMATION
 *
 * 운 신살인 경우 해당 시간층:
 *
 * - DECADE_CONTEXT
 * - ANNUAL_CONTEXT
 * - MONTHLY_CONTEXT
 *
 * 도 함께 연결할 수 있다.
 *
 * =================================================
 *
 * 신살 자체는 SUPPORTIVE / PRESSURE를
 * 결정하지 않는다.
 *
 * 이 Rule이 생성하는 상태는 항상:
 *
 * CONTEXTUAL
 *
 * 이다.
 *
 * 실제 관계 방향은 다른 핵심 Rule들이
 * 별도로 판정한다.
 *
 * =================================================
 *
 * DEFINITE / POSSIBLE도 보존한다.
 *
 * POSSIBLE 신살을 DEFINITE로 승격하지 않는다.
 */
@Component
public class RelationshipPeachBlossomAuxiliaryRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "RELATIONSHIP_PEACH_BLOSSOM_AUXILIARY";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType.PALACE,
                    LifeDomainEvidenceSourceType.NATAL_RELATION,
                    LifeDomainEvidenceSourceType.TRANSFORMATION,
                    LifeDomainEvidenceSourceType.DECADE_CONTEXT,
                    LifeDomainEvidenceSourceType.ANNUAL_CONTEXT,
                    LifeDomainEvidenceSourceType.MONTHLY_CONTEXT,
                    LifeDomainEvidenceSourceType.SHINSAL
            );

    @Override
    public LifeDomain domain() {

        return LifeDomain.RELATIONSHIP;
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
                "RelationshipPeachBlossomAuxiliaryRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "RelationshipPeachBlossomAuxiliaryRule은 RELATIONSHIP Bundle만 평가할 수 있습니다."
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
         * 도화 Evidence만 선택
         * =================================================
         */

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

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
                            != ShinsalType.PEACH_BLOSSOM
            ) {

                continue;
            }

            /*
             * =================================================
             * 핵심 Evidence Anchor
             * =================================================
             *
             * 도화만으로 Match를 만들지 않는다.
             *
             * 원국 관계 구조를 우선 Anchor로 사용한다.
             */

            List<LifeDomainEvidenceReference<?>>
                    coreAnchors =
                    collectCoreAnchors(
                            bundle
                    );

            if (
                    coreAnchors.isEmpty()
            ) {

                throw new IllegalStateException(
                        "도화 신살을 관계영역에 연결하려면 최소 하나의 비보조 관계 Evidence가 필요합니다."
                );
            }

            /*
             * =================================================
             * 시간층 Anchor
             * =================================================
             *
             * 운 신살이면 실제 matched layer에 대응하는
             * 시간층 Evidence만 추가한다.
             *
             * 예:
             *
             * ANNUAL 도화
             * -> ANNUAL_CONTEXT
             *
             * MONTHLY 도화
             * -> MONTHLY_CONTEXT
             *
             * 가능한 대운 중 하나를 여기서 선택하지 않는다.
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
                    "도화 신살 Evidence가 관계영역의 보조 Context로 연결되었습니다."
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
                    "신살 판정 근거 Basis는 "
                            + shinsalEvidence
                            .basis()
                            .name()
                            + "입니다."
            );

            /*
             * =================================================
             * Certainty 보존
             * =================================================
             */

            if (
                    shinsalEvidence.definite()
            ) {

                reasons.add(
                        "이 도화 Evidence의 확실성은 DEFINITE입니다."
                );
            }

            if (
                    shinsalEvidence.possibleOnly()
            ) {

                reasons.add(
                        "이 도화 Evidence의 확실성은 POSSIBLE이며 확정된 신살로 승격하지 않습니다."
                );
            }

            /*
             * =================================================
             * 원국 / 운 구분
             * =================================================
             */

            if (
                    shinsalEvidence.natalEvidence()
            ) {

                reasons.add(
                        "이 도화 Evidence는 원국에서 검출되었습니다."
                );
            }

            if (
                    shinsalEvidence.luckEvidence()
            ) {

                reasons.add(
                        "이 도화 Evidence는 운 Timeline에 적용되는 보조 Evidence입니다."
                );

                reasons.add(
                        "이 Evidence가 연결된 Timeline Segment 수는 "
                                + shinsalEvidence
                                .applicableSegmentCount()
                                + "개입니다."
                );
            }

            if (
                    shinsalEvidence.decadeEvidence()
            ) {

                reasons.add(
                        "도화 matchedPoint에 DECADE Layer가 포함되어 있습니다."
                );
            }

            if (
                    shinsalEvidence.annualEvidence()
            ) {

                reasons.add(
                        "도화 matchedPoint에 ANNUAL Layer가 포함되어 있습니다."
                );
            }

            if (
                    shinsalEvidence.monthlyEvidence()
            ) {

                reasons.add(
                        "도화 matchedPoint에 MONTHLY Layer가 포함되어 있습니다."
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
                        "17차 도화 근거: "
                                + reason
                );
            }

            /*
             * =================================================
             * 역할 제한
             * =================================================
             */

            reasons.add(
                    "도화는 관계 구조 자체를 결정하지 않고 기존 관계 Evidence에 대인 접점·주목·상호작용 맥락을 보조합니다."
            );

            reasons.add(
                    "도화 존재만으로 관계의 성립·연애 시작·결혼·이별·재결합 같은 실제 사건을 확정하지 않습니다."
            );

            reasons.add(
                    "도화 신살 자체로 기존 관계 Rule의 SUPPORTIVE, PRESSURE, MIXED 상태를 변경하지 않습니다."
            );

            /*
             * =================================================
             * Match
             * =================================================
             *
             * AUXILIARY가 방향성을 결정하지 않으므로
             * 상태는 CONTEXTUAL 고정.
             */

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
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
     * 원국 관계 Anchor
     * =================================================
     *
     * 신살만으로 Match를 만들지 않기 위한
     * 비보조 구조 Evidence.
     *
     * PALACE:
     * 관계가 작동하는 위치 Context.
     *
     * NATAL_RELATION:
     * 원국의 실제 관계 구조.
     *
     * TRANSFORMATION:
     * 합화·기능변화가 존재하는 경우
     * 그 구조도 그대로 포함.
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
                        LifeDomainEvidenceSourceType.PALACE
                )
        );

        addNonAuxiliaryReferences(
                result,
                bundle.evidencesOf(
                        LifeDomainEvidenceSourceType.NATAL_RELATION
                )
        );

        addNonAuxiliaryReferences(
                result,
                bundle.evidencesOf(
                        LifeDomainEvidenceSourceType.TRANSFORMATION
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
     * 신살 matched layer에 실제 존재하는
     * 시간층만 연결한다.
     *
     * NATAL 신살에는 temporal Anchor를
     * 강제로 붙이지 않는다.
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
                    "RelationshipPeachBlossomAuxiliaryRule은 SHINSAL Evidence만으로 Match를 생성할 수 없습니다."
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
     * 도화를:
     *
     * 인기 확정
     * 연애 성공
     * 이성 등장
     *
     * 으로 번역하지 않는다.
     *
     * AI Grounding에서는 이후:
     *
     * - 사람과의 접점이 두드러지는지
     * - 타인의 관심을 받는 상황이 늘어나는지
     * - 관계에 대한 관심이 커지는지
     *
     * 같은 생활언어 후보로 사용할 수 있다.
     */

    private String summaryOf(
            ShinsalEvidence shinsalEvidence
    ) {

        if (
                shinsalEvidence.possibleOnly()
        ) {

            return "대인관계의 접점·주목·상호작용이 두드러질 가능성을 보조하는 근거가 있으나 확정 Context는 아닙니다.";
        }

        return "대인관계의 접점·주목·상호작용이 두드러지는 맥락을 보조하는 근거가 존재합니다.";
    }
}