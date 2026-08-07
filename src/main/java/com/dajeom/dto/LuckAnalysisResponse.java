package com.dajeom.dto;

import com.dajeom.model.DecadeLuck;
import com.dajeom.model.LuckAnalysis;

import java.time.LocalDate;
import java.util.List;

public record LuckAnalysisResponse(
        String direction,
        String directionKorean,
        LuckStartAgeResponse startAge,
        LocalDate startDate,
        SolarTermPointResponse referenceSolarTerm,
        DecadeLuckResponse currentDecadeLuck,
        List<DecadeLuckResponse> decadeLucks,
        AnnualLuckResponse annualLuck,
        String ruleVersion
) {

    public LuckAnalysisResponse {
        decadeLucks =
                List.copyOf(decadeLucks);
    }

    public static LuckAnalysisResponse from(
            LuckAnalysis analysis
    ) {
        DecadeLuck current =
                analysis.currentDecadeLuck();

        return new LuckAnalysisResponse(
                analysis.direction().name(),

                analysis
                        .direction()
                        .getKoreanName(),

                LuckStartAgeResponse.from(
                        analysis.startAge()
                ),

                analysis.startDate(),

                SolarTermPointResponse.from(
                        analysis.referenceSolarTerm()
                ),

                current == null
                        ? null
                        : DecadeLuckResponse.from(
                        current
                ),

                analysis.decadeLucks()
                        .stream()
                        .map(
                                DecadeLuckResponse::from
                        )
                        .toList(),

                AnnualLuckResponse.from(
                        analysis.annualLuck()
                ),

                analysis.ruleVersion()
        );
    }
}