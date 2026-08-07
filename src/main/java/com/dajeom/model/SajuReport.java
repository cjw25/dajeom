package com.dajeom.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 전체 카테고리 보고서 결과다.
 */
public record SajuReport(
        int targetYear,
        String fullSajuChinese,
        String fullSajuKorean,
        List<SajuReportSectionModel> sections,
        int totalCharacterCount,
        LocalDateTime generatedAt,
        String generationMode,
        String ruleVersion
) {

    public SajuReport {
        Objects.requireNonNull(
                fullSajuChinese,
                "한자 사주팔자는 필수입니다."
        );

        Objects.requireNonNull(
                fullSajuKorean,
                "한글 사주팔자는 필수입니다."
        );

        Objects.requireNonNull(
                sections,
                "보고서 섹션은 필수입니다."
        );

        Objects.requireNonNull(
                generatedAt,
                "보고서 생성 시각은 필수입니다."
        );

        Objects.requireNonNull(
                generationMode,
                "보고서 생성 방식은 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "보고서 규칙 버전은 필수입니다."
        );

        sections = List.copyOf(sections);
    }
}