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
 * 건강·생활 영역의 세운 Context Rule.
 *
 * 14-6의 USEFUL_GOD Axis Signal을 하나씩 독립 Match로 보존한다.
 *
 * 여러 USEFUL_GOD Signal을 다시 집계하여
 * 하나의 MIXED / SUPPORTIVE / PRESSURE 상태로 만들지 않는다.
 *
 * upstream Java가 계산한 용신·희신 Context는
 * 각 Signal 범위 안에서만 유지한다.
 *
 * 실제 건강 상태·질병·회복·악화 사건으로 환산하지 않는다.
 */
@Component
public class HealthAnnualContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "HEALTH_ANNUAL_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType.ANNUAL_CONTEXT
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
    public Set<LifeDomainEvidenceSourceType> supportedSources() {
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

        if (bundle.domain() != LifeDomain.HEALTH_LIFESTYLE) {
            throw new IllegalArgumentException(
                    "HealthAnnualContextRule은 HEALTH_LIFESTYLE Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>> referenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType.ANNUAL_CONTEXT
                );

        if (referenceOptional.isEmpty()) {
            return List.of();
        }

        LifeDomainEvidenceReference<?> reference =
                referenceOptional.orElseThrow();

        AnnualLuckContextAnalysis context =
                reference.requireSource(
                        AnnualLuckContextAnalysis.class
                );

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        for (AnnualLuckContextSignal signal : context.annualSignals()) {
            Objects.requireNonNull(
                    signal,
                    "AnnualLuckContextAnalysis의 Signal은 null일 수 없습니다."
            );

            if (signal.axis() != AnnualLuckContextAxis.USEFUL_GOD) {
                continue;
            }

            LifeDomainSignalStatus status =
                    resolveStatus(signal);

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "14-6 AnnualLuckContextAnalysis의 현재 세운 연도는 "
                            + context.targetYear()
                            + "년입니다."
            );

            if (!context.decadeContextAvailable()) {
                reasons.add(
                        "대운 Segment Context를 사용할 수 없는 상태이지만 "
                                + "14차 정책에 따라 세운 자체 annualSignals는 그대로 보존합니다."
                );
            }

            if (context.decadeTimingUncertain()) {
                reasons.add(
                        "현재 세운 기간에 대운 교운시점 불확실성이 존재하므로 "
                                + "가능한 대운 Context 중 하나를 임의 확정하지 않습니다."
                );
            }

            reasons.add(
                    "AnnualLuckContextSignal code는 "
                            + signal.code()
                            + "입니다."
            );

            reasons.add(
                    "AnnualLuckContextSignal 원래 status는 "
                            + signal.status().name()
                            + "입니다."
            );

            reasons.add(
                    "AnnualLuckContextSignal summary: "
                            + signal.summary()
            );

            for (String signalReason : signal.reasons()) {
                reasons.add(
                        "AnnualLuckContextSignal ["
                                + signal.code()
                                + "] 근거: "
                                + signalReason
                );
            }

            reasons.add(
                    "이 Match는 하나의 USEFUL_GOD Signal만 보존하며 "
                            + "다른 USEFUL_GOD Signal과 합산하여 새 상태를 만들지 않습니다."
            );

            reasons.add(
                    "용신·희신 Context는 생활 균형의 명리적 조정 방향이며 "
                            + "실제 건강 상태·질병·회복·악화를 의미하지 않습니다."
            );

            reasons.add(
                    "조후·명리적 병약·통관 구조는 원국 HEALTH_LIFESTYLE Evidence의 "
                            + "별도 계산 결과를 사용하며 이 Rule에서 새로 생성하지 않습니다."
            );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.HEALTH_LIFESTYLE,
                            RULE_CODE,
                            status,
                            "현재 세운 USEFUL_GOD Signal ["
                                    + signal.code()
                                    + "]을 독립 Context로 보존합니다: "
                                    + signal.summary(),
                            List.of(reference),
                            List.copyOf(reasons)
                    )
            );
        }

        return List.copyOf(matches);
    }

    private LifeDomainSignalStatus resolveStatus(
            AnnualLuckContextSignal signal
    ) {
        if (signal.conflicted()) {
            return LifeDomainSignalStatus.MIXED;
        }

        if (signal.weakened()) {
            return LifeDomainSignalStatus.PRESSURE;
        }

        if (signal.supported()) {
            return LifeDomainSignalStatus.SUPPORTIVE;
        }

        if (signal.qualified()) {
            return LifeDomainSignalStatus.CONTEXTUAL;
        }

        throw new IllegalStateException(
                "지원하지 않는 AnnualLuckContextSignal 상태입니다: "
                        + signal.status()
        );
    }

    private Optional<LifeDomainEvidenceReference<?>> optionalSingleEvidence(
            LifeDomainEvidenceBundle bundle,
            LifeDomainEvidenceSourceType sourceType
    ) {
        List<LifeDomainEvidenceReference<?>> evidences =
                bundle.evidencesOf(sourceType);

        if (evidences.isEmpty()) {
            return Optional.empty();
        }

        if (evidences.size() != 1) {
            throw new IllegalStateException(
                    "HealthAnnualContextRule에는 최대 하나의 "
                            + sourceType
                            + " Evidence가 허용됩니다. actual="
                            + evidences.size()
            );
        }

        return Optional.of(evidences.get(0));
    }
}