package com.dajeom.dto;

import com.dajeom.domain.TianyiNobleman;

import java.util.List;
import java.util.Objects;

public record TianyiNoblemanResponse(
        String dayStem,
        String dayStemKorean,

        String first,
        String firstKorean,

        String second,
        String secondKorean,

        String branches,

        boolean existsInOriginalChart,

        List<HitResponse> hits
) {

    public TianyiNoblemanResponse {

        hits =
                hits == null
                        ? List.of()
                        : List.copyOf(
                        hits
                );
    }

    public static TianyiNoblemanResponse from(
            TianyiNobleman value
    ) {

        Objects.requireNonNull(
                value,
                "천을귀인 결과가 필요합니다."
        );

        List<HitResponse> hits =
                value.hits()
                        .stream()
                        .map(
                                HitResponse::from
                        )
                        .toList();

        return new TianyiNoblemanResponse(
                value.dayStem().hanja(),
                value.dayStem().korean(),

                value.first().hanja(),
                value.first().korean(),

                value.second().hanja(),
                value.second().korean(),

                value.hanja(),

                value.existsInOriginalChart(),

                hits
        );
    }

    public record HitResponse(
            String position,
            String positionKorean,
            String branch,
            String branchKorean
    ) {

        public static HitResponse from(
                TianyiNobleman.Hit hit
        ) {

            Objects.requireNonNull(
                    hit,
                    "천을귀인 적중 결과가 필요합니다."
            );

            return new HitResponse(
                    hit.position().name(),
                    hit.position().korean(),

                    hit.branch().hanja(),
                    hit.branch().korean()
            );
        }
    }
}