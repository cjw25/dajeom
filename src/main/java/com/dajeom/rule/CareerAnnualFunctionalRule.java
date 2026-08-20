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
 * CAREER 통합 Rule.
 *
 * =================================================
 *
 * Source:
 *
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * =================================================
 *
 * Functional 중심:
 *
 * RESPONSIBILITY
 *
 * 관성 / 관살 Family를
 *
 * - 책임
 * - 역할
 * - 규칙
 * - 조직적 요구
 * - 외부 기준과 압력
 *
 * 의 Functional Context로 사용한다.
 *
 * =================================================
 *
 * 절대 하지 않는 변환:
 *
 * RESPONSIBILITY active
 * -> 승진
 *
 * 관성이 들어옴
 * -> 취업
 *
 * 관성이 강함
 * -> 직장운 좋음
 *
 * EXPRESSION_CONFLICTS_RESPONSIBILITY
 * -> 해고
 *
 * RESPONSIBILITY_INTERNAL_MIXTURE
 * -> 직장 분쟁
 *
 * PRESSURE_GENERATES_PROCESSING
 * -> 시험 합격
 *
 * 같은 실제 사건 추론.
 *
 * =================================================
 *
 * 하나의 ANNUAL_FUNCTIONAL_CONTEXT 안에서:
 *
 * 1.
 * Annual RESPONSIBILITY Capacity
 *
 * 2.
 * 현재 16차 Timeline이 선택한
 *    정확한 Annual-Decade Functional Segment
 *
 * 3.
 * 현재 Segment의 Resolved RESPONSIBILITY
 *
 * 4.
 * RESPONSIBILITY 관련 CrossFunctional Pattern
 *
 * 5.
 * 14-6 Context Qualification
 *
 * 을 하나의 Match로 합성한다.
 *
 * =================================================
 *
 * CAREER에서 Pattern을 다음처럼 구분한다.
 *
 * -------------------------------------------------
 * 명시적 충돌 Pressure
 * -------------------------------------------------
 *
 * EXPRESSION_CONFLICTS_RESPONSIBILITY
 *
 * 표현 / 행동 출력과
 * 책임 / 규칙 기능의 충돌.
 *
 * 구조적으로 유효한 Full Contact일 때만
 * CAREER Pressure 후보로 사용한다.
 *
 * -------------------------------------------------
 * 내부 혼재
 * -------------------------------------------------
 *
 * RESPONSIBILITY_INTERNAL_MIXTURE
 *
 * RESPONSIBILITY 내부에
 * 서로 다른 책임 모드가 혼재한 구조.
 *
 * 이를 단순 PRESSURE가 아니라
 * MIXED로 우선 보존한다.
 *
 * -------------------------------------------------
 * 압력 조절 Context
 * -------------------------------------------------
 *
 * EXPRESSION_CONTROLS_PRESSURE
 *
 * 표현 기능이 압력 기능을 제어하는 구조.
 *
 * 자동 SUPPORTIVE로 만들지 않는다.
 *
 * 직업적 대응 / 조절 가능성을 가진
 * 구조적 Context로만 보존한다.
 *
 * -------------------------------------------------
 * 처리 기능으로 이어지는 흐름
 * -------------------------------------------------
 *
 * RESPONSIBILITY_GENERATES_PROCESSING
 * PRESSURE_GENERATES_PROCESSING
 *
 * 책임 / 압력이 처리·수용·학습 기능으로
 * 이어지는 구조.
 *
 * 자동 직업 호재로 만들지 않는다.
 *
 * -------------------------------------------------
 * 자원 → 책임
 * -------------------------------------------------
 *
 * RESOURCE_GENERATES_RESPONSIBILITY
 *
 * 현실 자원 기능에서
 * 책임 기능으로 이어지는 구조.
 *
 * 자동 승진 / 직업 안정으로 만들지 않는다.
 *
 * =================================================
 *
 * REFERENCE Pattern:
 *
 * Annual Full Contact
 * +
 * Exact Context
 *
 * 가 있어도 승격하지 않는다.
 *
 * =================================================
 *
 * SUPPORTIVE:
 *
 * 이 Rule 단독으로는 생성하지 않는다.
 *
 * RESPONSIBILITY가 원국 Capacity의
 * 반대 방향으로 들어온다는 사실만으로
 * 직업 결과가 유리하다고 확정할 수 없기 때문이다.
 */
@Component
public class CareerAnnualFunctionalRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "CAREER_ANNUAL_FUNCTIONAL";

    private static final NatalTenGodFunctionType
            CAREER_FUNCTION =
            NatalTenGodFunctionType
                    .RESPONSIBILITY;

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
                "CareerAnnualFunctionalRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerAnnualFunctionalRule은 CAREER Bundle만 평가할 수 있습니다."
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
                responsibilityContext =
                context
                        .requireAnnualFunctionContext(
                                CAREER_FUNCTION
                        );

        validateIdentity(
                context,
                responsibilityContext
        );

        /*
         * =================================================
         * Annual 자체 Function 활성
         * =================================================
         */

        boolean annualActive =
                responsibilityContext
                        .active();

        /*
         * =================================================
         * 현재 대운 결합 후 Resolved Function
         * =================================================
         */

        boolean currentResolvedActive =
                context
                        .currentResolvedFunctionActive(
                                CAREER_FUNCTION
                        );

        /*
         * Annual 자체 입력이 없더라도
         * 대운×세운 Transformation을 거쳐
         * 최종 RESPONSIBILITY가 활성될 수 있다.
         */

        if (
                !annualActive
                        && !currentResolvedActive
        ) {

            return List.of();
        }

        /*
         * =================================================
         * RESPONSIBILITY 관련 Cross
         * =================================================
         */

        List<AnnualCrossFunctionalSynthesis>
                responsibilityContacts =
                responsibilityRelated(
                        context
                                .currentContactedSyntheses()
                );

        List<AnnualCrossFunctionalSynthesis>
                effectiveResponsibilityContacts =
                responsibilityRelated(
                        context
                                .currentEffectiveFullContacts()
                );

        List<AnnualCrossFunctionalSynthesis>
                referenceResponsibilityContacts =
                responsibilityRelated(
                        context
                                .currentReferenceContacts()
                );

        LifeDomainSignalStatus status =
                resolveStatus(
                        context,
                        responsibilityContext,
                        annualActive,
                        currentResolvedActive,
                        effectiveResponsibilityContacts
                );

        List<String> reasons =
                buildReasons(
                        context,
                        responsibilityContext,
                        annualActive,
                        currentResolvedActive,
                        responsibilityContacts,
                        effectiveResponsibilityContacts,
                        referenceResponsibilityContacts,
                        status
                );

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.CAREER,
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

            AnnualFunctionCapacityContext
                    responsibilityContext,

            boolean annualActive,

            boolean currentResolvedActive,

            List<AnnualCrossFunctionalSynthesis>
                    effectiveResponsibilityContacts

    ) {

        /*
         * =================================================
         * 불완전 원국
         * =================================================
         */

        if (
                !context
                        .completeChart()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        /*
         * =================================================
         * 현재 대운 Functional Segment 존재
         * =================================================
         */

        if (
                context
                        .currentFunctionalSegmentAvailable()
        ) {

            /*
             * Annual에서 책임 기능이 들어왔더라도
             * 대운 결합 후 최종 Function이 비활성이면
             * Career 방향을 확정하지 않는다.
             */

            if (
                    !currentResolvedActive
            ) {

                return LifeDomainSignalStatus
                        .CONTEXTUAL;
            }

            /*
             * 교운 경계 등의 시간 불확실성.
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
                            && responsibilityContext
                            .weakSidePressureContact();

            List<AnnualCrossFunctionalSynthesis>
                    conflictPatterns =
                    effectiveResponsibilityContacts
                            .stream()
                            .filter(
                                    this::careerConflictPattern
                            )
                            .toList();

            List<AnnualCrossFunctionalSynthesis>
                    internalMixtures =
                    effectiveResponsibilityContacts
                            .stream()
                            .filter(
                                    this::responsibilityInternalMixture
                            )
                            .toList();

            List<AnnualCrossFunctionalSynthesis>
                    regulatingPatterns =
                    effectiveResponsibilityContacts
                            .stream()
                            .filter(
                                    this::responsibilityRegulationPattern
                            )
                            .toList();

            boolean conflictExists =
                    !conflictPatterns
                            .isEmpty();

            boolean internalMixtureExists =
                    !internalMixtures
                            .isEmpty();

            boolean regulationExists =
                    !regulatingPatterns
                            .isEmpty();

            boolean mixedContext =
                    context
                            .currentMixedContext()

                            || effectiveResponsibilityContacts
                            .stream()
                            .anyMatch(
                                    this::mixedContext
                            );

            /*
             * =================================================
             * RESPONSIBILITY_INTERNAL_MIXTURE
             * =================================================
             *
             * Pattern 이름 그대로
             * 책임 기능 내부 혼재를 나타내므로
             * 단순 PRESSURE로 평탄화하지 않는다.
             */

            if (
                    internalMixtureExists
            ) {

                return LifeDomainSignalStatus
                        .MIXED;
            }

            /*
             * =================================================
             * Conflict + Regulation
             * =================================================
             *
             * 책임 기능과 충돌하는 구조와
             * 압력을 조절하는 구조가 동시에
             * 유효하게 접촉하면 한 방향으로
             * 평탄화하지 않는다.
             */

            if (
                    conflictExists
                            && regulationExists
            ) {

                return LifeDomainSignalStatus
                        .MIXED;
            }

            /*
             * =================================================
             * Pressure 방향 + Context 혼재
             * =================================================
             */

            if (
                    (
                            conflictExists
                                    || capacityPressure
                    )
                            && mixedContext
            ) {

                return LifeDomainSignalStatus
                        .MIXED;
            }

            /*
             * =================================================
             * EXPRESSION_CONFLICTS_RESPONSIBILITY
             * =================================================
             */

            if (
                    conflictExists
            ) {

                boolean additionalQualification =
                        conflictPatterns
                                .stream()
                                .anyMatch(
                                        this::directionQualificationExists
                                );

                if (
                        additionalQualification
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
             * pressure-side RESPONSIBILITY.
             *
             * 이것도 책임·규칙·역할 요구를
             * 받아들이는 Capacity 부담이지
             * 해고나 실패 사건이 아니다.
             */

            if (
                    capacityPressure
            ) {

                if (
                        responsibilityContext
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
             * =================================================
             * Regulation / Generation
             * =================================================
             *
             * 식신제살 계열이나
             * 관인상생 / 살인상생 계열이
             * 존재해도 CAREER SUPPORTIVE를
             * 자동 생성하지 않는다.
             */

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        /*
         * =================================================
         * Annual-only
         * =================================================
         *
         * 대운 시작 전 / 대운 결합 분석 unavailable.
         */

        if (
                annualActive

                        && responsibilityContext
                        .weakSidePressureContact()

                        && !responsibilityContext
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
     * RESPONSIBILITY 관련 Pattern
     * =================================================
     */

    private List<AnnualCrossFunctionalSynthesis>
    responsibilityRelated(

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
                                involvesResponsibility(
                                        synthesis
                                                .patternType()
                                )
                )
                .toList();
    }

    private boolean involvesResponsibility(
            NatalCrossFunctionalPatternType patternType
    ) {

        Objects.requireNonNull(
                patternType,
                "CrossFunctional Pattern Type이 필요합니다."
        );

        if (
                patternType
                        .primaryFunction()
                        == CAREER_FUNCTION
        ) {

            return true;
        }

        return patternType
                .secondaryFunction()
                .map(
                        secondary ->
                                secondary
                                        == CAREER_FUNCTION
                )
                .orElse(
                        false
                );
    }

    /*
     * =================================================
     * 명시적 Career Conflict
     * =================================================
     */

    private boolean careerConflictPattern(
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
                .EXPRESSION_CONFLICTS_RESPONSIBILITY;
    }

    /*
     * =================================================
     * RESPONSIBILITY 내부 혼재
     * =================================================
     */

    private boolean responsibilityInternalMixture(
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
                .RESPONSIBILITY_INTERNAL_MIXTURE;
    }

    /*
     * =================================================
     * Responsibility Regulation
     * =================================================
     *
     * EXPRESSION_CONTROLS_PRESSURE.
     *
     * 압력 제어 구조로 보존하지만
     * Career SUPPORTIVE로 승격하지 않는다.
     */

    private boolean responsibilityRegulationPattern(
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
                .EXPRESSION_CONTROLS_PRESSURE;
    }

    /*
     * =================================================
     * Direction Qualification
     * =================================================
     *
     * Pattern 자체의 유효성을 지우지 않는다.
     *
     * 단 현재 Context가:
     *
     * QUALIFIED
     * WEAKENED
     * CONFLICTED
     * MIXED
     *
     * 라면 LifeDomain의 강한 방향 판정을
     * CONTEXTUAL로 제한한다.
     */

    private boolean directionQualificationExists(
            AnnualCrossFunctionalSynthesis synthesis
    ) {

        Objects.requireNonNull(
                synthesis
        );

        return synthesis
                .qualifiedContextExists()

                || synthesis
                .weakenedContextExists()

                || synthesis
                .conflictedContextExists()

                || synthesis
                .mixedContextStatus();
    }

    /*
     * =================================================
     * Context 혼재
     * =================================================
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

            AnnualFunctionCapacityContext
                    responsibilityContext

    ) {

        if (
                responsibilityContext
                        .functionType()
                        != CAREER_FUNCTION
        ) {

            throw new IllegalArgumentException(
                    "CAREER Annual Functional Rule에 RESPONSIBILITY가 아닌 Function이 전달되었습니다: "
                            + responsibilityContext.functionType()
            );
        }

        if (
                responsibilityContext
                        .targetYear()
                        != context
                        .targetYear()
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Context와 RESPONSIBILITY Capacity의 연도가 일치하지 않습니다. "
                            + "contextYear="
                            + context.targetYear()
                            + ", capacityYear="
                            + responsibilityContext.targetYear()
            );
        }

        if (
                responsibilityContext
                        .completeChart()
                        != context
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Context와 RESPONSIBILITY Capacity의 completeChart가 일치하지 않습니다."
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

            AnnualFunctionCapacityContext
                    responsibilityContext,

            boolean annualActive,

            boolean currentResolvedActive,

            List<AnnualCrossFunctionalSynthesis>
                    responsibilityContacts,

            List<AnnualCrossFunctionalSynthesis>
                    effectiveResponsibilityContacts,

            List<AnnualCrossFunctionalSynthesis>
                    referenceResponsibilityContacts,

            LifeDomainSignalStatus status

    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "ANNUAL_FUNCTIONAL_CONTEXT 하나에서 RESPONSIBILITY Capacity와 현재 대운 Segment의 CrossFunctional 결과를 통합하여 같은 파생 근거를 독립 Evidence처럼 중복 계산하지 않습니다."
        );

        reasons.add(
                "CAREER 영역의 Functional 중심은 RESPONSIBILITY이며 책임·역할·규칙·조직적 요구 기능 Context로 사용합니다."
        );

        reasons.add(
                "Annual RESPONSIBILITY 활성 상태는 "
                        + annualActive
                        + "입니다. directInputActive="
                        + responsibilityContext.directInputActive()
                        + ", transformedTargetActive="
                        + responsibilityContext.transformedTargetActive()
                        + "입니다."
        );

        reasons.add(
                "일간 Strength는 세운에서 재계산하지 않고 원국 고정값을 사용합니다. natalStrengthLevel="
                        + responsibilityContext.natalStrengthLevel()
                        + "입니다."
        );

        /*
         * =================================================
         * Capacity
         * =================================================
         */

        if (
                responsibilityContext
                        .weakSidePressureContact()
        ) {

            reasons.add(
                    "원국 weak-side에 pressure-side RESPONSIBILITY가 실제 활성되어 책임·규칙·역할 요구가 동일 압력 방향으로 접촉합니다."
            );

            reasons.add(
                    "이 Capacity 부담은 직업 실패·해고·불합격 같은 실제 사건을 의미하지 않습니다."
            );
        }

        if (
                responsibilityContext
                        .strongSidePressureContact()
        ) {

            reasons.add(
                    "원국 strong-side에 pressure-side RESPONSIBILITY가 접촉하여 반대 방향 Capacity Contact가 존재합니다."
            );

            reasons.add(
                    "반대 방향 접촉만으로 취업·승진·직업 안정 또는 SUPPORTIVE를 확정하지 않습니다."
            );
        }

        if (
                responsibilityContext
                        .annualOriginalFunctionReductionExists()
        ) {

            reasons.add(
                    "Annual RESPONSIBILITY 원기능 감소가 존재하며 책임 기능의 삭제나 직업 사건으로 변환하지 않습니다."
            );
        }

        if (
                responsibilityContext
                        .mixedAnnualOriginalFunctionCondition()
        ) {

            reasons.add(
                    "Annual RESPONSIBILITY 원기능 상태가 혼재되어 단일 방향으로 평탄화하지 않습니다."
            );
        }

        if (
                responsibilityContext
                        .transformedTargetActive()
        ) {

            reasons.add(
                    "RESPONSIBILITY에 Transformation Target 활성 경로가 존재하며 직접 입력과 별도 독립 호재로 중복 계산하지 않습니다."
            );
        }

        if (
                responsibilityContext
                        .natalBalancedZone()
        ) {

            reasons.add(
                    "원국 Strength가 balanced zone이므로 support/pressure 방향만으로 CAREER 결과를 확정하지 않습니다."
            );
        }

        if (
                responsibilityContext
                        .natalExtreme()
        ) {

            reasons.add(
                    "원국 Strength가 극단 영역이므로 일반 신강·신약 방향만으로 책임 기능 결과를 확정하지 않습니다."
            );
        }

        if (
                responsibilityContext
                        .followStructureReviewRequired()
        ) {

            reasons.add(
                    "원국에서 종격 가능성 추가 검토가 필요하여 일반 Capacity 방향을 단독 직업 판정 근거로 사용하지 않습니다."
            );
        }

        if (
                responsibilityContext
                        .natalStructuralCenter()
        ) {

            reasons.add(
                    "RESPONSIBILITY가 원국 Functional 구조의 중심축에 해당하지만 구조 중심이라는 사실만으로 직업 결과를 SUPPORTIVE로 승격하지 않습니다."
            );
        }

        if (
                responsibilityContext
                        .restrictiveNatalRelationContextExists()
        ) {

            reasons.add(
                    "RESPONSIBILITY 입력에 제한적 원국 Relation Context가 존재합니다."
            );
        }

        /*
         * =================================================
         * Current Segment
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
                    "현재 대운 결합 후 최종 RESPONSIBILITY 활성 상태는 "
                            + currentResolvedActive
                            + "입니다."
            );

            reasons.add(
                    "RESPONSIBILITY가 참여하는 현재 Cross Contact는 "
                            + responsibilityContacts.size()
                            + "개이고, 그중 structurally effective Full Contact는 "
                            + effectiveResponsibilityContacts.size()
                            + "개입니다."
            );

            for (
                    AnnualCrossFunctionalSynthesis synthesis
                    : effectiveResponsibilityContacts
            ) {

                reasons.add(
                        "유효 CAREER 관련 Cross Pattern: "
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
                                    + "의 EXACT TenGod Context Match는 동일 구조의 Context 재확인일 뿐 별도 독립 Evidence 또는 Pattern 승격 근거로 계산하지 않습니다."
                    );
                }

                appendPatternReason(
                        reasons,
                        synthesis
                );
            }

        } else {

            reasons.add(
                    "현재 16차 Timeline에서 사용할 수 있는 정확한 대운 Functional Segment가 없어 Annual 자체 RESPONSIBILITY Capacity까지만 사용합니다."
            );
        }

        /*
         * =================================================
         * REFERENCE
         * =================================================
         */

        if (
                !referenceResponsibilityContacts
                        .isEmpty()
        ) {

            reasons.add(
                    "RESPONSIBILITY 관련 REFERENCE Cross Pattern Contact가 "
                            + referenceResponsibilityContacts.size()
                            + "개 존재하지만 CAREER 방향 판정 근거로 승격하지 않습니다."
            );
        }

        /*
         * =================================================
         * Qualification
         * =================================================
         */

        if (
                context
                        .currentMixedContext()
        ) {

            reasons.add(
                    "현재 Annual-Decade Segment의 14-6 Context에 서로 다른 상태가 함께 존재하여 단일 방향으로 평탄화하지 않습니다."
            );
        }

        if (
                context
                        .currentSegmentQualificationRequired()
        ) {

            reasons.add(
                    "현재 RESPONSIBILITY 관련 여부와 별개로 Segment의 하나 이상의 CrossFunctional Synthesis가 추가 Qualification을 요구합니다."
            );
        }

        if (
                context
                        .currentFunctionalSegmentTimingUncertain()
        ) {

            reasons.add(
                    "현재 Functional Segment에 timing uncertainty가 존재하여 직업 방향을 강하게 확정하지 않습니다."
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
                            "Annual Capacity 또는 구조적으로 유효한 RESPONSIBILITY 충돌 구조에서 책임·역할·규칙 요구의 부담 방향이 확인되어 PRESSURE로 보존합니다. 실제 직업 사건 판정은 아닙니다."
                    );

            case MIXED ->
                    reasons.add(
                            "RESPONSIBILITY 내부 혼재 또는 충돌·조절 구조가 함께 존재하여 한 방향으로 평탄화하지 않고 MIXED로 보존합니다."
                    );

            case CONTEXTUAL ->
                    reasons.add(
                            "RESPONSIBILITY 활성 또는 구조적 접촉은 확인되지만 현재 공식 근거만으로 직업 결과의 유리·불리를 확정하지 않아 CONTEXTUAL로 유지합니다."
                    );

            case SUPPORTIVE ->
                    reasons.add(
                            "현재 Rule은 RESPONSIBILITY의 단순 활성이나 반대 방향 Capacity Contact만으로 SUPPORTIVE를 생성하지 않습니다."
                    );
        }

        return List.copyOf(
                reasons
        );
    }

    /*
     * =================================================
     * Pattern Reason
     * =================================================
     */

    private void appendPatternReason(

            List<String> reasons,

            AnnualCrossFunctionalSynthesis synthesis

    ) {

        NatalCrossFunctionalPatternType patternType =
                synthesis
                        .patternType();

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .EXPRESSION_CONFLICTS_RESPONSIBILITY
        ) {

            reasons.add(
                    "EXPRESSION_CONFLICTS_RESPONSIBILITY는 표현·실행 기능과 책임·규칙 기능의 구조적 충돌로 보존하며 해고·직장분쟁 같은 사건으로 변환하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .RESPONSIBILITY_INTERNAL_MIXTURE
        ) {

            reasons.add(
                    "RESPONSIBILITY_INTERNAL_MIXTURE는 책임 기능 내부의 서로 다른 작동 모드가 함께 존재하는 구조이므로 단순 PRESSURE 하나로 평탄화하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .EXPRESSION_CONTROLS_PRESSURE
        ) {

            reasons.add(
                    "EXPRESSION_CONTROLS_PRESSURE는 표현 기능이 압력 기능을 조절하는 구조로 보존하지만 자동 직업 호재나 SUPPORTIVE로 승격하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .RESPONSIBILITY_GENERATES_PROCESSING
        ) {

            reasons.add(
                    "RESPONSIBILITY_GENERATES_PROCESSING은 책임 기능이 처리·수용 기능으로 이어지는 구조적 흐름으로 보존하며 직업 안정이나 승진으로 자동 변환하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .PRESSURE_GENERATES_PROCESSING
        ) {

            reasons.add(
                    "PRESSURE_GENERATES_PROCESSING은 압력 기능이 처리·수용 기능으로 이어지는 구조적 흐름으로 보존하며 시험·직업 성과를 자동 확정하지 않습니다."
            );

            return;
        }

        if (
                patternType
                        == NatalCrossFunctionalPatternType
                        .RESOURCE_GENERATES_RESPONSIBILITY
        ) {

            reasons.add(
                    "RESOURCE_GENERATES_RESPONSIBILITY는 현실 자원 기능이 책임·역할 기능으로 이어지는 흐름으로 보존하며 취업·승진·소득 증가로 자동 변환하지 않습니다."
            );
        }
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
                    "현재 세운의 책임·역할·규칙 기능에서 원국 Capacity 또는 유효 CrossFunctional 충돌 구조에 따른 부담 방향이 확인됩니다.";

            case MIXED ->
                    "현재 세운의 책임·역할·규칙 기능에 내부 혼재 또는 서로 다른 구조적 작동이 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운의 책임·역할·규칙 기능이 활성 또는 구조적으로 접촉하지만 직업 결과의 방향을 단독으로 확정하지 않습니다.";

            case SUPPORTIVE ->
                    "현재 세운의 책임·역할·규칙 기능에 지지 방향이 확인됩니다.";
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
                    "CareerAnnualFunctionalRule에는 최대 하나의 "
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