package com.dajeom.calculation;

import com.dajeom.domain.DayMasterGroundAnalysis;
import com.dajeom.domain.DayMasterGroundPattern;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.StemGroundAnalysis;
import com.dajeom.domain.StemGroundHit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 일간의 지지 뿌리 구조를 정리한다.
 *
 * 점수 없음.
 * 가중치 없음.
 * 신강/신약 판정 없음.
 * 득지 판정 없음.
 *
 * RootingCalculator가 계산한 사실을
 * 일간 관점으로 정리하는 역할만 한다.
 */
@Component
public class DayMasterGroundCalculator {

    public DayMasterGroundAnalysis analyze(

            HeavenlyStem dayMaster,

            StemGroundAnalysis rooting

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                rooting,
                "일간 통근 분석은 필수입니다."
        );

        if (
                rooting.targetStem()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "통근 분석 대상 천간이 일간과 일치하지 않습니다."
            );
        }

        List<StemGroundHit> exactRoots =
                rooting.exactStemHits();

        List<StemGroundHit> sameElementRoots =
                rooting.sameElementOtherStemHits();

        DayMasterGroundPattern pattern =
                resolvePattern(
                        exactRoots,
                        sameElementRoots
                );

        return new DayMasterGroundAnalysis(
                dayMaster,
                pattern,
                exactRoots,
                sameElementRoots
        );
    }

    private DayMasterGroundPattern resolvePattern(

            List<StemGroundHit> exactRoots,

            List<StemGroundHit> sameElementRoots

    ) {

        boolean exact =
                exactRoots != null
                        &&
                        !exactRoots.isEmpty();

        boolean sameElement =
                sameElementRoots != null
                        &&
                        !sameElementRoots.isEmpty();

        if (
                exact
                        &&
                        sameElement
        ) {

            return DayMasterGroundPattern
                    .EXACT_AND_SAME_ELEMENT;
        }

        if (exact) {

            return DayMasterGroundPattern
                    .EXACT_STEM_ONLY;
        }

        if (sameElement) {

            return DayMasterGroundPattern
                    .SAME_ELEMENT_ONLY;
        }

        return DayMasterGroundPattern
                .NO_ROOT_CONNECTION;
    }
}