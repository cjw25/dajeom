package com.dajeom.rule;

import com.dajeom.model.ClimateAdjustmentNeedAnalysis;
import com.dajeom.model.ClimateEnvironmentAnalysis;
import com.dajeom.model.ClimateEnvironmentEvidence;
import com.dajeom.type.ClimateAdjustmentNeedLevel;
import com.dajeom.type.ClimateCorrectionTarget;
import com.dajeom.type.ClimateElementCandidateRole;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-3.
 *
 * 조후 필요성을 실제 五行 후보로 변환한다.
 *
 * =================================================
 *
 * 직접 조후 작용
 *
 * FIRE
 * -> 온난
 * -> 건조
 *
 * WATER
 * -> 냉각
 * -> 습윤
 *
 * =================================================
 *
 * 따라서:
 *
 * 寒濕
 * -> FIRE가 두 축을 동시에 보정 가능.
 *
 * 熱燥
 * -> WATER가 두 축을 동시에 보정 가능.
 *
 * 寒燥
 * -> FIRE와 WATER의 요구가 경쟁.
 *
 * 熱濕
 * -> WATER와 FIRE의 요구가 경쟁.
 *
 * =================================================
 *
 * WOOD / METAL
 *
 * 직접적인 한난조습 해결 오행으로
 * 무조건 승격하지 않는다.
 *
 * 다만 직접 후보가 부족할 때:
 *
 * WOOD -> FIRE의 생조 보조
 * METAL -> WATER의 생조 보조
 *
 * 로 제한적으로 SECONDARY 후보가 될 수 있다.
 *
 * 寒燥에서는 木을
 * 水 -> 木 -> 火 연결 가능성을 가진
 * 중재 후보로 보존할 수 있다.
 *
 * =================================================
 *
 * EARTH
 *
 * 土의 조습 성질은
 * 辰丑/未戌 및 다른 구조에 따라 달라지므로
 * 6-3에서는 일반화된 직접 조후 후보로
 * 자동 선택하지 않는다.
 *
 * 병약·통관 단계에서 필요한 경우
 * 별도로 후보가 올라올 수 있다.
 *
 * =================================================
 *
 * 모든 규칙은 조후 후보 선정을 위한
 * 다점 내부 heuristic이다.
 *
 * 최종 용신 결정은 아니다.
 */
@Component
public class ClimateElementCandidateRule {

    public ClimateElementDecision evaluate(

            FiveElement element,

            ClimateAdjustmentNeedAnalysis need

    ) {

        Objects.requireNonNull(
                element,
                "평가할 오행이 필요합니다."
        );

        Objects.requireNonNull(
                need,
                "조후 필요성 분석이 필요합니다."
        );

        int share =
                shareOf(
                        element,
                        need
                );

        ClimateElementPresenceLevel presence =
                ClimateElementPresenceLevel
                        .fromShareBasisPoints(
                                share
                        );

        /*
         * 조후 조정 자체가 필요하지 않으면
         * 모든 오행은 NOT_NEEDED.
         */
        if (
                !need.adjustmentRequired()
        ) {

            return ClimateElementDecision
                    .notNeeded(
                            element,
                            share,
                            presence
                    );
        }

        ClimateEnvironmentAnalysis climate =
                need
                        .climateEnvironmentAnalysis();

        List<ClimateCorrectionTarget> targets =
                new ArrayList<>();

        boolean directTemperatureCorrection =
                false;

        boolean directMoistureCorrection =
                false;

        boolean temperatureConflict =
                false;

        boolean moistureConflict =
                false;

        /*
         * ================================================
         * 한난축
         * ================================================
         */
        if (
                need.temperatureAdjustmentRequired()
        ) {

            if (
                    climate.cold()
            ) {

                if (
                        element == FiveElement.FIRE
                ) {

                    targets.add(
                            ClimateCorrectionTarget
                                    .WARMING
                    );

                    directTemperatureCorrection =
                            true;
                }

                /*
                 * 寒한데 水는 한기를
                 * 더 강화할 가능성이 있으므로
                 * 경쟁 신호.
                 */
                if (
                        element == FiveElement.WATER
                ) {

                    temperatureConflict =
                            true;
                }

            } else if (
                    climate.hot()
            ) {

                if (
                        element == FiveElement.WATER
                ) {

                    targets.add(
                            ClimateCorrectionTarget
                                    .COOLING
                    );

                    directTemperatureCorrection =
                            true;
                }

                if (
                        element == FiveElement.FIRE
                ) {

                    temperatureConflict =
                            true;
                }
            }
        }

        /*
         * ================================================
         * 조습축
         * ================================================
         */
        if (
                need.moistureAdjustmentRequired()
        ) {

            if (
                    climate.dry()
            ) {

                if (
                        element == FiveElement.WATER
                ) {

                    targets.add(
                            ClimateCorrectionTarget
                                    .MOISTENING
                    );

                    directMoistureCorrection =
                            true;
                }

                /*
                 * 燥한데 火는 건조를
                 * 더 강화할 수 있다.
                 */
                if (
                        element == FiveElement.FIRE
                ) {

                    moistureConflict =
                            true;
                }

            } else if (
                    climate.wet()
            ) {

                if (
                        element == FiveElement.FIRE
                ) {

                    targets.add(
                            ClimateCorrectionTarget
                                    .DRYING
                    );

                    directMoistureCorrection =
                            true;
                }

                if (
                        element == FiveElement.WATER
                ) {

                    moistureConflict =
                            true;
                }
            }
        }

        /*
         * ================================================
         * 간접 조후 보조 후보
         * ================================================
         */
        boolean bridgeCandidate =
                determineBridgeCandidate(
                        element,
                        need
                );

        if (
                bridgeCandidate
                        && !targets.contains(
                        ClimateCorrectionTarget.BRIDGING
                )
        ) {

            targets.add(
                    ClimateCorrectionTarget.BRIDGING
            );
        }

        boolean directCandidate =
                directTemperatureCorrection
                        || directMoistureCorrection;

        /*
         * 직접 보정도 아니고
         * 연결 후보도 아니라면
         * 현재 6-3 조후 후보가 아니다.
         */
        if (
                !directCandidate
                        && !bridgeCandidate
        ) {

            return ClimateElementDecision
                    .notNeeded(
                            element,
                            share,
                            presence
                    );
        }

        ClimateAdjustmentNeedLevel addressedNeed =
                determineAddressedNeed(
                        need,
                        directTemperatureCorrection,
                        directMoistureCorrection,
                        bridgeCandidate
                );

        ClimateElementCandidateRole role =
                determineRole(
                        need,
                        addressedNeed,
                        presence,
                        directTemperatureCorrection,
                        directMoistureCorrection,
                        temperatureConflict,
                        moistureConflict,
                        bridgeCandidate
                );

        return new ClimateElementDecision(
                element,
                role,
                List.copyOf(
                        targets
                ),
                addressedNeed,
                presence,
                share,
                directTemperatureCorrection,
                directMoistureCorrection,
                temperatureConflict,
                moistureConflict,
                bridgeCandidate
        );
    }

    private ClimateElementCandidateRole determineRole(

            ClimateAdjustmentNeedAnalysis need,

            ClimateAdjustmentNeedLevel addressedNeed,

            ClimateElementPresenceLevel presence,

            boolean directTemperatureCorrection,

            boolean directMoistureCorrection,

            boolean temperatureConflict,

            boolean moistureConflict,

            boolean bridgeCandidate

    ) {

        boolean direct =
                directTemperatureCorrection
                        || directMoistureCorrection;

        /*
         * ================================================
         * 간접 연결 후보
         * ================================================
         *
         * 직접 조후 후보보다 우선시키지 않는다.
         */
        if (
                bridgeCandidate
                        && !direct
        ) {

            if (
                    presence.saturated()
            ) {

                return ClimateElementCandidateRole
                        .CONDITIONAL;
            }

            return ClimateElementCandidateRole
                    .SECONDARY;
        }

        boolean conflict =
                temperatureConflict
                        || moistureConflict;

        /*
         * ================================================
         * 寒燥 / 熱濕
         * ================================================
         *
         * 한 축을 고치면서
         * 다른 축을 악화시키는 후보.
         */
        if (
                need.correctionConflict()
                        && conflict
        ) {

            ClimateAdjustmentNeedLevel competingNeed =
                    determineCompetingNeed(
                            need,
                            directTemperatureCorrection,
                            directMoistureCorrection
                    );

            /*
             * 자신이 해결하는 축이
             * 다른 축보다 명확하게 더 중요할 때만
             * PRIMARY를 허용한다.
             */
            if (
                    addressedNeed.rank()
                            > competingNeed.rank()
            ) {

                return applyPresenceAdjustment(
                        ClimateElementCandidateRole.PRIMARY,
                        presence,
                        addressedNeed
                );
            }

            /*
             * 두 축의 필요도가 같거나
             * 자신이 해결하는 축이 더 약하면
             * 6-3에서 강제로 PRIMARY를 선택하지 않는다.
             */
            return ClimateElementCandidateRole
                    .CONDITIONAL;
        }

        /*
         * ================================================
         * 단일축 또는 정렬된 복합축
         * ================================================
         */
        if (
                addressedNeed.low()
        ) {

            if (
                    presence.abundant()
            ) {

                return ClimateElementCandidateRole
                        .CONDITIONAL;
            }

            return ClimateElementCandidateRole
                    .SECONDARY;
        }

        return applyPresenceAdjustment(
                ClimateElementCandidateRole.PRIMARY,
                presence,
                addressedNeed
        );
    }

    /**
     * 현재 원국에 이미 후보 오행이
     * 많이 존재하는 경우:
     *
     * "더 넣으면 된다"
     *
     * 라고 단정하지 않기 위한 보정.
     *
     * 오행 비중은 이미 6-1 계산에 사용됐으므로
     * 여기서는 추가 점수를 주지 않고
     * 과도한 후보 승격만 방지한다.
     */
    private ClimateElementCandidateRole
    applyPresenceAdjustment(

            ClimateElementCandidateRole baseRole,

            ClimateElementPresenceLevel presence,

            ClimateAdjustmentNeedLevel need

    ) {

        if (
                baseRole != ClimateElementCandidateRole.PRIMARY
        ) {

            return baseRole;
        }

        if (
                presence.saturated()
        ) {

            return ClimateElementCandidateRole
                    .CONDITIONAL;
        }

        if (
                presence.high()
        ) {

            return ClimateElementCandidateRole
                    .SECONDARY;
        }

        if (
                need.low()
                        && presence.moderate()
        ) {

            return ClimateElementCandidateRole
                    .SECONDARY;
        }

        return ClimateElementCandidateRole
                .PRIMARY;
    }

    private ClimateAdjustmentNeedLevel
    determineAddressedNeed(

            ClimateAdjustmentNeedAnalysis need,

            boolean temperatureCorrection,

            boolean moistureCorrection,

            boolean bridgeCandidate

    ) {

        if (
                temperatureCorrection
                        && moistureCorrection
        ) {

            return ClimateAdjustmentNeedLevel
                    .max(
                            need.temperatureNeed(),
                            need.moistureNeed()
                    );
        }

        if (
                temperatureCorrection
        ) {

            return need
                    .temperatureNeed();
        }

        if (
                moistureCorrection
        ) {

            return need
                    .moistureNeed();
        }

        if (
                bridgeCandidate
        ) {

            return need
                    .overallNeed();
        }

        return ClimateAdjustmentNeedLevel
                .NONE;
    }

    private ClimateAdjustmentNeedLevel
    determineCompetingNeed(

            ClimateAdjustmentNeedAnalysis need,

            boolean temperatureCorrection,

            boolean moistureCorrection

    ) {

        if (
                temperatureCorrection
                        && !moistureCorrection
        ) {

            return need
                    .moistureNeed();
        }

        if (
                moistureCorrection
                        && !temperatureCorrection
        ) {

            return need
                    .temperatureNeed();
        }

        return ClimateAdjustmentNeedLevel
                .NONE;
    }

    /**
     * 직접 조후 오행의 생조가 필요한 경우
     * 제한적으로 연결 후보를 만든다.
     *
     * =================================================
     *
     * FIRE가 부족하고
     * 조후상 FIRE 방향이 필요한 경우
     *
     * WOOD
     * -> FIRE 생조 보조
     *
     * =================================================
     *
     * WATER가 부족하고
     * 조후상 WATER 방향이 필요한 경우
     *
     * METAL
     * -> WATER 생조 보조
     *
     * =================================================
     *
     * 단, 寒燥 / 熱濕처럼
     * 직접 보정 방향 자체가 경쟁하는 상황에서는
     * 이런 단순 생조 후보를 남발하지 않는다.
     *
     * 寒燥에서 木만 별도로
     * 중재 후보로 허용한다.
     */
    private boolean determineBridgeCandidate(

            FiveElement element,

            ClimateAdjustmentNeedAnalysis need

    ) {

        ClimateEnvironmentAnalysis climate =
                need
                        .climateEnvironmentAnalysis();

        /*
         * 寒燥:
         *
         * 火와 水가 서로 반대 부작용을 가지므로
         * 木을 중재 후보로 보존한다.
         *
         * 이것은 통관 확정이 아니다.
         */
        if (
                need.correctionConflict()
                        && climate.coldDry()
                        && element == FiveElement.WOOD
        ) {

            return true;
        }

        /*
         * 다른 COMPETING 구조에서는
         * 단순 생조 연결 후보를 자동 생성하지 않는다.
         */
        if (
                need.correctionConflict()
        ) {

            return false;
        }

        boolean fireRequired =
                (
                        need.temperatureAdjustmentRequired()
                                && climate.cold()
                )
                        || (
                        need.moistureAdjustmentRequired()
                                && climate.wet()
                );

        boolean waterRequired =
                (
                        need.temperatureAdjustmentRequired()
                                && climate.hot()
                )
                        || (
                        need.moistureAdjustmentRequired()
                                && climate.dry()
                );

        if (
                element == FiveElement.WOOD
                        && fireRequired
        ) {

            ClimateElementPresenceLevel firePresence =
                    ClimateElementPresenceLevel
                            .fromShareBasisPoints(
                                    shareOf(
                                            FiveElement.FIRE,
                                            need
                                    )
                            );

            return firePresence
                    .scarce();
        }

        if (
                element == FiveElement.METAL
                        && waterRequired
        ) {

            ClimateElementPresenceLevel waterPresence =
                    ClimateElementPresenceLevel
                            .fromShareBasisPoints(
                                    shareOf(
                                            FiveElement.WATER,
                                            need
                                    )
                            );

            return waterPresence
                    .scarce();
        }

        return false;
    }

    public int shareOf(

            FiveElement element,

            ClimateAdjustmentNeedAnalysis need

    ) {

        Objects.requireNonNull(
                element,
                "오행이 필요합니다."
        );

        Objects.requireNonNull(
                need,
                "조후 필요성 분석이 필요합니다."
        );

        ClimateEnvironmentEvidence evidence =
                need
                        .climateEnvironmentAnalysis()
                        .evidence();

        return switch (
                element
                ) {

            case WOOD ->
                    evidence
                            .woodShareBasisPoints();

            case FIRE ->
                    evidence
                            .fireShareBasisPoints();

            case EARTH ->
                    evidence
                            .earthShareBasisPoints();

            case METAL ->
                    evidence
                            .metalShareBasisPoints();

            case WATER ->
                    evidence
                            .waterShareBasisPoints();
        };
    }

    /**
     * Rule과 Analyzer 사이에서 사용하는
     * 내부 판정 결과.
     *
     * 독립적인 도메인 분석 객체가 아니라
     * ClimateElementCandidate 생성을 위한
     * 결정값이다.
     */
    public record ClimateElementDecision(

            FiveElement element,

            ClimateElementCandidateRole role,

            List<ClimateCorrectionTarget> targets,

            ClimateAdjustmentNeedLevel addressedNeedLevel,

            ClimateElementPresenceLevel presenceLevel,

            int existingShareBasisPoints,

            boolean directTemperatureCorrection,

            boolean directMoistureCorrection,

            boolean temperatureConflict,

            boolean moistureConflict,

            boolean bridgeCandidate

    ) {

        public ClimateElementDecision {

            Objects.requireNonNull(
                    element,
                    "오행이 필요합니다."
            );

            Objects.requireNonNull(
                    role,
                    "후보 역할이 필요합니다."
            );

            Objects.requireNonNull(
                    addressedNeedLevel,
                    "대응 필요도가 필요합니다."
            );

            Objects.requireNonNull(
                    presenceLevel,
                    "오행 보유 수준이 필요합니다."
            );

            targets =
                    targets == null
                            ? List.of()
                            : List.copyOf(
                            targets
                    );
        }

        private static ClimateElementDecision
        notNeeded(

                FiveElement element,

                int share,

                ClimateElementPresenceLevel presence

        ) {

            return new ClimateElementDecision(
                    element,
                    ClimateElementCandidateRole.NOT_NEEDED,
                    List.of(),
                    ClimateAdjustmentNeedLevel.NONE,
                    presence,
                    share,
                    false,
                    false,
                    false,
                    false,
                    false
            );
        }
    }
}