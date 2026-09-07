package com.dajeom.calculation;

import com.dajeom.domain.DayMasterRelationAnalysis;
import com.dajeom.domain.DayMasterSupportStructure;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandDayMasterAnalysis;
import com.dajeom.domain.StemGroundAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 일간 중심의 생조/동류/설기/극제 구조 조립기.
 *
 * 새로운 명리 판단을 하지 않는다.
 *
 * 계산 완료된:
 *
 * - 일간 통근
 * - 월령과 일간 관계
 * - 전체 생극관계
 *
 * 를 하나의 구조로 묶는다.
 */
@Component
public class DayMasterSupportStructureCalculator {

    public DayMasterSupportStructure analyze(

            HeavenlyStem dayMaster,

            StemGroundAnalysis dayMasterRooting,

            MonthCommandDayMasterAnalysis monthCommand,

            DayMasterRelationAnalysis relations

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                dayMasterRooting,
                "일간 통근 분석은 필수입니다."
        );

        Objects.requireNonNull(
                monthCommand,
                "월령-일간 관계는 필수입니다."
        );

        Objects.requireNonNull(
                relations,
                "일간 생극관계는 필수입니다."
        );

        return new DayMasterSupportStructure(
                dayMaster,
                dayMasterRooting,
                monthCommand,
                relations
        );
    }
}