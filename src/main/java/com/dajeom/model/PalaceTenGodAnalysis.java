package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalaceTenGodSourceType;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 12차-3.
 *
 * 궁위 × 십성 분석 결과.
 *
 * =================================================
 *
 * 구성:
 *
 * PalacePositionAnalysis
 * +
 * PalaceTenGodEvidence
 *
 * =================================================
 *
 * DAY 천간 자체는
 * PalacePositionAnalysis에 그대로 존재한다.
 *
 * 하지만 external ten-god Evidence에는 포함하지 않는다.
 *
 * 따라서:
 *
 * DAY Palace가 비어 있는 것이 아니라
 *
 * DAY Stem = 일간 Anchor
 *
 * 라는 구조가 그대로 보존된다.
 */
public record PalaceTenGodAnalysis(

        HeavenlyStem dayMaster,

        PalacePositionAnalysis positionAnalysis,

        List<PalaceTenGodEvidence> evidences,

        int knownPalaceCount,

        boolean completeChart

) {

    public PalaceTenGodAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "궁위 십성 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                positionAnalysis,
                "12-2 PalacePositionAnalysis가 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                positionAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "12-2와 12-3의 일간이 일치하지 않습니다."
            );
        }

        if (
                positionAnalysis.knownPalaceCount()
                        != knownPalaceCount
        ) {

            throw new IllegalArgumentException(
                    "12-2와 12-3의 knownPalaceCount가 일치하지 않습니다."
            );
        }

        if (
                positionAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "12-2와 12-3의 completeChart 상태가 일치하지 않습니다."
            );
        }

        for (
                PalaceTenGodEvidence evidence
                : evidences
        ) {

            if (
                    evidence.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "궁위 십성 Evidence의 일간이 일치하지 않습니다."
                );
            }

            if (
                    !positionAnalysis.containsPosition(
                            evidence.position()
                    )
            ) {

                throw new IllegalArgumentException(
                        "존재하지 않는 Palace에 십성 Evidence가 연결되었습니다: "
                                + evidence.position()
                );
            }

            /*
             * DAY 천간 자체를 외부 십성으로
             * 잘못 반환한 구현을 방어한다.
             */
            if (
                    evidence.position()
                            == PalacePosition.DAY

                            && evidence.sourceType()
                            == PalaceTenGodSourceType.VISIBLE_STEM
            ) {

                throw new IllegalArgumentException(
                        "DAY Palace의 천간은 일간 Anchor이므로 외부 십성 Evidence가 될 수 없습니다."
                );
            }
        }

        /*
         * 동일 Source 중복 방지.
         */
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

                PalaceTenGodEvidence a =
                        evidences.get(first);

                PalaceTenGodEvidence b =
                        evidences.get(second);

                if (
                        a.position() == b.position()
                                && a.sourceType() == b.sourceType()
                                && a.sourceStem() == b.sourceStem()
                ) {

                    throw new IllegalArgumentException(
                            "동일 궁위 십성 Source가 중복되었습니다: "
                                    + a.position()
                                    + " / "
                                    + a.sourceType()
                                    + " / "
                                    + a.sourceStem()
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * 궁위 조회
     * =================================================
     */

    public List<PalaceTenGodEvidence> evidencesOf(
            PalacePosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.position()
                                        == position
                )
                .toList();
    }

    public List<PalaceTenGodEvidence> yearEvidences() {

        return evidencesOf(
                PalacePosition.YEAR
        );
    }

    public List<PalaceTenGodEvidence> monthEvidences() {

        return evidencesOf(
                PalacePosition.MONTH
        );
    }

    public List<PalaceTenGodEvidence> dayEvidences() {

        return evidencesOf(
                PalacePosition.DAY
        );
    }

    public List<PalaceTenGodEvidence> hourEvidences() {

        return evidencesOf(
                PalacePosition.HOUR
        );
    }

    /*
     * =================================================
     * 십성 조회
     * =================================================
     */

    public List<PalaceTenGodEvidence> evidencesOf(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return List.of();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.tenGodType()
                                        == tenGodType
                )
                .toList();
    }

    public boolean contains(
            StructureTenGodType tenGodType
    ) {

        return !evidencesOf(
                tenGodType
        )
                .isEmpty();
    }

    public boolean contains(

            PalacePosition position,

            StructureTenGodType tenGodType

    ) {

        if (
                position == null
                        || tenGodType == null
        ) {

            return false;
        }

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.position()
                                        == position

                                        && evidence.tenGodType()
                                        == tenGodType
                );
    }

    /*
     * =================================================
     * 천간 투출 십성
     * =================================================
     */

    public List<PalaceTenGodEvidence> visibleEvidences() {

        return evidences
                .stream()
                .filter(
                        PalaceTenGodEvidence
                                ::visible
                )
                .toList();
    }

    public Optional<PalaceTenGodEvidence> visibleEvidenceOf(
            PalacePosition position
    ) {

        if (
                position == null
        ) {

            return Optional.empty();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.position()
                                        == position
                                        && evidence.visible()
                )
                .findFirst();
    }

    /*
     * =================================================
     * 지장간
     * =================================================
     */

    public List<PalaceTenGodEvidence> hiddenEvidences() {

        return evidences
                .stream()
                .filter(
                        PalaceTenGodEvidence
                                ::hidden
                )
                .toList();
    }

    public List<PalaceTenGodEvidence> hiddenEvidencesOf(
            PalacePosition position
    ) {

        return evidencesOf(
                position
        )
                .stream()
                .filter(
                        PalaceTenGodEvidence
                                ::hidden
                )
                .toList();
    }

    public Optional<PalaceTenGodEvidence> mainHiddenEvidenceOf(
            PalacePosition position
    ) {

        return evidencesOf(
                position
        )
                .stream()
                .filter(
                        PalaceTenGodEvidence
                                ::mainHiddenStem
                )
                .findFirst();
    }

    /*
     * =================================================
     * DAY Anchor
     * =================================================
     */

    public PalaceStemEvidence dayMasterAnchor() {

        return positionAnalysis
                .dayStemEvidence();
    }

    public boolean dayStemExcludedFromExternalTenGod() {

        return evidences
                .stream()
                .noneMatch(
                        evidence ->
                                evidence.position()
                                        == PalacePosition.DAY

                                        && evidence.sourceType()
                                        == PalaceTenGodSourceType.VISIBLE_STEM
                );
    }

    /*
     * =================================================
     * 시간 미상
     * =================================================
     */

    public boolean hourUnknown() {

        return !completeChart;
    }
}