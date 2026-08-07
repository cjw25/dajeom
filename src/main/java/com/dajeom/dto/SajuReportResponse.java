package com.dajeom.dto;

import com.dajeom.model.SajuReport;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 전체 사주 보고서 Response다.
 */
public record SajuReportResponse(
        int targetYear,
        String fullSajuChinese,
        String fullSajuKorean,
        List<SajuReportSectionResponse> sections,
        int sectionCount,
        int totalCharacterCount,
        LocalDateTime generatedAt,
        String generationMode,
        String ruleVersion
) {

    public SajuReportResponse {
        sections = List.copyOf(sections);
    }

    public static SajuReportResponse from(
            SajuReport report
    ) {
        List<SajuReportSectionResponse> sections =
                report.sections()
                        .stream()
                        .map(
                                SajuReportSectionResponse::from
                        )
                        .toList();

        return new SajuReportResponse(
                report.targetYear(),

                report.fullSajuChinese(),

                report.fullSajuKorean(),

                sections,

                sections.size(),

                report.totalCharacterCount(),

                report.generatedAt(),

                report.generationMode(),

                report.ruleVersion()
        );
    }
}