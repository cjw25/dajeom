package com.dajeom.rule;

import com.dajeom.model.DecadeLuckContextSignal;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.DecadeLuckContextAxis;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 건강·생활리듬 영역의 대운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 13-6 DecadeLuckPeriodContextAnalysis
 *
 * =================================================
 *
 * 직접 사용하는 Axis:
 *
 * USEFUL_GOD
 *
 * =================================================
 *
 * 중요한 경계:
 *
 * DecadeLuckContextAxis에는:
 *
 * - CLIMATE
 * - PATHOLOGY
 * - REMEDY
 * - PASSAGE
 *
 * 라는 독립 Axis가 존재하지 않는다.
 *
 * 따라서 18차 대운 Rule에서는:
 *
 * - 조후
 * - 명리적 병약
 * - 통관
 *
 * 을 대운에서 새로 계산하거나
 * 새 상태로 만들어내지 않는다.
 *
 * =================================================
 *
 * 원국의:
 *
 * ClimateHealthAnalysis
 *
 * 에서 보존된:
 *
 * - 조후 조정 필요
 * - 명리적 병약 구조
 * - 통관 필요
 * - 후보 충돌 / 재검토
 *
 * 은 기존 HealthLifestyleBalanceRule의
 * 원국 Evidence로 계속 유지된다.
 *
 * =================================================
 *
 * 대운에서는:
 *
 * 13-6에서 이미 교차검증된
 *
 * DecadeLuckContextAxis.USEFUL_GOD
 *
 * Signal만 생활리듬·균형의
 * temporal 조정 Context로 사용한다.
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
 * 용신 대운
 * -> 건강이 좋아진다
 *
 * USEFUL_GOD SUPPORTED
 * -> 병이 낫는다
 *
 * USEFUL_GOD WEAKENED
 * -> 병이 생긴다
 *
 * USEFUL_GOD CONFLICTED
 * -> 건강 문제가 발생한다
 *
 * 특정 오행
 * -> 특정 장기
 *
 * 같은 의료적 해석.
 *
 * =================================================
 *
 * SUPPORTIVE / PRESSURE / MIXED / CONTEXTUAL은
 * 명리적 생활 균형 조정축에 대한 상태이지
 *
 * 의료적 건강 상태,
 * 질병 여부,
 * 발병 확률,
 * 치료 필요성
 *
 * 이 아니다.
 *
 * =================================================
 *
 * DECADE_CONTEXT가 없는 Bundle:
 *
 * - Natal Bundle
 * - 현재 활성 대운 Context가 없는 Segment
 *
 * 에서는 아무 Match도 생성하지 않는다.
 */
@Component
public class HealthDecadeContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "HEALTH_DECADE_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .DECADE_CONTEXT
            );

    @Override
    public LifeDomain domain() {

        return LifeDomain.HEALTH_LIFESTYLE;
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
                "HealthDecadeContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.HEALTH_LIFESTYLE
        ) {

            throw new IllegalArgumentException(
                    "HealthDecadeContextRule은 HEALTH_LIFESTYLE Bundle만 평가할 수 있습니다."
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
         * Natal / 대운 Context 없는 Segment
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

        /*
         * =================================================
         * USEFUL_GOD Axis
         * =================================================
         *
         * 13-6이 이미:
         *
         * 원국의 용신·희신 구조
         * ×
         * 현재 대운
         *
         * 을 교차분석하여 만든 Signal.
         *
         * 18차에서는 오행을 다시 비교하거나
         * 용신 여부를 재판정하지 않는다.
         */

        List<DecadeLuckContextSignal>
                usefulGodSignals =
                decadeContext
                        .signalsOf(
                                DecadeLuckContextAxis
                                        .USEFUL_GOD
                        );

        if (
                usefulGodSignals.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 상태 집계
         * =================================================
         */

        SignalCounts counts =
                countStatuses(
                        usefulGodSignals
                );

        LifeDomainSignalStatus status =
                resolveStatus(
                        counts
                );

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
                "USEFUL_GOD Axis의 Decade Context Signal은 "
                        + usefulGodSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "SUPPORTED="
                        + counts.supported()
                        + ", QUALIFIED="
                        + counts.qualified()
                        + ", WEAKENED="
                        + counts.weakened()
                        + ", CONFLICTED="
                        + counts.conflicted()
                        + "입니다."
        );

        /*
         * =================================================
         * 원 Signal의 deterministic 근거 보존
         * =================================================
         *
         * 13-6 Signal의:
         *
         * - code
         * - summary
         * - axis
         * - status
         * - reasons
         *
         * 를 18차 Match까지 전달한다.
         */

        for (
                DecadeLuckContextSignal signal
                : usefulGodSignals
        ) {

            reasons.add(
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
                    String signalReason
                    : signal.reasons()
            ) {

                reasons.add(
                        "Decade Signal ["
                                + signal.code()
                                + "] 근거: "
                                + signalReason
                );
            }
        }

        /*
         * =================================================
         * 구조 경계
         * =================================================
         */

        reasons.add(
                "13차가 이미 판정한 USEFUL_GOD 대운 Context를 생활 균형의 temporal 상태로 변환할 뿐, 용신·격국·십성·관계·합화를 18차에서 다시 계산하지 않습니다."
        );

        reasons.add(
                "DecadeLuckContextAxis에는 조후·병약·통관 전용 Axis가 없으므로 대운의 조후·병약·통관 상태를 18차에서 새로 생성하지 않습니다."
        );

        reasons.add(
                "원국 6차 ClimateHealthAnalysis의 조후·명리적 병약·통관 Evidence는 HealthLifestyleBalanceRule에서 별도로 계속 보존됩니다."
        );

        /*
         * =================================================
         * 의료적 해석 금지
         * =================================================
         */

        reasons.add(
                "USEFUL_GOD 대운 Context는 생활리듬·균형 조정 방향에 대한 명리적 Evidence이며 실제 건강 상태나 질병 여부를 의미하지 않습니다."
        );

        reasons.add(
                "SUPPORTED 상태를 회복·치유 사건으로, WEAKENED 또는 CONFLICTED 상태를 발병·악화 사건으로 직접 환산하지 않습니다."
        );

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.HEALTH_LIFESTYLE,
                        RULE_CODE,
                        status,
                        summaryOf(
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
     * 13-6 -> 18차 상태 변환
     * =================================================
     *
     * 대운 Context를 하나의 점수나
     * 단일 길흉으로 축약하지 않는다.
     *
     * -------------------------------------------------
     *
     * CONFLICTED 존재
     *
     * -> 상충 Evidence가 있다는 의미이므로
     *    MIXED.
     *
     * -------------------------------------------------
     *
     * SUPPORTED / QUALIFIED
     * +
     * WEAKENED
     *
     * -> 지원·조건부 근거와
     *    부담 근거가 함께 있으므로
     *    MIXED.
     *
     * -------------------------------------------------
     *
     * WEAKENED만 존재
     * -> PRESSURE.
     *
     * -------------------------------------------------
     *
     * SUPPORTED 존재
     * -> SUPPORTIVE.
     *
     * -------------------------------------------------
     *
     * QUALIFIED만 존재
     * -> CONTEXTUAL.
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
                    "현재 대운의 용신·희신 Context가 생활 균형의 조정 방향을 지지하는 구조로 나타납니다.";

            case PRESSURE ->
                    "현재 대운의 용신·희신 Context에 생활 균형 조정축을 약화시키는 방향이 존재합니다.";

            case MIXED ->
                    "현재 대운의 용신·희신 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운에 용신·희신 관련 Context가 존재하지만 생활 균형 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    /*
     * =================================================
     * Temporal Evidence 조회
     * =================================================
     *
     * 0개:
     *
     * Natal Bundle 또는
     * 현재 Segment에 활성 대운 Context 없음.
     *
     * 정상.
     *
     * 1개:
     *
     * 정상적인 현재 대운 Context.
     *
     * 2개 이상:
     *
     * 동일 Segment Bundle의
     * Evidence 구성 오류.
     *
     * 교운 불확실성을 해결하기 위해
     * Rule에서 대운 하나를 임의 선택하지 않는다.
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
                    "HealthDecadeContextRule에는 최대 하나의 "
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