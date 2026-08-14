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
 * 직업·사회 영역의 역마 보조 Rule.
 *
 * =================================================
 *
 * 신살은 AUXILIARY Evidence다.
 *
 * 따라서:
 *
 * 역마 존재
 * -> 이직한다
 *
 * 역마 존재
 * -> 직장을 옮긴다
 *
 * 역마 운
 * -> 출장 간다
 *
 * 역마 운
 * -> 해외로 간다
 *
 * 역마 운
 * -> 이사한다
 *
 * 같은 실제 사건을 생성하지 않는다.
 *
 * =================================================
 *
 * 이 Rule에서 역마는:
 *
 * - 활동범위 변화
 * - 이동성이 필요한 환경
 * - 고정된 자리 밖의 활동
 * - 외부 접점 증가
 * - 업무 환경 변화
 * - 역할 수행 범위의 확장 또는 전환
 *
 * 같은 직업·사회적 맥락을
 * 보조하는 Evidence로만 사용한다.
 *
 * =================================================
 *
 * 핵심 직업 Evidence:
 *
 * TEN_GOD_COMPOSITE
 *
 * 기존 Career Rule이 이미:
 *
 * - 관성 구조
 * - 역할과 책임
 * - 압박 구조
 * - 식상 제어
 * - 인성 중재
 * - 재성 지원
 * - 교차검증 Pattern
 *
 * 을 판정한다.
 *
 * 역마는 그 판단을 대체하지 않는다.
 *
 * =================================================
 *
 * 운 역마의 경우 실제 matched layer에 따라:
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
 * 를 보조 Anchor로 연결한다.
 *
 * =================================================
 *
 * 역마 자체는:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 *
 * 상태를 결정하지 않는다.
 *
 * 이 Rule은 항상:
 *
 * CONTEXTUAL
 *
 * Match만 생성한다.
 *
 * =================================================
 *
 * DEFINITE / POSSIBLE은
 * 17차 ShinsalEvidence 값을 그대로 보존한다.
 */
@Component
public class CareerTravelHorseAuxiliaryRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "CAREER_TRAVEL_HORSE_AUXILIARY";

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
                "CareerTravelHorseAuxiliaryRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerTravelHorseAuxiliaryRule은 CAREER Bundle만 평가할 수 있습니다."
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
         * 직업 핵심 Anchor
         * =================================================
         *
         * 역마 단독으로 Career Match를 만들지 않는다.
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
         * 역마 Evidence 선택
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
                            != ShinsalType.TRAVEL_HORSE
            ) {

                continue;
            }

            /*
             * =================================================
             * 실제 운 Layer에 대응하는 Context만 연결
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
                    "역마 Evidence가 직업·사회 영역의 보조 Context로 연결되었습니다."
            );

            reasons.add(
                    "역마는 기존 직업 구조를 대체하지 않고 활동범위·이동성·환경 변화 맥락을 보조합니다."
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
                        "이 역마 Evidence의 확실성은 DEFINITE입니다."
                );
            }

            if (
                    shinsalEvidence.possibleOnly()
            ) {

                reasons.add(
                        "이 역마 Evidence의 확실성은 POSSIBLE이며 확정 Evidence로 승격하지 않습니다."
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
                        "이 역마 Evidence는 원국에서 검출되었습니다."
                );
            }

            if (
                    shinsalEvidence.luckEvidence()
            ) {

                reasons.add(
                        "이 역마 Evidence는 운 Timeline에 적용되는 보조 Evidence입니다."
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
                        "역마 matchedPoint에 DECADE Layer가 포함되어 있습니다."
                );
            }

            if (
                    shinsalEvidence.annualEvidence()
            ) {

                reasons.add(
                        "역마 matchedPoint에 ANNUAL Layer가 포함되어 있습니다."
                );
            }

            if (
                    shinsalEvidence.monthlyEvidence()
            ) {

                reasons.add(
                        "역마 matchedPoint에 MONTHLY Layer가 포함되어 있습니다."
                );
            }

            /*
             * =================================================
             * 17차 원본 deterministic 근거 보존
             * =================================================
             */

            for (
                    String reason
                    : shinsalEvidence.reasons()
            ) {

                reasons.add(
                        "17차 역마 근거: "
                                + reason
                );
            }

            /*
             * =================================================
             * 직업영역 의미
             * =================================================
             */

            reasons.add(
                    "역마는 직업영역에서 이동 그 자체보다 활동범위·업무환경·외부 접점이 변화하거나 넓어지는 맥락을 보조적으로 해석하는 데 사용합니다."
            );

            reasons.add(
                    "고정된 자리 안에서 역할을 수행하는지, 외부 활동과 변화가 많은 환경에서 역할을 수행하는지는 기존 Career Evidence와 함께 해석합니다."
            );

            reasons.add(
                    "역마 존재만으로 이직·퇴사·전직·출장·해외이동·이사 같은 실제 사건을 확정하지 않습니다."
            );

            reasons.add(
                    "역마 자체로 기존 Career Rule의 SUPPORTIVE, PRESSURE, MIXED 상태를 변경하지 않습니다."
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
     * 직업 핵심 Anchor
     * =================================================
     *
     * 10차 십성 복합 구조를
     * 직업·사회 영역의 핵심 비보조 Evidence로 사용한다.
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
     * 시간층 Anchor
     * =================================================
     *
     * ShinsalEvidence의 실제 matched layer에
     * 대응하는 시간층만 연결한다.
     *
     * 가능한 대운 중 하나를
     * 여기서 임의로 선택하지 않는다.
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
                    "CareerTravelHorseAuxiliaryRule은 SHINSAL Evidence만으로 Match를 생성할 수 없습니다."
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
     * 이후 AI Grounding에서는:
     *
     * - 한곳에 고정된 일보다 활동범위가 넓어지는지
     * - 외부 사람/환경과 접점이 많아지는지
     * - 기존 업무 방식에 변화가 필요한지
     *
     * 같은 일반 생활언어로 풀 수 있다.
     */

    private String summaryOf(
            ShinsalEvidence shinsalEvidence
    ) {

        if (
                shinsalEvidence.possibleOnly()
        ) {

            return "직업·사회 활동에서 이동성·환경 변화·외부 접점이 두드러질 가능성을 보조하는 근거가 있으나 확정 Context는 아닙니다.";
        }

        return "직업·사회 활동에서 이동성·환경 변화·외부 접점이 두드러지는 맥락을 보조하는 근거가 존재합니다.";
    }
}