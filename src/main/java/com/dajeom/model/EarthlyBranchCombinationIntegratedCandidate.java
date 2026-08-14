package com.dajeom.model;

import com.dajeom.type.EarthlyBranchCombinationCandidateKind;
import com.dajeom.type.EarthlyBranchCombinationIntegrationReasonType;
import com.dajeom.type.EarthlyBranchCombinationIntegrationRole;
import com.dajeom.type.EarthlyBranchCombinationObstructionLevel;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-8.
 *
 * 9-7 방해 분석까지 완료된
 * 지지 결합 후보 하나에 대해:
 *
 * - 동일 목표 오행 수렴 후보
 * - 다른 목표 오행 경쟁 후보
 * - 상대 우선도
 * - 최종 통합 역할
 *
 * 을 붙인 결과.
 *
 * =================================================
 *
 * sourceEvidence
 *
 * 9-7의:
 *
 * EarthlyBranchCombinationObstructionEvidence
 *
 * =================================================
 *
 * convergentCandidates
 *
 * 구성원을 하나 이상 공유하면서
 * 목표 오행도 같은 후보.
 *
 * 예:
 *
 * 辰酉六合 → 金
 * 酉丑半合 → 金
 *
 * 둘은 酉를 공유하며 목표도 金이므로
 * 수렴 관계다.
 *
 * =================================================
 *
 * competingCandidates
 *
 * 구성원을 공유하지만
 * 서로 다른 목표 오행을 향하는 후보.
 *
 * 예:
 *
 * 辰酉六合 → 金
 * 申辰 부분삼합 → 水
 *
 * 둘은 辰을 공유하지만
 * 목표 오행이 다르므로 경쟁 관계다.
 *
 * =================================================
 *
 * 후보를 삭제하지 않는다.
 */
public record EarthlyBranchCombinationIntegratedCandidate(

        EarthlyBranchCombinationObstructionEvidence sourceEvidence,

        EarthlyBranchCombinationIntegrationRole role,

        List<EarthlyBranchCombinationObstructionEvidence>
        convergentCandidates,

        List<EarthlyBranchCombinationObstructionEvidence>
        higherPriorityConvergentCandidates,

        List<EarthlyBranchCombinationObstructionEvidence>
        competingCandidates,

        List<EarthlyBranchCombinationObstructionEvidence>
        higherPriorityCompetingCandidates,

        List<EarthlyBranchCombinationObstructionEvidence>
        equalPriorityCompetingCandidates,

        List<EarthlyBranchCombinationIntegrationReasonType>
        reasons

) {

    public EarthlyBranchCombinationIntegratedCandidate {

        Objects.requireNonNull(
                sourceEvidence,
                "9-7 결합 방해 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "9-8 결합 통합 역할이 필요합니다."
        );

        /*
         * =================================================
         * Compact constructor 입력 정규화
         * =================================================
         *
         * compact constructor body가 끝나기 전에는
         * record component field가 아직 자동 대입되지 않는다.
         *
         * 따라서 constructor 내부 검증에서는
         * this.sourceEvidence 등을 읽는 instance helper를
         * 사용하지 않고 constructor parameter를 직접 전달한다.
         */

        convergentCandidates =
                convergentCandidates == null
                        ? List.of()
                        : List.copyOf(
                        convergentCandidates
                );

        higherPriorityConvergentCandidates =
                higherPriorityConvergentCandidates == null
                        ? List.of()
                        : List.copyOf(
                        higherPriorityConvergentCandidates
                );

        competingCandidates =
                competingCandidates == null
                        ? List.of()
                        : List.copyOf(
                        competingCandidates
                );

        higherPriorityCompetingCandidates =
                higherPriorityCompetingCandidates == null
                        ? List.of()
                        : List.copyOf(
                        higherPriorityCompetingCandidates
                );

        equalPriorityCompetingCandidates =
                equalPriorityCompetingCandidates == null
                        ? List.of()
                        : List.copyOf(
                        equalPriorityCompetingCandidates
                );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        /*
         * =================================================
         * 자기 자신 포함 금지
         * =================================================
         *
         * 기존:
         *
         * validateNoSelf(candidates, label)
         *
         * 내부에서 this.sourceEvidence를 읽었기 때문에
         * compact constructor 중에는 null이었다.
         *
         * 현재:
         *
         * constructor parameter sourceEvidence를
         * 명시적으로 전달한다.
         */

        validateNoSelf(
                sourceEvidence,
                convergentCandidates,
                "수렴 후보"
        );

        validateNoSelf(
                sourceEvidence,
                competingCandidates,
                "경쟁 후보"
        );

        /*
         * =================================================
         * 수렴 후보 정합성
         * =================================================
         */

        for (
                EarthlyBranchCombinationObstructionEvidence candidate
                : convergentCandidates
        ) {

            if (
                    candidate.targetElement()
                            != sourceEvidence.targetElement()
            ) {

                throw new IllegalArgumentException(
                        "수렴 후보는 현재 후보와 목표 오행이 같아야 합니다."
                );
            }

            if (
                    !sharesPosition(
                            sourceEvidence,
                            candidate
                    )
            ) {

                throw new IllegalArgumentException(
                        "수렴 후보는 현재 후보와 하나 이상의 구성원 위치를 공유해야 합니다."
                );
            }
        }

        /*
         * =================================================
         * 경쟁 후보 정합성
         * =================================================
         */

        for (
                EarthlyBranchCombinationObstructionEvidence candidate
                : competingCandidates
        ) {

            if (
                    candidate.targetElement()
                            == sourceEvidence.targetElement()
            ) {

                throw new IllegalArgumentException(
                        "경쟁 후보는 현재 후보와 다른 목표 오행을 가져야 합니다."
                );
            }

            if (
                    !sharesPosition(
                            sourceEvidence,
                            candidate
                    )
            ) {

                throw new IllegalArgumentException(
                        "경쟁 후보는 현재 후보와 하나 이상의 구성원 위치를 공유해야 합니다."
                );
            }
        }

        /*
         * =================================================
         * 상위 수렴 후보 subset 검증
         * =================================================
         */

        for (
                EarthlyBranchCombinationObstructionEvidence candidate
                : higherPriorityConvergentCandidates
        ) {

            if (
                    !convergentCandidates.contains(
                            candidate
                    )
            ) {

                throw new IllegalArgumentException(
                        "상위 수렴 후보는 전체 수렴 후보 목록에 포함되어야 합니다."
                );
            }
        }

        /*
         * =================================================
         * 상위 경쟁 후보 subset 검증
         * =================================================
         */

        for (
                EarthlyBranchCombinationObstructionEvidence candidate
                : higherPriorityCompetingCandidates
        ) {

            if (
                    !competingCandidates.contains(
                            candidate
                    )
            ) {

                throw new IllegalArgumentException(
                        "상위 경쟁 후보는 전체 경쟁 후보 목록에 포함되어야 합니다."
                );
            }
        }

        /*
         * =================================================
         * 동급 경쟁 후보 subset 검증
         * =================================================
         */

        for (
                EarthlyBranchCombinationObstructionEvidence candidate
                : equalPriorityCompetingCandidates
        ) {

            if (
                    !competingCandidates.contains(
                            candidate
                    )
            ) {

                throw new IllegalArgumentException(
                        "동급 경쟁 후보는 전체 경쟁 후보 목록에 포함되어야 합니다."
                );
            }
        }

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "9-8 통합 후보에는 하나 이상의 판정 근거가 필요합니다."
            );
        }
    }

    /*
     * =================================================
     * 자기 자신 포함 검증
     * =================================================
     *
     * static helper.
     *
     * compact constructor에서 아직 초기화되지 않은
     * this.sourceEvidence를 참조하지 않는다.
     */
    private static void validateNoSelf(

            EarthlyBranchCombinationObstructionEvidence sourceEvidence,

            List<EarthlyBranchCombinationObstructionEvidence> candidates,

            String label

    ) {

        Objects.requireNonNull(
                sourceEvidence,
                "현재 9-7 결합 방해 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                candidates,
                "9-8 비교 후보 목록이 필요합니다."
        );

        if (
                candidates.contains(
                        sourceEvidence
                )
        ) {

            throw new IllegalArgumentException(
                    label + "에 현재 후보 자신을 포함할 수 없습니다."
            );
        }
    }

    private static boolean sharesPosition(

            EarthlyBranchCombinationObstructionEvidence first,

            EarthlyBranchCombinationObstructionEvidence second

    ) {

        Objects.requireNonNull(
                first,
                "첫 번째 결합 방해 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                second,
                "두 번째 결합 방해 Evidence가 필요합니다."
        );

        return first
                .candidatePositions()
                .stream()
                .anyMatch(
                        second
                                .candidatePositions()
                                ::contains
                );
    }

    /*
     * =================================================
     * 기본 정보
     * =================================================
     */

    public EarthlyBranchCombinationCandidateKind candidateKind() {

        return sourceEvidence
                .candidateKind();
    }

    public FiveElement targetElement() {

        return sourceEvidence
                .targetElement();
    }

    public List<EarthlyBranchPosition> candidatePositions() {

        return sourceEvidence
                .candidatePositions();
    }

    public EarthlyBranchCombinationObstructionLevel obstructionLevel() {

        return sourceEvidence
                .level();
    }

    public boolean baseMeaningful() {

        return sourceEvidence
                .baseMeaningful();
    }

    public boolean baseCandidateRemains() {

        return sourceEvidence
                .baseCandidateRemains();
    }

    public boolean monthBranchInvolved() {

        return sourceEvidence
                .monthBranchInvolved();
    }

    public boolean dayBranchInvolved() {

        return sourceEvidence
                .dayBranchInvolved();
    }

    /*
     * =================================================
     * 수렴
     * =================================================
     */

    public boolean hasConvergence() {

        return !convergentCandidates
                .isEmpty();
    }

    public int convergenceCount() {

        return convergentCandidates
                .size();
    }

    public boolean hasHigherPriorityConvergentCandidate() {

        return !higherPriorityConvergentCandidates
                .isEmpty();
    }

    /*
     * =================================================
     * 경쟁
     * =================================================
     */

    public boolean hasCompetition() {

        return !competingCandidates
                .isEmpty();
    }

    public int competitionCount() {

        return competingCandidates
                .size();
    }

    public boolean hasHigherPriorityCompetitor() {

        return !higherPriorityCompetingCandidates
                .isEmpty();
    }

    public boolean hasEqualPriorityCompetitor() {

        return !equalPriorityCompetingCandidates
                .isEmpty();
    }

    public boolean unresolvedCompetition() {

        return role.competing();
    }

    /*
     * =================================================
     * 최종 역할
     * =================================================
     */

    public boolean primary() {

        return role.primary();
    }

    public boolean supporting() {

        return role.supporting();
    }

    public boolean competing() {

        return role.competing();
    }

    public boolean deferred() {

        return role.deferred();
    }

    public boolean suppressed() {

        return role.suppressed();
    }

    public boolean effective() {

        return role.effective();
    }

    public boolean requiresReview() {

        return role.requiresReview();
    }

    /*
     * =================================================
     * 조회
     * =================================================
     */

    public boolean involves(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return false;
        }

        return candidatePositions()
                .contains(
                        position
                );
    }

    public List<EarthlyBranchPosition> sharedPositionsWith(
            EarthlyBranchCombinationObstructionEvidence other
    ) {

        if (
                other == null
        ) {

            return List.of();
        }

        return candidatePositions()
                .stream()
                .filter(
                        other
                                .candidatePositions()
                                ::contains
                )
                .toList();
    }

    public boolean hasReason(
            EarthlyBranchCombinationIntegrationReasonType reason
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
}