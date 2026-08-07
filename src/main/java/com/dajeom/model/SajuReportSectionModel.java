package com.dajeom.model;

import com.dajeom.type.SajuReportCategory;

import java.util.List;
import java.util.Objects;

/**
 * 카테고리별로 생성된 최종 사주 보고서다.
 */
public record SajuReportSectionModel(
        SajuReportCategory category,
        String title,
        String coreSummary,
        String content,
        int characterCount,
        double favorableScore,
        double challengingScore,
        double netScore,
        List<String> usedSignalCodes
) {

    public SajuReportSectionModel {
        Objects.requireNonNull(
                category,
                "보고서 카테고리는 필수입니다."
        );

        Objects.requireNonNull(
                title,
                "보고서 제목은 필수입니다."
        );

        Objects.requireNonNull(
                coreSummary,
                "핵심 요약은 필수입니다."
        );

        Objects.requireNonNull(
                content,
                "보고서 본문은 필수입니다."
        );

        Objects.requireNonNull(
                usedSignalCodes,
                "사용된 해석 신호 코드는 필수입니다."
        );

        if (characterCount < 0) {
            throw new IllegalArgumentException(
                    "문자 수는 음수일 수 없습니다."
            );
        }

        usedSignalCodes =
                List.copyOf(usedSignalCodes);
    }
}