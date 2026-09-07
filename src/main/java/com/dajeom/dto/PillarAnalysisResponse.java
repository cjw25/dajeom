package com.dajeom.dto;

import com.dajeom.calculation.HiddenStemCalculator;
import com.dajeom.domain.TenGod;
import com.dajeom.domain.TwelveStage;

import java.util.List;
import java.util.Objects;

public record PillarAnalysisResponse(
        boolean dayMaster,

        String stemTenGod,
        String stemTenGodHanja,
        String stemDisplay,

        String branchTenGod,
        String branchTenGodHanja,

        /*
         * 일간 기준 12운성.
         *
         * 천을귀인 화면에서 괄호 밖 값.
         */
        String dayMasterTwelveStage,
        String dayMasterTwelveStageHanja,

        /*
         * 해당 기둥 천간 자체를 해당 지지에 대입한 12운성.
         *
         * 천을귀인 화면에서 괄호 안 값.
         */
        String pillarStemTwelveStage,
        String pillarStemTwelveStageHanja,

        List<HiddenStemResponse> hiddenStems
) {

    public PillarAnalysisResponse {

        Objects.requireNonNull(
                stemTenGod,
                "천간 십성이 필요합니다."
        );

        Objects.requireNonNull(
                stemTenGodHanja,
                "천간 십성 한자가 필요합니다."
        );

        Objects.requireNonNull(
                stemDisplay,
                "천간 표시값이 필요합니다."
        );

        Objects.requireNonNull(
                branchTenGod,
                "지지 대표 십성이 필요합니다."
        );

        Objects.requireNonNull(
                branchTenGodHanja,
                "지지 대표 십성 한자가 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterTwelveStage,
                "일간 기준 12운성이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterTwelveStageHanja,
                "일간 기준 12운성 한자가 필요합니다."
        );

        Objects.requireNonNull(
                pillarStemTwelveStage,
                "기둥 천간 기준 12운성이 필요합니다."
        );

        Objects.requireNonNull(
                pillarStemTwelveStageHanja,
                "기둥 천간 기준 12운성 한자가 필요합니다."
        );

        hiddenStems =
                hiddenStems == null
                        ? List.of()
                        : List.copyOf(
                        hiddenStems
                );
    }

    public static PillarAnalysisResponse of(
            TenGod stemTenGod,
            boolean dayMaster,
            TenGod branchTenGod,
            TwelveStage dayMasterTwelveStage,
            TwelveStage pillarStemTwelveStage,
            List<HiddenStemCalculator.HiddenStemTenGod> hiddenStems
    ) {

        Objects.requireNonNull(
                stemTenGod,
                "천간 십성이 필요합니다."
        );

        Objects.requireNonNull(
                branchTenGod,
                "지지 대표 십성이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterTwelveStage,
                "일간 기준 12운성이 필요합니다."
        );

        Objects.requireNonNull(
                pillarStemTwelveStage,
                "기둥 천간 기준 12운성이 필요합니다."
        );

        Objects.requireNonNull(
                hiddenStems,
                "지장간 결과가 필요합니다."
        );

        List<HiddenStemResponse> hiddenStemResponses =
                hiddenStems
                        .stream()
                        .map(
                                HiddenStemResponse::from
                        )
                        .toList();

        String stemDisplay =
                dayMaster
                        ? "일간(나)"
                        : stemTenGod.korean();

        return new PillarAnalysisResponse(
                dayMaster,

                stemTenGod.korean(),
                stemTenGod.hanja(),
                stemDisplay,

                branchTenGod.korean(),
                branchTenGod.hanja(),

                dayMasterTwelveStage.korean(),
                dayMasterTwelveStage.hanja(),

                pillarStemTwelveStage.korean(),
                pillarStemTwelveStage.hanja(),

                hiddenStemResponses
        );
    }
}