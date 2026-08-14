package com.dajeom.engine;

import com.dajeom.analyzer.UsefulGodMethodAnalyzer;
import com.dajeom.analyzer.UsefulGodRelationAnalyzer;
import com.dajeom.analyzer.UsefulGodSelectionAnalyzer;
import com.dajeom.model.ClimateHealthAnalysis;
import com.dajeom.model.UsefulGodAnalysis;
import com.dajeom.model.UsefulGodMethodAnalysis;
import com.dajeom.model.UsefulGodRelationAnalysis;
import com.dajeom.model.UsefulGodSelectionAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 7차-10.
 *
 * 용신·희신·기신 전체 분석 Engine.
 *
 * =================================================
 *
 * 입력
 *
 * 6-10
 * ClimateHealthAnalysis
 *
 * =================================================
 *
 * 실행
 *
 * 7-1
 * UsefulGodMethodAnalyzer
 *
 * ↓
 *
 * 7-2 ~ 7-8
 * UsefulGodSelectionAnalyzer
 *
 * 내부에서:
 *
 * - BalancingUsefulGodAnalyzer
 * - ClimateUsefulGodAnalyzer
 * - StructureUsefulGodAnalyzer
 * - RemedyUsefulGodAnalyzer
 * - PassageUsefulGodAnalyzer
 * - FollowStructureUsefulGodAnalyzer
 *
 * 를 실행하고
 *
 * UsefulGodSelectionAnalysis를 만든다.
 *
 * ↓
 *
 * 7-9
 * UsefulGodRelationAnalyzer
 *
 * ↓
 *
 * 7-10
 * UsefulGodAnalysis
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 이 Engine에서는:
 *
 * - 새로운 용신 점수 계산
 * - 새로운 신강신약 계산
 * - 새로운 격국 계산
 * - 새로운 조후 계산
 * - 임의의 단일 용신 선택
 *
 * 을 하지 않는다.
 *
 * 이전 단계의 결과를 순서대로 실행·통합한다.
 */
@Component
public class UsefulGodAnalysisEngine {

    private final UsefulGodMethodAnalyzer
            usefulGodMethodAnalyzer;

    private final UsefulGodSelectionAnalyzer
            usefulGodSelectionAnalyzer;

    private final UsefulGodRelationAnalyzer
            usefulGodRelationAnalyzer;

    public UsefulGodAnalysisEngine(

            UsefulGodMethodAnalyzer
                    usefulGodMethodAnalyzer,

            UsefulGodSelectionAnalyzer
                    usefulGodSelectionAnalyzer,

            UsefulGodRelationAnalyzer
                    usefulGodRelationAnalyzer

    ) {

        this.usefulGodMethodAnalyzer =
                Objects.requireNonNull(
                        usefulGodMethodAnalyzer,
                        "UsefulGodMethodAnalyzer가 필요합니다."
                );

        this.usefulGodSelectionAnalyzer =
                Objects.requireNonNull(
                        usefulGodSelectionAnalyzer,
                        "UsefulGodSelectionAnalyzer가 필요합니다."
                );

        this.usefulGodRelationAnalyzer =
                Objects.requireNonNull(
                        usefulGodRelationAnalyzer,
                        "UsefulGodRelationAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 정식 7차 진입점
     * =================================================
     *
     * 6-10 ClimateHealthAnalysis를 받아
     * 7-1부터 7-9까지 순서대로 실행한다.
     */
    public UsefulGodAnalysis analyze(
            ClimateHealthAnalysis climateHealthAnalysis
    ) {

        Objects.requireNonNull(
                climateHealthAnalysis,
                "6-10 조후·병약·통관 통합 분석이 필요합니다."
        );

        /*
         * =================================================
         * 7-1
         *
         * 용신법 분리 및 적용도 판정
         * =================================================
         */
        UsefulGodMethodAnalysis methodAnalysis =
                usefulGodMethodAnalyzer
                        .analyze(
                                climateHealthAnalysis
                        );

        return analyze(
                methodAnalysis
        );
    }

    /**
     * =================================================
     * 7-1 결과가 이미 계산되어 있는 경우
     * =================================================
     *
     * 동일한 7-1을 다시 계산하지 않고
     * 7-2부터 이어서 실행한다.
     */
    public UsefulGodAnalysis analyze(
            UsefulGodMethodAnalysis methodAnalysis
    ) {

        Objects.requireNonNull(
                methodAnalysis,
                "7-1 용신법 분석이 필요합니다."
        );

        /*
         * =================================================
         * 7-2 ~ 7-8
         * =================================================
         *
         * UsefulGodSelectionAnalyzer 내부에서:
         *
         * 7-2 억부
         * 7-3 조후
         * 7-4 격국
         * 7-5 병약
         * 7-6 통관
         * 7-7 종격
         * 7-8 통합
         *
         * 을 순차 실행한다.
         */
        UsefulGodSelectionAnalysis selectionAnalysis =
                usefulGodSelectionAnalyzer
                        .analyze(
                                methodAnalysis
                        );

        /*
         * =================================================
         * 7-9
         *
         * 용신을 기준으로
         * 희신·기신·조건부·중립 판정
         * =================================================
         */
        UsefulGodRelationAnalysis relationAnalysis =
                usefulGodRelationAnalyzer
                        .analyze(
                                selectionAnalysis
                        );

        /*
         * =================================================
         * 7-10
         *
         * 최종 통합
         * =================================================
         */
        return new UsefulGodAnalysis(
                methodAnalysis.dayMaster(),
                methodAnalysis.dayMasterElement(),
                methodAnalysis.monthBranch(),
                methodAnalysis,
                selectionAnalysis,
                relationAnalysis,
                methodAnalysis.completeChart()
        );
    }
}