package com.dajeom.calculation;

import com.dajeom.domain.DayMasterStrengthStatus;
import com.dajeom.domain.LifePeriodKey;
import com.dajeom.domain.NatalMyeongriProfile;
import com.dajeom.domain.PeriodLifeClaim;
import com.dajeom.domain.PeriodLifeClaimCode;
import com.dajeom.domain.PeriodLifeSignal;
import com.dajeom.domain.PeriodLifeSignalCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 대운 / 세운 / 월운의 구조 Signal을
 * "지나온 흐름" 해석용 PeriodLifeClaim으로 변환한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Calculator는 실제 과거 사건을 추정하지 않는다.
 *
 * 예:
 *
 * AUTHORITY_PRESSURE_INCREASED
 *
 * ↓
 *
 * RESPONSIBILITY_AND_EVALUATION_PRESSURE_INCREASED
 *
 * 까지만 생성한다.
 *
 * 다음처럼 바꾸지 않는다.
 *
 * "그때 취업했다"
 * "상사와 갈등했다"
 * "시험을 봤다"
 *
 * =================================================
 *
 * 원국 상태와 기간 Signal을 함께 보기 때문에
 *
 * 같은 SUPPORT_INCREASED라도:
 *
 * 身弱한 원국
 *
 * → 지원·회복을 활용하기 쉬운 흐름
 *
 * 印이 과한 원국
 *
 * → 준비·안정 쪽이 더 강해질 수 있는 흐름
 *
 * 처럼 다르게 번역할 수 있다.
 */
@Component
public class PastFlowLifeClaimCalculator {

    public List<PeriodLifeClaim> calculate(

            NatalMyeongriProfile profile,

            List<PeriodLifeSignal> signals

    ) {

        Objects.requireNonNull(
                profile,
                "NatalMyeongriProfile은 필수입니다."
        );

        Objects.requireNonNull(
                signals,
                "PeriodLifeSignal 목록은 필수입니다."
        );

        if (signals.isEmpty()) {

            return List.of();
        }

        /*
         * 기간별로 먼저 묶는다.
         */
        Map<
                LifePeriodKey,
                List<PeriodLifeSignal>
                > grouped =
                new LinkedHashMap<>();

        signals
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        signal ->
                                                signal
                                                        .period()
                                                        .startInclusive()
                                )
                )
                .forEach(
                        signal -> {

                            Objects.requireNonNull(
                                    signal,
                                    "PeriodLifeSignal 목록에 null이 포함될 수 없습니다."
                            );

                            grouped
                                    .computeIfAbsent(
                                            signal.period(),
                                            ignored ->
                                                    new ArrayList<>()
                                    )
                                    .add(signal);
                        }
                );

        List<PeriodLifeClaim> result =
                new ArrayList<>();

        for (
                Map.Entry<
                        LifePeriodKey,
                        List<PeriodLifeSignal>
                        > entry
                : grouped.entrySet()
        ) {

            PeriodClaimCollector collector =
                    new PeriodClaimCollector(
                            entry.getKey()
                    );

            for (
                    PeriodLifeSignal signal
                    : entry.getValue()
            ) {

                applySignal(
                        profile,
                        signal,
                        collector
                );
            }

            result.addAll(
                    collector.toClaims()
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Signal → Claim
     * =================================================
     */

    private void applySignal(

            NatalMyeongriProfile profile,

            PeriodLifeSignal signal,

            PeriodClaimCollector claims

    ) {

        switch (signal.code()) {

            case SUPPORT_INCREASED ->
                    applySupportIncrease(
                            profile,
                            claims
                    );

            case SUPPORT_DECREASED ->
                    applySupportDecrease(
                            profile,
                            claims
                    );

            case OUTPUT_INCREASED ->
                    applyOutputIncrease(
                            profile,
                            claims
                    );

            case WEALTH_PRESSURE_INCREASED ->
                    applyWealthIncrease(
                            profile,
                            claims
                    );

            case AUTHORITY_PRESSURE_INCREASED ->

                    claims.add(
                            PeriodLifeClaimCode
                                    .RESPONSIBILITY_AND_EVALUATION_PRESSURE_INCREASED,

                            PeriodLifeSignalCode
                                    .AUTHORITY_PRESSURE_INCREASED
                    );

            case PEER_COMPETITION_INCREASED ->

                    claims.add(
                            PeriodLifeClaimCode
                                    .AUTONOMY_AND_PEER_BOUNDARIES_BECAME_MORE_IMPORTANT,

                            PeriodLifeSignalCode
                                    .PEER_COMPETITION_INCREASED
                    );

            case STRUCTURAL_FRICTION_ACTIVATED ->

                    claims.add(
                            PeriodLifeClaimCode
                                    .EXISTING_STRUCTURE_REQUIRED_REORGANIZATION,

                            PeriodLifeSignalCode
                                    .STRUCTURAL_FRICTION_ACTIVATED
                    );

            case STRUCTURAL_FRICTION_RESOLVED ->

                    claims.add(
                            PeriodLifeClaimCode
                                    .REORGANIZATION_ROUTE_BECAME_AVAILABLE,

                            PeriodLifeSignalCode
                                    .STRUCTURAL_FRICTION_RESOLVED
                    );

            case BINDING_ACTIVATED ->

                    claims.add(
                            PeriodLifeClaimCode
                                    .STRONG_BINDING_REDIRECTED_PRIORITIES,

                            PeriodLifeSignalCode
                                    .BINDING_ACTIVATED
                    );

            case NATAL_DIRECTION_REINFORCED ->

                    claims.add(
                            PeriodLifeClaimCode
                                    .EXISTING_DIRECTION_WAS_REINFORCED,

                            PeriodLifeSignalCode
                                    .NATAL_DIRECTION_REINFORCED
                    );

            case NATAL_DIRECTION_CHALLENGED ->

                    claims.add(
                            PeriodLifeClaimCode
                                    .EXISTING_DIRECTION_REQUIRED_ADJUSTMENT,

                            PeriodLifeSignalCode
                                    .NATAL_DIRECTION_CHALLENGED
                    );
        }
    }

    /*
     * =================================================
     * 印 증가
     * =================================================
     */

    private void applySupportIncrease(

            NatalMyeongriProfile profile,

            PeriodClaimCollector claims

    ) {

        /*
         * 원국 자체가 印 과다.
         *
         * 더 들어온 지원을 무조건 좋은 것으로
         * 해석하지 않는다.
         */
        if (
                profile
                        .excessiveResourceContext()
        ) {

            claims.add(
                    PeriodLifeClaimCode
                            .SUPPORT_AND_PREPARATION_BECAME_MORE_DOMINANT,

                    PeriodLifeSignalCode
                            .SUPPORT_INCREASED
            );

            return;
        }

        /*
         * 일반 신약 쪽.
         */
        if (
                weakSide(
                        profile
                )
        ) {

            claims.add(
                    PeriodLifeClaimCode
                            .SUPPORT_AND_RECOVERY_BECAME_MORE_AVAILABLE,

                    PeriodLifeSignalCode
                            .SUPPORT_INCREASED
            );

            return;
        }

        /*
         * 강한 쪽에서는
         * 지원 증가를 무조건 이득이라고 보지 않는다.
         */
        if (
                strongSide(
                        profile
                )
        ) {

            claims.add(
                    PeriodLifeClaimCode
                            .SUPPORT_AND_PREPARATION_BECAME_MORE_DOMINANT,

                    PeriodLifeSignalCode
                            .SUPPORT_INCREASED
            );

            return;
        }

        /*
         * 특수 구조 등으로 일반 강약 판단이
         * 직접 적용되지 않는 경우에는
         * 가장 좁은 의미로만 남긴다.
         */
        claims.add(
                PeriodLifeClaimCode
                        .SUPPORT_AND_PREPARATION_BECAME_MORE_DOMINANT,

                PeriodLifeSignalCode
                        .SUPPORT_INCREASED
        );
    }

    /*
     * =================================================
     * 印 감소
     * =================================================
     */

    private void applySupportDecrease(

            NatalMyeongriProfile profile,

            PeriodClaimCollector claims

    ) {

        /*
         * 특히 약한 원국에서는
         * 지원 자원이 줄어드는 시기에
         * 자기 페이스 관리가 중요해진다.
         */
        claims.add(
                PeriodLifeClaimCode
                        .SELF_PACING_AND_SUPPORT_ALLOCATION_BECAME_MORE_IMPORTANT,

                PeriodLifeSignalCode
                        .SUPPORT_DECREASED
        );
    }

    /*
     * =================================================
     * 食傷 증가
     * =================================================
     */

    private void applyOutputIncrease(

            NatalMyeongriProfile profile,

            PeriodClaimCollector claims

    ) {

        /*
         * 身弱 또는 원국에서 이미
         * 食傷 설기가 부담으로 확정된 경우.
         */
        if (
                weakSide(profile)

                        ||

                        profile
                                .outputDrainsWeakStructure()
        ) {

            claims.add(
                    PeriodLifeClaimCode
                            .OUTPUT_DEMAND_REQUIRED_MORE_RECOVERY,

                    PeriodLifeSignalCode
                            .OUTPUT_INCREASED
            );

            return;
        }

        /*
         * 그 외에는 활동·표현·결과 생산이
         * 활발해지는 흐름으로 좁게 해석.
         */
        claims.add(
                PeriodLifeClaimCode
                        .EXPRESSION_AND_RESULT_PRODUCTION_BECAME_MORE_ACTIVE,

                PeriodLifeSignalCode
                        .OUTPUT_INCREASED
        );
    }

    /*
     * =================================================
     * 財 증가
     * =================================================
     */

    private void applyWealthIncrease(

            NatalMyeongriProfile profile,

            PeriodClaimCollector claims

    ) {

        /*
         * 印多用財 구조라면
         * 현실적인 요구가 오히려
         * 과한 준비를 행동으로 전환하는 역할.
         */
        if (
                profile
                        .wealthModeratesExcessResource()
        ) {

            claims.add(
                    PeriodLifeClaimCode
                            .PRACTICAL_DEMANDS_PUSHED_ACTION_FORWARD,

                    PeriodLifeSignalCode
                            .WEALTH_PRESSURE_INCREASED
            );

            return;
        }

        /*
         * 財印 직접 충돌이 있는 원국.
         */
        if (
                profile
                        .wealthResourceDirectConflictActive()
        ) {

            claims.add(
                    PeriodLifeClaimCode
                            .PRACTICAL_PRESSURE_CHALLENGED_STABILITY,

                    PeriodLifeSignalCode
                            .WEALTH_PRESSURE_INCREASED
            );

            return;
        }

        /*
         * 일반적인 財 활성.
         */
        claims.add(
                PeriodLifeClaimCode
                        .PRACTICAL_RESULTS_AND_RESOURCE_DECISIONS_BECAME_MORE_CENTRAL,

                PeriodLifeSignalCode
                        .WEALTH_PRESSURE_INCREASED
        );
    }

    /*
     * =================================================
     * 일반 강약 helper
     * =================================================
     */

    private boolean weakSide(
            NatalMyeongriProfile profile
    ) {

        if (
                !profile
                        .generalStrengthUsed()
        ) {

            return false;
        }

        DayMasterStrengthStatus status =
                profile
                        .strengthSensitiveContext()
                        .strengthStatus();

        return status
                == DayMasterStrengthStatus.WEAK_LEANING

                ||

                status
                        == DayMasterStrengthStatus.CLEARLY_WEAK;
    }

    private boolean strongSide(
            NatalMyeongriProfile profile
    ) {

        if (
                !profile
                        .generalStrengthUsed()
        ) {

            return false;
        }

        DayMasterStrengthStatus status =
                profile
                        .strengthSensitiveContext()
                        .strengthStatus();

        return status
                == DayMasterStrengthStatus.STRONG_LEANING

                ||

                status
                        == DayMasterStrengthStatus.CLEARLY_STRONG;
    }

    /*
     * =================================================
     * 기간별 Claim Collector
     * =================================================
     */

    private static final class PeriodClaimCollector {

        private final LifePeriodKey period;

        private final Map<
                PeriodLifeClaimCode,
                EnumSet<PeriodLifeSignalCode>
                > basesByCode =
                new EnumMap<>(
                        PeriodLifeClaimCode.class
                );

        private PeriodClaimCollector(
                LifePeriodKey period
        ) {

            this.period =
                    Objects.requireNonNull(
                            period,
                            "LifePeriodKey는 필수입니다."
                    );
        }

        private void add(

                PeriodLifeClaimCode code,

                PeriodLifeSignalCode... bases

        ) {

            Objects.requireNonNull(
                    code,
                    "PeriodLifeClaimCode는 필수입니다."
            );

            if (
                    bases == null
                            ||
                            bases.length == 0
            ) {

                throw new IllegalArgumentException(
                        "PeriodLifeClaim에는 최소 하나의 basis가 필요합니다."
                );
            }

            EnumSet<PeriodLifeSignalCode> merged =
                    basesByCode
                            .computeIfAbsent(
                                    code,
                                    ignored ->
                                            EnumSet.noneOf(
                                                    PeriodLifeSignalCode.class
                                            )
                            );

            for (
                    PeriodLifeSignalCode basis
                    : bases
            ) {

                merged.add(
                        Objects.requireNonNull(
                                basis,
                                "PeriodLifeSignalCode는 null일 수 없습니다."
                        )
                );
            }
        }

        private List<PeriodLifeClaim> toClaims() {

            List<PeriodLifeClaim> result =
                    new ArrayList<>();

            for (
                    PeriodLifeClaimCode code
                    : PeriodLifeClaimCode.values()
            ) {

                Set<PeriodLifeSignalCode> bases =
                        basesByCode.get(code);

                if (
                        bases == null
                                ||
                                bases.isEmpty()
                ) {

                    continue;
                }

                result.add(
                        new PeriodLifeClaim(
                                period,
                                code,
                                EnumSet.copyOf(bases)
                        )
                );
            }

            return List.copyOf(result);
        }
    }
}