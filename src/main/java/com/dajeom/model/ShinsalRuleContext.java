package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.ShinsalLayer;
import com.dajeom.type.ShinsalPointKind;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 신살 Rule 실행 Context.
 *
 * 실제 존재하는 천간/지지 Point만 포함한다.
 *
 * 출생시간 미상인 경우:
 *
 * HOUR Stem / Branch를 생성하지 않는다.
 */
public record ShinsalRuleContext(

        boolean completeChart,

        List<ShinsalPoint> points

) {

    public ShinsalRuleContext {

        points =
                points == null
                        ? List.of()
                        : List.copyOf(
                        points
                );

        if (
                points.isEmpty()
        ) {
            throw new IllegalArgumentException(
                    "ShinsalRuleContext에는 Point가 필요합니다."
            );
        }

        for (
                ShinsalPoint point
                : points
        ) {
            Objects.requireNonNull(
                    point,
                    "ShinsalPoint는 null일 수 없습니다."
            );
        }

        /*
         * 동일 위치 Point 중복 금지
         */
        for (
                int first = 0;
                first < points.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < points.size();
                    second++
            ) {

                ShinsalPoint a =
                        points.get(first);

                ShinsalPoint b =
                        points.get(second);

                if (
                        a.locationKey()
                                .equals(
                                        b.locationKey()
                                )
                ) {
                    throw new IllegalArgumentException(
                            "동일 ShinsalPoint 위치가 중복되었습니다: "
                                    + a.locationKey()
                    );
                }
            }
        }

        /*
         * 년주
         */
        requireNatalPoint(
                points,
                PalacePosition.YEAR,
                ShinsalPointKind.HEAVENLY_STEM
        );

        requireNatalPoint(
                points,
                PalacePosition.YEAR,
                ShinsalPointKind.EARTHLY_BRANCH
        );

        /*
         * 월주
         */
        requireNatalPoint(
                points,
                PalacePosition.MONTH,
                ShinsalPointKind.HEAVENLY_STEM
        );

        requireNatalPoint(
                points,
                PalacePosition.MONTH,
                ShinsalPointKind.EARTHLY_BRANCH
        );

        /*
         * 일주
         */
        requireNatalPoint(
                points,
                PalacePosition.DAY,
                ShinsalPointKind.HEAVENLY_STEM
        );

        requireNatalPoint(
                points,
                PalacePosition.DAY,
                ShinsalPointKind.EARTHLY_BRANCH
        );

        /*
         * 시주
         */
        boolean hourStemExists =
                hasNatalPoint(
                        points,
                        PalacePosition.HOUR,
                        ShinsalPointKind.HEAVENLY_STEM
                );

        boolean hourBranchExists =
                hasNatalPoint(
                        points,
                        PalacePosition.HOUR,
                        ShinsalPointKind.EARTHLY_BRANCH
                );

        if (
                hourStemExists
                        != hourBranchExists
        ) {
            throw new IllegalArgumentException(
                    "시주의 천간과 지지는 함께 존재하거나 함께 없어야 합니다."
            );
        }

        if (
                completeChart
                        && !hourStemExists
        ) {
            throw new IllegalArgumentException(
                    "completeChart=true이면 시주 Point가 필요합니다."
            );
        }

        if (
                !completeChart
                        && hourStemExists
        ) {
            throw new IllegalArgumentException(
                    "completeChart=false인데 HOUR Point가 존재합니다."
            );
        }
    }

    /**
     * 일간.
     */
    public HeavenlyStem dayMaster() {

        return natalStem(
                PalacePosition.DAY
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "원국 DAY Stem이 없습니다."
                                )
                );
    }

    public Optional<HeavenlyStem> natalStem(
            PalacePosition position
    ) {

        if (
                position == null
        ) {
            return Optional.empty();
        }

        return points
                .stream()
                .filter(
                        point ->
                                point.layer()
                                        == ShinsalLayer.NATAL
                )
                .filter(
                        point ->
                                point.kind()
                                        == ShinsalPointKind.HEAVENLY_STEM
                )
                .filter(
                        point ->
                                point.palacePosition()
                                        .orElse(null)
                                        == position
                )
                .map(
                        ShinsalPoint::requireStem
                )
                .findFirst();
    }

    public Optional<EarthlyBranch> natalBranch(
            PalacePosition position
    ) {

        if (
                position == null
        ) {
            return Optional.empty();
        }

        return points
                .stream()
                .filter(
                        point ->
                                point.layer()
                                        == ShinsalLayer.NATAL
                )
                .filter(
                        point ->
                                point.kind()
                                        == ShinsalPointKind.EARTHLY_BRANCH
                )
                .filter(
                        point ->
                                point.palacePosition()
                                        .orElse(null)
                                        == position
                )
                .map(
                        ShinsalPoint::requireBranch
                )
                .findFirst();
    }

    public EarthlyBranch natalYearBranch() {

        return natalBranch(
                PalacePosition.YEAR
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "원국 YEAR Branch가 없습니다."
                                )
                );
    }

    public EarthlyBranch natalDayBranch() {

        return natalBranch(
                PalacePosition.DAY
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "원국 DAY Branch가 없습니다."
                                )
                );
    }

    public List<ShinsalPoint> pointsOf(
            ShinsalLayer layer
    ) {

        if (
                layer == null
        ) {
            return List.of();
        }

        return points
                .stream()
                .filter(
                        point ->
                                point.layer()
                                        == layer
                )
                .toList();
    }

    public List<ShinsalPoint> stemPoints() {

        return points
                .stream()
                .filter(
                        point ->
                                point.kind()
                                        == ShinsalPointKind.HEAVENLY_STEM
                )
                .toList();
    }

    public List<ShinsalPoint> branchPoints() {

        return points
                .stream()
                .filter(
                        point ->
                                point.kind()
                                        == ShinsalPointKind.EARTHLY_BRANCH
                )
                .toList();
    }

    public boolean hasLayer(
            ShinsalLayer layer
    ) {

        return !pointsOf(
                layer
        )
                .isEmpty();
    }

    private static void requireNatalPoint(

            List<ShinsalPoint> points,

            PalacePosition position,

            ShinsalPointKind kind

    ) {

        if (
                !hasNatalPoint(
                        points,
                        position,
                        kind
                )
        ) {
            throw new IllegalArgumentException(
                    "필수 원국 ShinsalPoint가 없습니다: "
                            + position
                            + " / "
                            + kind
            );
        }
    }

    private static boolean hasNatalPoint(

            List<ShinsalPoint> points,

            PalacePosition position,

            ShinsalPointKind kind

    ) {

        return points
                .stream()
                .anyMatch(
                        point ->
                                point.layer()
                                        == ShinsalLayer.NATAL

                                        && point.kind()
                                        == kind

                                        && point
                                        .palacePosition()
                                        .orElse(null)
                                        == position
                );
    }
}