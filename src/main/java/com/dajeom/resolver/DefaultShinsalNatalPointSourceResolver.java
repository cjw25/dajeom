package com.dajeom.resolver;

import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.ShinsalPoint;
import com.dajeom.type.PalacePosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 17차-4.
 *
 * PalaceAnalysis
 * ->
 * 원국 ShinsalPoint 변환 Adapter.
 *
 * =================================================
 *
 * completeChart=true:
 *
 * YEAR STEM
 * YEAR BRANCH
 * MONTH STEM
 * MONTH BRANCH
 * DAY STEM
 * DAY BRANCH
 * HOUR STEM
 * HOUR BRANCH
 *
 * 총 8개.
 *
 * =================================================
 *
 * completeChart=false:
 *
 * YEAR STEM
 * YEAR BRANCH
 * MONTH STEM
 * MONTH BRANCH
 * DAY STEM
 * DAY BRANCH
 *
 * 총 6개.
 *
 * =================================================
 *
 * 중요:
 *
 * - 출생시간 미상 HOUR 생성 금지
 * - 자정/정오/子시 대체 금지
 * - 운 Point 생성 금지
 * - PalaceAnalysis의 실제 knownPositions만 사용
 */
@Component
public class DefaultShinsalNatalPointSourceResolver
        implements ShinsalNatalPointSourceResolver {

    @Override
    public List<ShinsalPoint> resolveNatalPoints(
            PalaceAnalysis natalAnalysis
    ) {

        Objects.requireNonNull(
                natalAnalysis,
                "17차 원국 신살 Point 생성에는 PalaceAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 실제 존재하는 원국 위치만 사용.
         * =================================================
         *
         * 명시적으로 order 정렬해서
         * YEAR -> MONTH -> DAY -> HOUR 순서를 안정화한다.
         */

        List<PalacePosition> positions =
                natalAnalysis
                        .knownPositions()
                        .stream()
                        .sorted(
                                Comparator.comparingInt(
                                        PalacePosition::order
                                )
                        )
                        .toList();

        int expectedPositionCount =
                natalAnalysis.completeChart()
                        ? 4
                        : 3;

        if (
                positions.size()
                        != expectedPositionCount
        ) {

            throw new IllegalStateException(
                    "PalaceAnalysis의 knownPositions 수가 completeChart 상태와 일치하지 않습니다. expected="
                            + expectedPositionCount
                            + ", actual="
                            + positions.size()
            );
        }

        /*
         * =================================================
         * 시간 미상 방어
         * =================================================
         */

        if (
                !natalAnalysis.completeChart()
                        && positions.contains(
                        PalacePosition.HOUR
                )
        ) {

            throw new IllegalStateException(
                    "출생시간 미상 원국의 knownPositions에 HOUR가 포함되어 있습니다."
            );
        }

        if (
                natalAnalysis.completeChart()
                        && !positions.contains(
                        PalacePosition.HOUR
                )
        ) {

            throw new IllegalStateException(
                    "completeChart=true인데 원국 knownPositions에 HOUR가 없습니다."
            );
        }

        List<ShinsalPoint> result =
                new ArrayList<>(
                        expectedPositionCount * 2
                );

        for (
                PalacePosition position
                : positions
        ) {

            /*
             * =================================================
             * 천간 Point
             * =================================================
             */

            result.add(
                    ShinsalPoint
                            .natalStem(
                                    position,

                                    natalAnalysis
                                            .positionAnalysis()
                                            .stemEvidenceOf(
                                                    position
                                            )
                                            .orElseThrow(
                                                    () ->
                                                            new IllegalStateException(
                                                                    "17차 원국 신살 Point 생성에 필요한 Palace 천간 Evidence가 없습니다: "
                                                                            + position
                                                            )
                                            )
                                            .stem()
                            )
            );

            /*
             * =================================================
             * 지지 Point
             * =================================================
             */

            result.add(
                    ShinsalPoint
                            .natalBranch(
                                    position,

                                    natalAnalysis
                                            .positionAnalysis()
                                            .branchEvidenceOf(
                                                    position
                                            )
                                            .orElseThrow(
                                                    () ->
                                                            new IllegalStateException(
                                                                    "17차 원국 신살 Point 생성에 필요한 Palace 지지 Evidence가 없습니다: "
                                                                            + position
                                                            )
                                            )
                                            .branch()
                            )
            );
        }

        /*
         * =================================================
         * 최종 Point 수
         * =================================================
         */

        int expectedPointCount =
                natalAnalysis.completeChart()
                        ? 8
                        : 6;

        if (
                result.size()
                        != expectedPointCount
        ) {

            throw new IllegalStateException(
                    "원국 ShinsalPoint 수가 잘못되었습니다. expected="
                            + expectedPointCount
                            + ", actual="
                            + result.size()
            );
        }

        /*
         * =================================================
         * NATAL 전용 검증
         * =================================================
         */

        for (
                ShinsalPoint point
                : result
        ) {

            Objects.requireNonNull(
                    point,
                    "원국 ShinsalPoint는 null일 수 없습니다."
            );

            if (
                    !point.natal()
            ) {

                throw new IllegalStateException(
                        "ShinsalNatalPointSourceResolver가 운 ShinsalPoint를 생성했습니다: "
                                + point.locationKey()
                );
            }

            if (
                    point.palacePosition()
                            .isEmpty()
            ) {

                throw new IllegalStateException(
                        "원국 ShinsalPoint에 PalacePosition이 없습니다."
                );
            }

            if (
                    !natalAnalysis.completeChart()
                            && point
                            .palacePosition()
                            .orElseThrow()
                            == PalacePosition.HOUR
            ) {

                throw new IllegalStateException(
                        "출생시간 미상 원국에서 HOUR ShinsalPoint가 생성되었습니다."
                );
            }
        }

        /*
         * =================================================
         * 위치 중복 검증
         * =================================================
         */

        Set<String> locationKeys =
                new HashSet<>();

        for (
                ShinsalPoint point
                : result
        ) {

            if (
                    !locationKeys.add(
                            point.locationKey()
                    )
            ) {

                throw new IllegalStateException(
                        "동일 원국 ShinsalPoint 위치가 중복 생성되었습니다: "
                                + point.locationKey()
                );
            }
        }

        return List.copyOf(
                result
        );
    }
}