package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 일간의 지지 뿌리 구조.
 *
 * 아직:
 *
 * - 득지
 * - 신강/신약
 * - 강한 뿌리
 * - 약한 뿌리
 *
 * 를 판정하지 않는다.
 *
 * 원국에서 실제 확인되는
 * 통근 위치와 형태만 정리한다.
 */
public record DayMasterGroundAnalysis(

        HeavenlyStem dayMaster,

        DayMasterGroundPattern pattern,

        List<StemGroundHit> exactStemRoots,

        List<StemGroundHit> sameElementOtherStemRoots

) {

    public DayMasterGroundAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                pattern,
                "일간 뿌리 형태는 필수입니다."
        );

        exactStemRoots =
                exactStemRoots == null
                        ? List.of()
                        : List.copyOf(
                        exactStemRoots
                );

        sameElementOtherStemRoots =
                sameElementOtherStemRoots == null
                        ? List.of()
                        : List.copyOf(
                        sameElementOtherStemRoots
                );

        for (
                StemGroundHit hit
                : exactStemRoots
        ) {

            Objects.requireNonNull(
                    hit,
                    "동일 천간 근 목록에 null이 포함될 수 없습니다."
            );

            if (
                    hit.targetStem()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "통근 분석 대상 천간이 일간과 일치하지 않습니다."
                );
            }

            if (!hit.exactStem()) {

                throw new IllegalArgumentException(
                        "exactStemRoots에는 동일 천간 근만 포함할 수 있습니다."
                );
            }
        }

        for (
                StemGroundHit hit
                : sameElementOtherStemRoots
        ) {

            Objects.requireNonNull(
                    hit,
                    "동일 오행 연결 목록에 null이 포함될 수 없습니다."
            );

            if (
                    hit.targetStem()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "통근 분석 대상 천간이 일간과 일치하지 않습니다."
                );
            }

            if (
                    !hit.sameElementOtherStem()
            ) {

                throw new IllegalArgumentException(
                        "sameElementOtherStemRoots에는 같은 오행의 다른 천간 연결만 포함할 수 있습니다."
                );
            }
        }
    }

    public boolean hasExactStemRoot() {

        return !exactStemRoots.isEmpty();
    }

    public boolean hasSameElementOtherStemRoot() {

        return !sameElementOtherStemRoots.isEmpty();
    }

    public boolean hasAnyGroundConnection() {

        return hasExactStemRoot()
                ||
                hasSameElementOtherStemRoot();
    }

    /*
     * =================================================
     * 위치별 조회
     * =================================================
     */

    public List<StemGroundHit> yearBranchRoots() {

        return rootsAt(
                PillarPosition.YEAR
        );
    }

    public List<StemGroundHit> monthBranchRoots() {

        return rootsAt(
                PillarPosition.MONTH
        );
    }

    public List<StemGroundHit> dayBranchRoots() {

        return rootsAt(
                PillarPosition.DAY
        );
    }

    public List<StemGroundHit> hourBranchRoots() {

        return rootsAt(
                PillarPosition.HOUR
        );
    }

    /*
     * =================================================
     * 지장간 위치별 조회
     * =================================================
     */

    public List<StemGroundHit> mainRoots() {

        return rootsOfPhase(
                HiddenStemPhase.MAIN
        );
    }

    public List<StemGroundHit> middleRoots() {

        return rootsOfPhase(
                HiddenStemPhase.MIDDLE
        );
    }

    public List<StemGroundHit> earlyRoots() {

        return rootsOfPhase(
                HiddenStemPhase.EARLY
        );
    }

    /*
     * =================================================
     * 동일 천간 근 위치 조회
     * =================================================
     */

    public List<StemGroundHit> exactRootsAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return exactStemRoots
                .stream()
                .filter(
                        hit ->
                                hit.branchPosition()
                                        == position
                )
                .toList();
    }

    /*
     * =================================================
     * 동일 오행 다른 천간 위치 조회
     * =================================================
     */

    public List<StemGroundHit> sameElementRootsAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return sameElementOtherStemRoots
                .stream()
                .filter(
                        hit ->
                                hit.branchPosition()
                                        == position
                )
                .toList();
    }

    private List<StemGroundHit> rootsAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return allRoots()
                .stream()
                .filter(
                        hit ->
                                hit.branchPosition()
                                        == position
                )
                .toList();
    }

    private List<StemGroundHit> rootsOfPhase(
            HiddenStemPhase phase
    ) {

        Objects.requireNonNull(
                phase,
                "지장간 위치는 필수입니다."
        );

        return allRoots()
                .stream()
                .filter(
                        hit ->
                                hit.hiddenStemPhase()
                                        == phase
                )
                .toList();
    }

    private List<StemGroundHit> allRoots() {

        return java.util.stream.Stream
                .concat(
                        exactStemRoots.stream(),
                        sameElementOtherStemRoots.stream()
                )
                .toList();
    }
}