package com.dajeom.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * 원국 네 천간의 통근 분석 결과.
 *
 * 점수화하지 않는다.
 * 각 천간이 실제 어느 지지의 어느 지장간과
 * 연결되는지만 보존한다.
 */
public record NatalRootAnalysis(

        StemGroundAnalysis yearStem,

        StemGroundAnalysis monthStem,

        StemGroundAnalysis dayStem,

        Optional<StemGroundAnalysis> hourStem

) {

    public NatalRootAnalysis {

        Objects.requireNonNull(
                yearStem,
                "년간 통근 분석은 필수입니다."
        );

        Objects.requireNonNull(
                monthStem,
                "월간 통근 분석은 필수입니다."
        );

        Objects.requireNonNull(
                dayStem,
                "일간 통근 분석은 필수입니다."
        );

        Objects.requireNonNull(
                hourStem,
                "시간 통근 Optional은 필수입니다."
        );
    }

    public Optional<StemGroundAnalysis> analysisAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return switch (position) {

            case YEAR ->
                    Optional.of(
                            yearStem
                    );

            case MONTH ->
                    Optional.of(
                            monthStem
                    );

            case DAY ->
                    Optional.of(
                            dayStem
                    );

            case HOUR ->
                    hourStem;
        };
    }
}