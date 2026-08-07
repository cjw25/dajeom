package com.dajeom.model;

import com.dajeom.type.LuckDirection;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * 대운과 세운을 포함한
 * 운세 계산 결과다.
 */
public record LuckAnalysis(

        LuckDirection direction,

        LuckStartAge startAge,

        LocalDate startDate,

        SolarTermPoint referenceSolarTerm,

        List<DecadeLuck> decadeLucks,

        AnnualLuck annualLuck,

        String ruleVersion
) {

    public LuckAnalysis {

        Objects.requireNonNull(
                direction,
                "대운 방향은 필수입니다."
        );

        Objects.requireNonNull(
                startAge,
                "대운 시작 나이는 필수입니다."
        );

        Objects.requireNonNull(
                startDate,
                "대운 시작일은 필수입니다."
        );

        Objects.requireNonNull(
                referenceSolarTerm,
                "대운 계산 기준 절입은 필수입니다."
        );

        Objects.requireNonNull(
                decadeLucks,
                "대운 목록은 필수입니다."
        );

        Objects.requireNonNull(
                annualLuck,
                "세운은 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "운세 규칙 버전은 필수입니다."
        );

        decadeLucks =
                List.copyOf(
                        decadeLucks
                );
    }

    public DecadeLuck currentDecadeLuck() {

        return decadeLucks.stream()
                .filter(
                        DecadeLuck::current
                )
                .findFirst()
                .orElse(null);
    }
}