package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.HiddenStemComponent;
import com.dajeom.domain.HiddenStemExposureAnalysis;
import com.dajeom.domain.HiddenStemExposureHit;
import com.dajeom.domain.MonthCommandAnalysis;
import com.dajeom.domain.PillarPosition;
import com.dajeom.rule.HiddenStemSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 월령 구조 분석기.
 *
 * 담당:
 *
 * - 월지의 지장간 조회
 * - 월지 지장간의 천간 투출 결과 추출
 * - MonthCommandAnalysis 생성
 *
 * 담당하지 않음:
 *
 * - 월주 계산
 * - 절기 계산
 * - 신강/신약 판단
 * - 격국 판단
 * - 점수 계산
 * - 강약 수치 계산
 *
 * 이 클래스는 명리 판단 이전의
 * 사실 구조만 만든다.
 */
@Component
public class MonthCommandCalculator {

    private final HiddenStemSource hiddenStemSource;

    public MonthCommandCalculator(
            HiddenStemSource hiddenStemSource
    ) {

        this.hiddenStemSource =
                Objects.requireNonNull(
                        hiddenStemSource,
                        "HiddenStemSource는 필수입니다."
                );
    }

    public MonthCommandAnalysis analyze(

            EarthlyBranch monthBranch,

            HiddenStemExposureAnalysis exposureAnalysis

    ) {

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                exposureAnalysis,
                "지장간 투출 분석 결과는 필수입니다."
        );

        List<HiddenStemComponent> hiddenStems =
                hiddenStemSource.hiddenStemsOf(
                        monthBranch
                );

        if (hiddenStems == null) {

            hiddenStems =
                    List.of();
        }

        List<HiddenStemExposureHit> monthExposureHits =
                exposureAnalysis
                        .hits()
                        .stream()
                        .filter(
                                hit ->
                                        hit.sourceBranchPosition()
                                                == PillarPosition.MONTH
                        )
                        .filter(
                                hit ->
                                        hit.sourceBranch()
                                                == monthBranch
                        )
                        .toList();

        return new MonthCommandAnalysis(
                monthBranch,
                hiddenStems,
                monthExposureHits
        );
    }
}