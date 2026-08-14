package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-1.
 *
 * 월령에서 추출한 전체 격국 후보군.
 *
 * 이 단계에서는 후보를 제거하지 않는다.
 *
 * 월지에 존재하는:
 *
 * - 정기
 * - 중기
 * - 여기
 *
 * 를 모두 유지한다.
 *
 * 이후 단계에서 투출 여부와
 * 실제 격국 성립 조건을 검토한다.
 */
public record MonthStructureCandidateAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        HeavenlyStem monthMainHiddenStem,

        List<MonthStructureCandidate> candidates

) {

    public MonthStructureCandidateAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                monthMainHiddenStem,
                "월지 정기가 필요합니다."
        );

        candidates =
                candidates == null
                        ? List.of()
                        : List.copyOf(
                        candidates
                );

        if (
                candidates.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "월령 격국 후보가 하나 이상 필요합니다."
            );
        }

        long mainQiCount =
                candidates
                        .stream()
                        .filter(
                                MonthStructureCandidate
                                        ::mainQi
                        )
                        .count();

        if (
                mainQiCount != 1
        ) {

            throw new IllegalArgumentException(
                    "월지 정기 후보는 정확히 하나여야 합니다."
            );
        }

        MonthStructureCandidate main =
                candidates
                        .stream()
                        .filter(
                                MonthStructureCandidate
                                        ::mainQi
                        )
                        .findFirst()
                        .orElseThrow();

        if (
                main.hiddenStem()
                        != monthMainHiddenStem
        ) {

            throw new IllegalArgumentException(
                    "월지 정기와 정기 격 후보의 천간이 일치하지 않습니다."
            );
        }

        boolean duplicateStem =
                candidates
                        .stream()
                        .map(
                                MonthStructureCandidate
                                        ::hiddenStem
                        )
                        .distinct()
                        .count()
                        != candidates.size();

        if (
                duplicateStem
        ) {

            throw new IllegalArgumentException(
                    "동일한 월지 지장간이 격 후보에 중복되었습니다."
            );
        }
    }

    public MonthStructureCandidate mainQiCandidate() {

        return candidates
                .stream()
                .filter(
                        MonthStructureCandidate
                                ::mainQi
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "월지 정기 후보를 찾을 수 없습니다."
                                )
                );
    }

    public Optional<MonthStructureCandidate>
    middleQiCandidate() {

        return candidates
                .stream()
                .filter(
                        MonthStructureCandidate
                                ::middleQi
                )
                .findFirst();
    }

    public Optional<MonthStructureCandidate>
    residualQiCandidate() {

        return candidates
                .stream()
                .filter(
                        MonthStructureCandidate
                                ::residualQi
                )
                .findFirst();
    }

    public List<MonthStructureCandidate>
    exposedCandidates() {

        return candidates
                .stream()
                .filter(
                        MonthStructureCandidate
                                ::exposed
                )
                .toList();
    }

    public List<MonthStructureCandidate>
    hiddenOnlyCandidates() {

        return candidates
                .stream()
                .filter(
                        MonthStructureCandidate
                                ::hiddenOnly
                )
                .toList();
    }

    public List<MonthStructureCandidate>
    monthStemExposedCandidates() {

        return candidates
                .stream()
                .filter(
                        MonthStructureCandidate
                                ::monthStemExposed
                )
                .toList();
    }

    public boolean mainQiExposed() {

        return mainQiCandidate()
                .exposed();
    }

    public boolean mainQiExposedAtMonthStem() {

        return mainQiCandidate()
                .exposedAtMonthStem();
    }

    public int exposedCandidateCount() {

        return exposedCandidates()
                .size();
    }

    public Optional<MonthStructureCandidate>
    candidateOf(
            StructureTenGodType tenGod
    ) {

        if (
                tenGod == null
        ) {

            return Optional.empty();
        }

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.tenGod()
                                        == tenGod
                )
                .findFirst();
    }

    /**
     * 여러 월령 지장간이 동시에
     * 천간으로 올라온 구조인지.
     *
     * 이후 5-7 혼잡도 분석에서
     * 사용할 수 있다.
     */
    public boolean multipleCandidatesExposed() {

        return exposedCandidateCount()
                >= 2;
    }
}