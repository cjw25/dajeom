package com.dajeom.model;

import com.dajeom.type.ClimateElementCandidateRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PassageCandidateRole;
import com.dajeom.type.PassageNeedLevel;
import com.dajeom.type.RemedyCandidateRole;

import java.util.Objects;

/**
 * 정밀화 6차-7.
 *
 * 6-8 조후·병약·통관 충돌 조정에서
 * 사용할 수 있는 얇은 통관 후보 요약.
 *
 * 원본 PassageCandidate의 Evidence를
 * 대체하지 않는다.
 *
 * UI나 최종 Grounding용 결과가 아니라
 * 다음 단계의 교차검증 편의를 위한 모델이다.
 */
public record PassageCandidateSummary(

        FiveElement element,

        PassageCandidateRole passageRole,

        PassageNeedLevel passageNeed,

        RemedyCandidateRole remedyRole,

        ClimateElementCandidateRole climateRole,

        boolean dayMasterDisadvantaged,

        boolean bridgeInsufficient,

        boolean remedyAligned,

        boolean climateAligned,

        boolean conflict

) {

    public PassageCandidateSummary {

        Objects.requireNonNull(
                element,
                "오행이 필요합니다."
        );

        Objects.requireNonNull(
                passageRole,
                "통관 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                passageNeed,
                "통관 필요도가 필요합니다."
        );

        Objects.requireNonNull(
                remedyRole,
                "병약 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                climateRole,
                "조후 후보 역할이 필요합니다."
        );
    }

    public static PassageCandidateSummary from(
            PassageCandidate candidate
    ) {

        Objects.requireNonNull(
                candidate,
                "통관 후보가 필요합니다."
        );

        return new PassageCandidateSummary(
                candidate.element(),
                candidate.role(),
                candidate.strongestNeed(),
                candidate.remedyRole(),
                candidate.climateRole(),
                candidate.dayMasterDisadvantaged(),
                candidate.bridgeInsufficient(),
                candidate.remedyAligned(),
                candidate.climateAligned(),
                candidate.crossAnalysisConflict()
        );
    }

    public boolean tripleSelected() {

        return passageRole.selected()
                && remedyRole.selected()
                && climateRole.selected();
    }

    public boolean passageAndRemedyAligned() {

        return passageRole.candidate()
                && remedyAligned;
    }

    public boolean passageAndClimateAligned() {

        return passageRole.candidate()
                && climateAligned;
    }

    public boolean cleanPassageCandidate() {

        return passageRole.candidate()
                && !conflict;
    }
}