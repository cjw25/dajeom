package com.dajeom.rule;

import com.dajeom.model.AnnualLuckContextAnalysis;
import com.dajeom.model.AnnualLuckContextSignal;
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
 * 관계 영역의 세운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 14-6 AnnualLuckContextAnalysis
 *
 * =================================================
 *
 * 관계 영역에서 읽는 세운 축:
 *
 * 1.
 * DAY Palace 관련 Annual Context
 *
 * 2.
 * DIRECT_WEALTH 관련 Annual Context
 *
 * 3.
 * INDIRECT_WEALTH 관련 Annual Context
 *
 * 4.
 * DIRECT_OFFICER 관련 Annual Context
 *
 * 5.
 * SEVEN_KILLINGS 관련 Annual Context
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 각각의 축은 독립 Match로 유지한다.
 *
 * 서로 다른 축이 같은 세운에
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
 * 배우자 그 자체
 *
 * 로 확정하지 않는다.
 *
 * 관계 해석에서 중요한 위치 Context로만 사용한다.
 *
 * =================================================
 *
 * 현재 18차 Evidence에는
 * 성별 정보가 포함되어 있지 않다.
 *
 * 따라서:
 *
 * 남성 -> 재성 = 배우자성
 * 여성 -> 관살 = 배우자성
 *
 * 같은 성별 기반 선택을 하지 않는다.
 *
 * 각 재성·관살 Signal은
 * 해당 StructureTenGodType의
 * 독립적인 관계영역 Context로 보존한다.
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
 * 위 상태 변환은
 * 하나의 동일 Palace/TenGod 축 안에서
 * 14-6이 이미 계산한 Signal 상태를
 * LifeDomainSignalStatus로 변환하는
 * Java deterministic 계산이다.
 *
 * 서로 다른 독립 축의 Signal을 합쳐
 * 새로운 상태를 계산하지 않는다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * DAY Palace 지원
 * -> 연애 성사
 *
 * 재성 세운
 * -> 배우자가 생긴다
 *
 * 관살 세운
 * -> 결혼한다
 *
 * 관계 Context 약화
 * -> 이별한다
 *
 * 같은 실제 사건 결과 확정.
 *
 * =================================================
 *
 * Natal Bundle에는 ANNUAL_CONTEXT가 없으므로
 * 아무 Match도 생성하지 않는다.
 *
 * Timeline Segment Bundle에서만 작동한다.
 */
@Component
public class RelationshipAnnualContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "RELATIONSHIP_ANNUAL_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .ANNUAL_CONTEXT
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
                "RelationshipAnnualContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "RelationshipAnnualContextRule은 RELATIONSHIP Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                annualReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .ANNUAL_CONTEXT
                );

        /*
         * =================================================
         * Natal Bundle
         * =================================================
         */

        if (
                annualReferenceOptional
                        .isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                annualReference =
                annualReferenceOptional
                        .orElseThrow();

        AnnualLuckContextAnalysis annualContext =
                annualReference
                        .requireSource(
                                AnnualLuckContextAnalysis.class
                        );

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * DAY Palace Annual Context
         * =================================================
         */

        List<AnnualLuckContextSignal>
                dayPalaceSignals =
                annualContext
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
                            annualContext
                    );

            reasons.add(
                    "DAY 궁위에 관여하는 Annual Context Signal은 "
                            + dayPalaceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    statusSummary(
                            counts
                    )
            );

            reasons.add(
                    "DAY 궁위를 배우자 자체로 고정하지 않고 관계 영역의 위치 Context로만 사용합니다."
            );

            reasons.add(
                    "DAY 궁위 세운 작동을 연애 성사·결혼·이별 같은 실제 관계 사건으로 직접 환산하지 않습니다."
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
                                    annualReference
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
         * DIRECT_WEALTH Annual Context
         * =================================================
         */

        addTenGodMatch(
                matches,
                annualReference,
                annualContext,
                StructureTenGodType.DIRECT_WEALTH,
                annualContext.signalsOf(
                        StructureTenGodType.DIRECT_WEALTH
                )
        );

        /*
         * =================================================
         * 3.
         * INDIRECT_WEALTH Annual Context
         * =================================================
         */

        addTenGodMatch(
                matches,
                annualReference,
                annualContext,
                StructureTenGodType.INDIRECT_WEALTH,
                annualContext.signalsOf(
                        StructureTenGodType.INDIRECT_WEALTH
                )
        );

        /*
         * =================================================
         * 4.
         * DIRECT_OFFICER Annual Context
         * =================================================
         */

        addTenGodMatch(
                matches,
                annualReference,
                annualContext,
                StructureTenGodType.DIRECT_OFFICER,
                annualContext.signalsOf(
                        StructureTenGodType.DIRECT_OFFICER
                )
        );

        /*
         * =================================================
         * 5.
         * SEVEN_KILLINGS Annual Context
         * =================================================
         */

        addTenGodMatch(
                matches,
                annualReference,
                annualContext,
                StructureTenGodType.SEVEN_KILLINGS,
                annualContext.signalsOf(
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

            LifeDomainEvidenceReference<?> annualReference,

            AnnualLuckContextAnalysis annualContext,

            StructureTenGodType tenGodType,

            List<AnnualLuckContextSignal> signals

    ) {

        Objects.requireNonNull(
                matches,
                "Relationship Annual Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                annualReference,
                "Annual Evidence Reference가 필요합니다."
        );

        Objects.requireNonNull(
                annualContext,
                "AnnualLuckContextAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodType,
                "StructureTenGodType이 필요합니다."
        );

        Objects.requireNonNull(
                signals,
                "Annual Context Signal 목록은 null일 수 없습니다."
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
                        annualContext
                );

        reasons.add(
                tenGodType.name()
                        + " 관련 Annual Context Signal은 "
                        + signals.size()
                        + "개입니다."
        );

        reasons.add(
                statusSummary(
                        counts
                )
        );

        reasons.add(
                "현재 18차 Evidence에는 성별 정보가 없으므로 "
                        + tenGodType.name()
                        + "을 배우자성으로 임의 확정하지 않습니다."
        );

        reasons.add(
                tenGodType.name()
                        + " 관련 세운 Context를 배우자 등장·연애·결혼·관계 단절 같은 실제 사건으로 직접 환산하지 않습니다."
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
                                annualReference
                        ),
                        List.copyOf(
                                reasons
                        )
                )
        );
    }

    /*
     * =================================================
     * 공통 세운 이유
     * =================================================
     */

    private List<String> baseReasons(
            AnnualLuckContextAnalysis annualContext
    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "14-6 AnnualLuckContextAnalysis의 현재 세운 연도는 "
                        + annualContext.targetYear()
                        + "년입니다."
        );

        reasons.add(
                "14차에서 이미 계산한 십성·원국 관계·격국·용신·궁위·합화 Context를 18차에서 다시 계산하지 않습니다."
        );

        if (
                !annualContext
                        .decadeContextAvailable()
        ) {

            reasons.add(
                    "대운 Segment Context를 사용할 수 없는 상태이지만 14차 정책에 따라 세운 자체 annualSignals는 그대로 보존합니다."
            );
        }

        if (
                annualContext
                        .decadeTimingUncertain()
        ) {

            reasons.add(
                    "현재 세운 기간에 대운 교운시점 불확실성이 존재하므로 가능한 대운 Context 하나를 임의로 확정하지 않습니다."
            );
        }

        return reasons;
    }

    /*
     * =================================================
     * 동일 축 내부 상태 집계
     * =================================================
     */

    private SignalCounts countStatuses(
            List<AnnualLuckContextSignal> signals
    ) {

        long supported =
                signals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .supported()
                        )
                        .count();

        long qualified =
                signals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .qualified()
                        )
                        .count();

        long weakened =
                signals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .weakened()
                        )
                        .count();

        long conflicted =
                signals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .conflicted()
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
     * 동일 축 LifeDomain 상태 변환
     * =================================================
     */

    private LifeDomainSignalStatus resolveStatus(
            SignalCounts counts
    ) {

        boolean supportiveSide =
                counts.supported() > 0
                        || counts.qualified() > 0;

        boolean weakenedSide =
                counts.weakened() > 0;

        if (
                counts.conflicted() > 0
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                supportiveSide
                        && weakenedSide
        ) {

            return LifeDomainSignalStatus
                    .MIXED;
        }

        if (
                weakenedSide
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
                    "현재 세운에서 DAY 궁위 관련 Context가 관계 영역을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 세운에서 DAY 궁위 관련 Context에 관계영역 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 세운의 DAY 궁위 Context 내부에 지원과 약화 또는 상충 상태가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운에서 DAY 궁위 관련 Context가 존재하지만 관계 방향을 단독으로 확정할 수준은 아닙니다.";
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
                    "현재 세운에서 "
                            + axis
                            + " 관련 Context가 관계 영역의 지지 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 세운에서 "
                            + axis
                            + " 관련 Context에 관계영역 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 세운의 "
                            + axis
                            + " Context 내부에 지원과 약화 또는 상충 상태가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운에서 "
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
                    "RelationshipAnnualContextRule에는 최대 하나의 "
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