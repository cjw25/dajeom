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
 * DIRECT_WEALTH 관련 Context
 *
 * 3.
 * INDIRECT_WEALTH 관련 Context
 *
 * 4.
 * DIRECT_OFFICER 관련 Context
 *
 * 5.
 * SEVEN_KILLINGS 관련 Context
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 각각의 축은 독립 Match로 유지한다.
 *
 * 서로 다른 축이 같은 대운에
 * 동시에 존재한다는 이유만으로:
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
 * 각 재성·관살 Signal은
 * 해당 StructureTenGodType의
 * 독립적인 관계영역 Context로 보존한다.
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
 * 동일 축 내부 상태 변환:
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
 * 동일 축 내부에
 * 지원 계열과 약화 계열 동시 존재
 * -> MIXED
 *
 * =================================================
 *
 * 위 상태 변환은 하나의 동일 Palace/TenGod 축 안에서
 * 13-6이 이미 계산한 Signal 상태를
 * LifeDomainSignalStatus로 변환하는
 * Java deterministic 계산이다.
 *
 * 서로 다른 독립 축을 합쳐
 * 새로운 상태를 계산하지 않는다.
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
                    "DAY 궁위를 배우자 자체로 고정하지 않고 관계 영역의 위치 Context로만 사용합니다."
            );

            reasons.add(
                    "DAY 궁위의 대운 작동을 연애·결혼·이별 같은 실제 관계 사건으로 직접 환산하지 않습니다."
            );

            reasons.add(
                    "이 Match는 DAY 궁위 축만 판정하며 재성·관살 축과 결합하여 새로운 상태를 만들지 않습니다."
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
         * DIRECT_WEALTH 대운 Context
         * =================================================
         */

        addTenGodMatch(
                matches,
                decadeReference,
                decadeContext,
                StructureTenGodType.DIRECT_WEALTH,
                decadeContext.signalsOf(
                        StructureTenGodType.DIRECT_WEALTH
                )
        );

        /*
         * =================================================
         * 3.
         * INDIRECT_WEALTH 대운 Context
         * =================================================
         */

        addTenGodMatch(
                matches,
                decadeReference,
                decadeContext,
                StructureTenGodType.INDIRECT_WEALTH,
                decadeContext.signalsOf(
                        StructureTenGodType.INDIRECT_WEALTH
                )
        );

        /*
         * =================================================
         * 4.
         * DIRECT_OFFICER 대운 Context
         * =================================================
         */

        addTenGodMatch(
                matches,
                decadeReference,
                decadeContext,
                StructureTenGodType.DIRECT_OFFICER,
                decadeContext.signalsOf(
                        StructureTenGodType.DIRECT_OFFICER
                )
        );

        /*
         * =================================================
         * 5.
         * SEVEN_KILLINGS 대운 Context
         * =================================================
         */

        addTenGodMatch(
                matches,
                decadeReference,
                decadeContext,
                StructureTenGodType.SEVEN_KILLINGS,
                decadeContext.signalsOf(
                        StructureTenGodType.SEVEN_KILLINGS
                )
        );

        return List.copyOf(
                matches
        );
    }

    /*
     * =================================================
     * TenGod 독립 Match
     * =================================================
     */

    private void addTenGodMatch(

            List<LifeDomainRuleMatch> matches,

            LifeDomainEvidenceReference<?> decadeReference,

            DecadeLuckPeriodContextAnalysis decadeContext,

            StructureTenGodType tenGodType,

            List<DecadeLuckContextSignal> signals

    ) {

        Objects.requireNonNull(
                matches,
                "Relationship Decade Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                decadeReference,
                "Decade Evidence Reference가 필요합니다."
        );

        Objects.requireNonNull(
                decadeContext,
                "DecadeLuckPeriodContextAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodType,
                "StructureTenGodType이 필요합니다."
        );

        Objects.requireNonNull(
                signals,
                "Decade Context Signal 목록은 null일 수 없습니다."
        );

        if (
                signals.isEmpty()
        ) {

            return;
        }

        SignalCounts counts =
                countStatuses(
                        signals
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
                tenGodType.name()
                        + " 관련 Decade Context Signal은 "
                        + signals.size()
                        + "개입니다."
        );

        reasons.add(
                statusSummary(
                        counts
                )
        );

        appendSignalReasons(
                reasons,
                signals
        );

        reasons.add(
                "현재 LifeDomain Evidence에는 성별 정보가 없으므로 "
                        + tenGodType.name()
                        + "을 배우자성으로 임의 확정하지 않습니다."
        );

        reasons.add(
                tenGodType.name()
                        + " 관련 대운 Context를 배우자 등장·연애·결혼·관계 단절 같은 실제 사건으로 직접 환산하지 않습니다."
        );

        reasons.add(
                "이 Match는 "
                        + tenGodType.name()
                        + " 축만 판정하며 다른 재성·관살·궁위 축과 결합하여 새로운 상태를 생성하지 않습니다."
        );

        matches.add(
                new LifeDomainRuleMatch(
                        LifeDomain.RELATIONSHIP,
                        RULE_CODE,
                        status,
                        summaryOfTenGod(
                                tenGodType,
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
     * 동일 축 내부 상태 집계
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
     * 동일 축 13-6 -> LifeDomain 상태
     * =================================================
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
                    "현재 대운의 DAY 궁위 Context 내부에 지원과 약화 또는 상충 상태가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운에서 DAY 궁위 관련 Context가 존재하지만 관계 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfTenGod(

            StructureTenGodType tenGodType,

            LifeDomainSignalStatus status

    ) {

        String axis =
                tenGodType.name();

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운에서 "
                            + axis
                            + " 관련 Context가 관계 영역의 지지 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 대운에서 "
                            + axis
                            + " 관련 Context에 관계영역 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 대운의 "
                            + axis
                            + " Context 내부에 지원과 약화 또는 상충 상태가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운에서 "
                            + axis
                            + " 관련 Context가 존재하지만 관계 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    /*
     * =================================================
     * Temporal Evidence 조회
     * =================================================
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