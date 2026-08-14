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
import java.util.Collections;
import java.util.IdentityHashMap;
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
 * 재성 관련 Annual Context
 *
 * DIRECT_WEALTH
 * INDIRECT_WEALTH
 *
 * 3.
 * 관살 관련 Annual Context
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
 * 재성과 관살 모두
 * 관계 영역에서 참조 가능한
 * 구조적 Context로 보존한다.
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
         *
         * DAY 궁위에 세운 Context가
         * 직접 관여하는 경우.
         *
         * 배우자 사건으로 변환하지 않는다.
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
                    "DAY 궁위를 배우자 자체로 고정하지 않고 관계 영역의 주요 위치 Context로만 사용합니다."
            );

            reasons.add(
                    "DAY 궁위 세운 작동을 연애 성사·결혼·이별 같은 실제 관계 사건으로 직접 환산하지 않습니다."
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
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 2.
         * 재성 + 관살 Annual Context
         * =================================================
         *
         * 성별 정보가 없으므로
         * 어느 Family도 배우자성으로 선택하지 않는다.
         *
         * 네 종류 Signal을 모두 보존한다.
         *
         * 동일 Signal이 둘 이상의 조회 결과에
         * 포함될 가능성에 대비하여
         * Object Identity 기준으로 중복 제거.
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

        List<AnnualLuckContextSignal>
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
                            annualContext
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
                    "Object Identity 중복 제거 후 관계영역 십성 Context Signal은 "
                            + relationshipTenGodSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    statusSummary(
                            counts
                    )
            );

            reasons.add(
                    "현재 18차 Evidence에는 성별 정보가 없으므로 재성 또는 관살 중 하나를 배우자성으로 임의 선택하지 않습니다."
            );

            reasons.add(
                    "재성·관살 관련 세운 Context를 배우자 등장·연애·결혼·관계 단절 같은 실제 사건으로 직접 환산하지 않습니다."
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
                                    annualReference
                            ),
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 3.
         * DAY Palace + 관계 십성 동시 관여
         * =================================================
         *
         * 서로 다른 축이 동시에 존재하는 것을
         * 별도 Context로 남긴다.
         *
         * 둘이 동시에 있다고 해서
         * 관계 사건 가능성이 높다고 해석하지 않는다.
         */

        if (
                !dayPalaceSignals.isEmpty()
                        && !relationshipTenGodSignals
                        .isEmpty()
        ) {

            List<AnnualLuckContextSignal>
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
                            annualContext
                    );

            reasons.add(
                    "DAY 궁위 Context와 재성·관살 Context가 같은 세운에서 함께 존재합니다."
            );

            reasons.add(
                    "중복 제거 후 두 축을 합친 Annual Context Signal은 "
                            + combinedSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    statusSummary(
                            counts
                    )
            );

            reasons.add(
                    "궁위 축과 십성 축이 동시에 활성되는 사실을 별도로 보존하되 이를 연애·결혼 사건으로 승격하지 않습니다."
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
                                    annualReference
                            ),
                            reasons
                    )
            );
        }

        return List.copyOf(
                matches
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
     * 18차 상태 변환
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
                    "현재 세운의 DAY 궁위 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운에서 DAY 궁위 관련 Context가 존재하지만 관계 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfTenGod(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 세운에서 재성·관살 관련 Context가 관계 영역을 지지하는 방향으로 나타납니다.";

            case PRESSURE ->
                    "현재 세운에서 재성·관살 관련 Context에 관계영역 부담 방향이 존재합니다.";

            case MIXED ->
                    "현재 세운의 재성·관살 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운에서 재성·관살 관련 Context가 존재하지만 관계 방향을 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfCombined(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 세운에서 DAY 궁위와 관계 십성 Context가 함께 지지 방향을 형성합니다.";

            case PRESSURE ->
                    "현재 세운에서 DAY 궁위와 관계 십성 Context가 함께 부담 방향을 포함합니다.";

            case MIXED ->
                    "현재 세운에서 DAY 궁위와 관계 십성 축에 지원·약화·상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 세운에서 DAY 궁위와 관계 십성 축이 함께 관여하지만 단독 관계 결과로 확정할 수준은 아닙니다.";
        };
    }

    /*
     * =================================================
     * Object Identity 중복 제거
     * =================================================
     */

    @SafeVarargs
    private final List<AnnualLuckContextSignal>
    mergeIdentityUnique(
            List<AnnualLuckContextSignal>... signalLists
    ) {

        Set<AnnualLuckContextSignal> unique =
                Collections
                        .newSetFromMap(
                                new IdentityHashMap<>()
                        );

        for (
                List<AnnualLuckContextSignal> signals
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