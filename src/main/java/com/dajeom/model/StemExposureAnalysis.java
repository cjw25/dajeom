package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 특정 천간 하나에 대한 전체 투간 분석 결과.
 *
 * 예:
 *
 * 庚
 *
 * 월지 申 정기 庚
 * ↓
 * 월간 庚으로 투출
 *
 * 이런 모든 근거를 하나로 합쳐 관리한다.
 */
public record StemExposureAnalysis(

        HeavenlyStem stem,

        boolean exposed,

        boolean monthBranchExposed,

        boolean mainQiExposed,

        boolean monthMainQiExposed,

        boolean samePillarExposed,

        boolean repeatedExposure,

        int visibleStemCount,

        int sourceCount,

        int evidenceCount,

        int totalSourceWeight,

        List<StemExposureEvidence> evidences

) {

    public StemExposureAnalysis {

        Objects.requireNonNull(
                stem,
                "투간 분석 대상 천간이 필요합니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        if (
                visibleStemCount < 0
                        || sourceCount < 0
                        || evidenceCount < 0
                        || totalSourceWeight < 0
        ) {

            throw new IllegalArgumentException(
                    "투간 분석 수치는 음수가 될 수 없습니다."
            );
        }

        if (
                evidenceCount
                        != evidences.size()
        ) {

            throw new IllegalArgumentException(
                    "투간 근거 개수와 실제 Evidence 개수가 일치하지 않습니다."
            );
        }

        if (
                exposed
                        != !evidences.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "투간 여부와 Evidence 존재 여부가 일치하지 않습니다."
            );
        }
    }

    /**
     * 가장 강한 투간 근원.
     */
    public Optional<StemExposureEvidence>
    strongestEvidence() {

        return evidences
                .stream()
                .max(
                        Comparator.comparingInt(
                                StemExposureEvidence
                                        ::effectiveSourceWeight
                        )
                );
    }

    /**
     * 투간되지 않은 지장간.
     */
    public boolean hiddenOnly() {

        return !exposed;
    }

    /**
     * 한 군데 이상의 지장간 근원이 있고
     * 천간에도 실제로 드러난 상태.
     */
    public boolean activeExposure() {

        return sourceCount > 0
                && visibleStemCount > 0
                && exposed;
    }

    /**
     * 월지의 정기가 실제 천간으로 투출.
     *
     * 이후 격국 판단에서 사용할 수 있다.
     */
    public boolean strongStructureEvidence() {

        return monthMainQiExposed;
    }
}