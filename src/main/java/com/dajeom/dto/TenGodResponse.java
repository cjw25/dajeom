package com.dajeom.dto;

import com.dajeom.model.TenGodAssignment;

/**
 * 특정 천간 또는 지장간에 배정된 십성 Response다.
 */
public record TenGodResponse(
        String sourcePosition,
        String sourcePositionKorean,
        String stem,
        String stemKorean,
        boolean visibleStem,
        String hiddenStemRole,
        String hiddenStemRoleKorean,
        String tenGod,
        String tenGodKorean,
        String tenGodChinese
) {

    public static TenGodResponse from(
            TenGodAssignment assignment
    ) {
        String role =
                assignment.hiddenStemRole() == null
                        ? null
                        : assignment
                        .hiddenStemRole()
                        .name();

        String roleKorean =
                assignment.hiddenStemRole() == null
                        ? null
                        : assignment
                        .hiddenStemRole()
                        .getKoreanName();

        return new TenGodResponse(
                assignment.sourcePosition().name(),

                assignment
                        .sourcePosition()
                        .getKoreanName(),

                assignment
                        .targetStem()
                        .getChineseCharacter(),

                assignment
                        .targetStem()
                        .getKoreanName(),

                assignment.visibleStem(),

                role,

                roleKorean,

                assignment.tenGod().name(),

                assignment
                        .tenGod()
                        .getKoreanName(),

                assignment
                        .tenGod()
                        .getChineseCharacter()
        );
    }
}