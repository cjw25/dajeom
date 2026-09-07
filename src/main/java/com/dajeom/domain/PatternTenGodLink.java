package com.dajeom.domain;

import java.util.Objects;

/**
 * 두 십성 노드 사이의 방향성 생극 연결.
 *
 * 예:
 *
 * 傷官 → 財
 * type = GENERATES
 *
 * 財 → 官
 * type = GENERATES
 *
 * 食神 → 七殺
 * type = CONTROLS
 *
 * 七殺 → 印
 * type = GENERATES
 *
 * 印 → 日主
 * type = GENERATES
 *
 * =================================================
 *
 * 주의:
 *
 * 이 Link가 존재한다고 해서
 *
 * - 食神制殺 확정
 * - 財生官 확정
 * - 殺印相生 확정
 *
 * 은 아니다.
 *
 * 오행상 직접 생극 연결이 존재한다는
 * 구조적 사실만 나타낸다.
 */
public record PatternTenGodLink(

        PatternTenGodNode source,

        PatternTenGodNode target,

        PatternTenGodLinkType type

) {

    public PatternTenGodLink {

        Objects.requireNonNull(
                source,
                "출발 노드는 필수입니다."
        );

        Objects.requireNonNull(
                target,
                "도착 노드는 필수입니다."
        );

        Objects.requireNonNull(
                type,
                "생극 연결 타입은 필수입니다."
        );

        if (
                source.equals(
                        target
                )
        ) {

            throw new IllegalArgumentException(
                    "동일 노드 자신과의 생극 Link는 만들 수 없습니다."
            );
        }
    }

    public boolean sourceVisible() {

        return source.visible();
    }

    public boolean targetVisible() {

        return target.visible();
    }

    public boolean bothVisible() {

        return sourceVisible()
                &&
                targetVisible();
    }

    public boolean sourceHidden() {

        return source.hidden();
    }

    public boolean targetHidden() {

        return target.hidden();
    }

    public boolean targetDayMaster() {

        return target.dayMaster();
    }

    public boolean sourceType(
            MonthCommandPatternCandidateType type
    ) {

        return source.hasTenGodType(
                type
        );
    }

    public boolean targetType(
            MonthCommandPatternCandidateType type
    ) {

        return target.hasTenGodType(
                type
        );
    }

    public boolean matches(

            MonthCommandPatternCandidateType sourceType,

            MonthCommandPatternCandidateType targetType,

            PatternTenGodLinkType linkType

    ) {

        return type
                == linkType

                &&

                sourceType(
                        sourceType
                )

                &&

                targetType(
                        targetType
                );
    }
}