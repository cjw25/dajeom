package com.dajeom.rule;

import com.dajeom.model.AnnualLuckContextAnalysis;
import com.dajeom.model.AnnualLuckContextSignal;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.AnnualLuckContextAxis;
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
 * 건강·생활리듬 영역의 세운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 14-6 AnnualLuckContextAnalysis
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
 * AnnualLuckContextAxis에는:
 *
 * - CLIMATE
 * - PATHOLOGY
 * - REMEDY
 * - PASSAGE
 *
 * 라는 독립 Axis가 존재하지 않는다.
 *
 * 따라서 이 Rule에서는:
 *
 * 조후
 * 병약
 * 통관
 *
 * 이 세운에서 직접 다시 판정된 것처럼
 * 추론하지 않는다.
 *
 * =================================================
 *
 * 6차 원국:
 *
 * ClimateHealthAnalysis
 *
 * 에서 확정된:
 *
 * - 조후 필요
 * - 명리적 병약
 * - 통관 필요
 * - 후보 충돌
 *
 * 은 기존 원국 HEALTH_LIFESTYLE Rule에서
 * 그대로 보존한다.
 *
 * =================================================
 *
 * 세운에서는:
 *
 * 7차 최종 용신·희신·기신 판단을
 * 14차가 세운과 교차한
 *
 * AnnualLuckContextAxis.USEFUL_GOD
 *
 * Signal만 읽는다.
 *
 * =================================================
 *
 * 상태 변환:
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
 * 여러 Signal에서
 * 지원과 약화가 함께 존재
 * -> MIXED
 *
 * =================================================
 *
 * 금지:
 *
 * 용신 세운 지원
 * -> 건강이 좋아진다
 *
 * 기신 관련 약화
 * -> 병이 생긴다
 *
 * CONFLICTED
 * -> 질환 발생
 *
 * 특정 오행
 * -> 특정 장기
 *
 * 같은 의료적 해석.
 *
 * =================================================
 *
 * 여기서 SUPPORTIVE / PRESSURE는
 * 생활리듬·균형 조정축에 대한
 * 명리적 Context일 뿐이다.
 *
 * 의료적 건강 상태나
 * 질병 확률이 아니다.
 *
 * =================================================
 *
 * Natal Bundle에는 ANNUAL_CONTEXT가 없으므로
 * 아무 Match도 생성하지 않는다.
 *
 * Timeline Segment Bundle에서만 작동한다.
 */
@Component
public class HealthAnnualContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "HEALTH_ANNUAL_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .ANNUAL_CONTEXT
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
                "HealthAnnualContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.HEALTH_LIFESTYLE
        ) {

            throw new IllegalArgumentException(
                    "HealthAnnualContextRule은 HEALTH_LIFESTYLE Bundle만 평가할 수 있습니다."
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
         * 원국 Bundle에 ANNUAL_CONTEXT가 없는 것은
         * 정상 상태다.
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
         * USEFUL_GOD Axis
         * =================================================
         *
         * 14차 Resolver가 이미:
         *
         * 원국 용신·희신·기신 판단
         * ×
         * 세운
         *
         * 을 교차하여 생성한 Signal이다.
         *
         * 18차에서 오행을 다시 비교하지 않는다.
         */

        List<AnnualLuckContextSignal>
                usefulGodSignals =
                annualContext
                        .signalsOf(
                                AnnualLuckContextAxis
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
                "14-6 AnnualLuckContextAnalysis의 현재 세운 연도는 "
                        + annualContext.targetYear()
                        + "년입니다."
        );

        reasons.add(
                "USEFUL_GOD Axis의 Annual Context Signal은 "
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
         * Signal 자체의 deterministic 근거 보존
         * =================================================
         *
         * code / summary / reasons를 버리지 않는다.
         *
         * 이후 20차 AI Grounding에서도
         * 원 Signal 근거를 추적할 수 있게 한다.
         */

        for (
                AnnualLuckContextSignal signal
                : usefulGodSignals
        ) {

            reasons.add(
                    "Annual Signal ["
                            + signal.code()
                            + "] "
                            + signal.summary()
                            + " / status="
                            + signal.status().name()
            );

            for (
                    String signalReason
                    : signal.reasons()
            ) {

                reasons.add(
                        "Annual Signal ["
                                + signal.code()
                                + "] 근거: "
                                + signalReason
                );
            }
        }

        /*
         * =================================================
         * 대운 Context 사용 가능 여부
         * =================================================
         *
         * 대운 Context가 없어도
         * annualSignals는 버리지 않는다.
         */

        if (
                !annualContext
                        .decadeContextAvailable()
        ) {

            reasons.add(
                    "현재 세운은 대운 Segment Context를 사용할 수 없지만, 14차 정책에 따라 원국×세운만으로 확정 가능한 annualSignals는 그대로 사용합니다."
            );

            if (
                    annualContext
                            .decadeContextUnavailableReason()
                            != null

                            && !annualContext
                            .decadeContextUnavailableReason()
                            .isBlank()
            ) {

                reasons.add(
                        "대운 Context 사용 불가 사유: "
                                + annualContext
                                .decadeContextUnavailableReason()
                );
            }
        }

        /*
         * =================================================
         * 교운 불확실성
         * =================================================
         *
         * 가능한 대운 Segment를
         * 하나로 선택하지 않는다.
         */

        if (
                annualContext
                        .decadeTimingUncertain()
        ) {

            reasons.add(
                    "현재 세운 기간에는 대운 교운시점 불확실성이 존재하므로 가능한 대운 Context를 하나의 확정 Context로 임의 선택하지 않습니다."
            );
        }

        /*
         * =================================================
         * 의료적 해석 금지
         * =================================================
         */

        reasons.add(
                "USEFUL_GOD Axis는 생활 균형의 명리적 조정 방향 Context이며 실제 건강 상태·질병·장기 상태를 의미하지 않습니다."
        );

        reasons.add(
                "AnnualLuckContextAxis에는 조후·병약·통관 전용 Axis가 없으므로 18차에서 해당 세운 상태를 새로 만들어내지 않습니다."
        );

        reasons.add(
                "6차 ClimateHealthAnalysis의 조후·명리적 병약·통관 구조는 원국 HEALTH_LIFESTYLE Evidence에서 별도로 계속 보존됩니다."
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
     * 상태 집계
     * =================================================
     */

    private SignalCounts countStatuses(
            List<AnnualLuckContextSignal> signals
    ) {

        long supported =
                signals
                        .stream()
                        .filter(
                                AnnualLuckContextSignal
                                        ::supported
                        )
                        .count();

        long qualified =
                signals
                        .stream()
                        .filter(
                                AnnualLuckContextSignal
                                        ::qualified
                        )
                        .count();

        long weakened =
                signals
                        .stream()
                        .filter(
                                AnnualLuckContextSignal
                                        ::weakened
                        )
                        .count();

        long conflicted =
                signals
                        .stream()
                        .filter(
                                AnnualLuckContextSignal
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
     * 18차 상태 변환
     * =================================================
     *
     * 하나의 길흉으로 축약하지 않는다.
     *
     * CONFLICTED
     * ->
     * 상충 근거 자체가 존재하므로 MIXED.
     *
     * SUPPORTED / QUALIFIED
     * +
     * WEAKENED
     * ->
     * 지원과 부담이 동시에 있으므로 MIXED.
     *
     * WEAKENED만 존재
     * ->
     * PRESSURE.
     *
     * SUPPORTED 존재
     * ->
     * SUPPORTIVE.
     *
     * QUALIFIED만 존재
     * ->
     * CONTEXTUAL.
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
                    "현재 세운의 용신·희신 Context가 생활 균형의 조정 방향을 지지하는 구조로 나타납니다.";

            case PRESSURE ->
                    "현재 세운의 용신·희신 Context에 생활 균형 조정축을 약화시키는 방향이 존재합니다.";

            case MIXED ->
                    "현재 세운의 용신·희신 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운에 용신·희신 관련 Context가 존재하지만 생활 균형 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    /*
     * =================================================
     * Temporal Evidence 조회
     * =================================================
     *
     * Natal Bundle
     * -> 0개 정상.
     *
     * Segment Bundle
     * -> 1개 정상.
     *
     * 동일 Segment에 2개 이상
     * -> Evidence 구성 오류.
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
                    "HealthAnnualContextRule에는 최대 하나의 "
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