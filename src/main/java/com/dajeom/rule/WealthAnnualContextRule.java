package com.dajeom.rule;

import com.dajeom.model.AnnualLuckContextAnalysis;
import com.dajeom.model.AnnualLuckContextSignal;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
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
 * 재물 영역의 세운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 14-6 AnnualLuckContextAnalysis
 *
 * =================================================
 *
 * 대상 십성:
 *
 * DIRECT_WEALTH
 * INDIRECT_WEALTH
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 14차에서 이미 계산된:
 *
 * - 세운 십성
 * - 원국 × 세운
 * - 용신
 * - 격국
 * - 궁위
 * - 관계
 * - 합화
 * - 필요한 경우 대운 Context
 *
 * 결과를 18차에서 다시 계산하지 않는다.
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
 * 지원 계열과 약화 계열이 동시에 존재
 * -> MIXED
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * 재성 세운
 * -> 돈이 들어온다
 *
 * 재성 약화
 * -> 손실이 생긴다
 *
 * 재성 충돌
 * -> 투자 실패
 *
 * 같은 실제 사건 확정.
 *
 * =================================================
 *
 * 원국 Bundle에는 ANNUAL_CONTEXT가 없으므로
 * 이 Rule은 아무 Match도 생성하지 않는다.
 *
 * 실제 Timeline Segment Bundle에서만
 * temporal Evidence가 존재할 때 작동한다.
 */
@Component
public class WealthAnnualContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "WEALTH_ANNUAL_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .ANNUAL_CONTEXT
            );

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

    @Override
    public List<LifeDomainRuleMatch> evaluate(
            LifeDomainEvidenceBundle bundle
    ) {

        Objects.requireNonNull(
                bundle,
                "WealthAnnualContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthAnnualContextRule은 WEALTH Bundle만 평가할 수 있습니다."
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
         *
         * 원국 Bundle에는 세운 Evidence가 없다.
         *
         * 정상 상태이므로 예외를 발생시키지 않는다.
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

        /*
         * =================================================
         * 재성 관련 세운 Signal
         * =================================================
         *
         * 동일 Signal이 정재/편재 양쪽 조회에서
         * 중복될 가능성에 대비하여
         * Object Identity 기준으로 중복 제거한다.
         */

        List<AnnualLuckContextSignal>
                directWealthSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_WEALTH
                        );

        List<AnnualLuckContextSignal>
                indirectWealthSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .INDIRECT_WEALTH
                        );

        Set<AnnualLuckContextSignal>
                uniqueSignals =
                Collections
                        .newSetFromMap(
                                new IdentityHashMap<>()
                        );

        uniqueSignals.addAll(
                directWealthSignals
        );

        uniqueSignals.addAll(
                indirectWealthSignals
        );

        if (
                uniqueSignals.isEmpty()
        ) {

            return List.of();
        }

        List<AnnualLuckContextSignal>
                wealthSignals =
                List.copyOf(
                        uniqueSignals
                );

        /*
         * =================================================
         * 14차 상태 집계
         * =================================================
         *
         * status 자체를 새로 계산하지 않는다.
         *
         * 14차에서 이미 결정된 상태를
         * 생활영역 표현으로 변환할 뿐이다.
         */

        long supportedCount =
                wealthSignals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .supported()
                        )
                        .count();

        long qualifiedCount =
                wealthSignals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .qualified()
                        )
                        .count();

        long weakenedCount =
                wealthSignals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .weakened()
                        )
                        .count();

        long conflictedCount =
                wealthSignals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .conflicted()
                        )
                        .count();

        /*
         * =================================================
         * 18차 상태 변환
         * =================================================
         */

        LifeDomainSignalStatus status =
                resolveStatus(
                        supportedCount,
                        qualifiedCount,
                        weakenedCount,
                        conflictedCount
                );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "14-6 AnnualLuckContextAnalysis의 현재 세운 연도는 "
                        + annualContext.targetYear()
                        + "년입니다."
        );

        reasons.add(
                "정재 관련 Annual Context Signal은 "
                        + directWealthSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "편재 관련 Annual Context Signal은 "
                        + indirectWealthSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "Object Identity 중복 제거 후 재성 관련 Signal은 "
                        + wealthSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "SUPPORTED="
                        + supportedCount
                        + ", QUALIFIED="
                        + qualifiedCount
                        + ", WEAKENED="
                        + weakenedCount
                        + ", CONFLICTED="
                        + conflictedCount
                        + "입니다."
        );

        reasons.add(
                "14차가 판정한 Context 상태를 18차 생활영역 상태로 변환할 뿐, 세운 십성·격국·용신·관계·합화를 다시 계산하지 않습니다."
        );

        reasons.add(
                "재성 관련 세운 Context를 실제 수입·재산 증가·투자 수익·손실 사건으로 직접 환산하지 않습니다."
        );

        if (
                !annualContext
                        .decadeContextAvailable()
        ) {

            reasons.add(
                    "현재 세운은 대운 Segment Context를 사용할 수 없는 상태이지만, 14차 정책에 따라 세운 자체 annualSignals는 그대로 보존하여 사용합니다."
            );
        }

        if (
                annualContext
                        .decadeTimingUncertain()
        ) {

            reasons.add(
                    "현재 세운 기간 안에 대운 교운시점 불확실성이 존재하며, 가능한 대운 Context를 하나로 임의 선택하지 않습니다."
            );
        }

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.WEALTH,
                        RULE_CODE,
                        status,
                        summaryOf(
                                status
                        ),
                        List.of(
                                annualReference
                        ),
                        reasons
                )
        );
    }

    /*
     * =================================================
     * 상태 결정
     * =================================================
     *
     * 14차 Context 상태를
     * 18차 생활영역 상태로만 변환한다.
     *
     * =================================================
     *
     * CONFLICTED는 그 자체로
     * 상반 Evidence가 있다는 뜻이므로 MIXED.
     *
     * SUPPORTED / QUALIFIED와
     * WEAKENED가 함께 있어도 MIXED.
     *
     * WEAKENED만 존재하면 PRESSURE.
     *
     * SUPPORTED가 존재하고 제한 신호가 없으면
     * SUPPORTIVE.
     *
     * QUALIFIED만 존재하면 CONTEXTUAL.
     */

    private LifeDomainSignalStatus resolveStatus(

            long supportedCount,

            long qualifiedCount,

            long weakenedCount,

            long conflictedCount

    ) {

        boolean supportiveSide =
                supportedCount > 0
                        || qualifiedCount > 0;

        boolean weakenedSide =
                weakenedCount > 0;

        if (
                conflictedCount > 0
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
                supportedCount > 0
        ) {

            return LifeDomainSignalStatus
                    .SUPPORTIVE;
        }

        return LifeDomainSignalStatus
                .CONTEXTUAL;
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

            case SUPPORTIVE ->
                    "현재 세운에서 재성 관련 Context가 재물 기능을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 세운에서 재성 관련 Context에 약화·부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 세운의 재성 관련 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운에서 재성 관련 Context가 존재하지만 단독 방향으로 확정할 수준은 아닙니다.";
        };
    }

    /*
     * =================================================
     * Temporal Evidence 조회
     * =================================================
     *
     * Natal Bundle에서는 0개가 정상.
     *
     * Segment Bundle에서는
     * 동일 SourceType이 2개 이상이면
     * Evidence 구성 오류다.
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
                    "WealthAnnualContextRule에는 최대 하나의 "
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