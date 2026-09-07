package com.dajeom.domain;

import java.util.Objects;

/**
 * 투출 십성 천간 하나가
 * 지장간에서 발견한 통근 지점.
 *
 * =================================================
 *
 * exactStem = true
 *
 * 예:
 * 甲 투출 + 寅中甲
 *
 * =================================================
 *
 * exactStem = false
 *
 * 같은 십성 계열 오행의 다른 음양.
 *
 * 예:
 * 甲 투출 + 卯中乙
 *
 * 둘 다 통근 기반으로 보되,
 * 완전 동일 천간인지 여부는 보존한다.
 */
public record PatternTenGodRootHit(

        PatternTenGodNode visibleNode,

        PatternTenGodNode hiddenNode,

        boolean exactStem

) {

    public PatternTenGodRootHit {

        Objects.requireNonNull(
                visibleNode,
                "투출 천간 노드는 필수입니다."
        );

        Objects.requireNonNull(
                hiddenNode,
                "지장간 노드는 필수입니다."
        );

        if (!visibleNode.visible()) {

            throw new IllegalArgumentException(
                    "visibleNode는 투출 천간이어야 합니다."
            );
        }

        if (!hiddenNode.hidden()) {

            throw new IllegalArgumentException(
                    "hiddenNode는 지장간이어야 합니다."
            );
        }

        if (
                exactStem
                        !=
                        (
                                visibleNode.stem()
                                        == hiddenNode.stem()
                        )
        ) {

            throw new IllegalArgumentException(
                    "exactStem 값이 실제 천간 비교 결과와 일치하지 않습니다."
            );
        }
    }

    public PillarPosition rootPosition() {

        return hiddenNode.position();
    }

    public EarthlyBranch rootBranch() {

        return hiddenNode
                .branch()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "지장간 노드에 지지 정보가 없습니다."
                                )
                );
    }

    public boolean monthRoot() {

        return hiddenNode.position()
                == PillarPosition.MONTH;
    }
}