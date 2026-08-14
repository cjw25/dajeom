package com.dajeom.model;

import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.UsefulGodCandidateRole;
import com.dajeom.type.UsefulGodConvergenceLevel;
import com.dajeom.type.UsefulGodIntegrationReasonType;
import com.dajeom.type.UsefulGodMethodType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 7차-8.
 *
 * 하나의 五行에 대한
 * 6개 용신법 통합 결과.
 *
 * =================================================
 *
 * contributions에는 항상 정확히:
 *
 * BALANCING
 * CLIMATE_CONTROL
 * STRUCTURE
 * REMEDY
 * PASSAGE
 * FOLLOW_STRUCTURE
 *
 * 가 한 번씩 존재한다.
 *
 * =================================================
 *
 * role은 7-8의 통합 결과다.
 *
 * 아직:
 *
 * 희신
 * 기신
 *
 * 을 뜻하지 않는다.
 */
public record UsefulGodCandidate(

        FiveElement element,

        UsefulGodCandidateRole role,

        UsefulGodConvergenceLevel convergenceLevel,

        ClimateElementPresenceLevel presenceLevel,

        int existingShareBasisPoints,

        List<UsefulGodMethodContribution> contributions,

        boolean followStructureMode,

        List<UsefulGodIntegrationReasonType> reasons

) {

    public UsefulGodCandidate {

        Objects.requireNonNull(
                element,
                "용신 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "용신 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                convergenceLevel,
                "용신법 수렴 단계가 필요합니다."
        );

        Objects.requireNonNull(
                presenceLevel,
                "현재 오행 세력 수준이 필요합니다."
        );

        /*
         * =================================================
         * Compact constructor 입력 정규화
         * =================================================
         *
         * record compact constructor에서는
         * constructor body가 끝난 뒤
         * component field가 자동 대입된다.
         *
         * 따라서 이 constructor 내부에서는
         * this.contributions를 사용하는 instance method를
         * 호출하면 안 된다.
         *
         * 아래에서 정규화한 지역변수 contributions를
         * 직접 사용한다.
         */

        contributions =
                contributions == null
                        ? List.of()
                        : List.copyOf(
                        contributions
                );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                existingShareBasisPoints < 0
                        || existingShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "현재 오행 비중은 0~10000bp 사이여야 합니다."
            );
        }

        if (
                ClimateElementPresenceLevel
                        .fromShareBasisPoints(
                                existingShareBasisPoints
                        )
                        != presenceLevel
        ) {

            throw new IllegalArgumentException(
                    "현재 오행 비중과 PresenceLevel이 일치하지 않습니다."
            );
        }

        if (
                contributions.size()
                        != UsefulGodMethodType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "용신 통합 후보에는 6개 용신법 Contribution이 모두 필요합니다."
            );
        }

        EnumSet<UsefulGodMethodType> methods =
                EnumSet.noneOf(
                        UsefulGodMethodType.class
                );

        for (
                UsefulGodMethodContribution contribution
                : contributions
        ) {

            Objects.requireNonNull(
                    contribution,
                    "용신법 Contribution에는 null을 넣을 수 없습니다."
            );

            if (
                    !methods.add(
                            contribution.methodType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 용신법 Contribution이 중복되었습니다: "
                                + contribution.methodType()
                );
            }
        }

        if (
                reasons
                        .stream()
                        .distinct()
                        .count()
                        != reasons.size()
        ) {

            throw new IllegalArgumentException(
                    "용신 통합 근거가 중복되었습니다."
            );
        }

        if (
                convergenceLevel.inactive()
                        && !role.notSelected()
        ) {

            throw new IllegalArgumentException(
                    "활성 용신 근거가 없는 오행을 선택할 수 없습니다."
            );
        }

        /*
         * =================================================
         * CORE 충돌 검증
         * =================================================
         *
         * hasCoreConflict()를 호출하지 않는다.
         *
         * hasCoreConflict()는 생성 완료 후 사용할
         * instance convenience이며,
         * compact constructor 내부에서는 아직
         * this.contributions가 대입되지 않았다.
         */

        boolean coreConflict =
                contributions
                        .stream()
                        .anyMatch(
                                UsefulGodMethodContribution
                                        ::coreConflict
                        );

        if (
                role.primaryUsefulGod()
                        && coreConflict
        ) {

            throw new IllegalArgumentException(
                    "CORE 용신법과 직접 충돌하는 오행을 주용신 후보로 지정할 수 없습니다."
            );
        }

        if (
                followStructureMode
        ) {

            /*
             * =================================================
             * 종격 Contribution 조회
             * =================================================
             *
             * contributionOf(...) 역시
             * this.contributions를 사용하는 instance method라
             * compact constructor 내부에서는 호출하지 않는다.
             */

            UsefulGodMethodContribution follow =
                    contributions
                            .stream()
                            .filter(
                                    contribution ->
                                            contribution
                                                    .methodType()
                                                    == UsefulGodMethodType
                                                    .FOLLOW_STRUCTURE
                            )
                            .findFirst()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "종격 모드에 FOLLOW_STRUCTURE Contribution이 없습니다."
                                            )
                            );

            if (
                    !follow.coreMethod()
            ) {

                throw new IllegalArgumentException(
                        "종격 모드에서는 FOLLOW_STRUCTURE 용신법이 CORE여야 합니다."
                );
            }

            if (
                    follow.followFlowBreaking()
                            && role.selected()
            ) {

                throw new IllegalArgumentException(
                        "종격 흐름을 깨는 오행을 최종 용신 후보로 선택할 수 없습니다."
                );
            }
        }
    }

    public boolean primaryUsefulGod() {

        return role.primaryUsefulGod();
    }

    public boolean supportingUsefulGod() {

        return role.supportingUsefulGod();
    }

    public boolean deferred() {

        return role.deferred();
    }

    public boolean notSelected() {

        return role.notSelected();
    }

    public boolean selected() {

        return role.selected();
    }

    public boolean candidate() {

        return role.candidate();
    }

    public Optional<UsefulGodMethodContribution> contributionOf(
            UsefulGodMethodType methodType
    ) {

        if (
                methodType == null
        ) {

            return Optional.empty();
        }

        return contributions
                .stream()
                .filter(
                        contribution ->
                                contribution.methodType()
                                        == methodType
                )
                .findFirst();
    }

    public List<UsefulGodMethodContribution> coreContributions() {

        return contributions
                .stream()
                .filter(
                        UsefulGodMethodContribution::coreMethod
                )
                .toList();
    }

    public List<UsefulGodMethodContribution> selectedContributions() {

        return contributions
                .stream()
                .filter(
                        UsefulGodMethodContribution::selected
                )
                .toList();
    }

    public List<UsefulGodMethodContribution> meaningfulSelectedContributions() {

        return contributions
                .stream()
                .filter(
                        UsefulGodMethodContribution::meaningfulSelected
                )
                .toList();
    }

    public List<UsefulGodMethodContribution> conditionalContributions() {

        return contributions
                .stream()
                .filter(
                        UsefulGodMethodContribution::conditionalCandidate
                )
                .toList();
    }

    public int corePrimaryMethodCount() {

        return (int) contributions
                .stream()
                .filter(
                        UsefulGodMethodContribution::corePrimary
                )
                .count();
    }

    public int coreSelectedMethodCount() {

        return (int) contributions
                .stream()
                .filter(
                        UsefulGodMethodContribution::coreSelected
                )
                .count();
    }

    public int meaningfulSelectedMethodCount() {

        return meaningfulSelectedContributions()
                .size();
    }

    public int selectedMethodCount() {

        return selectedContributions()
                .size();
    }

    public boolean hasCoreConflict() {

        return contributions
                .stream()
                .anyMatch(
                        UsefulGodMethodContribution::coreConflict
                );
    }

    public boolean hasActiveConflict() {

        return contributions
                .stream()
                .anyMatch(
                        UsefulGodMethodContribution::activeConflict
                );
    }

    public boolean hasCoreConditionalWarning() {

        return contributions
                .stream()
                .anyMatch(
                        UsefulGodMethodContribution
                                ::coreConditionalWarning
                );
    }

    public boolean hasConditionalEvidence() {

        return !conditionalContributions()
                .isEmpty();
    }

    public boolean hasCaution() {

        return contributions
                .stream()
                .anyMatch(
                        UsefulGodMethodContribution::caution
                );
    }

    public boolean followFlowBreaking() {

        return contributionOf(
                UsefulGodMethodType.FOLLOW_STRUCTURE
        )
                .map(
                        UsefulGodMethodContribution
                                ::followFlowBreaking
                )
                .orElse(
                        false
                );
    }

    public boolean saturated() {

        return presenceLevel.saturated();
    }

    public boolean scarce() {

        return presenceLevel.scarce();
    }

    public boolean hasReason(
            UsefulGodIntegrationReasonType reason
    ) {

        if (
                reason == null
        ) {

            return false;
        }

        return reasons.contains(
                reason
        );
    }

    public double existingSharePercent() {

        return existingShareBasisPoints
                / 100.0;
    }
}