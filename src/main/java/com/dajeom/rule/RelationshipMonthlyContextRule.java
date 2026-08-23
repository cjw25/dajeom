package com.dajeom.rule;

import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.MonthlyLuckContextAnalysis;
import com.dajeom.model.MonthlyLuckContextSignal;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.PalacePosition;
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
 * 관계 영역의 월운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 15-7 MonthlyLuckContextAnalysis
 *
 * =================================================
 *
 * 관계 영역에서 참조하는 월운 Signal:
 *
 * - DAY 궁위가 실제 태깅된 Signal
 * - DIRECT_WEALTH가 실제 태깅된 Signal
 * - INDIRECT_WEALTH가 실제 태깅된 Signal
 * - DIRECT_OFFICER가 실제 태깅된 Signal
 * - SEVEN_KILLINGS가 실제 태깅된 Signal
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * MonthlyLuckContextSignal은
 * 15-7에서 이미 계산된
 * 최소 deterministic Evidence 단위다.
 *
 * 따라서 이 Rule에서는:
 *
 * MonthlyLuckContextSignal 1개
 * ->
 * LifeDomainRuleMatch 1개
 *
 * 로 보존한다.
 *
 * =================================================
 *
 * 서로 다른 Monthly Signal을:
 *
 * - 합치지 않는다.
 * - 개수로 점수화하지 않는다.
 * - 하나의 대표 상태로 다시 집계하지 않는다.
 * - DAY + 재성/관살을 결합하지 않는다.
 * - 재성 + 관살을 결합하지 않는다.
 * - 서로 다른 Axis를 결합하지 않는다.
 *
 * 단순 공존만으로:
 *
 * - 새로운 관계
 * - 새로운 인과
 * - 새로운 순서
 * - 강화
 * - 약화
 * - 상쇄
 * - 복합 상태
 * - 추가 종합 의미
 *
 * 를 생성하지 않는다.
 *
 * =================================================
 *
 * MonthlyLuckContextAxis는
 * 15-7의 독립 분석축이다.
 *
 * USEFUL_GOD
 * STRUCTURE
 * TEN_GOD
 * PALACE
 * NATAL_RELATION
 * ANNUAL_INTERACTION
 * DECADE_INTERACTION
 * TRANSFORMATION
 * THREE_LAYER_RELATION
 * FOUR_LAYER_RELATION
 *
 * 등의 서로 다른 Axis를
 * 관계영역이라는 이유로 다시 하나의 상태로
 * 강제 축약하지 않는다.
 *
 * =================================================
 *
 * 관계 계산 자체는 유지한다.
 *
 * 예:
 *
 * NATAL_RELATION
 * ANNUAL_INTERACTION
 * DECADE_INTERACTION
 * THREE_LAYER_RELATION
 * FOUR_LAYER_RELATION
 *
 * 같은 Axis가 존재한다면
 * 그것은 15-7 이전 Java 계산층에서
 * 실제 계산된 관계 결과다.
 *
 * 이 Rule은 그 계산 결과를 다시 관계형으로
 * 확장하지 않고 해당 Signal 그대로 보존한다.
 *
 * =================================================
 *
 * 시간층 경계:
 *
 * 반드시 MonthlyLuckContextAnalysis의
 * monthlySignals만 사용한다.
 *
 * decadeContexts.segmentSignals를 포함하는
 * allSignals()는 사용하지 않는다.
 *
 * 대운 Context는 별도의
 * DECADE_CONTEXT Rule이 담당한다.
 *
 * 세운 Context 역시 별도의
 * ANNUAL_CONTEXT Rule이 담당한다.
 *
 * =================================================
 *
 * 상태 변환:
 *
 * 하나의 MonthlyLuckContextSignal에 대해서만:
 *
 * SUPPORTED
 * -> SUPPORTIVE
 *
 * QUALIFIED
 * -> CONTEXTUAL
 *
 * WEAKENED
 * -> PRESSURE
 *
 * CONFLICTED
 * -> MIXED
 *
 * 여러 Signal의 상태를 합쳐
 * 새로운 MIXED 등을 만들지 않는다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * DAY 궁위 활성
 * -> 연애 시작
 *
 * 재성 월운
 * -> 배우자 등장
 *
 * 관살 월운
 * -> 결혼
 *
 * CONFLICTED
 * -> 이별
 *
 * 합
 * -> 관계 성립
 *
 * 충
 * -> 관계 단절
 *
 * 같은 실제 사건 확정.
 */
@Component
public class RelationshipMonthlyContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "RELATIONSHIP_MONTHLY_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .MONTHLY_CONTEXT
            );

    private static final Set<StructureTenGodType>
            RELATIONSHIP_TEN_GOD_TYPES =
            Set.of(
                    StructureTenGodType.DIRECT_WEALTH,
                    StructureTenGodType.INDIRECT_WEALTH,
                    StructureTenGodType.DIRECT_OFFICER,
                    StructureTenGodType.SEVEN_KILLINGS
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
                "RelationshipMonthlyContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "RelationshipMonthlyContextRule은 RELATIONSHIP Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                monthlyReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .MONTHLY_CONTEXT
                );

        /*
         * =================================================
         * Natal Bundle
         * =================================================
         */

        if (
                monthlyReferenceOptional.isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                monthlyReference =
                monthlyReferenceOptional
                        .orElseThrow();

        MonthlyLuckContextAnalysis monthlyContext =
                monthlyReference
                        .requireSource(
                                MonthlyLuckContextAnalysis.class
                        );

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * Monthly Signal 단위 독립 Match
         * =================================================
         *
         * monthlySignals는 15-7의
         * 최소 deterministic Evidence 단위다.
         *
         * 관계영역 관련 태그가 존재하는 Signal만
         * 하나씩 독립적으로 보존한다.
         */

        for (
                MonthlyLuckContextSignal signal
                : monthlyContext.monthlySignals()
        ) {

            Objects.requireNonNull(
                    signal,
                    "MonthlyLuckContextAnalysis의 monthlySignal은 null일 수 없습니다."
            );

            if (
                    !relationshipRelevant(
                            signal
                    )
            ) {

                continue;
            }

            LifeDomainSignalStatus status =
                    resolveStatus(
                            signal
                    );

            List<String> reasons =
                    baseReasons(
                            monthlyContext
                    );

            reasons.add(
                    "15-7 Monthly Signal code는 "
                            + signal.code()
                            + "입니다."
            );

            reasons.add(
                    "Monthly Signal axis는 "
                            + signal.axis().name()
                            + "입니다."
            );

            reasons.add(
                    "Monthly Signal 원래 status는 "
                            + signal.status().name()
                            + "입니다."
            );

            reasons.add(
                    "Monthly Signal summary: "
                            + signal.summary()
            );

            if (
                    signal.involves(
                            PalacePosition.DAY
                    )
            ) {

                reasons.add(
                        "이 Monthly Signal에는 DAY 궁위가 실제 태깅되어 있습니다."
                );
            }

            List<StructureTenGodType>
                    relevantTenGodTypes =
                    signal
                            .tenGodTypes()
                            .stream()
                            .filter(
                                    RELATIONSHIP_TEN_GOD_TYPES
                                            ::contains
                            )
                            .toList();

            if (
                    !relevantTenGodTypes.isEmpty()
            ) {

                reasons.add(
                        "이 Monthly Signal에 실제 태깅된 관계영역 참조 십성 유형은 "
                                + relevantTenGodTypes
                                + "입니다."
                );
            }

            for (
                    String reason
                    : signal.reasons()
            ) {

                reasons.add(
                        "Monthly Signal ["
                                + signal.code()
                                + "] 근거: "
                                + reason
                );
            }

            reasons.add(
                    "이 Match는 하나의 MonthlyLuckContextSignal만 보존하며 다른 Monthly Signal의 상태와 합산하지 않습니다."
            );

            reasons.add(
                    "DAY 궁위·재성·관살 태그가 같은 Signal 안에 함께 존재하더라도 그것은 해당 Signal의 upstream 계산 결과이며, 이 Rule에서 별도의 Combined 상태를 추가 생성하지 않습니다."
            );

            reasons.add(
                    "현재 Evidence에는 성별 정보가 없으므로 재성 또는 관살을 배우자성으로 임의 확정하지 않습니다."
            );

            reasons.add(
                    "이 Monthly Signal을 연애 시작·배우자 등장·결혼·이별·재결합 같은 실제 관계 사건으로 직접 환산하지 않습니다."
            );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            status,
                            summaryOf(
                                    signal
                            ),
                            List.of(
                                    monthlyReference
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
     * 관계영역 관련 Signal
     * =================================================
     */

    private boolean relationshipRelevant(
            MonthlyLuckContextSignal signal
    ) {

        if (
                signal.involves(
                        PalacePosition.DAY
                )
        ) {

            return true;
        }

        for (
                StructureTenGodType tenGodType
                : RELATIONSHIP_TEN_GOD_TYPES
        ) {

            if (
                    signal.involves(
                            tenGodType
                    )
            ) {

                return true;
            }
        }

        return false;
    }

    /*
     * =================================================
     * 월운 기본 근거
     * =================================================
     */

    private List<String> baseReasons(
            MonthlyLuckContextAnalysis monthlyContext
    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "15-7 월운 Context의 사주 연도는 "
                        + monthlyContext.sajuYear()
                        + "년입니다."
        );

        reasons.add(
                "현재 월운 monthOrder는 "
                        + monthlyContext.monthOrder()
                        + ", monthIndex는 "
                        + monthlyContext.monthIndex()
                        + "입니다."
        );

        reasons.add(
                "현재 월운 간지는 "
                        + monthlyContext.monthlyPillar()
                        + "입니다."
        );

        reasons.add(
                "상위 세운 간지는 "
                        + monthlyContext.annualPillar()
                        + "입니다."
        );

        if (
                !monthlyContext
                        .decadeContextAvailable()
        ) {

            reasons.add(
                    "현재 월운에서는 대운 Segment Context를 사용할 수 없지만 대운 Sequence와 무관하게 확정 가능한 monthlySignals는 그대로 보존합니다."
            );

            if (
                    monthlyContext
                            .decadeContextUnavailableReason()
                            != null

                            && !monthlyContext
                            .decadeContextUnavailableReason()
                            .isBlank()
            ) {

                reasons.add(
                        "월운의 대운 Context 사용 불가 사유: "
                                + monthlyContext
                                .decadeContextUnavailableReason()
                );
            }
        }

        if (
                monthlyContext
                        .decadeTimingUncertain()
        ) {

            reasons.add(
                    "현재 월운 안에 대운 교운시점 불확실성이 존재하지만 가능한 Monthly-Decade Context 중 하나를 이 Rule에서 임의 선택하지 않습니다."
            );
        }

        reasons.add(
                "이 Rule은 MonthlyLuckContextAnalysis.monthlySignals만 사용하고 decadeContexts.segmentSignals를 포함하는 allSignals()는 사용하지 않습니다."
        );

        reasons.add(
                "대운과 세운의 관계영역 Context는 각각 DECADE_CONTEXT와 ANNUAL_CONTEXT Rule에서 별도로 보존합니다."
        );

        return reasons;
    }

    /*
     * =================================================
     * 단일 Signal 상태 변환
     * =================================================
     */

    private LifeDomainSignalStatus resolveStatus(
            MonthlyLuckContextSignal signal
    ) {

        Objects.requireNonNull(
                signal,
                "MonthlyLuckContextSignal이 필요합니다."
        );

        if (
                signal.supported()
        ) {

            return LifeDomainSignalStatus
                    .SUPPORTIVE;
        }

        if (
                signal.qualified()
        ) {

            return LifeDomainSignalStatus
                    .CONTEXTUAL;
        }

        if (
                signal.weakened()
        ) {

            return LifeDomainSignalStatus
                    .PRESSURE;
        }

        if (
                signal.conflicted()
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        throw new IllegalStateException(
                "지원하지 않는 MonthlyLuckContextSignal 상태입니다: "
                        + signal.status()
        );
    }

    /*
     * =================================================
     * 내부 Summary
     * =================================================
     *
     * 새로운 관계 의미를 생성하지 않는다.
     *
     * 15-7 Signal의 code / axis / summary를
     * LifeDomain Match 경계에 그대로 노출한다.
     */

    private String summaryOf(
            MonthlyLuckContextSignal signal
    ) {

        return "현재 월운의 "
                + signal.axis().name()
                + " Signal ["
                + signal.code()
                + "]을 관계영역 Context로 보존합니다: "
                + signal.summary();
    }

    /*
     * =================================================
     * Temporal Evidence 조회
     * =================================================
     *
     * 0개:
     * Natal Bundle.
     *
     * 1개:
     * 현재 Timeline Segment의 Monthly Context.
     *
     * 2개 이상:
     * 동일 Segment Bundle 구성 오류.
     */

    private Optional<LifeDomainEvidenceReference<?>>
    optionalSingleEvidence(

            LifeDomainEvidenceBundle bundle,

            LifeDomainEvidenceSourceType sourceType

    ) {

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
                    "RelationshipMonthlyContextRule에는 최대 하나의 "
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