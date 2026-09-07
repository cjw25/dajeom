package com.dajeom.rule;

import com.dajeom.domain.DayMasterFollowingStructureAnalysis;
import com.dajeom.domain.DayMasterFollowingStructureReason;
import com.dajeom.domain.DayMasterFollowingStructureState;
import com.dajeom.domain.DayMasterForceStatusAnalysis;
import com.dajeom.domain.DayMasterGroundStatus;
import com.dajeom.domain.DayMasterGroundStatusAnalysis;
import com.dajeom.domain.DayMasterRelationType;
import com.dajeom.domain.DayMasterSeasonAnalysis;
import com.dajeom.domain.DayMasterSpecialStructureCandidateAnalysis;
import com.dajeom.domain.DayMasterSpecialStructureCandidateType;
import com.dajeom.domain.FiveElementSeasonState;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandDayMasterAnalysis;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;

/**
 * 종격 계열 실제 확정 Rule.
 *
 * =================================================
 *
 * 후보 존재
 * != 종격 성립
 *
 * =================================================
 *
 * 약한 쪽의 종격 공통 전제:
 *
 * - 일간 무근
 * - 인성/비겁 생부 없음
 *
 * 이후 월령 MAIN이
 * 어떤 방향을 주도하는지 본다.
 *
 * =================================================
 *
 * 從財
 *
 * 월령 MAIN:
 * 일간이 극하는 오행
 *
 * =================================================
 *
 * 從官殺
 *
 * 월령 MAIN:
 * 일간을 극하는 오행
 *
 * 또한 식상 제어 구조가 있으면
 * 순수 종관살로 확정하지 않는다.
 *
 * =================================================
 *
 * 從兒
 *
 * 월령 MAIN:
 * 일간이 생하는 오행
 *
 * 관살이 직접 개입하면
 * 순수 종아로 확정하지 않는다.
 *
 * 財는 食傷生財 흐름이 가능하므로
 * 자동 탈락시키지 않는다.
 *
 * =================================================
 *
 * 從勢
 *
 * 무근 + 무생부
 * +
 * 財 / 官殺 / 食傷 방향이 복합적으로 존재
 * +
 * 단일 종격으로 정리되지 않음.
 *
 * =================================================
 *
 * 강한 쪽:
 *
 * 從旺
 * 從強
 *
 * 은 월령/근/인비 구조를 별도로 본다.
 *
 * =================================================
 *
 * 숫자 / 가중치 / 다수결 없음.
 */
@Component
public class DayMasterFollowingStructureRule {

    public DayMasterFollowingStructureAnalysis assess(

            HeavenlyStem dayMaster,

            DayMasterSeasonAnalysis season,

            MonthCommandDayMasterAnalysis monthCommand,

            DayMasterGroundStatusAnalysis ground,

            DayMasterForceStatusAnalysis force,

            DayMasterSpecialStructureCandidateAnalysis candidates

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                season,
                "월령 계절 분석은 필수입니다."
        );

        Objects.requireNonNull(
                monthCommand,
                "월령-일간 관계 분석은 필수입니다."
        );

        Objects.requireNonNull(
                ground,
                "득지 분석은 필수입니다."
        );

        Objects.requireNonNull(
                force,
                "득세 분석은 필수입니다."
        );

        Objects.requireNonNull(
                candidates,
                "특수구조 후보 분석은 필수입니다."
        );

        validateDayMaster(
                dayMaster,
                season,
                monthCommand,
                ground,
                force,
                candidates
        );

        DayMasterRelationType monthMainRelation =
                monthCommand
                        .mainRelationType()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "월령 MAIN 지장간 관계가 없습니다."
                                        )
                        );

        boolean rootAbsent =
                ground.status()
                        == DayMasterGroundStatus.NO_ROOT;

        boolean generatingSupport =
                force
                        .hasAnyGeneratingSupport();

        boolean sameElementSupport =
                force
                        .hasAnySameElementSupport();

        boolean support =
                generatingSupport
                        ||
                        sameElementSupport;

        boolean output =
                force
                        .hasGeneratingOutflowStructure();

        boolean wealth =
                force
                        .hasWealthDirectionStructure();

        boolean control =
                force
                        .hasControlPressureStructure();

        /*
         * =================================================
         * 1. 약한 쪽 종격
         * =================================================
         */

        if (
                candidates.hasCandidate(
                        DayMasterSpecialStructureCandidateType
                                .WEAK_FOLLOWING_REVIEW
                )
        ) {

            /*
             * 진종의 기본 전제:
             *
             * - 무근
             * - 생부 없음
             */
            if (
                    rootAbsent
                            &&
                            !support
            ) {

                /*
                 * =========================================
                 * 從兒
                 * =========================================
                 *
                 * 월령 MAIN이 식상 방향.
                 *
                 * 官殺이 직접 개입하면
                 * 食傷과 官殺이 서로 맞서는 구조가 되므로
                 * 순수 從兒로 확정하지 않는다.
                 *
                 * 財는 食傷生財가 가능하므로 허용.
                 */
                if (
                        monthMainRelation
                                == DayMasterRelationType
                                .GENERATED_BY_DAY_MASTER

                                &&
                                output

                                &&
                                !control
                ) {

                    return result(
                            dayMaster,
                            DayMasterFollowingStructureState
                                    .FOLLOW_OUTPUT,
                            season,
                            monthCommand,
                            ground,
                            DayMasterFollowingStructureReason
                                    .NO_DAY_MASTER_ROOT,
                            DayMasterFollowingStructureReason
                                    .NO_ORIGINAL_SUPPORT,
                            DayMasterFollowingStructureReason
                                    .MONTH_COMMAND_OUTPUT_DIRECTION,
                            DayMasterFollowingStructureReason
                                    .OUTPUT_STRUCTURE_PRESENT
                    );
                }

                /*
                 * =========================================
                 * 從官 / 從殺
                 * =========================================
                 *
                 * 월령 MAIN이 관살 방향.
                 *
                 * 식상은 官殺을 제하는 방향이므로
                 * 순수 종관살에서는 막는다.
                 *
                 * 財는 官殺을 생할 수 있으므로
                 * 자동 탈락시키지 않는다.
                 */
                if (
                        monthMainRelation
                                == DayMasterRelationType
                                .CONTROLS_DAY_MASTER

                                &&
                                control

                                &&
                                !output
                ) {

                    return result(
                            dayMaster,
                            DayMasterFollowingStructureState
                                    .FOLLOW_CONTROL,
                            season,
                            monthCommand,
                            ground,
                            DayMasterFollowingStructureReason
                                    .NO_DAY_MASTER_ROOT,
                            DayMasterFollowingStructureReason
                                    .NO_ORIGINAL_SUPPORT,
                            DayMasterFollowingStructureReason
                                    .MONTH_COMMAND_CONTROL_DIRECTION,
                            DayMasterFollowingStructureReason
                                    .CONTROL_STRUCTURE_PRESENT
                    );
                }

                /*
                 * =========================================
                 * 從財
                 * =========================================
                 *
                 * 월령 MAIN이 財 방향.
                 *
                 * 食傷은 財를 생할 수 있고,
                 * 財는 官殺로 흐를 수 있으므로
                 *
                 * 단순히 output/control이 존재한다는
                 * 이유만으로 從財를 깨지 않는다.
                 *
                 * 다만 월령 주도 방향 자체가
                 * 財여야 한다.
                 */
                if (
                        monthMainRelation
                                == DayMasterRelationType
                                .CONTROLLED_BY_DAY_MASTER

                                &&
                                wealth
                ) {

                    return result(
                            dayMaster,
                            DayMasterFollowingStructureState
                                    .FOLLOW_WEALTH,
                            season,
                            monthCommand,
                            ground,
                            DayMasterFollowingStructureReason
                                    .NO_DAY_MASTER_ROOT,
                            DayMasterFollowingStructureReason
                                    .NO_ORIGINAL_SUPPORT,
                            DayMasterFollowingStructureReason
                                    .MONTH_COMMAND_WEALTH_DIRECTION,
                            DayMasterFollowingStructureReason
                                    .WEALTH_STRUCTURE_PRESENT
                    );
                }

                /*
                 * =========================================
                 * 從勢
                 * =========================================
                 *
                 * 財 / 官殺 / 食傷의 여러 방향이
                 * 실제로 함께 존재하지만
                 * 위의 순수 단일 종격으로
                 * 정리되지 않는 경우.
                 *
                 * 개수를 비교하지 않는다.
                 *
                 * 서로 다른 방향의 존재 여부만 본다.
                 */
                boolean mixedOpposingDirections =
                        (
                                output
                                        &&
                                        wealth
                        )
                                ||
                                (
                                        output
                                                &&
                                                control
                                )
                                ||
                                (
                                        wealth
                                                &&
                                                control
                                );

                if (
                        mixedOpposingDirections
                                &&
                                candidates.hasCandidate(
                                        DayMasterSpecialStructureCandidateType
                                                .FOLLOW_MOMENTUM_REVIEW
                                )
                ) {

                    return result(
                            dayMaster,
                            DayMasterFollowingStructureState
                                    .FOLLOW_MOMENTUM,
                            season,
                            monthCommand,
                            ground,
                            DayMasterFollowingStructureReason
                                    .NO_DAY_MASTER_ROOT,
                            DayMasterFollowingStructureReason
                                    .NO_ORIGINAL_SUPPORT,
                            DayMasterFollowingStructureReason
                                    .MULTIPLE_OPPOSING_DIRECTIONS,
                            DayMasterFollowingStructureReason
                                    .NO_SINGLE_PURE_FOLLOWING_DIRECTION
                    );
                }
            }
        }

        /*
         * =================================================
         * 2. 從旺
         * =================================================
         *
         * 현재 엔진의 엄격한 구분:
         *
         * - 월령 MAIN이 일간과 같은 오행
         * - 실제 근 존재
         * - 비겁/동류 생조 존재
         * - 財/官殺 방향 없음
         *
         * 인성 존재는 從旺을 깨는 것으로
         * 보지 않는다.
         *
         * 食傷은 이후 순수성 판단에서
         * 별도로 세밀화할 수 있으므로
         * 현재 자동 탈락시키지 않는다.
         */
        if (
                candidates.hasCandidate(
                        DayMasterSpecialStructureCandidateType
                                .FOLLOW_PROSPEROUS_REVIEW
                )
                        &&
                        ground.status()
                                != DayMasterGroundStatus.NO_ROOT
                        &&
                        monthMainRelation
                                == DayMasterRelationType.SAME_ELEMENT
                        &&
                        sameElementSupport
                        &&
                        !wealth
                        &&
                        !control
        ) {

            return result(
                    dayMaster,
                    DayMasterFollowingStructureState
                            .FOLLOW_PROSPEROUS,
                    season,
                    monthCommand,
                    ground,
                    DayMasterFollowingStructureReason
                            .DAY_MASTER_ROOT_PRESENT,
                    DayMasterFollowingStructureReason
                            .MONTH_COMMAND_SAME_ELEMENT,
                    DayMasterFollowingStructureReason
                            .SAME_ELEMENT_SUPPORT_PRESENT
            );
        }

        /*
         * =================================================
         * 3. 從強
         * =================================================
         *
         * 현재 엔진에서는 從旺과 구별하기 위해:
         *
         * - 월령 MAIN이 일간을 생하는 방향
         * - 실제 근 존재
         * - 인성 생조 존재
         * - 비겁/동류 존재
         * - 財/官殺 없음
         *
         * 을 엄격한 검토 기준으로 둔다.
         *
         * 이것은 모든 유파의 유일한 정의가 아니라
         * 엔진 내부에서 從旺과 從強을
         * 혼동하지 않기 위한 보수적 Rule이다.
         */
        if (
                candidates.hasCandidate(
                        DayMasterSpecialStructureCandidateType
                                .FOLLOW_STRONG_REVIEW
                )
                        &&
                        ground.status()
                                != DayMasterGroundStatus.NO_ROOT
                        &&
                        monthMainRelation
                                == DayMasterRelationType
                                .GENERATES_DAY_MASTER
                        &&
                        generatingSupport
                        &&
                        sameElementSupport
                        &&
                        !wealth
                        &&
                        !control
        ) {

            return result(
                    dayMaster,
                    DayMasterFollowingStructureState
                            .FOLLOW_STRONG,
                    season,
                    monthCommand,
                    ground,
                    DayMasterFollowingStructureReason
                            .DAY_MASTER_ROOT_PRESENT,
                    DayMasterFollowingStructureReason
                            .MONTH_COMMAND_SUPPORTS_DAY_MASTER,
                    DayMasterFollowingStructureReason
                            .GENERATING_SUPPORT_PRESENT,
                    DayMasterFollowingStructureReason
                            .SAME_ELEMENT_SUPPORT_PRESENT
            );
        }

        /*
         * =================================================
         * 4. 엄격한 종격 조건 불성립
         * =================================================
         */

        EnumSet<DayMasterFollowingStructureReason> failureReasons =
                EnumSet.noneOf(
                        DayMasterFollowingStructureReason.class
                );

        if (rootAbsent) {

            failureReasons.add(
                    DayMasterFollowingStructureReason
                            .NO_DAY_MASTER_ROOT
            );

        } else {

            failureReasons.add(
                    DayMasterFollowingStructureReason
                            .DAY_MASTER_ROOT_PRESENT
            );
        }

        if (generatingSupport) {

            failureReasons.add(
                    DayMasterFollowingStructureReason
                            .GENERATING_SUPPORT_PRESENT
            );
        }

        if (sameElementSupport) {

            failureReasons.add(
                    DayMasterFollowingStructureReason
                            .SAME_ELEMENT_SUPPORT_PRESENT
            );
        }

        if (support) {

            failureReasons.add(
                    DayMasterFollowingStructureReason
                            .ORIGINAL_SUPPORT_REMAINS
            );

        } else {

            failureReasons.add(
                    DayMasterFollowingStructureReason
                            .NO_ORIGINAL_SUPPORT
            );
        }

        appendMonthCommandReason(
                failureReasons,
                monthMainRelation
        );

        if (output) {

            failureReasons.add(
                    DayMasterFollowingStructureReason
                            .OUTPUT_STRUCTURE_PRESENT
            );
        }

        if (wealth) {

            failureReasons.add(
                    DayMasterFollowingStructureReason
                            .WEALTH_STRUCTURE_PRESENT
            );
        }

        if (control) {

            failureReasons.add(
                    DayMasterFollowingStructureReason
                            .CONTROL_STRUCTURE_PRESENT
            );
        }

        failureReasons.add(
                DayMasterFollowingStructureReason
                        .STRICT_FOLLOWING_CONDITION_NOT_MET
        );

        return new DayMasterFollowingStructureAnalysis(
                dayMaster,
                DayMasterFollowingStructureState
                        .NOT_ESTABLISHED,
                season.seasonState(),
                ground.status(),
                monthMainRelation,
                monthCommand.mainExposed(),
                failureReasons
        );
    }

    private DayMasterFollowingStructureAnalysis result(

            HeavenlyStem dayMaster,

            DayMasterFollowingStructureState state,

            DayMasterSeasonAnalysis season,

            MonthCommandDayMasterAnalysis monthCommand,

            DayMasterGroundStatusAnalysis ground,

            DayMasterFollowingStructureReason... reasons

    ) {

        DayMasterRelationType monthMainRelation =
                monthCommand
                        .mainRelationType()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "월령 MAIN 관계가 없습니다."
                                        )
                        );

        EnumSet<DayMasterFollowingStructureReason> reasonSet =
                EnumSet.noneOf(
                        DayMasterFollowingStructureReason.class
                );

        for (
                DayMasterFollowingStructureReason reason
                : reasons
        ) {

            reasonSet.add(
                    Objects.requireNonNull(
                            reason,
                            "종격 판정 이유에는 null이 들어갈 수 없습니다."
                    )
            );
        }

        if (reasonSet.isEmpty()) {

            throw new IllegalStateException(
                    "종격 판정 이유가 없습니다."
            );
        }

        return new DayMasterFollowingStructureAnalysis(
                dayMaster,
                state,
                season.seasonState(),
                ground.status(),
                monthMainRelation,
                monthCommand.mainExposed(),
                reasonSet
        );
    }

    private void appendMonthCommandReason(

            EnumSet<DayMasterFollowingStructureReason> reasons,

            DayMasterRelationType relation

    ) {

        switch (relation) {

            case GENERATES_DAY_MASTER ->
                    reasons.add(
                            DayMasterFollowingStructureReason
                                    .MONTH_COMMAND_SUPPORTS_DAY_MASTER
                    );

            case SAME_ELEMENT ->
                    reasons.add(
                            DayMasterFollowingStructureReason
                                    .MONTH_COMMAND_SAME_ELEMENT
                    );

            case GENERATED_BY_DAY_MASTER ->
                    reasons.add(
                            DayMasterFollowingStructureReason
                                    .MONTH_COMMAND_OUTPUT_DIRECTION
                    );

            case CONTROLLED_BY_DAY_MASTER ->
                    reasons.add(
                            DayMasterFollowingStructureReason
                                    .MONTH_COMMAND_WEALTH_DIRECTION
                    );

            case CONTROLS_DAY_MASTER ->
                    reasons.add(
                            DayMasterFollowingStructureReason
                                    .MONTH_COMMAND_CONTROL_DIRECTION
                    );
        }
    }

    private void validateDayMaster(

            HeavenlyStem dayMaster,

            DayMasterSeasonAnalysis season,

            MonthCommandDayMasterAnalysis monthCommand,

            DayMasterGroundStatusAnalysis ground,

            DayMasterForceStatusAnalysis force,

            DayMasterSpecialStructureCandidateAnalysis candidates

    ) {

        if (
                season.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "월령 계절 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                monthCommand.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "월령-일간 관계 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                ground.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "득지 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                force.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "득세 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                candidates.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "특수구조 후보 분석의 일간이 일치하지 않습니다."
            );
        }
    }
}