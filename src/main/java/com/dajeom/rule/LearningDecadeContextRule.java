package com.dajeom.rule;

import com.dajeom.model.DecadeLuckContextSignal;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
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
 * 학습·성장 영역의 대운 Context Rule.
 *
 * 핵심 경계:
 *
 * - 13-6가 계산한 DecadeLuckContextSignal 하나를 하나의 LifeDomainRuleMatch로 보존한다.
 * - 서로 다른 Signal의 상태·개수·십성 유형을 합산하여 새 상태를 만들지 않는다.
 * - 같은 대운에 여러 독립 Signal이 공존한다는 이유만으로 MIXED 또는 복합 의미를 만들지 않는다.
 * - upstream Java가 실제 계산한 관계·변환·격국·용신 Context는 각 Signal 내부 의미로 그대로 유지한다.
 * - 이 Rule에서 독립 Signal끼리 인과·강화·약화·상쇄·순서·새 종합 의미로 연결하지 않는다.
 * - 시험 합격 / 자격 취득 / 성과 발생 / 학업 실패 같은 실제 사건 결과를 확정하지 않는다.
 */
@Component
public class LearningDecadeContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "LEARNING_DECADE_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType.DECADE_CONTEXT
            );

    private static final Set<StructureTenGodType>
            RELEVANT_TEN_GOD_TYPES =
            Set.of(
                    StructureTenGodType.DIRECT_RESOURCE,
                    StructureTenGodType.INDIRECT_RESOURCE,
                    StructureTenGodType.EATING_GOD,
                    StructureTenGodType.HURTING_OFFICER
            );

    @Override
    public LifeDomain domain() {
        return LifeDomain.LEARNING_GROWTH;
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
                "LearningDecadeContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (bundle.domain() != LifeDomain.LEARNING_GROWTH) {
            throw new IllegalArgumentException(
                    "LearningDecadeContextRule은 LEARNING_GROWTH Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>> referenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType.DECADE_CONTEXT
                );

        if (referenceOptional.isEmpty()) {
            return List.of();
        }

        LifeDomainEvidenceReference<?> reference =
                referenceOptional.orElseThrow();

        DecadeLuckPeriodContextAnalysis context =
                reference.requireSource(
                        DecadeLuckPeriodContextAnalysis.class
                );

        Set<DecadeLuckContextSignal> seen =
                Collections.newSetFromMap(
                        new IdentityHashMap<>()
                );

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        for (DecadeLuckContextSignal signal : context.signals()) {
            Objects.requireNonNull(
                    signal,
                    "DecadeLuckPeriodContextAnalysis의 Signal은 null일 수 없습니다."
            );

            if (!relevant(signal) || !seen.add(signal)) {
                continue;
            }

            List<StructureTenGodType> matchedTypes =
                    signal.tenGodTypes()
                            .stream()
                            .filter(RELEVANT_TEN_GOD_TYPES::contains)
                            .toList();

            LifeDomainSignalStatus status =
                    resolveStatus(signal);

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "13-6 대운 Context의 Sequence는 "
                            + context.sequenceNumber()
                            + "입니다."
            );

            reasons.add(
                    "현재 대운 간지는 "
                            + context.luckPillar()
                            + "입니다."
            );

            reasons.add(
                    "DecadeLuckContextSignal code는 "
                            + signal.code()
                            + "입니다."
            );

            reasons.add(
                    "DecadeLuckContextSignal axis는 "
                            + signal.axis().name()
                            + "입니다."
            );

            reasons.add(
                    "DecadeLuckContextSignal 원래 status는 "
                            + signal.status().name()
                            + "입니다."
            );

            reasons.add(
                    "실제 태깅된 학습·성장 관련 십성 유형은 "
                            + matchedTypes
                            + "입니다."
            );

            reasons.add(
                    "DecadeLuckContextSignal summary: "
                            + signal.summary()
            );

            for (String signalReason : signal.reasons()) {
                reasons.add(
                        "DecadeLuckContextSignal ["
                                + signal.code()
                                + "] 근거: "
                                + signalReason
                );
            }

            reasons.add(
                    "이 Match는 하나의 DecadeLuckContextSignal만 보존하며 "
                            + "다른 독립 Signal과 합산하여 새로운 상태를 생성하지 않습니다."
            );

            reasons.add(
                    "upstream에서 이미 계산된 관계·변환·격국·용신 Context는 "
                            + "현재 Signal 범위 안에서만 유지하고, "
                            + "다른 Signal과의 추가 관계형 풀이를 생성하지 않습니다."
            );

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.LEARNING_GROWTH,
                            RULE_CODE,
                            status,
                            summaryOf(signal),
                            List.of(reference),
                            List.copyOf(reasons)
                    )
            );
        }

        return List.copyOf(matches);
    }

    private boolean relevant(
            DecadeLuckContextSignal signal
    ) {
        for (StructureTenGodType tenGodType : RELEVANT_TEN_GOD_TYPES) {
            if (signal.involves(tenGodType)) {
                return true;
            }
        }

        return false;
    }

    private LifeDomainSignalStatus resolveStatus(
            DecadeLuckContextSignal signal
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
                "지원하지 않는 DecadeLuckContextSignal 상태입니다: "
                        + signal.status()
        );
    }

    private String summaryOf(
            DecadeLuckContextSignal signal
    ) {
        return "현재 대운의 학습·성장 관련 "
                + signal.axis().name()
                + " Signal ["
                + signal.code()
                + "]을 독립 Context로 보존합니다: "
                + signal.summary();
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
                    "LearningDecadeContextRule에는 최대 하나의 "
                            + sourceType
                            + " Evidence가 허용됩니다. actual="
                            + evidences.size()
            );
        }

        return Optional.of(evidences.get(0));
    }
}