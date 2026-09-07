package com.dajeom.calculation;

import com.dajeom.domain.DayMasterForceStatusAnalysis;
import com.dajeom.domain.DayMasterGroundStatus;
import com.dajeom.domain.DayMasterGroundStatusAnalysis;
import com.dajeom.domain.DayMasterSeasonAnalysis;
import com.dajeom.domain.DayMasterSpecialStructureCandidateAnalysis;
import com.dajeom.domain.DayMasterSpecialStructureCandidateType;
import com.dajeom.domain.FiveElementSeasonState;
import com.dajeom.domain.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * 일반 신강/신약 판정 전에
 * 종격 계열 특수구조 후보를 선별한다.
 *
 * =================================================
 *
 * 핵심 원칙:
 *
 * 후보 선별 != 성격 확정
 *
 * =================================================
 *
 * 특히:
 *
 * 무근
 * != 從格
 *
 * 실령
 * != 從格
 *
 * 재성이 존재
 * != 從財
 *
 * 관살이 존재
 * != 從殺
 *
 * 식상이 존재
 * != 從兒
 *
 * =================================================
 *
 * 숫자 / 점수 / 퍼센트 / 다수결 없음.
 */
@Component
public class DayMasterSpecialStructureCandidateCalculator {

    public DayMasterSpecialStructureCandidateAnalysis analyze(

            HeavenlyStem dayMaster,

            DayMasterSeasonAnalysis season,

            DayMasterGroundStatusAnalysis ground,

            DayMasterForceStatusAnalysis force

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                season,
                "월령 상태 분석은 필수입니다."
        );

        Objects.requireNonNull(
                ground,
                "득지 구조 분석은 필수입니다."
        );

        Objects.requireNonNull(
                force,
                "득세 구조 분석은 필수입니다."
        );

        if (
                season.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "월령 분석의 일간이 일치하지 않습니다."
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

        boolean rootAbsent =
                ground.status()
                        == DayMasterGroundStatus.NO_ROOT;

        boolean generatingSupport =
                force
                        .hasAnyGeneratingSupport();

        boolean sameElementSupport =
                force
                        .hasAnySameElementSupport();

        boolean output =
                force
                        .hasGeneratingOutflowStructure();

        boolean wealth =
                force
                        .hasWealthDirectionStructure();

        boolean control =
                force
                        .hasControlPressureStructure();

        boolean support =
                generatingSupport
                        ||
                        sameElementSupport;

        EnumSet<DayMasterSpecialStructureCandidateType>
                candidates =
                EnumSet.noneOf(
                        DayMasterSpecialStructureCandidateType.class
                );

        /*
         * =================================================
         * 1. 약한 쪽 從象 검토
         * =================================================
         *
         * 《滴天髓》의 핵심 문맥:
         *
         * 日主孤立無氣
         * 無地人元
         * 絕無一毫生扶之意
         *
         * 따라서:
         *
         * - 무근
         * - 생부 없음
         *
         * 을 가장 먼저 본다.
         *
         * 월령 하나만으로 從을 판정하지 않는다.
         */
        boolean pureWeakFollowingBase =
                rootAbsent
                        &&
                        !support
                        &&
                        (
                                output
                                        ||
                                        wealth
                                        ||
                                        control
                        );

        if (pureWeakFollowingBase) {

            candidates.add(
                    DayMasterSpecialStructureCandidateType
                            .WEAK_FOLLOWING_REVIEW
            );

            /*
             * 아래 후보들은 서로 배타적이지 않다.
             *
             * 아직 "어느 신을 실제로 따른다"를
             * 판정하는 단계가 아니기 때문이다.
             */

            if (wealth) {

                candidates.add(
                        DayMasterSpecialStructureCandidateType
                                .FOLLOW_WEALTH_REVIEW
                );
            }

            if (control) {

                candidates.add(
                        DayMasterSpecialStructureCandidateType
                                .FOLLOW_CONTROL_REVIEW
                );
            }

            if (output) {

                candidates.add(
                        DayMasterSpecialStructureCandidateType
                                .FOLLOW_OUTPUT_REVIEW
                );
            }

            /*
             * 財 / 官殺 / 食傷 중
             * 복수 방향이 함께 존재한다.
             *
             * 개수를 세는 것이 아니라
             * 서로 다른 명리 방향이
             * 함께 존재하는지 확인한다.
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

            if (mixedOpposingDirections) {

                candidates.add(
                        DayMasterSpecialStructureCandidateType
                                .FOLLOW_MOMENTUM_REVIEW
                );
            }
        }

        /*
         * =================================================
         * 2. 從旺 후보
         * =================================================
         *
         * 《滴天髓闡微》:
         *
         * 四柱皆比劫
         * 無官殺之制
         * 有印綬之生
         * 旺之極者
         *
         * 여기서는 확정하지 않고
         * 후보 문턱만 만든다.
         *
         * - 월령 旺/相
         * - 근 존재
         * - 동류 구조 존재
         * - 관살 방향 없음
         * - 재성 방향 없음
         *
         * 食傷 존재 여부는 여기서
         * 자동 탈락조건으로 사용하지 않는다.
         */
        boolean seasonSupportsStrongSide =
                season.seasonState()
                        == FiveElementSeasonState.WANG
                        ||
                        season.seasonState()
                                == FiveElementSeasonState.XIANG;

        boolean rootPresent =
                !rootAbsent;

        boolean prosperousCandidate =
                seasonSupportsStrongSide
                        &&
                        rootPresent
                        &&
                        sameElementSupport
                        &&
                        !control
                        &&
                        !wealth;

        if (prosperousCandidate) {

            candidates.add(
                    DayMasterSpecialStructureCandidateType
                            .FOLLOW_PROSPEROUS_REVIEW
            );
        }

        /*
         * =================================================
         * 3. 從強 후보
         * =================================================
         *
         * 《滴天髓闡微》:
         *
         * 印綬重重
         * 比劫疊疊
         * 日主又當令
         * 絕無一毫財星官殺之氣
         *
         * 후보 문턱:
         *
         * - 旺/相
         * - 근 존재
         * - 인성 방향 존재
         * - 비겁 방향 존재
         * - 재성 없음
         * - 관살 없음
         */
        boolean strongCandidate =
                seasonSupportsStrongSide
                        &&
                        rootPresent
                        &&
                        generatingSupport
                        &&
                        sameElementSupport
                        &&
                        !wealth
                        &&
                        !control;

        if (strongCandidate) {

            candidates.add(
                    DayMasterSpecialStructureCandidateType
                            .FOLLOW_STRONG_REVIEW
            );
        }

        /*
         * 아무 특수 후보도 걸리지 않았다면
         * 일반 신강/신약 판단 경로.
         */
        if (candidates.isEmpty()) {

            candidates.add(
                    DayMasterSpecialStructureCandidateType
                            .GENERAL_STRENGTH_REVIEW
            );
        }

        return new DayMasterSpecialStructureCandidateAnalysis(
                dayMaster,
                season.seasonState(),
                ground.status(),
                Set.copyOf(
                        candidates
                ),
                rootAbsent,
                generatingSupport,
                sameElementSupport,
                output,
                wealth,
                control
        );
    }
}