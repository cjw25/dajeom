package com.dajeom.domain;

import java.util.Objects;

/**
 * 원국 천간에서 발견된 天干五合 한 건.
 *
 * =================================================
 *
 * 예:
 *
 * 丁 日主
 * +
 * 壬 正官
 *
 * → 丁壬合
 * → 五合상 木
 *
 * =================================================
 *
 * 중요:
 *
 * combinationElement는
 * 고전적인 五合의 化氣 오행을 나타낼 뿐이다.
 *
 * 이 객체가 존재한다고 해서:
 *
 * - 合化 성립
 * - 合去 성립
 * - 官 제거
 * - 殺 제거
 *
 * 를 의미하지 않는다.
 *
 * 실제 合化 여부는 별도의
 * StemTransformation 계층에서 판단한다.
 */
public record PatternStemCombinationOccurrence(

        PatternTenGodNode left,

        PatternTenGodNode right,

        FiveElement combinationElement

) {

    public PatternStemCombinationOccurrence {

        Objects.requireNonNull(
                left,
                "왼쪽 천간 노드는 필수입니다."
        );

        Objects.requireNonNull(
                right,
                "오른쪽 천간 노드는 필수입니다."
        );

        Objects.requireNonNull(
                combinationElement,
                "천간합 오행은 필수입니다."
        );

        if (left.equals(right)) {

            throw new IllegalArgumentException(
                    "동일 천간 노드 자신과 합을 만들 수 없습니다."
            );
        }

        if (left.hidden() || right.hidden()) {

            throw new IllegalArgumentException(
                    "天干五合 분석에는 일간 또는 투출 천간만 사용할 수 있습니다."
            );
        }

        if (
                !left.dayMaster()
                        &&
                        !left.visible()
        ) {

            throw new IllegalArgumentException(
                    "왼쪽 노드는 일간 또는 투출 천간이어야 합니다."
            );
        }

        if (
                !right.dayMaster()
                        &&
                        !right.visible()
        ) {

            throw new IllegalArgumentException(
                    "오른쪽 노드는 일간 또는 투출 천간이어야 합니다."
            );
        }
    }

    public boolean involvesDayMaster() {

        return left.dayMaster()
                ||
                right.dayMaster();
    }

    public boolean involvesPosition(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "위치는 필수입니다."
        );

        return left.position() == position
                ||
                right.position() == position;
    }

    public boolean involvesStem(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "천간은 필수입니다."
        );

        return left.stem() == stem
                ||
                right.stem() == stem;
    }

    public boolean involvesTenGod(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 타입은 필수입니다."
        );

        return left.hasTenGodType(type)
                ||
                right.hasTenGodType(type);
    }

    public boolean betweenTenGods(

            MonthCommandPatternCandidateType first,

            MonthCommandPatternCandidateType second

    ) {

        Objects.requireNonNull(
                first,
                "첫 번째 십성은 필수입니다."
        );

        Objects.requireNonNull(
                second,
                "두 번째 십성은 필수입니다."
        );

        return (
                left.hasTenGodType(first)
                        &&
                        right.hasTenGodType(second)
        )
                ||
                (
                        left.hasTenGodType(second)
                                &&
                                right.hasTenGodType(first)
                );
    }

    /**
     * 특정 십성의 상대편 노드를 반환.
     */
    public PatternTenGodNode counterpartOf(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 타입은 필수입니다."
        );

        if (left.hasTenGodType(type)) {

            return right;
        }

        if (right.hasTenGodType(type)) {

            return left;
        }

        throw new IllegalArgumentException(
                "이 천간합에는 요청한 십성이 존재하지 않습니다: "
                        + type
        );
    }
}