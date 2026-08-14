package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalacePositionContextType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 12차-2.
 *
 * 궁위 천간·지지 위치 분석.
 *
 * =================================================
 *
 * completeChart = true
 *
 * YEAR
 * MONTH
 * DAY
 * HOUR
 *
 * 각각:
 *
 * Stem Evidence
 * Branch Evidence
 *
 * 가 존재한다.
 *
 * =================================================
 *
 * completeChart = false
 *
 * YEAR
 * MONTH
 * DAY
 *
 * 만 존재한다.
 *
 * HOUR의 Stem/Branch Evidence를
 * 임의 생성하지 않는다.
 *
 * =================================================
 *
 * 이 객체에서는 아직:
 *
 * - 십성
 * - 지장간
 * - 12운성
 * - 천간 관계
 * - 지지 관계
 * - 합화
 *
 * 를 결합하지 않는다.
 */
public record PalacePositionAnalysis(

        HeavenlyStem dayMaster,

        List<PalaceStemEvidence> stemEvidences,

        List<PalaceBranchEvidence> branchEvidences,

        int knownPalaceCount,

        boolean completeChart

) {

    public PalacePositionAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "궁위 위치 분석에는 일간이 필요합니다."
        );

        stemEvidences =
                stemEvidences == null
                        ? List.of()
                        : List.copyOf(
                        stemEvidences
                );

        branchEvidences =
                branchEvidences == null
                        ? List.of()
                        : List.copyOf(
                        branchEvidences
                );

        if (
                knownPalaceCount != 3
                        && knownPalaceCount != 4
        ) {

            throw new IllegalArgumentException(
                    "knownPalaceCount는 3 또는 4여야 합니다."
            );
        }

        if (
                stemEvidences.size()
                        != knownPalaceCount
        ) {

            throw new IllegalArgumentException(
                    "궁위 천간 Evidence 개수와 knownPalaceCount가 일치하지 않습니다."
            );
        }

        if (
                branchEvidences.size()
                        != knownPalaceCount
        ) {

            throw new IllegalArgumentException(
                    "궁위 지지 Evidence 개수와 knownPalaceCount가 일치하지 않습니다."
            );
        }

        if (
                completeChart
                        != (
                        knownPalaceCount == 4
                )
        ) {

            throw new IllegalArgumentException(
                    "completeChart와 knownPalaceCount 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 일간 동일성
         * =================================================
         */

        for (
                PalaceStemEvidence evidence
                : stemEvidences
        ) {

            if (
                    evidence.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "궁위 천간 Evidence의 일간이 서로 일치하지 않습니다."
                );
            }
        }

        for (
                PalaceBranchEvidence evidence
                : branchEvidences
        ) {

            if (
                    evidence.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "궁위 지지 Evidence의 일간이 서로 일치하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * 위치 중복 검증
         * =================================================
         */

        validateStemPositions(
                stemEvidences
        );

        validateBranchPositions(
                branchEvidences
        );

        /*
         * =================================================
         * YEAR / MONTH / DAY 필수
         * =================================================
         */

        requireStemPosition(
                stemEvidences,
                PalacePosition.YEAR
        );

        requireStemPosition(
                stemEvidences,
                PalacePosition.MONTH
        );

        requireStemPosition(
                stemEvidences,
                PalacePosition.DAY
        );

        requireBranchPosition(
                branchEvidences,
                PalacePosition.YEAR
        );

        requireBranchPosition(
                branchEvidences,
                PalacePosition.MONTH
        );

        requireBranchPosition(
                branchEvidences,
                PalacePosition.DAY
        );

        /*
         * =================================================
         * HOUR 존재 여부
         * =================================================
         */

        boolean hasHourStem =
                containsStemPosition(
                        stemEvidences,
                        PalacePosition.HOUR
                );

        boolean hasHourBranch =
                containsBranchPosition(
                        branchEvidences,
                        PalacePosition.HOUR
                );

        if (
                hasHourStem
                        != hasHourBranch
        ) {

            throw new IllegalArgumentException(
                    "HOUR 궁위의 천간과 지지는 함께 존재하거나 함께 없어야 합니다."
            );
        }

        if (
                completeChart
                        != hasHourStem
        ) {

            throw new IllegalArgumentException(
                    "completeChart와 HOUR Palace 존재 여부가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 동일 Palace의 Stem/Branch Context 정합성
         * =================================================
         */

        for (
                PalacePosition position
                : knownPositions(
                completeChart
        )
        ) {

            PalaceStemEvidence stemEvidence =
                    stemEvidences
                            .stream()
                            .filter(
                                    evidence ->
                                            evidence.position()
                                                    == position
                            )
                            .findFirst()
                            .orElseThrow();

            PalaceBranchEvidence branchEvidence =
                    branchEvidences
                            .stream()
                            .filter(
                                    evidence ->
                                            evidence.position()
                                                    == position
                            )
                            .findFirst()
                            .orElseThrow();

            if (
                    stemEvidence.positionContext()
                            != branchEvidence.positionContext()
            ) {

                throw new IllegalArgumentException(
                        "동일 Palace의 천간·지지 Context가 일치하지 않습니다: "
                                + position
                );
            }
        }
    }

    /*
     * =================================================
     * Palace 조회
     * =================================================
     */

    public Optional<PalaceStemEvidence> stemEvidenceOf(
            PalacePosition position
    ) {

        if (
                position == null
        ) {

            return Optional.empty();
        }

        return stemEvidences
                .stream()
                .filter(
                        evidence ->
                                evidence.position()
                                        == position
                )
                .findFirst();
    }

    public Optional<PalaceBranchEvidence> branchEvidenceOf(
            PalacePosition position
    ) {

        if (
                position == null
        ) {

            return Optional.empty();
        }

        return branchEvidences
                .stream()
                .filter(
                        evidence ->
                                evidence.position()
                                        == position
                )
                .findFirst();
    }

    /*
     * =================================================
     * YEAR
     * =================================================
     */

    public PalaceStemEvidence yearStemEvidence() {

        return stemEvidenceOf(
                PalacePosition.YEAR
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "YEAR Palace Stem Evidence가 없습니다."
                                )
                );
    }

    public PalaceBranchEvidence yearBranchEvidence() {

        return branchEvidenceOf(
                PalacePosition.YEAR
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "YEAR Palace Branch Evidence가 없습니다."
                                )
                );
    }

    public HeavenlyStem yearStem() {

        return yearStemEvidence()
                .stem();
    }

    public EarthlyBranch yearBranch() {

        return yearBranchEvidence()
                .branch();
    }

    /*
     * =================================================
     * MONTH
     * =================================================
     */

    public PalaceStemEvidence monthStemEvidence() {

        return stemEvidenceOf(
                PalacePosition.MONTH
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "MONTH Palace Stem Evidence가 없습니다."
                                )
                );
    }

    public PalaceBranchEvidence monthBranchEvidence() {

        return branchEvidenceOf(
                PalacePosition.MONTH
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "MONTH Palace Branch Evidence가 없습니다."
                                )
                );
    }

    public HeavenlyStem monthStem() {

        return monthStemEvidence()
                .stem();
    }

    public EarthlyBranch monthBranch() {

        return monthBranchEvidence()
                .branch();
    }

    /*
     * =================================================
     * DAY
     * =================================================
     */

    public PalaceStemEvidence dayStemEvidence() {

        return stemEvidenceOf(
                PalacePosition.DAY
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "DAY Palace Stem Evidence가 없습니다."
                                )
                );
    }

    public PalaceBranchEvidence dayBranchEvidence() {

        return branchEvidenceOf(
                PalacePosition.DAY
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "DAY Palace Branch Evidence가 없습니다."
                                )
                );
    }

    public HeavenlyStem dayStem() {

        return dayStemEvidence()
                .stem();
    }

    public EarthlyBranch dayBranch() {

        return dayBranchEvidence()
                .branch();
    }

    /*
     * =================================================
     * HOUR
     * =================================================
     */

    public Optional<PalaceStemEvidence> hourStemEvidence() {

        return stemEvidenceOf(
                PalacePosition.HOUR
        );
    }

    public Optional<PalaceBranchEvidence> hourBranchEvidence() {

        return branchEvidenceOf(
                PalacePosition.HOUR
        );
    }

    public Optional<HeavenlyStem> hourStem() {

        return hourStemEvidence()
                .map(
                        PalaceStemEvidence
                                ::stem
                );
    }

    public Optional<EarthlyBranch> hourBranch() {

        return hourBranchEvidence()
                .map(
                        PalaceBranchEvidence
                                ::branch
                );
    }

    /*
     * =================================================
     * Context
     * =================================================
     */

    public Optional<PalacePositionContextType> contextOf(
            PalacePosition position
    ) {

        return stemEvidenceOf(
                position
        )
                .map(
                        PalaceStemEvidence
                                ::positionContext
                );
    }

    /*
     * =================================================
     * 알려진 Palace
     * =================================================
     */

    public List<PalacePosition> knownPositions() {

        return knownPositions(
                completeChart
        );
    }

    public boolean containsPosition(
            PalacePosition position
    ) {

        return stemEvidenceOf(
                position
        )
                .isPresent()

                && branchEvidenceOf(
                position
        )
                .isPresent();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }

    /*
     * =================================================
     * Validation Helper
     * =================================================
     */

    private static void validateStemPositions(
            List<PalaceStemEvidence> evidences
    ) {

        for (
                int first = 0;
                first < evidences.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < evidences.size();
                    second++
            ) {

                if (
                        evidences
                                .get(first)
                                .position()

                                == evidences
                                .get(second)
                                .position()
                ) {

                    throw new IllegalArgumentException(
                            "동일 Palace의 천간 Evidence가 중복되었습니다: "
                                    + evidences
                                    .get(first)
                                    .position()
                    );
                }
            }
        }
    }

    private static void validateBranchPositions(
            List<PalaceBranchEvidence> evidences
    ) {

        for (
                int first = 0;
                first < evidences.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < evidences.size();
                    second++
            ) {

                if (
                        evidences
                                .get(first)
                                .position()

                                == evidences
                                .get(second)
                                .position()
                ) {

                    throw new IllegalArgumentException(
                            "동일 Palace의 지지 Evidence가 중복되었습니다: "
                                    + evidences
                                    .get(first)
                                    .position()
                    );
                }
            }
        }
    }

    private static void requireStemPosition(

            List<PalaceStemEvidence> evidences,

            PalacePosition position

    ) {

        if (
                !containsStemPosition(
                        evidences,
                        position
                )
        ) {

            throw new IllegalArgumentException(
                    "필수 Palace 천간 Evidence가 없습니다: "
                            + position
            );
        }
    }

    private static void requireBranchPosition(

            List<PalaceBranchEvidence> evidences,

            PalacePosition position

    ) {

        if (
                !containsBranchPosition(
                        evidences,
                        position
                )
        ) {

            throw new IllegalArgumentException(
                    "필수 Palace 지지 Evidence가 없습니다: "
                            + position
            );
        }
    }

    private static boolean containsStemPosition(

            List<PalaceStemEvidence> evidences,

            PalacePosition position

    ) {

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.position()
                                        == position
                );
    }

    private static boolean containsBranchPosition(

            List<PalaceBranchEvidence> evidences,

            PalacePosition position

    ) {

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.position()
                                        == position
                );
    }

    private static List<PalacePosition> knownPositions(
            boolean completeChart
    ) {

        List<PalacePosition> result =
                new ArrayList<>();

        result.add(
                PalacePosition.YEAR
        );

        result.add(
                PalacePosition.MONTH
        );

        result.add(
                PalacePosition.DAY
        );

        if (
                completeChart
        ) {

            result.add(
                    PalacePosition.HOUR
            );
        }

        result.sort(
                Comparator.comparingInt(
                        PalacePosition::order
                )
        );

        return List.copyOf(
                result
        );
    }
}