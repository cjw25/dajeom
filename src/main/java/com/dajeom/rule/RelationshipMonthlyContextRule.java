package com.dajeom.rule;

import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.MonthlyLuckContextAnalysis;
import com.dajeom.model.MonthlyLuckContextSignal;
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
 * 관계 영역의 월운 Context Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 15-7 MonthlyLuckContextAnalysis
 *
 * =================================================
 *
 * 관계 영역에서 읽는 월운 Context:
 *
 * 1.
 * DAY 궁위 관련 Signal
 *
 * 2.
 * 재성 관련 Signal
 *
 * - DIRECT_WEALTH
 * - INDIRECT_WEALTH
 *
 * 3.
 * 관살 관련 Signal
 *
 * - DIRECT_OFFICER
 * - SEVEN_KILLINGS
 *
 * 4.
 * DAY 궁위와 관계 십성의 동시 관여
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * DAY 궁위는 관계 해석에서 중요한 위치이지만
 * 배우자 자체 또는 실제 관계 사건을 의미하지 않는다.
 *
 * =================================================
 *
 * 현재 18차 Evidence에는 Gender가 없다.
 *
 * 따라서:
 *
 * 남성 -> 재성 = 배우자성
 * 여성 -> 관살 = 배우자성
 *
 * 같은 성별 기반 선택을 하지 않는다.
 *
 * 재성과 관살 모두 관계영역의
 * 구조적 Context로 보존한다.
 *
 * =================================================
 *
 * 시간층 경계:
 *
 * 반드시 MonthlyLuckContextAnalysis의
 *
 * signalsOf(...)
 *
 * API만 사용한다.
 *
 * 이 API는 monthlySignals만 조회한다.
 *
 * allSignals()는:
 *
 * monthlySignals
 * +
 * decadeContexts.segmentSignals
 *
 * 이므로 사용하지 않는다.
 *
 * 대운 Context는 별도의
 * DECADE_CONTEXT Rule이 담당한다.
 *
 * 세운 Context 역시 별도의
 * ANNUAL_CONTEXT Rule이 담당한다.
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
 * DAY 궁위 활성
 * -> 연애 시작
 *
 * 재성 월운
 * -> 배우자 등장
 *
 * 관살 월운
 * -> 결혼
 *
 * CONFLICTED
 * -> 이별
 *
 * 합
 * -> 관계 성립
 *
 * 충
 * -> 관계 단절
 *
 * 같은 실제 사건 확정.
 *
 * =================================================
 *
 * 최종 사용자 풀이에서는
 * DAY, 재성, 관살, SUPPORTED 등의
 * 내부 명리 용어를 그대로 보여주는 것이 아니라
 *
 * 관계의:
 *
 * - 가까워지는 방식
 * - 거리 조정
 * - 기대와 부담
 * - 관계에서의 긴장
 * - 관계 접점 증가
 * - 현실적인 관계 조언
 *
 * 등 생활언어로 변환할 수 있도록
 * deterministic 근거를 보존한다.
 */
@Component
public class RelationshipMonthlyContextRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "RELATIONSHIP_MONTHLY_CONTEXT";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .MONTHLY_CONTEXT
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
                "RelationshipMonthlyContextRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "RelationshipMonthlyContextRule은 RELATIONSHIP Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                monthlyReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .MONTHLY_CONTEXT
                );

        /*
         * =================================================
         * Natal Bundle
         * =================================================
         */

        if (
                monthlyReferenceOptional.isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                monthlyReference =
                monthlyReferenceOptional.orElseThrow();

        MonthlyLuckContextAnalysis monthlyContext =
                monthlyReference
                        .requireSource(
                                MonthlyLuckContextAnalysis.class
                        );

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * DAY 궁위 관련 월운 Context
         * =================================================
         *
         * signalsOf(PalacePosition.DAY)는
         * monthlySignals 중 DAY가 직접 태깅된
         * 모든 Signal을 읽는다.
         *
         * PALACE Axis로만 제한하지 않는다.
         *
         * 따라서:
         *
         * - PALACE
         * - NATAL_RELATION
         * - TRANSFORMATION
         * - THREE_LAYER_RELATION
         * - FOUR_LAYER_RELATION
         *
         * 등에서 DAY가 실제 관여했다면
         * 그 Signal도 보존될 수 있다.
         */

        List<MonthlyLuckContextSignal>
                dayPalaceSignals =
                monthlyContext
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
                            monthlyContext
                    );

            reasons.add(
                    "DAY 궁위에 관여하는 Monthly Context Signal은 "
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
                    "DAY 궁위는 관계영역의 중요한 위치 Context로 사용하지만 배우자 자체로 고정하지 않습니다."
            );

            reasons.add(
                    "DAY 궁위의 월운 작동을 연애 시작·결혼·이별·재결합 같은 실제 관계 사건으로 직접 환산하지 않습니다."
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
                                    monthlyReference
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
         * 재성 + 관살 관련 월운 Context
         * =================================================
         *
         * 현재 Evidence에는 Gender가 없으므로
         * 재성 또는 관살 중 하나를
         * 배우자성으로 선택하지 않는다.
         */

        List<MonthlyLuckContextSignal>
                directWealthSignals =
                monthlyContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_WEALTH
                        );

        List<MonthlyLuckContextSignal>
                indirectWealthSignals =
                monthlyContext
                        .signalsOf(
                                StructureTenGodType
                                        .INDIRECT_WEALTH
                        );

        List<MonthlyLuckContextSignal>
                directOfficerSignals =
                monthlyContext
                        .signalsOf(
                                StructureTenGodType
                                        .DIRECT_OFFICER
                        );

        List<MonthlyLuckContextSignal>
                sevenKillingsSignals =
                monthlyContext
                        .signalsOf(
                                StructureTenGodType
                                        .SEVEN_KILLINGS
                        );

        List<MonthlyLuckContextSignal>
                relationshipTenGodSignals =
                mergeIdentityUnique(
                        directWealthSignals,
                        indirectWealthSignals,
                        directOfficerSignals,
                        sevenKillingsSignals
                );

        if (
                !relationshipTenGodSignals.isEmpty()
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
                            monthlyContext
                    );

            reasons.add(
                    "정재 관련 Monthly Context Signal은 "
                            + directWealthSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "편재 관련 Monthly Context Signal은 "
                            + indirectWealthSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "정관 관련 Monthly Context Signal은 "
                            + directOfficerSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "칠살 관련 Monthly Context Signal은 "
                            + sevenKillingsSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "Object Identity 중복 제거 후 관계영역 십성 Signal은 "
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
                    "재성·관살 관련 월운 Context는 관계영역의 접점·기대·역할·부담을 해석하기 위한 근거이며 배우자 등장이나 결혼 여부를 직접 의미하지 않습니다."
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
                                    monthlyReference
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
         * DAY 궁위 + 관계 십성 동시 관여
         * =================================================
         *
         * 관계의 위치 축과
         * 재성·관살 기능축이
         * 같은 월운 안에서 함께 관여하는 사실을
         * 별도 Match로 보존한다.
         *
         * 동시 관여
         * !=
         * 실제 관계 사건 성립.
         */

        if (
                !dayPalaceSignals.isEmpty()
                        && !relationshipTenGodSignals.isEmpty()
        ) {

            List<MonthlyLuckContextSignal>
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
                            monthlyContext
                    );

            reasons.add(
                    "DAY 궁위 Context와 재성·관살 Context가 같은 월운에서 함께 존재합니다."
            );

            reasons.add(
                    "DAY 궁위 관련 Signal은 "
                            + dayPalaceSignals.size()
                            + "개입니다."
            );

            reasons.add(
                    "관계 십성 관련 Signal은 "
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
                    "관계 위치축과 관계 기능축의 동시 관여를 보존하지만 이를 실제 만남·연애·결혼·이별 확률로 변환하지 않습니다."
            );

            reasons.add(
                    "최종 사용자 풀이에서는 관계에 대한 관심·접점·거리조정·기대·부담이 함께 두드러지는 시기인지 설명하는 근거로 사용할 수 있습니다."
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
                                    monthlyReference
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
     * 월운 기본 근거
     * =================================================
     */

    private List<String> baseReasons(
            MonthlyLuckContextAnalysis monthlyContext
    ) {

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "15-7 월운 Context의 사주 연도는 "
                        + monthlyContext.sajuYear()
                        + "년입니다."
        );

        reasons.add(
                "현재 월운 monthOrder는 "
                        + monthlyContext.monthOrder()
                        + ", monthIndex는 "
                        + monthlyContext.monthIndex()
                        + "입니다."
        );

        reasons.add(
                "현재 월운 간지는 "
                        + monthlyContext.monthlyPillar()
                        + "입니다."
        );

        reasons.add(
                "상위 세운 간지는 "
                        + monthlyContext.annualPillar()
                        + "입니다."
        );

        if (
                !monthlyContext
                        .decadeContextAvailable()
        ) {

            reasons.add(
                    "현재 월운에서는 대운 Segment Context를 사용할 수 없지만 대운 Sequence와 무관하게 확정 가능한 monthlySignals는 그대로 보존합니다."
            );

            if (
                    monthlyContext
                            .decadeContextUnavailableReason()
                            != null

                            && !monthlyContext
                            .decadeContextUnavailableReason()
                            .isBlank()
            ) {

                reasons.add(
                        "월운의 대운 Context 사용 불가 사유: "
                                + monthlyContext
                                .decadeContextUnavailableReason()
                );
            }
        }

        if (
                monthlyContext
                        .decadeTimingUncertain()
        ) {

            reasons.add(
                    "현재 월운 안에 대운 교운시점 불확실성이 존재하지만 가능한 Monthly-Decade Context 중 하나를 이 Rule에서 임의 선택하지 않습니다."
            );
        }

        reasons.add(
                "이 Rule은 MonthlyLuckContextAnalysis.monthlySignals만 사용하고 decadeContexts.segmentSignals를 포함하는 allSignals()는 사용하지 않습니다."
        );

        reasons.add(
                "대운과 세운의 관계영역 Context는 각각 DECADE_CONTEXT와 ANNUAL_CONTEXT Rule에서 별도로 보존합니다."
        );

        return reasons;
    }

    /*
     * =================================================
     * 원 Signal deterministic 근거 보존
     * =================================================
     */

    private void appendSignalReasons(

            List<String> target,

            List<MonthlyLuckContextSignal> signals

    ) {

        for (
                MonthlyLuckContextSignal signal
                : signals
        ) {

            target.add(
                    "Monthly Signal ["
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
                        "Monthly Signal ["
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
            List<MonthlyLuckContextSignal> signals
    ) {

        long supported =
                signals
                        .stream()
                        .filter(
                                MonthlyLuckContextSignal
                                        ::supported
                        )
                        .count();

        long qualified =
                signals
                        .stream()
                        .filter(
                                MonthlyLuckContextSignal
                                        ::qualified
                        )
                        .count();

        long weakened =
                signals
                        .stream()
                        .filter(
                                MonthlyLuckContextSignal
                                        ::weakened
                        )
                        .count();

        long conflicted =
                signals
                        .stream()
                        .filter(
                                MonthlyLuckContextSignal
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
     * 15-7 -> 18차 상태 변환
     * =================================================
     *
     * 상태는 길흉 점수가 아니다.
     *
     * 여러 근거가 겹치는 경우
     * 한 방향으로 삭제하거나 평균내지 않는다.
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
     * 내부 Summary
     * =================================================
     *
     * 최종 사용자용 점사 문장이 아니다.
     *
     * 이후 AI Grounding에서
     * 생활언어로 변환하기 위한
     * 의미 요약이다.
     */

    private String summaryOfDayPalace(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 월운에서 가까운 관계의 위치 Context를 지지하는 근거가 존재합니다.";

            case PRESSURE ->
                    "현재 월운에서 가까운 관계의 위치 Context에 조정·부담 근거가 존재합니다.";

            case MIXED ->
                    "현재 월운에서 가까운 관계의 위치 Context에 지원과 약화 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 월운에서 가까운 관계의 위치 Context가 관여하지만 관계 결과를 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfTenGod(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 월운에서 관계의 기대·역할·현실적 접점에 지지 방향의 근거가 존재합니다.";

            case PRESSURE ->
                    "현재 월운에서 관계의 기대·역할·현실적 접점에 부담 또는 약화 근거가 존재합니다.";

            case MIXED ->
                    "현재 월운에서 관계의 기대·역할·현실적 접점에 지원과 부담 또는 상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 월운에서 관계의 기대·역할·현실적 접점이 두드러지지만 관계 결과를 단독으로 확정할 수준은 아닙니다.";
        };
    }

    private String summaryOfCombined(
            LifeDomainSignalStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 월운에서 가까운 관계의 위치와 관계 기능이 함께 지지되는 근거가 존재합니다.";

            case PRESSURE ->
                    "현재 월운에서 가까운 관계의 위치와 관계 기능에 함께 조정·부담 근거가 존재합니다.";

            case MIXED ->
                    "현재 월운에서 가까운 관계의 위치와 관계 기능에 지원·약화·상충 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 월운에서 가까운 관계의 위치와 관계 기능이 함께 관여하지만 실제 관계 사건을 확정할 수준은 아닙니다.";
        };
    }

    /*
     * =================================================
     * Object Identity 중복 제거
     * =================================================
     */

    @SafeVarargs
    private final List<MonthlyLuckContextSignal>
    mergeIdentityUnique(
            List<MonthlyLuckContextSignal>... signalLists
    ) {

        Set<MonthlyLuckContextSignal> unique =
                Collections
                        .newSetFromMap(
                                new IdentityHashMap<>()
                        );

        for (
                List<MonthlyLuckContextSignal> signals
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
     * Natal Bundle.
     *
     * 1개:
     * 현재 Timeline Segment의 Monthly Context.
     *
     * 2개 이상:
     * 동일 Segment Bundle 구성 오류.
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
                    "RelationshipMonthlyContextRule에는 최대 하나의 "
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