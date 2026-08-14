package com.dajeom.rule;

import com.dajeom.model.ExtremeStrengthStructureAnalysis;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FollowWeakDirection;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.SpecialStructureType;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-8.
 *
 * 특수 월령 구조와
 * 4-7 종격 후보를 연결하는 Rule.
 *
 * =================================================
 *
 * 건록
 *
 * 다점 기본 정책:
 *
 * 甲 -> 寅
 * 乙 -> 卯
 * 丙 -> 巳
 * 丁 -> 午
 * 戊 -> 巳
 * 己 -> 午
 * 庚 -> 申
 * 辛 -> 酉
 * 壬 -> 亥
 * 癸 -> 子
 *
 * =================================================
 *
 * 양인
 *
 * 다점 기본 정책에서는
 * 양간만 핵심 양인 후보로 사용한다.
 *
 * 甲 -> 卯
 * 丙 -> 午
 * 戊 -> 午
 * 庚 -> 酉
 * 壬 -> 子
 *
 * 음간의 양인 여부는 유파 차이가 있으므로
 * 현재 코어에서는 강제로 확정하지 않는다.
 *
 * 향후 Ruleset을 지원하면
 * 이 정책만 교체할 수 있다.
 *
 * =================================================
 *
 * 종격 계열
 *
 * 여기서 새로 종격 조건을 계산하지 않는다.
 *
 * 반드시 4-7
 * ExtremeStrengthStructureAnalysis의
 * 후보 결과를 그대로 상속한다.
 */
@Component
public class SpecialStructureRule {

    /**
     * 일간의 건록 지지.
     */
    public EarthlyBranch jianLuBranchOf(
            HeavenlyStem dayMaster
    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        int stemIndex =
                stemIndexOf(
                        dayMaster
                );

        int branchIndex =
                switch (
                        stemIndex
                        ) {

                    /*
                     * 甲 -> 寅
                     */
                    case 0 ->
                            2;

                    /*
                     * 乙 -> 卯
                     */
                    case 1 ->
                            3;

                    /*
                     * 丙 -> 巳
                     */
                    case 2 ->
                            5;

                    /*
                     * 丁 -> 午
                     */
                    case 3 ->
                            6;

                    /*
                     * 戊 -> 巳
                     */
                    case 4 ->
                            5;

                    /*
                     * 己 -> 午
                     */
                    case 5 ->
                            6;

                    /*
                     * 庚 -> 申
                     */
                    case 6 ->
                            8;

                    /*
                     * 辛 -> 酉
                     */
                    case 7 ->
                            9;

                    /*
                     * 壬 -> 亥
                     */
                    case 8 ->
                            11;

                    /*
                     * 癸 -> 子
                     */
                    case 9 ->
                            0;

                    default ->
                            throw new IllegalStateException(
                                    "건록 지지를 계산할 수 없습니다: "
                                            + dayMaster
                            );
                };

        return EarthlyBranch.fromIndex(
                branchIndex
        );
    }

    public boolean isJianLuMonth(

            HeavenlyStem dayMaster,

            EarthlyBranch monthBranch

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        return jianLuBranchOf(
                dayMaster
        )
                == monthBranch;
    }

    /**
     * 다점 기본 양인 정책.
     *
     * 양간:
     *
     * 甲 丙 戊 庚 壬
     *
     * 에 대해서만 양인 월지를 반환한다.
     *
     * 음간은 Optional.empty().
     */
    public Optional<EarthlyBranch>
    yangRenBranchOf(
            HeavenlyStem dayMaster
    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        int stemIndex =
                stemIndexOf(
                        dayMaster
                );

        return switch (
                stemIndex
                ) {

            /*
             * 甲 -> 卯
             */
            case 0 ->
                    Optional.of(
                            EarthlyBranch.fromIndex(
                                    3
                            )
                    );

            /*
             * 丙 -> 午
             */
            case 2 ->
                    Optional.of(
                            EarthlyBranch.fromIndex(
                                    6
                            )
                    );

            /*
             * 戊 -> 午
             */
            case 4 ->
                    Optional.of(
                            EarthlyBranch.fromIndex(
                                    6
                            )
                    );

            /*
             * 庚 -> 酉
             */
            case 6 ->
                    Optional.of(
                            EarthlyBranch.fromIndex(
                                    9
                            )
                    );

            /*
             * 壬 -> 子
             */
            case 8 ->
                    Optional.of(
                            EarthlyBranch.fromIndex(
                                    0
                            )
                    );

            /*
             * 음간은 현재 기본 정책에서
             * 양인 후보를 확정하지 않는다.
             */
            default ->
                    Optional.empty();
        };
    }

    public boolean isYangRenMonth(

            HeavenlyStem dayMaster,

            EarthlyBranch monthBranch

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        return yangRenBranchOf(
                dayMaster
        )
                .map(
                        branch ->
                                branch
                                        == monthBranch
                )
                .orElse(
                        false
                );
    }

    public boolean isYangStem(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        return stemIndexOf(
                stem
        ) % 2 == 0;
    }

    /**
     * 일반 내격에서 보류된 비겁 월령이지만
     * 현재 기본 정책의 건록/양인에
     * 정확히 해당하지 않는 경우.
     *
     * 해당 월령을 버리지 않고
     * generic special review로 남긴다.
     */
    public boolean requiresCompanionMonthReview(

            boolean mainQiSpecialDeferred,

            boolean jianLuCandidate,

            boolean yangRenCandidate

    ) {

        return mainQiSpecialDeferred
                && !jianLuCandidate
                && !yangRenCandidate;
    }

    /**
     * 4-7 극단구조 결과를
     * 5-8 특수격 후보 타입으로 변환한다.
     *
     * 여기서 종격 여부를 새로 판정하지 않는다.
     */
    public Optional<SpecialStructureType>
    determineFollowCandidateType(
            ExtremeStrengthStructureAnalysis extreme
    ) {

        Objects.requireNonNull(
                extreme,
                "극단 신강·신약 분석이 필요합니다."
        );

        if (
                extreme.followStrongCandidate()
        ) {

            return Optional.of(
                    SpecialStructureType
                            .FOLLOW_STRONG_CANDIDATE
            );
        }

        if (
                !extreme.followWeakCandidate()
        ) {

            return Optional.empty();
        }

        FollowWeakDirection direction =
                extreme.followWeakDirection();

        if (
                direction.output()
        ) {

            return Optional.of(
                    SpecialStructureType
                            .FOLLOW_OUTPUT_CANDIDATE
            );
        }

        if (
                direction.wealth()
        ) {

            return Optional.of(
                    SpecialStructureType
                            .FOLLOW_WEALTH_CANDIDATE
            );
        }

        if (
                direction.officer()
        ) {

            return Optional.of(
                    SpecialStructureType
                            .FOLLOW_OFFICER_CANDIDATE
            );
        }

        if (
                direction.mixed()
        ) {

            return Optional.of(
                    SpecialStructureType
                            .FOLLOW_WEAK_MIXED_CANDIDATE
            );
        }

        throw new IllegalStateException(
                "종약 후보이지만 종약 방향을 결정할 수 없습니다."
        );
    }

    private int stemIndexOf(
            HeavenlyStem stem
    ) {

        for (
                int index = 0;
                index < 10;
                index++
        ) {

            if (
                    HeavenlyStem.fromIndex(
                            index
                    ) == stem
            ) {

                return index;
            }
        }

        throw new IllegalArgumentException(
                "알 수 없는 천간입니다: "
                        + stem
        );
    }
}