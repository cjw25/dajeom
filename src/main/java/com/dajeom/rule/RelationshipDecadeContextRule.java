package com.dajeom.rule;

import com.dajeom.model.DecadeLuckContextSignal;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 관계 영역의 대운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 13-6 DecadeLuckPeriodContextAnalysis
 *
 * =================================================
 *
 * 관계 영역에서 읽는 대운 축:
 *
 * 1.
 * DAY Palace 관련 Context
 *
 * 2.
 * 재성 관련 Context
 *
 * DIRECT_WEALTH
 * INDIRECT_WEALTH
 *
 * 3.
 * 관살 관련 Context
 *
 * DIRECT_OFFICER
 * SEVEN_KILLINGS
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * DAY Palace
 * =
 * 배우자 자체
 *
 * 로 확정하지 않는다.
 *
 * 관계 해석에서 중요한
 * 위치 Context로만 사용한다.
 *
 * =================================================
 *
 * 현재 LifeDomain Evidence에는
 * 성별 정보가 없다.
 *
 * 따라서:
 *
 * 남성 -> 재성 = 배우자성
 * 여성 -> 관살 = 배우자성
 *
 * 같은 선택을 하지 않는다.
 *
 * 재성과 관살 모두
 * 관계 영역의 구조적 Context로 보존한다.
 *
 * =================================================
 *
 * 13차에서 이미 계산된:
 *
 * - 대운 십성
 * - 원국 × 대운 관계
 * - 용신·희신
 * - 격국
 * - 궁위
 * - 관계
 * - 합화·기능변화
 *
 * 를 18차에서 다시 계산하지 않는다.
 *
 * =================================================
 *
 * 상태 변환:
 *
 * SUPPORTED
 * -> SUPPORTIVE
 *
 * QUALIFIED만 존재
 * -> CONTEXTUAL
 *
 * WEAKENED만 존재
 * -> PRESSURE
 *
 * CONFLICTED 존재
 * -> MIXED
 *
 * 지원 계열과 약화 계열 동시 존재
 * -> MIXED
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * DAY 궁위 대운 지원
 * -> 연애가 생긴다
 *
 * 재성 대운
 * -> 배우자가 생긴다
 *
 * 관살 대운
 * -> 결혼한다
 *
 * 관계 Context 약화
 * -> 이별한다
 *
 * 관계 Context 충돌
 * -> 관계 문제가 발생한다
 *
 * 같은 실제 사건 결과 확정.
 *
 * =================================================
 *
 * DECADE_CONTEXT가 없는 Bundle:
 *
 * - Natal Bundle
 * - 비활성 대운 Segment
 *
 * 에서는 아무 Match도 생성하지 않는다.
 */
@Component
public class RelationshipDecadeContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "RELATIONSHIP_DECADE_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .DECADE_CONTEXT
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
                "RelationshipDecadeContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "RelationshipDecadeContextRule은 RELATIONSHIP Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                decadeReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .DECADE_CONTEXT
                );

        /*
         * =================================================
         * Natal / 비활성 Segment
         * =================================================
         */

        if (
                decadeReferenceOptional
                        .isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                decadeReference =
                decadeReferenceOptional
                        .orElseThrow();

        DecadeLuckPeriodContextAnalysis decadeContext =
                decadeReference
                        .requireSource(
                                DecadeLuckPeriodContextAnalysis.class
                        );

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * DAY Palace 대운 Context
         * =================================================
         *
         * DAY 궁위에 관여하는 모든 13-6 Signal을
         * 그대로 읽는다.
         *
         * Axis를 특정하지 않는다.
         *
         * PALACE뿐 아니라
         * RELATION / TRANSFORMATION 등에서
         * DAY가 직접 태깅된 Signal도
         * 함께 보존될 수 있다.
         */

        List<DecadeLuckContextSignal>
                dayPalaceSignals =
                decadeContext
                        .signalsOf(
                                PalacePosition.DAY
                        );

        if (
                !dayPalaceSignals.isEmpty()
        ) {

            SignalCounts counts =
                    countStatuses(
                            dayPalaceSignals
                    );

            LifeDomainSignalStatus status =
                    resolveStatus(
                            counts
                    );

            List<String> reasons =
                    baseReasons(
                            decadeContext
                    );

            reasons.add(
                    "DAY 궁위에 관여하는 Decade Context Signal은 "
                            + dayPalaceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    statusSummary(
                            counts
                    )
            );

            appendSignalReasons(
                    reasons,
                    dayPalaceSignals
            );

            reasons.add(
                    "DAY 궁위를 배우자 자체로 고정하지 않고 관계 영역의 주요 위치 Context로만 사용합니다."
            );

            reasons.add(
                    "DAY 궁위의 대운 작동을 연애·결혼·이별 같은 실제 관계 사건으로 직접 환산하지 않습니다."
            );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            status,
                            summaryOfDayPalace(
                                    status
                            ),
                            List.of(
                                    decadeReference
                            ),
                            List.copyOf(
                                    reasons
                            )
                    )
            );
        }

        /*
         * =================================================
         * 2.
         * 재성 + 관살 대운 Context
         * =================================================
         *
         * 성별 정보가 없으므로
         * 어느 한 Family도 배우자성으로 선택하지 않는다.
         *
         * 정재 / 편재 / 정관 / 칠살
         * 네 종류의 Signal을 모두 읽는다.
         */

        List<DecadeLuckContextSignal>
                directWealthSignals =
                decadeContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_WEALTH
                        );

        List<DecadeLuckContextSignal>
                indirectWealthSignals =
                decadeContext
                        .signalsOf(
                                StructureTenGodType
                                        .INDIRECT_WEALTH
                        );

        List<DecadeLuckContextSignal>
                directOfficerSignals =
                decadeContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_OFFICER
                        );

        List<DecadeLuckContextSignal>
                sevenKillingsSignals =
                decadeContext
                        .signalsOf(
                                StructureTenGodType
                                        .SEVEN_KILLINGS
                        );

        List<DecadeLuckContextSignal>
                relationshipTenGodSignals =
                mergeIdentityUnique(
                        directWealthSignals,
                        indirectWealthSignals,
                        directOfficerSignals,
                        sevenKillingsSignals
                );

        if (
                !relationshipTenGodSignals
                        .isEmpty()
        ) {

            SignalCounts counts =
                    countStatuses(
                            relationshipTenGodSignals
                    );

            LifeDomainSignalStatus status =
                    resolveStatus(
                            counts
                    );

            List<String> reasons =
                    baseReasons(
                            decadeContext
                    );

            reasons.add(
                    "정재 관련 Decade Context Signal은 "
                            + directWealthSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "편재 관련 Decade Context Signal은 "
                            + indirectWealthSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "정관 관련 Decade Context Signal은 "
                            + directOfficerSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "칠살 관련 Decade Context Signal은 "
                            + sevenKillingsSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "Object Identity 중복 제거 후 관계영역 십성 Context Signal은 "
                            + relationshipTenGodSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    statusSummary(
                            counts
                    )
            );

            appendSignalReasons(
                    reasons,
                    relationshipTenGodSignals
            );

            reasons.add(
                    "현재 18차 Evidence에는 성별 정보가 없으므로 재성 또는 관살 중 하나를 배우자성으로 임의 선택하지 않습니다."
            );

            reasons.add(
                    "재성·관살 관련 대운 Context를 배우자 등장·연애·결혼·관계 단절 같은 실제 사건으로 직접 환산하지 않습니다."
            );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            status,
                            summaryOfTenGod(
                                    status
                            ),
                            List.of(
                                    decadeReference
                            ),
                            List.copyOf(
                                    reasons
                            )
                    )
            );
        }

        /*
         * =================================================
         * 3.
         * DAY Palace + 관계 십성 동시 관여
         * =================================================
         *
         * 위치 축과 십성 축이
         * 같은 대운에서 동시에 존재하는 사실을
         * 별도 Evidence로 보존한다.
         *
         * 동시 존재
         * =
         * 관계 사건 확률 상승
         *
         * 으로 해석하지 않는다.
         */

        if (
                !dayPalaceSignals.isEmpty()
                        && !relationshipTenGodSignals.isEmpty()
        ) {

            List<DecadeLuckContextSignal>
                    combinedSignals =
                    mergeIdentityUnique(
                            dayPalaceSignals,
                            relationshipTenGodSignals
                    );

            SignalCounts counts =
                    countStatuses(
                            combinedSignals
                    );

            LifeDomainSignalStatus status =
                    resolveStatus(
                            counts
                    );

            List<String> reasons =
                    baseReasons(
                            decadeContext
                    );

            reasons.add(
                    "DAY 궁위 Context와 재성·관살 Context가 같은 대운에서 함께 존재합니다."
            );

            reasons.add(
                    "DAY 궁위 Signal은 "
                            + dayPalaceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "관계 십성 Signal은 "
                            + relationshipTenGodSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "Object Identity 중복 제거 후 두 축을 합친 Signal은 "
                            + combinedSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    statusSummary(
                            counts
                    )
            );

            appendSignalReasons(
                    reasons,
                    combinedSignals
            );

            reasons.add(
                    "궁위 축과 십성 축의 동시 관여를 별도로 보존하지만 이를 연애·결혼·이별 같은 실제 관계 사건으로 승격하지 않습니다."
            );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            status,
                            summaryOfCombined(
                                    status
                            ),
                            List.of(
                                    decadeReference
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
     * 공통 대운 근거
     * =================================================
     */

    private List<String> baseReasons(
            DecadeLuckPeriodContextAnalysis decadeContext
    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "13-6 대운 Context의 Sequence는 "
                        + decadeContext.sequenceNumber()
                        + "입니다."
        );

        reasons.add(
                "현재 대운 간지는 "
                        + decadeContext.luckPillar()
                        + "입니다."
        );

        reasons.add(
                "13차에서 이미 계산한 대운 십성·원국 관계·용신·격국·궁위·합화 Context를 18차에서 다시 계산하지 않습니다."
        );

        return reasons;
    }

    /*
     * =================================================
     * 원 Signal 근거 전달
     * =================================================
     *
     * 13-6의 deterministic:
     *
     * - code
     * - summary
     * - axis
     * - status
     * - reasons
     *
     * 를 LifeDomainRuleMatch까지 전달한다.
     */

    private void appendSignalReasons(

            List<String> target,

            List<DecadeLuckContextSignal> signals

    ) {

        for (
                DecadeLuckContextSignal signal
                : signals
        ) {

            target.add(
                    "Decade Signal ["
                            + signal.code()
                            + "] "
                            + signal.summary()
                            + " / axis="
                            + signal.axis().name()
                            + " / status="
                            + signal.status().name()
            );

            for (
                    String reason
                    : signal.reasons()
            ) {

                target.add(
                        "Decade Signal ["
                                + signal.code()
                                + "] 근거: "
                                + reason
                );
            }
        }
    }

    /*
     * =================================================
     * 상태 집계
     * =================================================
     */

    private SignalCounts countStatuses(
            List<DecadeLuckContextSignal> signals
    ) {

        long supported =
                signals
                        .stream()
                        .filter(
                                DecadeLuckContextSignal
                                        ::supported
                        )
                        .count();

        long qualified =
                signals
                        .stream()
                        .filter(
                                DecadeLuckContextSignal
                                        ::qualified
                        )
                        .count();

        long weakened =
                signals
                        .stream()
                        .filter(
                                DecadeLuckContextSignal
                                        ::weakened
                        )
                        .count();

        long conflicted =
                signals
                        .stream()
                        .filter(
                                DecadeLuckContextSignal
                                        ::conflicted
                        )
                        .count();

        return new SignalCounts(
                supported,
                qualified,
                weakened,
                conflicted
        );
    }

    /*
     * =================================================
     * 13-6 -> 18차 상태
     * =================================================
     *
     * CONFLICTED
     * -> MIXED
     *
     * SUPPORTED / QUALIFIED
     * +
     * WEAKENED
     * -> MIXED
     *
     * WEAKENED만 존재
     * -> PRESSURE
     *
     * SUPPORTED 존재
     * -> SUPPORTIVE
     *
     * QUALIFIED만 존재
     * -> CONTEXTUAL
     */

    private LifeDomainSignalStatus resolveStatus(
            SignalCounts counts
    ) {

        boolean supportiveSide =
                counts.supported() > 0
                        || counts.qualified() > 0;

        boolean pressureSide =
                counts.weakened() > 0;

        if (
                counts.conflicted() > 0
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                supportiveSide
                        && pressureSide
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                pressureSide
        ) {

            return LifeDomainSignalStatus
                    .PRESSURE;
        }

        if (
                counts.supported() > 0
        ) {

            return LifeDomainSignalStatus
                    .SUPPORTIVE;
        }

        return LifeDomainSignalStatus
                .CONTEXTUAL;
    }

    private String statusSummary(
            SignalCounts counts
    ) {

        return "SUPPORTED="
                + counts.supported()
                + ", QUALIFIED="
                + counts.qualified()
                + ", WEAKENED="
                + counts.weakened()
                + ", CONFLICTED="
                + counts.conflicted()
                + "입니다.";
    }

    /*
     * =================================================
     * Summary
     * =================================================
     */

    private String summaryOfDayPalace(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운에서 DAY 궁위 관련 Context가 관계 영역을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 대운에서 DAY 궁위 관련 Context에 관계영역 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 대운의 DAY 궁위 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운에서 DAY 궁위 관련 Context가 존재하지만 관계 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfTenGod(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운에서 재성·관살 관련 Context가 관계 영역을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 대운에서 재성·관살 관련 Context에 관계영역 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 대운의 재성·관살 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운에서 재성·관살 관련 Context가 존재하지만 관계 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfCombined(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운에서 DAY 궁위와 관계 십성 Context가 함께 지지 방향을 형성합니다.";

            case PRESSURE ->
                    "현재 대운에서 DAY 궁위와 관계 십성 Context가 함께 부담 방향을 포함합니다.";

            case MIXED ->
                    "현재 대운에서 DAY 궁위와 관계 십성 축에 지원·약화·상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운에서 DAY 궁위와 관계 십성 축이 함께 관여하지만 단독 관계 결과로 확정할 수준은 아닙니다.";
        };
    }

    /*
     * =================================================
     * Object Identity 중복 제거
     * =================================================
     */

    @SafeVarargs
    private final List<DecadeLuckContextSignal>
    mergeIdentityUnique(
            List<DecadeLuckContextSignal>... signalLists
    ) {

        Set<DecadeLuckContextSignal> unique =
                Collections
                        .newSetFromMap(
                                new IdentityHashMap<>()
                        );

        for (
                List<DecadeLuckContextSignal> signals
                : signalLists
        ) {

            if (
                    signals == null
            ) {

                continue;
            }

            unique.addAll(
                    signals
            );
        }

        return List.copyOf(
                unique
        );
    }

    /*
     * =================================================
     * Temporal Evidence 조회
     * =================================================
     *
     * 0개:
     *
     * Natal Bundle 또는
     * ACTIVE 대운 Context가 없는 Segment.
     *
     * 정상.
     *
     * 1개:
     *
     * 정상적인 대운 Context.
     *
     * 2개 이상:
     *
     * 동일 Segment Bundle 구성 오류.
     *
     * Rule에서 어느 대운이 맞는지
     * 임의 선택하지 않는다.
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
                    "RelationshipDecadeContextRule에는 최대 하나의 "
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

    /*
     * =================================================
     * 내부 상태 집계
     * =================================================
     */

    private record SignalCounts(

            long supported,

            long qualified,

            long weakened,

            long conflicted

    ) {
    }
}