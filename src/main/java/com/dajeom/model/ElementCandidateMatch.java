package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.PillarPart;

import java.util.Objects;

/**
 * 대운/세운에서 유입되는 오행이
 * 현재 원국의 각 용신 후보군과
 * 어떤 관계인지 기록한다.
 *
 * 하나의 오행이 서로 다른 분석법에서
 * 다른 의미를 가질 수 있으므로
 * 단일 favorable 값으로 축약하지 않는다.
 */
public record ElementCandidateMatch(

        LuckPillarSource source,

        PillarPart pillarPart,

        FiveElement element,

        boolean yongshinCandidate,

        boolean huishinCandidate,

        boolean gishinCandidate,

        boolean temperatureCandidate,

        boolean mediationCandidate

) {

    public ElementCandidateMatch {

        Objects.requireNonNull(
                source,
                "운 출처는 필수입니다."
        );

        Objects.requireNonNull(
                pillarPart,
                "천간/지지 구분은 필수입니다."
        );

        Objects.requireNonNull(
                element,
                "오행은 필수입니다."
        );

        if (!source.isExternalLuck()) {

            throw new IllegalArgumentException(
                    "오행 유입 분석은 대운 또는 세운만 사용할 수 있습니다."
            );
        }
    }

    /**
     * 어떤 후보군과도 관계가 없는지 확인한다.
     */
    public boolean hasAnyCandidateMatch() {

        return yongshinCandidate
                || huishinCandidate
                || gishinCandidate
                || temperatureCandidate
                || mediationCandidate;
    }
}