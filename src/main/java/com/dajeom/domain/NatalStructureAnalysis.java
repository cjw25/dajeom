package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 원국 내부 명리 구조 전체.
 *
 * 아직 최종:
 *
 * - 신강/신약
 * - 격국
 * - 용신
 * - 희신
 * - 기신
 *
 * 을 결정하지 않는다.
 *
 * 수학적 점수나 가중치는 없다.
 */
public record NatalStructureAnalysis(

        HeavenlyStem dayMaster,

        NatalRootAnalysis rooting,

        HiddenStemExposureAnalysis hiddenStemExposure,

        MonthCommandAnalysis monthCommand,

        MonthCommandDayMasterAnalysis monthCommandDayMaster,

        DayMasterSeasonAnalysis dayMasterSeason,

        DayMasterRelationAnalysis dayMasterRelations,

        DayMasterSupportStructure dayMasterSupport,

        DayMasterGroundAnalysis dayMasterGround,

        DayMasterForceFormationAnalysis dayMasterForceFormation,

        List<StemRelation> stemRelations,

        List<BranchRelation> branchRelations,

        List<BranchGroupRelation> branchGroupRelations

) {

    public NatalStructureAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                rooting,
                "통근 분석은 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStemExposure,
                "지장간 투출 분석은 필수입니다."
        );

        Objects.requireNonNull(
                monthCommand,
                "월령 분석은 필수입니다."
        );

        Objects.requireNonNull(
                monthCommandDayMaster,
                "월령-일간 관계 분석은 필수입니다."
        );

        Objects.requireNonNull(
                dayMasterSeason,
                "일간 계절 상태 분석은 필수입니다."
        );

        Objects.requireNonNull(
                dayMasterRelations,
                "일간 생극관계 분석은 필수입니다."
        );

        Objects.requireNonNull(
                dayMasterSupport,
                "일간 지원 구조는 필수입니다."
        );

        Objects.requireNonNull(
                dayMasterGround,
                "일간 뿌리 구조는 필수입니다."
        );

        Objects.requireNonNull(
                dayMasterForceFormation,
                "일간 세력 형성 구조는 필수입니다."
        );

        if (
                monthCommandDayMaster.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "월령-일간 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                monthCommandDayMaster.monthBranch()
                        != monthCommand.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "월령 분석의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                dayMasterSeason.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "계절 상태 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                dayMasterRelations.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "생극관계 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                dayMasterSupport.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "일간 지원 구조의 일간이 일치하지 않습니다."
            );
        }

        if (
                dayMasterGround.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "일간 뿌리 구조의 일간이 일치하지 않습니다."
            );
        }

        if (
                dayMasterForceFormation.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "일간 세력 형성 구조의 일간이 일치하지 않습니다."
            );
        }

        stemRelations =
                stemRelations == null
                        ? List.of()
                        : List.copyOf(
                        stemRelations
                );

        branchRelations =
                branchRelations == null
                        ? List.of()
                        : List.copyOf(
                        branchRelations
                );

        branchGroupRelations =
                branchGroupRelations == null
                        ? List.of()
                        : List.copyOf(
                        branchGroupRelations
                );
    }

    public StemGroundAnalysis dayMasterRooting() {

        return rooting.dayStem();
    }

    public boolean monthMainStemExposed() {

        return monthCommandDayMaster
                .mainExposed();
    }

    public List<DayMasterHiddenStemRelation>
    monthCommandDayMasterRelations() {

        return dayMasterRelations
                .monthCommandRelations();
    }

    public boolean hasStemRelations() {

        return !stemRelations.isEmpty();
    }

    public boolean hasBranchRelations() {

        return !branchRelations.isEmpty();
    }

    public boolean hasBranchGroupRelations() {

        return !branchGroupRelations.isEmpty();
    }
}