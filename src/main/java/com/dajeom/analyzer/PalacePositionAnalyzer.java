package com.dajeom.analyzer;

import com.dajeom.model.FourPillars;
import com.dajeom.model.PalaceBranchEvidence;
import com.dajeom.model.PalacePositionAnalysis;
import com.dajeom.model.PalaceStemEvidence;
import com.dajeom.rule.PalacePositionRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalacePositionContextType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 12차-2.
 *
 * FourPillars를:
 *
 * YEAR
 * MONTH
 * DAY
 * HOUR
 *
 * Palace로 분리하고
 *
 * 각 Palace를:
 *
 * Stem Evidence
 * Branch Evidence
 *
 * 로 나눈다.
 *
 * =================================================
 *
 * 알려진 시간:
 *
 * 4 Palace
 * 4 Stem Evidence
 * 4 Branch Evidence
 *
 * =================================================
 *
 * 시간 미상:
 *
 * 3 Palace
 * 3 Stem Evidence
 * 3 Branch Evidence
 *
 * HOUR Evidence는 생성하지 않는다.
 *
 * =================================================
 *
 * 이번 단계에서는:
 *
 * - 십성
 * - 지장간
 * - 12운성
 * - 합충형파해
 * - 합화
 *
 * 를 계산하지 않는다.
 */
@Component
public class PalacePositionAnalyzer {

    private final PalacePositionRule
            palacePositionRule;

    public PalacePositionAnalyzer(
            PalacePositionRule palacePositionRule
    ) {

        this.palacePositionRule =
                Objects.requireNonNull(
                        palacePositionRule,
                        "PalacePositionRule이 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 12-2 API
     * =================================================
     */
    public PalacePositionAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "궁위 위치 분석에는 FourPillars가 필요합니다."
        );

        HeavenlyStem dayMaster =
                extractDayMaster(
                        fourPillars
                );

        List<PalaceStemEvidence> stemEvidences =
                new ArrayList<>();

        List<PalaceBranchEvidence> branchEvidences =
                new ArrayList<>();

        /*
         * =================================================
         * YEAR
         * =================================================
         */

        addPalace(
                stemEvidences,
                branchEvidences,
                dayMaster,
                PalacePosition.YEAR,
                extractYearStem(
                        fourPillars
                ),
                extractYearBranch(
                        fourPillars
                )
        );

        /*
         * =================================================
         * MONTH
         * =================================================
         */

        addPalace(
                stemEvidences,
                branchEvidences,
                dayMaster,
                PalacePosition.MONTH,
                extractMonthStem(
                        fourPillars
                ),
                extractMonthBranch(
                        fourPillars
                )
        );

        /*
         * =================================================
         * DAY
         * =================================================
         */

        addPalace(
                stemEvidences,
                branchEvidences,
                dayMaster,
                PalacePosition.DAY,
                extractDayStem(
                        fourPillars
                ),
                extractDayBranch(
                        fourPillars
                )
        );

        /*
         * =================================================
         * HOUR
         * =================================================
         *
         * 시주가 실제 존재할 때만 생성한다.
         */

        boolean completeChart =
                fourPillars.hourPillar()
                        != null;

        if (
                completeChart
        ) {

            addPalace(
                    stemEvidences,
                    branchEvidences,
                    dayMaster,
                    PalacePosition.HOUR,
                    extractHourStem(
                            fourPillars
                    ),
                    extractHourBranch(
                            fourPillars
                    )
            );
        }

        int knownPalaceCount =
                completeChart
                        ? 4
                        : 3;

        return new PalacePositionAnalysis(
                dayMaster,
                stemEvidences,
                branchEvidences,
                knownPalaceCount,
                completeChart
        );
    }

    /*
     * =================================================
     * Palace 생성
     * =================================================
     */

    private void addPalace(

            List<PalaceStemEvidence> stemEvidences,

            List<PalaceBranchEvidence> branchEvidences,

            HeavenlyStem dayMaster,

            PalacePosition position,

            HeavenlyStem stem,

            EarthlyBranch branch

    ) {

        Objects.requireNonNull(
                stemEvidences,
                "궁위 천간 Evidence 목록이 필요합니다."
        );

        Objects.requireNonNull(
                branchEvidences,
                "궁위 지지 Evidence 목록이 필요합니다."
        );

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                position,
                "궁위 위치가 필요합니다."
        );

        Objects.requireNonNull(
                stem,
                "궁위 천간이 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "궁위 지지가 필요합니다."
        );

        PalacePositionContextType positionContext =
                palacePositionRule
                        .contextOf(
                                position
                        );

        boolean dayMasterAnchor =
                position == PalacePosition.DAY;

        stemEvidences.add(
                new PalaceStemEvidence(
                        dayMaster,
                        position,
                        positionContext,
                        stem,
                        dayMasterAnchor
                )
        );

        branchEvidences.add(
                new PalaceBranchEvidence(
                        dayMaster,
                        position,
                        positionContext,
                        branch
                )
        );
    }

    /*
     * =================================================
     * FourPillars 추출
     * =================================================
     *
     * 여기만 FourPillars 내부 Pillar API와
     * 직접 결합한다.
     *
     * 실제 프로젝트의 Pillar accessor가
     * stem()/branch()가 아니라면
     * 아래 메서드만 현재 API에 맞춘다.
     *
     * 기존 FourPillars 또는 Pillar에
     * 호환용 accessor를 추가하지 않는다.
     */

    private HeavenlyStem extractDayMaster(
            FourPillars fourPillars
    ) {

        return extractDayStem(
                fourPillars
        );
    }

    private HeavenlyStem extractYearStem(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars.yearPillar(),
                "년주가 필요합니다."
        );

        return Objects.requireNonNull(
                fourPillars
                        .yearPillar()
                        .stem(),
                "년간을 찾을 수 없습니다."
        );
    }

    private EarthlyBranch extractYearBranch(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars.yearPillar(),
                "년주가 필요합니다."
        );

        return Objects.requireNonNull(
                fourPillars
                        .yearPillar()
                        .branch(),
                "년지를 찾을 수 없습니다."
        );
    }

    private HeavenlyStem extractMonthStem(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars.monthPillar(),
                "월주가 필요합니다."
        );

        return Objects.requireNonNull(
                fourPillars
                        .monthPillar()
                        .stem(),
                "월간을 찾을 수 없습니다."
        );
    }

    private EarthlyBranch extractMonthBranch(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars.monthPillar(),
                "월주가 필요합니다."
        );

        return Objects.requireNonNull(
                fourPillars
                        .monthPillar()
                        .branch(),
                "월지를 찾을 수 없습니다."
        );
    }

    private HeavenlyStem extractDayStem(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars.dayPillar(),
                "일주가 필요합니다."
        );

        return Objects.requireNonNull(
                fourPillars
                        .dayPillar()
                        .stem(),
                "일간을 찾을 수 없습니다."
        );
    }

    private EarthlyBranch extractDayBranch(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars.dayPillar(),
                "일주가 필요합니다."
        );

        return Objects.requireNonNull(
                fourPillars
                        .dayPillar()
                        .branch(),
                "일지를 찾을 수 없습니다."
        );
    }

    private HeavenlyStem extractHourStem(
            FourPillars fourPillars
    ) {

        if (
                fourPillars.hourPillar() == null
        ) {

            throw new IllegalStateException(
                    "시간 미상 원국에서는 시간 Palace 천간을 추출할 수 없습니다."
            );
        }

        return Objects.requireNonNull(
                fourPillars
                        .hourPillar()
                        .stem(),
                "시간을 찾을 수 없습니다."
        );
    }

    private EarthlyBranch extractHourBranch(
            FourPillars fourPillars
    ) {

        if (
                fourPillars.hourPillar() == null
        ) {

            throw new IllegalStateException(
                    "시간 미상 원국에서는 시지 Palace를 추출할 수 없습니다."
            );
        }

        return Objects.requireNonNull(
                fourPillars
                        .hourPillar()
                        .branch(),
                "시지를 찾을 수 없습니다."
        );
    }
}