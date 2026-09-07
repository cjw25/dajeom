package com.dajeom.domain;

import java.util.List;

public record HiddenStemExposureAnalysis(

        List<HiddenStemExposureHit> hits

) {

    public HiddenStemExposureAnalysis {

        hits =
                hits == null
                        ? List.of()
                        : List.copyOf(
                        hits
                );
    }

    public List<HiddenStemExposureHit> monthCommandHits() {

        return hits
                .stream()
                .filter(
                        HiddenStemExposureHit::fromMonthCommand
                )
                .toList();
    }

    public boolean hasMonthCommandExposure() {

        return hits
                .stream()
                .anyMatch(
                        HiddenStemExposureHit::fromMonthCommand
                );
    }
}