package com.dajeom.dto;

import com.dajeom.model.SajuReportSectionModel;

import java.util.List;

/**
 * 카테고리별 최종 보고서 Response다.
 */
public record SajuReportSectionResponse(
        String category,
        String categoryKorean,
        String categoryDescription,
        String title,
        String coreSummary,
        String content,
        int characterCount,
        double favorableScore,
        double challengingScore,
        double netScore,
        List<String> usedSignalCodes
) {

    public SajuReportSectionResponse {
        usedSignalCodes =
                List.copyOf(usedSignalCodes);
    }

    public static SajuReportSectionResponse from(
            SajuReportSectionModel model
    ) {
        return new SajuReportSectionResponse(
                model.category().name(),

                model
                        .category()
                        .getKoreanName(),

                model
                        .category()
                        .getDescription(),

                model.title(),

                model.coreSummary(),

                model.content(),

                model.characterCount(),

                model.favorableScore(),

                model.challengingScore(),

                model.netScore(),

                model.usedSignalCodes()
        );
    }
}