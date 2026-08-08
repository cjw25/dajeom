package com.dajeom.model;

import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.StructureType;
import com.dajeom.type.TenGod;

import java.util.List;
import java.util.Objects;

/**
 * 원국의 기본 격국/구조 분석 결과.
 *
 * V1에서는 월령의 정기와
 * 해당 천간의 투간 여부를 중심으로
 * 기본 구조를 판단한다.
 *
 * 이것만으로 고전 명리의 모든 격국을
 * 확정한다는 의미는 아니다.
 */
public record StructureAnalysis(

        StructureType type,

        EarthlyBranch monthBranch,

        HeavenlyStem monthMainStem,

        TenGod monthMainTenGod,

        List<PillarPosition> exposedPositions,

        AnalysisConfidence confidence,

        String ruleVersion

) {

    public StructureAnalysis {

        Objects.requireNonNull(
                type,
                "격국 종류는 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                monthMainStem,
                "월지 정기는 필수입니다."
        );

        Objects.requireNonNull(
                monthMainTenGod,
                "월지 정기 십성은 필수입니다."
        );

        Objects.requireNonNull(
                exposedPositions,
                "투간 위치 목록은 필수입니다."
        );

        Objects.requireNonNull(
                confidence,
                "분석 신뢰도는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "Rule 버전은 필수입니다."
        );

        exposedPositions =
                List.copyOf(
                        exposedPositions
                );
    }

    /**
     * 월지 정기가 천간에 투출되어 있는지.
     */
    public boolean isMainStemExposed() {

        return !exposedPositions.isEmpty();
    }

    /**
     * 현재 V1 기준에서
     * 기본 격국 후보가 결정되었는지.
     */
    public boolean isDetermined() {

        return type
                != StructureType.UNDETERMINED;
    }
}