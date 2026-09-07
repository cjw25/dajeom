package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

public record LifeClaimCoverageEntry(

        LifeInterpretationCategory category,

        LifeClaimCoverageState state,

        List<LifeClaim> claims

) {

    public LifeClaimCoverageEntry {

        Objects.requireNonNull(
                category,
                "카테고리는 필수입니다."
        );

        Objects.requireNonNull(
                state,
                "Coverage 상태는 필수입니다."
        );

        claims =
                claims == null
                        ? List.of()
                        : List.copyOf(claims);

        if (
                state == LifeClaimCoverageState.COVERED
                        &&
                        claims.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "COVERED 상태에는 최소 하나의 LifeClaim이 필요합니다."
            );
        }

        if (
                state == LifeClaimCoverageState.MISSING
                        &&
                        !claims.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "MISSING 상태에는 LifeClaim이 존재할 수 없습니다."
            );
        }
    }

    public boolean covered() {

        return state
                == LifeClaimCoverageState.COVERED;
    }

    public boolean missing() {

        return state
                == LifeClaimCoverageState.MISSING;
    }
}