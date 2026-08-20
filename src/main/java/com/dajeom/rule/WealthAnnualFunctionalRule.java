package com.dajeom.rule;

import com.dajeom.model.AnnualCrossFunctionalSynthesis;
import com.dajeom.model.AnnualFunctionCapacityContext;
import com.dajeom.model.AnnualFunctionalLifeDomainContext;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Annual Functional 기반
 * WEALTH 통합 Rule.
 *
 * =================================================
 *
 * 하나의 파생 Evidence:
 *
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * 안에서:
 *
 * 1.
 * Annual RESOURCE_REALITY Capacity
 *
 * 2.
 * 현재 16차 Timeline이 실제 선택한
 * Annual-Decade Functional Segment
 *
 * 3.
 * 현재 Segment의 Resolved RESOURCE_REALITY
 *
 * 4.
 * RESOURCE_REALITY가 참여하는
 * CrossFunctional Pattern
 *
 * 5.
 * 14-6 Context Qualification
 *
 * 을 한 번만 합성한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 같은 ANNUAL_FUNCTIONAL_CONTEXT를
 *
 * Capacity Match
 * +
 * Cross Match
 *
 * 두 개의 독립 Match로 나누지 않는다.
 *
 * 둘은 같은 Annual Functional 체인에서
 * 파생된 동일 Provenance이므로
 * 하나의 LifeDomainRuleMatch 안에서 읽는다.
 *
 * =================================================
 *
 * RESOURCE_REALITY:
 *
 * 재성 Family의 기능적 표현.
 *
 * 이 Rule에서는:
 *
 * - 현실적 자원
 * - 소유
 * - 교환
 * - 금전적 요구를 포함하는 현실 자원 처리
 *
 * 의 Function Context로 사용한다.
 *
 * =================================================
 *
 * 절대 하지 않는 변환:
 *
 * RESOURCE_REALITY active
 * -> 돈이 들어온다
 *
 * strong-side + 재성
 * -> 재물운 좋다
 *
 * 비겁쟁재
 * -> 돈을 잃는다
 *
 * 재다신약
 * -> 파산한다
 *
 * 재생관
 * -> 승진해서 돈 번다
 *
 * 재극인
 * -> 투자 실패
 *
 * 같은 실제 사건 추론.
 *
 * =================================================
 *
 * CrossFunctional에서
 * WEALTH Pressure 방향으로 사용할 수 있는 것은
 * 구조적으로 명시적인 두 Pattern으로 제한한다.
 *
 * SELF_ASSERTION_COMPETES_FOR_RESOURCE
 *
 * RESOURCE_OVERLOADS_CORE_SELF
 *
 * 단,
 *
 * 반드시 usableAsEffectiveAnnualPatternContact=true
 * 인 경우만 사용한다.
 *
 * REFERENCE Pattern은
 * Full Contact나 Exact Context가 있어도
 * 방향 근거로 승격하지 않는다.
 *
 * =================================================
 *
 * RESOURCE_GENERATES_RESPONSIBILITY
 * RESOURCE_CONTROLS_PROCESSING
 *
 * 도 RESOURCE_REALITY 관련 Pattern이지만
 * 그 존재만으로 재물의 SUPPORTIVE/PRESSURE를
 * 결정하지 않는다.
 *
 * 해당 작동은 Context로만 보존한다.
 *
 * =================================================
 *
 * SUPPORTIVE:
 *
 * 이 Rule에서는 현재 생성하지 않는다.
 *
 * 이유:
 *
 * strong-side 원국에 pressure-side 재성이
 * 들어왔다는 사실만으로
 * 재물 결과가 유리하다고 확정할 수 없기 때문이다.
 *
 * 이후 UsefulGod / Domain-level synthesis 등
 * 독립 기준과 함께 판단해야 한다.
 */
@Component
public class WealthAnnualFunctionalRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "WEALTH_ANNUAL_FUNCTIONAL";

    private static final NatalTenGodFunctionType
            WEALTH_FUNCTION =
            NatalTenGodFunctionType
                    .RESOURCE_REALITY;

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .ANNUAL_FUNCTIONAL_CONTEXT
            );

    /*
     * =================================================
     * Metadata
     * =================================================
     */

    @Override
    public LifeDomain domain() {

        return LifeDomain.WEALTH;
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

    /*
     * =================================================
     * Evaluate
     * =================================================
     */

    @Override
    public List<LifeDomainRuleMatch> evaluate(
            LifeDomainEvidenceBundle bundle
    ) {

        Objects.requireNonNull(
                bundle,
                "WealthAnnualFunctionalRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthAnnualFunctionalRule은 WEALTH Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                referenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .ANNUAL_FUNCTIONAL_CONTEXT
                );

        /*
         * Natal Bundle에는
         * Annual Functional Evidence가 없다.
         */

        if (
                referenceOptional.isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                reference =
                referenceOptional
                        .orElseThrow();

        AnnualFunctionalLifeDomainContext context =
                reference
                        .requireSource(
                                AnnualFunctionalLifeDomainContext.class
                        );

        if (
                !context
                        .annualFunctionalAvailable()
        ) {

            return List.of();
        }

        AnnualFunctionCapacityContext
                capacityContext =
                context
                        .requireAnnualFunctionContext(
                                WEALTH_FUNCTION
                        );

        validateIdentity(
                context,
                capacityContext
        );

        /*
         * =================================================
         * Annual 자체 활성
         * =================================================
         */

        boolean annualActive =
                capacityContext
                        .active();

        /*
         * =================================================
         * 현재 대운 Segment 결합 후 활성
         * =================================================
         *
         * 대운 Segment가 없으면 false.
         *
         * Annual 자체 active=false이어도
         * 대운×세운 Transformation 결과에 의해
         * 현재 Resolved Function이 active가 될 수 있으므로
         * annualActive만 보고 먼저 종료하면 안 된다.
         */

        boolean currentResolvedActive =
                context
                        .currentResolvedFunctionActive(
                                WEALTH_FUNCTION
                        );

        if (
                !annualActive
                        && !currentResolvedActive
        ) {

            return List.of();
        }

        /*
         * =================================================
         * RESOURCE_REALITY 관련 Cross Contact
         * =================================================
         */

        List<AnnualCrossFunctionalSynthesis>
                resourceContacts =
                resourceRelated(
                        context
                                .currentContactedSyntheses()
                );

        /*
         * 구조적으로 유효한 Full Contact만
         * 방향 판정 대상.
         */

        List<AnnualCrossFunctionalSynthesis>
                effectiveResourceContacts =
                resourceRelated(
                        context
                                .currentEffectiveFullContacts()
                );

        /*
         * REFERENCE는 별도로 보존.
         *
         * 절대 effective 목록에 합치지 않는다.
         */

        List<AnnualCrossFunctionalSynthesis>
                referenceResourceContacts =
                resourceRelated(
                        context
                                .currentReferenceContacts()
                );

        /*
         * =================================================
         * Status
         * =================================================
         */

        LifeDomainSignalStatus status =
                resolveStatus(
                        context,
                        capacityContext,
                        annualActive,
                        currentResolvedActive,
                        effectiveResourceContacts
                );

        List<String> reasons =
                buildReasons(
                        context,
                        capacityContext,
                        annualActive,
                        currentResolvedActive,
                        resourceContacts,
                        effectiveResourceContacts,
                        referenceResourceContacts,
                        status
                );

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.WEALTH,
                        RULE_CODE,
                        status,
                        summaryOf(
                                status
                        ),
                        List.of(
                                reference
                        ),
                        reasons
                )
        );
    }

    /*
     * =================================================
     * Status
     * =================================================
     */

    private LifeDomainSignalStatus resolveStatus(

            AnnualFunctionalLifeDomainContext context,

            AnnualFunctionCapacityContext capacityContext,

            boolean annualActive,

            boolean currentResolvedActive,

            List<AnnualCrossFunctionalSynthesis>
                    effectiveResourceContacts

    ) {

        /*
         * =================================================
         * 원국 불완전
         * =================================================
         */

        if (
                !context.completeChart()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        /*
         * =================================================
         * 현재 대운 Functional Segment가 있는 경우
         * =================================================
         */

        if (
                context.currentFunctionalSegmentAvailable()
        ) {

            /*
             * 16차 Segment가 특정 대운을 선택했지만
             * 최종 Resolved RESOURCE_REALITY가 inactive.
             *
             * Annual 자체 입력이 있었다는 사실은
             * Context로 보존하되
             * 방향을 확정하지 않는다.
             */

            if (
                    !currentResolvedActive
            ) {

                return LifeDomainSignalStatus
                        .CONTEXTUAL;
            }

            /*
             * date-only / 교운 등의 Segment timing uncertainty.
             *
             * 현재 Segment를 16차가 선택해서 전달하더라도
             * 시간 경계 불확실성 자체는 삭제하지 않는다.
             */

            if (
                    context
                            .currentFunctionalSegmentTimingUncertain()
            ) {

                return LifeDomainSignalStatus
                        .CONTEXTUAL;
            }

            boolean capacityPressure =
                    annualActive
                            && capacityContext
                            .weakSidePressureContact();

            boolean effectivePressurePattern =
                    effectiveResourceContacts
                            .stream()
                            .anyMatch(
                                    this::wealthPressurePattern
                            );

            boolean mixedCrossContext =
                    effectiveResourceContacts
                            .stream()
                            .anyMatch(
                                    this::mixedContext
                            );

            /*
             * =================================================
             * MIXED
             * =================================================
             *
             * 구조적으로 Pressure 방향이 존재하는데
             * 같은 Pattern Context 안에서
             * 지지/제한/충돌 상태가 혼재한 경우.
             *
             * Context 자체를 독립 Evidence로 세는 것이 아니라
             * 동일 Pattern의 qualification으로만 사용한다.
             */

            if (
                    (
                            capacityPressure
                                    || effectivePressurePattern
                    )
                            && (
                            mixedCrossContext
                                    || context
                                    .currentMixedContext()
                    )
            ) {

                return LifeDomainSignalStatus
                        .MIXED;
            }

            /*
             * =================================================
             * 명시적 Cross Pressure Pattern
             * =================================================
             *
             * REFERENCE는 effectiveResourceContacts에
             * 들어오지 않으므로 여기서 승격될 수 없다.
             */

            if (
                    effectivePressurePattern
            ) {

                /*
                 * QUALIFIED만 존재하는 경우
                 * Pattern 자체는 유효하더라도
                 * 현재 조건부 작동을 보존한다.
                 */

                boolean qualifiedOnly =
                        effectiveResourceContacts
                                .stream()
                                .filter(
                                        this::wealthPressurePattern
                                )
                                .anyMatch(
                                        synthesis ->
                                                synthesis
                                                        .qualifiedContextExists()

                                                        && !synthesis
                                                        .weakenedContextExists()

                                                        && !synthesis
                                                        .conflictedContextExists()
                                );

                if (
                        qualifiedOnly
                ) {

                    return LifeDomainSignalStatus
                            .CONTEXTUAL;
                }

                return LifeDomainSignalStatus
                        .PRESSURE;
            }

            /*
             * =================================================
             * Annual Capacity Pressure
             * =================================================
             *
             * weak-side
             * +
             * pressure-side RESOURCE_REALITY
             *
             * 단독 방향.
             *
             * Transformation / 원기능 감소 / 극단구조 등
             * 추가 qualification이 있으면 확정하지 않는다.
             */

            if (
                    capacityPressure
            ) {

                if (
                        capacityContext
                                .qualificationRequired()

                                || context
                                .currentSegmentQualificationRequired()
                ) {

                    return LifeDomainSignalStatus
                            .CONTEXTUAL;
                }

                return LifeDomainSignalStatus
                        .PRESSURE;
            }

            /*
             * strong-side pressure 접촉,
             * 재생관,
             * 재극인 등은
             * 재물 결과를 자동 결정하지 않는다.
             */

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        /*
         * =================================================
         * Annual-only
         * =================================================
         *
         * 대운 이전 / 대운 unavailable 등의 경우.
         *
         * Cross Segment를 억지로 선택하지 않고
         * Annual Capacity까지만 사용한다.
         */

        if (
                annualActive
                        && capacityContext
                        .weakSidePressureContact()

                        && !capacityContext
                        .qualificationRequired()
        ) {

            return LifeDomainSignalStatus
                    .PRESSURE;
        }

        return LifeDomainSignalStatus
                .CONTEXTUAL;
    }

    /*
     * =================================================
     * RESOURCE 관련 Pattern
     * =================================================
     *
     * enum의 typed participant를 사용한다.
     *
     * 문자열 코드 / 한국어 이름 파싱 금지.
     */

    private List<AnnualCrossFunctionalSynthesis>
    resourceRelated(

            List<AnnualCrossFunctionalSynthesis>
                    syntheses

    ) {

        if (
                syntheses == null
                        || syntheses.isEmpty()
        ) {

            return List.of();
        }

        return syntheses
                .stream()
                .filter(
                        synthesis ->
                                involvesResource(
                                        synthesis
                                                .patternType()
                                )
                )
                .toList();
    }

    private boolean involvesResource(
            NatalCrossFunctionalPatternType patternType
    ) {

        Objects.requireNonNull(
                patternType,
                "CrossFunctional Pattern Type이 필요합니다."
        );

        if (
                patternType
                        .primaryFunction()
                        == WEALTH_FUNCTION
        ) {

            return true;
        }

        return patternType
                .secondaryFunction()
                .map(
                        secondary ->
                                secondary
                                        == WEALTH_FUNCTION
                )
                .orElse(
                        false
                );
    }

    /*
     * =================================================
     * WEALTH Pressure Pattern
     * =================================================
     *
     * 명시적으로 재성 자원에
     * 경합 / 자기기반 과중이 걸리는 구조만 사용.
     *
     * 재생관 / 재극인을 자동 Pressure로 만들지 않는다.
     */

    private boolean wealthPressurePattern(
            AnnualCrossFunctionalSynthesis synthesis
    ) {

        Objects.requireNonNull(
                synthesis
        );

        if (
                !synthesis
                        .usableAsEffectiveAnnualPatternContact()
        ) {

            return false;
        }

        return synthesis
                .patternType()
                == NatalCrossFunctionalPatternType
                .SELF_ASSERTION_COMPETES_FOR_RESOURCE

                || synthesis
                .patternType()
                == NatalCrossFunctionalPatternType
                .RESOURCE_OVERLOADS_CORE_SELF;
    }

    /*
     * =================================================
     * Pattern 내부 Context 혼재
     * =================================================
     *
     * Exact Match 개수를 세지 않는다.
     *
     * 같은 Pattern 안에서
     * 서로 반대 방향의 Context Status가
     * 공존하는지만 본다.
     */

    private boolean mixedContext(
            AnnualCrossFunctionalSynthesis synthesis
    ) {

        Objects.requireNonNull(
                synthesis
        );

        if (
                synthesis
                        .mixedContextStatus()
        ) {

            return true;
        }

        boolean supportiveSide =
                synthesis
                        .supportedContextExists()

                        || synthesis
                        .qualifiedContextExists();

        boolean limitingSide =
                synthesis
                        .weakenedContextExists()

                        || synthesis
                        .conflictedContextExists();

        return supportiveSide
                && limitingSide;
    }

    /*
     * =================================================
     * Identity
     * =================================================
     */

    private void validateIdentity(

            AnnualFunctionalLifeDomainContext context,

            AnnualFunctionCapacityContext capacityContext

    ) {

        if (
                capacityContext
                        .functionType()
                        != WEALTH_FUNCTION
        ) {

            throw new IllegalArgumentException(
                    "WEALTH Annual Functional Rule에 RESOURCE_REALITY가 아닌 Function이 전달되었습니다: "
                            + capacityContext.functionType()
            );
        }

        if (
                capacityContext
                        .targetYear()
                        != context
                        .targetYear()
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Context와 RESOURCE_REALITY Capacity의 연도가 일치하지 않습니다. "
                            + "contextYear="
                            + context.targetYear()
                            + ", capacityYear="
                            + capacityContext.targetYear()
            );
        }

        if (
                capacityContext
                        .completeChart()
                        != context
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Context와 RESOURCE_REALITY Capacity의 completeChart가 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * Reasons
     * =================================================
     */

    private List<String> buildReasons(

            AnnualFunctionalLifeDomainContext context,

            AnnualFunctionCapacityContext capacityContext,

            boolean annualActive,

            boolean currentResolvedActive,

            List<AnnualCrossFunctionalSynthesis>
                    resourceContacts,

            List<AnnualCrossFunctionalSynthesis>
                    effectiveResourceContacts,

            List<AnnualCrossFunctionalSynthesis>
                    referenceResourceContacts,

            LifeDomainSignalStatus status

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "ANNUAL_FUNCTIONAL_CONTEXT 하나에서 Annual Capacity와 현재 대운 Segment의 CrossFunctional 결과를 통합하여 같은 파생 근거를 별도 독립 Evidence로 중복 계산하지 않습니다."
        );

        reasons.add(
                "WEALTH 영역의 Functional 기준은 RESOURCE_REALITY이며 현실적 자원·소유·교환 기능 Context로만 사용합니다."
        );

        reasons.add(
                "Annual RESOURCE_REALITY 활성 상태는 "
                        + annualActive
                        + "입니다. directInputActive="
                        + capacityContext.directInputActive()
                        + ", transformedTargetActive="
                        + capacityContext.transformedTargetActive()
                        + "입니다."
        );

        reasons.add(
                "일간 Strength는 세운에서 재계산하지 않고 원국 고정값을 사용합니다. natalStrengthLevel="
                        + capacityContext.natalStrengthLevel()
                        + "입니다."
        );

        /*
         * =================================================
         * Annual Capacity
         * =================================================
         */

        if (
                capacityContext
                        .weakSidePressureContact()
        ) {

            reasons.add(
                    "원국 weak-side에 pressure-side RESOURCE_REALITY가 실제 접촉하여 동일 압력 방향의 Capacity 조건이 존재합니다."
            );

            reasons.add(
                    "이 조건은 자원·소유·교환 요구를 감당하는 부담 방향이며 금전 손실 사건을 뜻하지 않습니다."
            );
        }

        if (
                capacityContext
                        .strongSidePressureContact()
        ) {

            reasons.add(
                    "원국 strong-side에 pressure-side RESOURCE_REALITY가 접촉하여 반대 방향 Capacity Contact가 존재합니다."
            );

            reasons.add(
                    "반대 방향 Contact 자체를 재물운 호조나 SUPPORTIVE로 자동 변환하지 않습니다."
            );
        }

        if (
                capacityContext
                        .annualOriginalFunctionReductionExists()
        ) {

            reasons.add(
                    "Annual RESOURCE_REALITY 원기능 감소가 존재하며 기능 삭제나 재물 손실로 해석하지 않습니다."
            );
        }

        if (
                capacityContext
                        .mixedAnnualOriginalFunctionCondition()
        ) {

            reasons.add(
                    "Annual RESOURCE_REALITY의 원기능 상태가 혼재되어 단일 방향으로 단순화하지 않습니다."
            );
        }

        if (
                capacityContext
                        .transformedTargetActive()
        ) {

            reasons.add(
                    "RESOURCE_REALITY에 Transformation Target 활성 경로가 존재하며 직접 입력과 별도 호재로 중복 계산하지 않습니다."
            );
        }

        if (
                capacityContext
                        .natalExtreme()
        ) {

            reasons.add(
                    "원국 Strength가 극단 영역이므로 일반 신강·신약 방향만으로 재물 기능 결과를 확정하지 않습니다."
            );
        }

        if (
                capacityContext
                        .followStructureReviewRequired()
        ) {

            reasons.add(
                    "원국에서 종격 가능성 추가 검토가 필요하여 일반 Capacity 방향을 단독 확정 근거로 사용하지 않습니다."
            );
        }

        if (
                capacityContext
                        .restrictiveNatalRelationContextExists()
        ) {

            reasons.add(
                    "RESOURCE_REALITY 입력에 제한적 원국 Relation Context가 존재합니다."
            );
        }

        /*
         * =================================================
         * Current Decade Segment
         * =================================================
         */

        if (
                context
                        .currentFunctionalSegmentAvailable()
        ) {

            int sequence =
                    context
                            .timelineDecadeSequenceNumber()
                            .orElseThrow();

            reasons.add(
                    "현재 16차 Timeline이 선택한 대운 Sequence "
                            + sequence
                            + "에 대응하는 Annual Functional Segment만 사용합니다."
            );

            reasons.add(
                    "현재 대운 결합 후 최종 RESOURCE_REALITY 활성 상태는 "
                            + currentResolvedActive
                            + "입니다."
            );

            reasons.add(
                    "RESOURCE_REALITY가 참여하는 현재 Cross Contact는 "
                            + resourceContacts.size()
                            + "개이고, 그중 structurally effective Full Contact는 "
                            + effectiveResourceContacts.size()
                            + "개입니다."
            );

            for (
                    AnnualCrossFunctionalSynthesis synthesis
                    : effectiveResourceContacts
            ) {

                reasons.add(
                        "유효 Cross Pattern: "
                                + synthesis
                                .patternType()
                                .koreanName()
                                + " [type="
                                + synthesis.patternType()
                                + ", newlyCompleted="
                                + synthesis.newlyCompleted()
                                + ", decadeContinuation="
                                + synthesis.decadeFullContinuation()
                                + ", transformationParticipant="
                                + synthesis.transformationTargetParticipantContact()
                                + ", originalFunctionReduction="
                                + synthesis.originalFunctionReductionExists()
                                + ", qualificationRequired="
                                + synthesis.qualificationRequired()
                                + "]"
                );

                if (
                        synthesis
                                .exactTenGodContextMatchExists()
                ) {

                    reasons.add(
                            synthesis.patternType().koreanName()
                                    + "에는 EXACT TenGod Context Match가 존재하지만 동일 Pattern의 Context 재확인일 뿐 독립 Evidence 또는 Pattern 승격 근거로 계산하지 않습니다."
                    );
                }
            }

        } else {

            reasons.add(
                    "현재 16차 Timeline에 사용할 수 있는 정확한 대운 Functional Segment가 없어 Annual 자체 RESOURCE_REALITY Capacity까지만 사용합니다."
            );
        }

        /*
         * =================================================
         * REFERENCE
         * =================================================
         */

        if (
                !referenceResourceContacts
                        .isEmpty()
        ) {

            reasons.add(
                    "RESOURCE_REALITY 관련 REFERENCE Cross Pattern Contact가 "
                            + referenceResourceContacts.size()
                            + "개 존재하지만 방향 판정 근거로 승격하지 않습니다."
            );
        }

        /*
         * =================================================
         * Pressure Pattern
         * =================================================
         */

        List<AnnualCrossFunctionalSynthesis>
                pressurePatterns =
                effectiveResourceContacts
                        .stream()
                        .filter(
                                this::wealthPressurePattern
                        )
                        .toList();

        for (
                AnnualCrossFunctionalSynthesis synthesis
                : pressurePatterns
        ) {

            if (
                    synthesis.patternType()
                            == NatalCrossFunctionalPatternType
                            .SELF_ASSERTION_COMPETES_FOR_RESOURCE
            ) {

                reasons.add(
                        "구조적으로 유효한 SELF_ASSERTION_COMPETES_FOR_RESOURCE가 현재 Segment에서 Full Contact하여 자원 기능에 경합 Pressure Context가 존재합니다."
                );

                reasons.add(
                        "경합 구조를 실제 지출·손실·경쟁 패배 사건으로 변환하지 않습니다."
                );

            } else if (
                    synthesis.patternType()
                            == NatalCrossFunctionalPatternType
                            .RESOURCE_OVERLOADS_CORE_SELF
            ) {

                reasons.add(
                        "구조적으로 유효한 RESOURCE_OVERLOADS_CORE_SELF가 현재 Segment에서 Full Contact하여 현실 자원 요구와 자기기반 사이의 과중 Pressure Context가 존재합니다."
                );

                reasons.add(
                        "과중 구조를 파산·재산 손실 같은 실제 사건으로 변환하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * Non-directional Resource Patterns
         * =================================================
         */

        boolean generationExists =
                effectiveResourceContacts
                        .stream()
                        .anyMatch(
                                synthesis ->
                                        synthesis.patternType()
                                                == NatalCrossFunctionalPatternType
                                                .RESOURCE_GENERATES_RESPONSIBILITY
                        );

        if (
                generationExists
        ) {

            reasons.add(
                    "RESOURCE_GENERATES_RESPONSIBILITY의 유효 접촉은 현실 자원 기능이 책임·역할 기능으로 이어지는 구조적 흐름으로 보존하며 재물 증가 또는 SUPPORTIVE로 자동 변환하지 않습니다."
            );
        }

        boolean controlsProcessingExists =
                effectiveResourceContacts
                        .stream()
                        .anyMatch(
                                synthesis ->
                                        synthesis.patternType()
                                                == NatalCrossFunctionalPatternType
                                                .RESOURCE_CONTROLS_PROCESSING
                        );

        if (
                controlsProcessingExists
        ) {

            reasons.add(
                    "RESOURCE_CONTROLS_PROCESSING의 유효 접촉은 자원 기능과 처리·수용 기능 사이의 제어 관계로 보존하며 재물의 단순 호악으로 변환하지 않습니다."
            );
        }

        /*
         * =================================================
         * Context Qualification
         * =================================================
         */

        if (
                context
                        .currentMixedContext()
        ) {

            reasons.add(
                    "현재 Annual-Decade Segment의 14-6 Context에는 서로 다른 상태가 함께 존재하여 혼재 조건을 보존합니다."
            );
        }

        if (
                context
                        .currentSegmentQualificationRequired()
        ) {

            reasons.add(
                    "현재 Segment의 하나 이상의 CrossFunctional Synthesis가 추가 Qualification을 요구합니다."
            );
        }

        if (
                context
                        .currentFunctionalSegmentTimingUncertain()
        ) {

            reasons.add(
                    "현재 Functional Segment에는 timing uncertainty가 존재하여 방향을 강하게 확정하지 않습니다."
            );
        }

        /*
         * =================================================
         * Final
         * =================================================
         */

        switch (
                status
        ) {

            case PRESSURE ->
                    reasons.add(
                            "Annual Capacity 또는 구조적으로 유효한 RESOURCE_REALITY Pressure Pattern에서 부담 방향이 확인되어 PRESSURE로 보존합니다. 이는 재물 손실 사건 판정이 아닙니다."
                    );

            case MIXED ->
                    reasons.add(
                            "RESOURCE_REALITY의 명시적 Pressure 방향과 같은 Functional Pattern의 지지·제한·충돌 Context가 혼재하여 MIXED로 보존합니다."
                    );

            case CONTEXTUAL ->
                    reasons.add(
                            "RESOURCE_REALITY의 활성 또는 구조적 접촉은 확인되지만 현재 공식 근거만으로 재물 결과의 유리·불리를 확정하지 않아 CONTEXTUAL로 유지합니다."
                    );

            case SUPPORTIVE ->
                    reasons.add(
                            "현재 Rule은 RESOURCE_REALITY의 단순 활성이나 반대방향 Capacity Contact만으로 SUPPORTIVE를 생성하지 않습니다."
                    );
        }

        return List.copyOf(
                reasons
        );
    }

    /*
     * =================================================
     * Summary
     * =================================================
     */

    private String summaryOf(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case PRESSURE ->
                    "현재 세운의 현실적 자원·소유·교환 기능에서 원국 Capacity 또는 유효 CrossFunctional 구조에 따른 부담 방향이 확인됩니다.";

            case MIXED ->
                    "현재 세운의 현실적 자원·소유·교환 기능에 부담 방향과 서로 다른 Qualification Context가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운의 현실적 자원·소유·교환 기능이 활성 또는 구조적으로 접촉하지만 재물 결과의 방향을 단독으로 확정하지 않습니다.";

            case SUPPORTIVE ->
                    "현재 세운의 현실적 자원·소유·교환 기능에 지지 방향이 확인됩니다.";
        };
    }

    /*
     * =================================================
     * Evidence
     * =================================================
     */

    private Optional<LifeDomainEvidenceReference<?>>
    optionalSingleEvidence(

            LifeDomainEvidenceBundle bundle,

            LifeDomainEvidenceSourceType sourceType

    ) {

        Objects.requireNonNull(
                bundle
        );

        Objects.requireNonNull(
                sourceType
        );

        List<LifeDomainEvidenceReference<?>>
                evidences =
                bundle
                        .evidencesOf(
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
                    "WealthAnnualFunctionalRule에는 최대 하나의 "
                            + sourceType
                            + " Evidence가 허용됩니다. actual="
                            + evidences.size()
            );
        }

        return Optional.of(
                evidences.get(0)
        );
    }
}