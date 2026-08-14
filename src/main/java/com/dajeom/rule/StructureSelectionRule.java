package com.dajeom.rule;

import com.dajeom.model.SpecialStructureEvidence;
import com.dajeom.model.StructureEstablishmentEvidence;
import com.dajeom.model.StructurePurityEvidence;
import com.dajeom.type.StructureCandidateRole;
import com.dajeom.type.StructureSelectionReasonType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-9.
 *
 * 주격/보조격 후보를 결정하는 Rule.
 *
 * 중요한 원칙:
 *
 * 하나의 종합점수를 만들어
 *
 * highestScore = primary
 *
 * 형태로 결정하지 않는다.
 *
 * 후보 유형별 성립 조건과
 * 구조적인 우선순위를 명시적으로 비교한다.
 *
 * =================================================
 *
 * 전체 우선순위
 *
 * 1.
 * 정확한 월지 특수구조
 *
 * 건록
 * 양인
 *
 * 2.
 * 4-7에서 엄격하게 검증된 종격 후보
 *
 * 3.
 * 일반 월령격
 *
 * 4.
 * 기타 비겁 월령 특수검토
 *
 * =================================================
 */
@Component
public class StructureSelectionRule {

    /**
     * 정확한 월지 특수구조.
     *
     * 건록/양인 후보 가운데
     * exactMonthStructureCandidate가 있는지 찾는다.
     */
    public Optional<SpecialStructureEvidence>
    findExactMonthSpecialPrimary(
            List<SpecialStructureEvidence> evidences
    ) {

        Objects.requireNonNull(
                evidences,
                "특수격 Evidence 목록이 필요합니다."
        );

        return evidences
                .stream()
                .filter(
                        SpecialStructureEvidence
                                ::exactMonthStructureCandidate
                )
                .findFirst();
    }

    /**
     * 4-7에서 전달된 엄격한 종격 후보.
     */
    public Optional<SpecialStructureEvidence>
    findFollowStructurePrimary(
            List<SpecialStructureEvidence> evidences
    ) {

        Objects.requireNonNull(
                evidences,
                "특수격 Evidence 목록이 필요합니다."
        );

        return evidences
                .stream()
                .filter(
                        SpecialStructureEvidence
                                ::inheritedFollowCandidate
                )
                .findFirst();
    }

    /**
     * 비겁 월령 특수검토 후보.
     *
     * 정확한 건록/양인도 없고,
     * 실제 일반 내격 후보도 사용할 수 없을 때
     * 최종 fallback으로 사용한다.
     */
    public Optional<SpecialStructureEvidence>
    findCompanionMonthReview(
            List<SpecialStructureEvidence> evidences
    ) {

        Objects.requireNonNull(
                evidences,
                "특수격 Evidence 목록이 필요합니다."
        );

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence
                                        .type()
                                        .companionMonthReview()
                )
                .findFirst();
    }

    /**
     * 일반 월령격 가운데
     * 가장 중심적인 후보를 선택한다.
     *
     * 단순 점수를 사용하지 않고
     * 단계적인 구조 비교를 한다.
     */
    public Optional<StructurePurityEvidence>
    selectStandardPrimary(
            List<StructurePurityEvidence> evidences
    ) {

        Objects.requireNonNull(
                evidences,
                "일반격 Evidence 목록이 필요합니다."
        );

        List<StructurePurityEvidence> eligible =
                evidences
                        .stream()
                        .filter(
                                StructurePurityEvidence
                                        ::standardStructureCandidate
                        )
                        .filter(
                                StructurePurityEvidence
                                        ::activeCandidate
                        )
                        .toList();

        if (
                eligible.isEmpty()
        ) {

            return Optional.empty();
        }

        StructurePurityEvidence selected =
                eligible.get(
                        0
                );

        for (
                int index = 1;
                index < eligible.size();
                index++
        ) {

            StructurePurityEvidence challenger =
                    eligible.get(
                            index
                    );

            if (
                    compareStandard(
                            challenger,
                            selected
                    ) < 0
            ) {

                selected =
                        challenger;
            }
        }

        return Optional.of(
                selected
        );
    }

    /**
     * 일반격 후보 비교.
     *
     * 반환:
     *
     * 음수 -> first 우선
     * 양수 -> second 우선
     * 0    -> 동등
     */
    private int compareStandard(

            StructurePurityEvidence first,

            StructurePurityEvidence second

    ) {

        /*
         * ================================================
         * 1.
         * 월령 정기와 부기의 특수 비교
         * ================================================
         *
         * 정기를 기계적으로 항상 1순위로 두면:
         *
         * 정기 미투출 + WEAK
         * 중기 월간 투출 + ESTABLISHED
         *
         * 같은 구조를 처리할 수 없다.
         *
         * 반대로 투출 하나만 보고
         * 정기 권위를 쉽게 버려서도 안 된다.
         */
        if (
                first.mainQi()
                        != second.mainQi()
        ) {

            StructurePurityEvidence main =
                    first.mainQi()
                            ? first
                            : second;

            StructurePurityEvidence secondary =
                    first.mainQi()
                            ? second
                            : first;

            boolean secondaryOverridesMain =
                    secondaryMayOverrideMain(
                            main,
                            secondary
                    );

            StructurePurityEvidence preferred =
                    secondaryOverridesMain
                            ? secondary
                            : main;

            return preferred == first
                    ? -1
                    : 1;
        }

        /*
         * ================================================
         * 2.
         * 5-3 격 성립도
         * ================================================
         */
        int comparison =
                Integer.compare(
                        establishmentOf(
                                second
                        )
                                .level()
                                .rank(),

                        establishmentOf(
                                first
                        )
                                .level()
                                .rank()
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        /*
         * ================================================
         * 3.
         * 월간 직접 투출
         * ================================================
         */
        comparison =
                Boolean.compare(
                        establishmentOf(
                                second
                        )
                                .monthStemExposed(),

                        establishmentOf(
                                first
                        )
                                .monthStemExposed()
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        /*
         * ================================================
         * 4.
         * 투출 우선순위
         * ================================================
         */
        comparison =
                Integer.compare(
                        establishmentOf(
                                second
                        )
                                .exposureRank(),

                        establishmentOf(
                                first
                        )
                                .exposureRank()
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        /*
         * ================================================
         * 5.
         * 일간과의 강약 수용성
         * ================================================
         */
        comparison =
                Integer.compare(
                        balanceRank(
                                second
                        ),
                        balanceRank(
                                first
                        )
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        /*
         * ================================================
         * 6.
         * 파격
         *
         * 파격 level은 낮을수록 우선.
         * ================================================
         */
        comparison =
                Integer.compare(
                        damageRank(
                                first
                        ),
                        damageRank(
                                second
                        )
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        /*
         * ================================================
         * 7.
         * 순도
         * ================================================
         */
        comparison =
                Integer.compare(
                        second
                                .purityLevel()
                                .rank(),

                        first
                                .purityLevel()
                                .rank()
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        /*
         * ================================================
         * 8.
         * 성격 요소
         * ================================================
         */
        comparison =
                Integer.compare(
                        supportRank(
                                second
                        ),
                        supportRank(
                                first
                        )
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        /*
         * ================================================
         * 9.
         * 월령 source 권위
         * ================================================
         */
        comparison =
                Integer.compare(
                        establishmentOf(
                                second
                        )
                                .sourceAuthorityRank(),

                        establishmentOf(
                                first
                        )
                                .sourceAuthorityRank()
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        /*
         * ================================================
         * 10.
         * 실제 오행 비중
         *
         * 마지막 tie-breaker일 뿐
         * 주격 점수가 아니다.
         * ================================================
         */
        return Integer.compare(
                establishmentOf(
                        second
                )
                        .elementShareBasisPoints(),

                establishmentOf(
                        first
                )
                        .elementShareBasisPoints()
        );
    }

    /**
     * 정기보다 중기/여기가
     * 실제 주격 후보로 앞설 수 있는
     * 제한적인 조건.
     */
    private boolean secondaryMayOverrideMain(

            StructurePurityEvidence main,

            StructurePurityEvidence secondary

    ) {

        StructureEstablishmentEvidence mainEstablishment =
                establishmentOf(
                        main
                );

        StructureEstablishmentEvidence secondaryEstablishment =
                establishmentOf(
                        secondary
                );

        /*
         * 정기가 ESTABLISHED라면
         * 월령 본기의 권위를 유지한다.
         */
        if (
                mainEstablishment.established()
        ) {

            return false;
        }

        /*
         * 정기가 WEAK인데
         * 부기가 의미 있게 성립하면
         * 부기가 앞설 수 있다.
         */
        if (
                mainEstablishment.weak()

                        && secondaryEstablishment
                        .meaningfulCandidate()
        ) {

            return true;
        }

        /*
         * 가장 중요한 예외:
         *
         * 정기
         * -> SUPPORTED
         * -> 미투출
         *
         * 중기/여기
         * -> ESTABLISHED
         * -> 월간 직접 투출
         *
         * 이면 실제 천간 표현이 강한
         * 부기 후보가 앞설 수 있다.
         */
        if (
                mainEstablishment.supported()

                        && !mainEstablishment.exposed()

                        && secondaryEstablishment.established()

                        && secondaryEstablishment
                        .monthStemExposed()
        ) {

            return true;
        }

        return false;
    }

    /**
     * 주격이 아닌 일반격 후보의 역할.
     */
    public StructureCandidateRole
    determineNonPrimaryStandardRole(
            StructurePurityEvidence evidence
    ) {

        Objects.requireNonNull(
                evidence,
                "일반격 Evidence가 필요합니다."
        );

        if (
                !evidence.standardStructureCandidate()
        ) {

            return StructureCandidateRole
                    .REJECTED;
        }

        if (
                !evidence.activeCandidate()
        ) {

            return StructureCandidateRole
                    .REJECTED;
        }

        StructureEstablishmentEvidence establishment =
                establishmentOf(
                        evidence
                );

        if (
                establishment.weak()
        ) {

            return StructureCandidateRole
                    .DEFERRED;
        }

        /*
         * 의미 있는 후보라도:
         *
         * 강한 파격
         * +
         * 높은 혼잡
         *
         * 이 동시에 존재한다면
         * 보조격으로 바로 채택하지 않고 보류한다.
         */
        if (
                evidence.highlyMixed()

                        && evidence
                        .damageEvidence()
                        .hasStrongDamage()
        ) {

            return StructureCandidateRole
                    .DEFERRED;
        }

        return StructureCandidateRole
                .SECONDARY;
    }

    /**
     * 주격으로 선택되지 않은 특수격 후보.
     */
    public StructureCandidateRole
    determineNonPrimarySpecialRole(
            SpecialStructureEvidence evidence
    ) {

        Objects.requireNonNull(
                evidence,
                "특수격 Evidence가 필요합니다."
        );

        if (
                evidence.type()
                        .companionMonthReview()
        ) {

            return StructureCandidateRole
                    .DEFERRED;
        }

        /*
         * 정확한 건록/양인 또는
         * 엄격한 종격 후보라면
         * 주격이 아니더라도 의미 있는
         * 보조 특수구조로 보존한다.
         */
        return StructureCandidateRole
                .SECONDARY;
    }

    public List<StructureSelectionReasonType>
    reasonsForStandard(
            StructurePurityEvidence evidence
    ) {

        Objects.requireNonNull(
                evidence,
                "일반격 Evidence가 필요합니다."
        );

        List<StructureSelectionReasonType> reasons =
                new ArrayList<>();

        StructureEstablishmentEvidence establishment =
                establishmentOf(
                        evidence
                );

        if (
                evidence.mainQi()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .MAIN_QI_AUTHORITY
            );
        }

        if (
                establishment.monthStemExposed()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .MONTH_STEM_EXPOSURE
            );

        } else if (
                establishment.exposed()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .OTHER_STEM_EXPOSURE
            );
        }

        if (
                establishment.established()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .ESTABLISHED_STRUCTURE
            );

        } else if (
                establishment.supported()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .SUPPORTED_STRUCTURE
            );

        } else if (
                establishment.weak()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .WEAK_STRUCTURE
            );

        } else {

            reasons.add(
                    StructureSelectionReasonType
                            .INACTIVE_CANDIDATE
            );
        }

        if (
                evidence
                        .damageEvidence()
                        .supportEvidence()
                        .balanceEvidence()
                        .acceptable()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .BALANCE_ACCEPTABLE
            );

        } else if (
                evidence
                        .damageEvidence()
                        .supportEvidence()
                        .balanceEvidence()
                        .burdened()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .BALANCE_BURDENED
            );
        }

        if (
                evidence
                        .damageEvidence()
                        .supportEvidence()
                        .hasStrongSupport()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .STRONG_FORMATION_SUPPORT
            );

        } else if (
                evidence
                        .damageEvidence()
                        .supportEvidence()
                        .hasMeaningfulSupport()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .FORMATION_SUPPORT_PRESENT
            );
        }

        if (
                evidence
                        .damageEvidence()
                        .hasStrongDamage()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .STRONG_DAMAGE_PRESENT
            );

        } else if (
                evidence
                        .damageEvidence()
                        .hasMeaningfulDamage()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .MEANINGFUL_DAMAGE_PRESENT
            );

        } else {

            reasons.add(
                    StructureSelectionReasonType
                            .NO_MEANINGFUL_DAMAGE
            );
        }

        if (
                evidence.relativelyPure()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .RELATIVELY_PURE
            );

        } else if (
                evidence.highlyMixed()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .HIGHLY_MIXED
            );
        }

        return List.copyOf(
                reasons
        );
    }

    public List<StructureSelectionReasonType>
    reasonsForSpecial(
            SpecialStructureEvidence evidence
    ) {

        Objects.requireNonNull(
                evidence,
                "특수격 Evidence가 필요합니다."
        );

        List<StructureSelectionReasonType> reasons =
                new ArrayList<>();

        if (
                evidence.exactMonthStructureCandidate()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .EXACT_MONTH_SPECIAL
            );
        }

        if (
                evidence.inheritedFollowCandidate()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .STRICT_FOLLOW_STRUCTURE
            );
        }

        if (
                evidence.type()
                        .companionMonthReview()
        ) {

            reasons.add(
                    StructureSelectionReasonType
                            .SPECIAL_REVIEW_PENDING
            );
        }

        return List.copyOf(
                reasons
        );
    }

    private StructureEstablishmentEvidence
    establishmentOf(
            StructurePurityEvidence evidence
    ) {

        return evidence
                .damageEvidence()
                .supportEvidence()
                .balanceEvidence()
                .establishmentEvidence();
    }

    private int balanceRank(
            StructurePurityEvidence evidence
    ) {

        return evidence
                .damageEvidence()
                .supportEvidence()
                .balanceEvidence()
                .balanceLevel()
                .rank();
    }

    private int damageRank(
            StructurePurityEvidence evidence
    ) {

        return evidence
                .damageEvidence()
                .strongestDamageLevel()
                .rank();
    }

    private int supportRank(
            StructurePurityEvidence evidence
    ) {

        return evidence
                .damageEvidence()
                .supportEvidence()
                .strongestSupportLevel()
                .rank();
    }
}