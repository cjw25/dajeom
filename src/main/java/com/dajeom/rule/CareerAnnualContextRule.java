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
 * 직업·사회 영역의 세운 Context Rule.
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
 * DIRECT_OFFICER
 * SEVEN_KILLINGS
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 14차에서 이미 계산된:
 *
 * - 세운 십성
 * - 원국 × 세운
 * - 격국
 * - 용신
 * - 궁위
 * - 관계
 * - 합화
 * - 대운 사용 가능 시 대운 Segment Context
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
 * 지원 계열과 약화 계열이 동시에 존재
 * -> MIXED
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * 정관 세운
 * -> 취업한다
 *
 * 관살 지원
 * -> 승진한다
 *
 * 관살 약화
 * -> 퇴사한다
 *
 * 관살 충돌
 * -> 직장 문제가 생긴다
 *
 * 같은 실제 사건 결과 확정.
 *
 * =================================================
 *
 * 원국 Bundle에는 ANNUAL_CONTEXT가 없으므로
 * 아무 Match도 생성하지 않는다.
 *
 * Timeline Segment Bundle에서
 * ANNUAL_CONTEXT가 실제 존재할 때만 작동한다.
 */
@Component
public class CareerAnnualContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "CAREER_ANNUAL_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .ANNUAL_CONTEXT
            );

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

    @Override
    public List<LifeDomainRuleMatch> evaluate(
            LifeDomainEvidenceBundle bundle
    ) {

        Objects.requireNonNull(
                bundle,
                "CareerAnnualContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerAnnualContextRule은 CAREER Bundle만 평가할 수 있습니다."
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
         * 원국 Bundle에는 ANNUAL_CONTEXT가 없다.
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
         * 관살 관련 Annual Signal
         * =================================================
         *
         * 정관 / 칠살 조회 결과가
         * 같은 Signal 객체를 동시에 포함할 가능성에
         * 대비하여 Object Identity 기준으로
         * 중복 제거한다.
         */

        List<AnnualLuckContextSignal>
                directOfficerSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_OFFICER
                        );

        List<AnnualLuckContextSignal>
                sevenKillingsSignals =
                annualContext
                        .signalsOf(
                                StructureTenGodType
                                        .SEVEN_KILLINGS
                        );

        Set<AnnualLuckContextSignal>
                uniqueSignals =
                Collections
                        .newSetFromMap(
                                new IdentityHashMap<>()
                        );

        uniqueSignals.addAll(
                directOfficerSignals
        );

        uniqueSignals.addAll(
                sevenKillingsSignals
        );

        if (
                uniqueSignals.isEmpty()
        ) {

            return List.of();
        }

        List<AnnualLuckContextSignal>
                officerSignals =
                List.copyOf(
                        uniqueSignals
                );

        /*
         * =================================================
         * 14차 상태 집계
         * =================================================
         *
         * 18차에서 Context의 명리 상태를
         * 새로 판정하지 않는다.
         *
         * AnnualLuckContextSignal.status()가
         * 이미 결정한 결과를 집계한다.
         */

        long supportedCount =
                officerSignals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .supported()
                        )
                        .count();

        long qualifiedCount =
                officerSignals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .qualified()
                        )
                        .count();

        long weakenedCount =
                officerSignals
                        .stream()
                        .filter(
                                signal ->
                                        signal
                                                .status()
                                                .weakened()
                        )
                        .count();

        long conflictedCount =
                officerSignals
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
         * 18차 생활영역 상태 변환
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
                "정관 관련 Annual Context Signal은 "
                        + directOfficerSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "칠살 관련 Annual Context Signal은 "
                        + sevenKillingsSignals.size()
                        + "개입니다."
        );

        reasons.add(
                "Object Identity 중복 제거 후 관살 관련 Signal은 "
                        + officerSignals.size()
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
                "14차가 판정한 관살 관련 Context 상태를 18차 직업·사회 상태로 변환할 뿐, 세운 십성·격국·용신·관계·합화를 다시 계산하지 않습니다."
        );

        reasons.add(
                "정관·칠살 관련 세운 Context를 취업·승진·퇴사·이직·직장 갈등 같은 실제 사건으로 직접 환산하지 않습니다."
        );

        /*
         * =================================================
         * 대운 Context 사용 가능 여부
         * =================================================
         *
         * 대운 Context 사용 불가여도
         * annualSignals는 유효하다.
         */

        if (
                !annualContext
                        .decadeContextAvailable()
        ) {

            reasons.add(
                    "현재 세운은 대운 Segment Context를 사용할 수 없지만, 14차 정책에 따라 대운과 무관하게 확정 가능한 annualSignals는 그대로 보존하여 사용합니다."
            );
        }

        /*
         * =================================================
         * 교운 불확실성
         * =================================================
         *
         * 불확실 구간에서 가능한 대운을
         * 하나로 강제 선택하지 않는다.
         */

        if (
                annualContext
                        .decadeTimingUncertain()
        ) {

            reasons.add(
                    "현재 세운 기간에 대운 교운시점 불확실성이 존재하므로 가능한 대운 Context 중 하나를 확정값으로 임의 선택하지 않습니다."
            );
        }

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.CAREER,
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
     * AnnualLuckContextSignal의 기존 상태를
     * 생활영역 표현으로만 정리한다.
     *
     * =================================================
     *
     * CONFLICTED
     *
     * 자체가 상충 Evidence를 뜻하므로
     * 하나라도 존재하면 MIXED.
     *
     * =================================================
     *
     * SUPPORTED / QUALIFIED
     * +
     * WEAKENED
     *
     * 가 함께 존재해도 MIXED.
     *
     * =================================================
     *
     * WEAKENED만 존재
     * -> PRESSURE
     *
     * SUPPORTED가 존재하고
     * 제한 Evidence가 없음
     * -> SUPPORTIVE
     *
     * QUALIFIED만 존재
     * -> CONTEXTUAL
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
                    "현재 세운에서 관살 관련 Context가 직업·사회 역할 기능을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 세운에서 관살 관련 Context에 역할·책임의 약화 또는 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 세운의 관살 관련 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운에서 관살 관련 Context가 존재하지만 직업·사회 방향을 단독으로 확정할 수준은 아닙니다.";
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
     * 동일 Segment Bundle에
     * ANNUAL_CONTEXT가 2개 이상이면
     * Evidence 구성 오류.
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
                    "CareerAnnualContextRule에는 최대 하나의 "
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