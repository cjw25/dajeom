package com.dajeom.model;

import com.dajeom.type.LuckSourceType;
import com.dajeom.type.PillarPosition;

import java.util.Objects;

/**
 * 운 상호작용에 참여하는
 * 하나의 간지 출처.
 *
 * NATAL:
 * natalPosition 사용
 *
 * DECADE_LUCK:
 * referenceValue = 대운 순서
 *
 * ANNUAL_LUCK:
 * referenceValue = 세운 연도
 */
public record LuckPillarSource(

        LuckSourceType sourceType,

        PillarPosition natalPosition,

        Integer referenceValue,

        Pillar pillar

) {

    public LuckPillarSource {

        Objects.requireNonNull(
                sourceType,
                "출처 종류는 필수입니다."
        );

        Objects.requireNonNull(
                pillar,
                "간지는 필수입니다."
        );

        switch (sourceType) {

            case NATAL -> {

                if (natalPosition == null) {

                    throw new IllegalArgumentException(
                            "원국 출처에는 기둥 위치가 필요합니다."
                    );
                }

                if (referenceValue != null) {

                    throw new IllegalArgumentException(
                            "원국 출처에는 referenceValue를 사용할 수 없습니다."
                    );
                }
            }

            case DECADE_LUCK -> {

                if (natalPosition != null) {

                    throw new IllegalArgumentException(
                            "대운 출처에는 원국 위치를 사용할 수 없습니다."
                    );
                }

                if (
                        referenceValue == null
                                || referenceValue < 1
                ) {

                    throw new IllegalArgumentException(
                            "대운 출처에는 대운 순서가 필요합니다."
                    );
                }
            }

            case ANNUAL_LUCK -> {

                if (natalPosition != null) {

                    throw new IllegalArgumentException(
                            "세운 출처에는 원국 위치를 사용할 수 없습니다."
                    );
                }

                if (referenceValue == null) {

                    throw new IllegalArgumentException(
                            "세운 출처에는 세운 연도가 필요합니다."
                    );
                }
            }
        }
    }

    public boolean isNatal() {

        return sourceType
                == LuckSourceType.NATAL;
    }

    public boolean isExternalLuck() {

        return sourceType
                != LuckSourceType.NATAL;
    }

    public String displayName() {

        return switch (sourceType) {

            case NATAL ->
                    natalPosition.getKoreanName();

            case DECADE_LUCK ->
                    referenceValue
                            + "대운";

            case ANNUAL_LUCK ->
                    referenceValue
                            + "년 세운";
        };
    }
}