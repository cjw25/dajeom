package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

public record StemGroundAnalysis(

        HeavenlyStem targetStem,

        List<StemGroundHit> hits

) {

    public StemGroundAnalysis {

        Objects.requireNonNull(
                targetStem,
                "분석 대상 천간은 필수입니다."
        );

        hits =
                hits == null
                        ? List.of()
                        : List.copyOf(
                        hits
                );
    }

    public List<StemGroundHit> exactStemHits() {

        return hits
                .stream()
                .filter(
                        StemGroundHit::exactStem
                )
                .toList();
    }

    public List<StemGroundHit> sameElementOtherStemHits() {

        return hits
                .stream()
                .filter(
                        StemGroundHit::sameElementOtherStem
                )
                .toList();
    }

    public boolean hasExactStemConnection() {

        return hits
                .stream()
                .anyMatch(
                        StemGroundHit::exactStem
                );
    }

    public boolean hasSameElementOtherStemConnection() {

        return hits
                .stream()
                .anyMatch(
                        StemGroundHit::sameElementOtherStem
                );
    }

    public List<StemGroundHit> hitsAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return hits
                .stream()
                .filter(
                        hit ->
                                hit.branchPosition()
                                        == position
                )
                .toList();
    }
}