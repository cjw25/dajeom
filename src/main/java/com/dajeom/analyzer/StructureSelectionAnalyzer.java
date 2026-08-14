package com.dajeom.analyzer;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.SpecialStructureAnalysis;
import com.dajeom.model.SpecialStructureEvidence;
import com.dajeom.model.StructurePurityAnalysis;
import com.dajeom.model.StructurePurityEvidence;
import com.dajeom.model.StructureSelectionAnalysis;
import com.dajeom.model.StructureSelectionCandidate;
import com.dajeom.rule.StructureSelectionRule;
import com.dajeom.type.StructureCandidateKind;
import com.dajeom.type.StructureCandidateRole;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-9.
 *
 * 전체 일반격 / 특수격 후보를 받아
 * 주격 후보와 보조·보류·제외 후보를 결정한다.
 *
 * =================================================
 *
 * 입력:
 *
 * 4차
 * DayMasterStrengthAnalysis
 *
 * +
 *
 * 5-7
 * StructurePurityAnalysis
 *
 * +
 *
 * 5-8
 * SpecialStructureAnalysis
 *
 * =================================================
 *
 * 주격 우선순위:
 *
 * 1. 정확한 월지 특수구조
 *    - 건록
 *    - 양인
 *
 * 2. 엄격하게 성립한 종격 후보
 *
 * 3. 일반 월령격 후보
 *
 * 4. 비겁 월령 특수검토 후보
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * - 하나의 종합점수를 만들어 최고점을 고르지 않는다.
 * - StructureSelectionRule의 구조적 우선순위를 사용한다.
 * - 주격이 아닌 후보도 삭제하지 않는다.
 * - SECONDARY / DEFERRED / REJECTED 상태로 보존한다.
 * - 5-10 이전이므로 최종 외부 격국 확정값은 아니다.
 */
@Component
public class StructureSelectionAnalyzer {

    private final StructureSelectionRule
            structureSelectionRule;

    public StructureSelectionAnalyzer(
            StructureSelectionRule structureSelectionRule
    ) {

        this.structureSelectionRule =
                Objects.requireNonNull(
                        structureSelectionRule,
                        "StructureSelectionRule이 필요합니다."
                );
    }

    /**
     * =================================================
     * 정식 5-9 진입점
     * =================================================
     */
    public StructureSelectionAnalysis analyze(

            DayMasterStrengthAnalysis strength,

            StructurePurityAnalysis purity,

            SpecialStructureAnalysis special

    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                purity,
                "5-7 순도·혼잡도 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                special,
                "5-8 특수격 분석 결과가 필요합니다."
        );

        validateIdentity(
                strength,
                purity,
                special
        );

        /*
         * =================================================
         * 1.
         *
         * 주격 후보를 먼저 결정한다.
         *
         * Rule에 정의된 구조적 우선순위를 그대로 따른다.
         * =================================================
         */

        StructurePurityEvidence
                primaryStandardEvidence =
                null;

        SpecialStructureEvidence
                primarySpecialEvidence =
                null;

        /*
         * -------------------------------------------------
         * 1순위
         *
         * 정확한 월지 특수구조
         *
         * 건록 / 양인
         * -------------------------------------------------
         */
        Optional<SpecialStructureEvidence>
                exactMonthSpecial =
                structureSelectionRule
                        .findExactMonthSpecialPrimary(
                                special.evidences()
                        );

        if (
                exactMonthSpecial.isPresent()
        ) {

            primarySpecialEvidence =
                    exactMonthSpecial.get();
        }

        /*
         * -------------------------------------------------
         * 2순위
         *
         * 엄격한 종격 후보
         * -------------------------------------------------
         */
        if (
                primarySpecialEvidence == null
        ) {

            Optional<SpecialStructureEvidence>
                    followStructure =
                    structureSelectionRule
                            .findFollowStructurePrimary(
                                    special.evidences()
                            );

            if (
                    followStructure.isPresent()
            ) {

                primarySpecialEvidence =
                        followStructure.get();
            }
        }

        /*
         * -------------------------------------------------
         * 3순위
         *
         * 일반 월령격
         * -------------------------------------------------
         */
        if (
                primarySpecialEvidence == null
        ) {

            Optional<StructurePurityEvidence>
                    standardPrimary =
                    structureSelectionRule
                            .selectStandardPrimary(
                                    purity.evidences()
                            );

            if (
                    standardPrimary.isPresent()
            ) {

                primaryStandardEvidence =
                        standardPrimary.get();
            }
        }

        /*
         * -------------------------------------------------
         * 4순위
         *
         * 비겁 월령 특수검토
         *
         * 정확한 건록/양인도 아니고,
         * 엄격 종격도 아니며,
         * 일반 내격 후보도 사용할 수 없을 때의 fallback.
         * -------------------------------------------------
         */
        if (
                primarySpecialEvidence == null
                        && primaryStandardEvidence == null
        ) {

            Optional<SpecialStructureEvidence>
                    companionMonthReview =
                    structureSelectionRule
                            .findCompanionMonthReview(
                                    special.evidences()
                            );

            if (
                    companionMonthReview.isPresent()
            ) {

                primarySpecialEvidence =
                        companionMonthReview.get();
            }
        }

        /*
         * StructureSelectionAnalysis 자체가
         * 정확히 하나의 primaryCandidate를 요구한다.
         *
         * 여기서 임의로 월령 정기 등을 PRIMARY로
         * 승격시키지 않는다.
         *
         * 위의 정식 Rule에서도 주격을 찾지 못했다면
         * upstream 5차 분석의 불일치이므로 명확히 실패시킨다.
         */
        if (
                primaryStandardEvidence == null
                        && primarySpecialEvidence == null
        ) {

            throw new IllegalStateException(
                    "5-9 주격 후보를 결정할 수 없습니다. "
                            + "5-7 일반격 후보와 5-8 특수격 후보 상태를 확인해야 합니다."
            );
        }

        /*
         * =================================================
         * 2.
         *
         * 전체 후보 생성
         * =================================================
         */

        List<StructureSelectionCandidate> candidates =
                new ArrayList<>();

        StructureSelectionCandidate primaryCandidate =
                null;

        /*
         * =================================================
         * 2-1.
         *
         * 일반격 후보
         * =================================================
         */
        for (
                StructurePurityEvidence evidence
                : purity.evidences()
        ) {

            boolean primary =
                    evidence
                            == primaryStandardEvidence;

            StructureCandidateRole role =
                    primary
                            ? StructureCandidateRole.PRIMARY
                            : structureSelectionRule
                            .determineNonPrimaryStandardRole(
                                    evidence
                            );

            StructureSelectionCandidate candidate =
                    new StructureSelectionCandidate(
                            StructureCandidateKind.STANDARD,
                            role,
                            evidence.tenGod(),
                            null,
                            evidence,
                            null,
                            structureSelectionRule
                                    .reasonsForStandard(
                                            evidence
                                    )
                    );

            candidates.add(
                    candidate
            );

            if (
                    primary
            ) {

                primaryCandidate =
                        candidate;
            }
        }

        /*
         * =================================================
         * 2-2.
         *
         * 특수격 후보
         * =================================================
         */
        for (
                SpecialStructureEvidence evidence
                : special.evidences()
        ) {

            boolean primary =
                    evidence
                            == primarySpecialEvidence;

            StructureCandidateRole role =
                    primary
                            ? StructureCandidateRole.PRIMARY
                            : structureSelectionRule
                            .determineNonPrimarySpecialRole(
                                    evidence
                            );

            StructureSelectionCandidate candidate =
                    new StructureSelectionCandidate(
                            StructureCandidateKind.SPECIAL,
                            role,
                            null,
                            evidence.type(),
                            null,
                            evidence,
                            structureSelectionRule
                                    .reasonsForSpecial(
                                            evidence
                                    )
                    );

            candidates.add(
                    candidate
            );

            if (
                    primary
            ) {

                primaryCandidate =
                        candidate;
            }
        }

        /*
         * =================================================
         * 3.
         *
         * 최종 내부 무결성 확인
         * =================================================
         */
        if (
                primaryCandidate == null
        ) {

            throw new IllegalStateException(
                    "주격 Evidence는 선택되었지만 "
                            + "StructureSelectionCandidate로 변환되지 않았습니다."
            );
        }

        /*
         * =================================================
         * 4.
         *
         * 5-9 최종 결과
         * =================================================
         */
        return new StructureSelectionAnalysis(
                purity.dayMaster(),
                purity.monthBranch(),
                primaryCandidate,
                candidates
        );
    }

    /**
     * =================================================
     * 단계 간 identity 검증
     * =================================================
     */
    private void validateIdentity(

            DayMasterStrengthAnalysis strength,

            StructurePurityAnalysis purity,

            SpecialStructureAnalysis special

    ) {

        /*
         * 일간 일치.
         */
        if (
                strength.dayMaster()
                        != purity.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "4차 신강·신약 분석과 "
                            + "5-7 순도 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                strength.dayMaster()
                        != special.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "4차 신강·신약 분석과 "
                            + "5-8 특수격 분석의 일간이 일치하지 않습니다."
            );
        }

        /*
         * 월지 일치.
         */
        if (
                purity.monthBranch()
                        != special.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "5-7 순도 분석과 "
                            + "5-8 특수격 분석의 월지가 일치하지 않습니다."
            );
        }

        /*
         * 5-8은 5-7까지 계산된
         * 의미 있는 일반격 후보 수를 전달받는 구조이므로
         * 동일한 값이어야 한다.
         */
        if (
                purity.meaningfulStandardCandidateCount()
                        != special.meaningfulStandardCandidateCount()
        ) {

            throw new IllegalArgumentException(
                    "5-7과 5-8의 의미 있는 일반 내격 후보 수가 일치하지 않습니다."
            );
        }
    }
}