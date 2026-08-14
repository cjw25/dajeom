package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.UsefulGodRelationRole;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 7차-10.
 *
 * 용신·희신·기신 분석의 최종 통합 모델.
 *
 * =================================================
 *
 * 전체 흐름
 *
 * 7-1
 * UsefulGodMethodAnalysis
 *
 * ↓
 *
 * 7-2 ~ 7-7
 * 각 용신법별 독립 후보 분석
 *
 * ↓
 *
 * 7-8
 * UsefulGodSelectionAnalysis
 *
 * ↓
 *
 * 7-9
 * UsefulGodRelationAnalysis
 *
 * ↓
 *
 * 7-10
 * UsefulGodAnalysis
 *
 * =================================================
 *
 * 이 클래스에서는 새로운 명리 판단을 하지 않는다.
 *
 * 이미 계산된 결과를 일관된 형태로 묶고,
 * 이후:
 *
 * - 천간·지지 관계
 * - 합화
 * - 대운
 * - 세운
 * - 월운
 * - 생활영역
 * - AI Grounding
 *
 * 에서 쉽게 사용할 수 있도록 한다.
 *
 * =================================================
 *
 * 최종 용어
 *
 * USEFUL_GOD
 * = 용신
 *
 * FAVORABLE_GOD
 * = 희신
 *
 * UNFAVORABLE_GOD
 * = 기신
 *
 * CONDITIONAL
 * = 조건부
 *
 * NEUTRAL
 * = 중립
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 복수 용신을 허용한다.
 *
 * 2.
 * 주용신이 하나가 아니라고
 * 배열 첫 번째 오행을 임의 선택하지 않는다.
 *
 * 3.
 * 기신이 없는 명식도 허용한다.
 *
 * 4.
 * 희신이 없는 명식도 허용한다.
 *
 * 5.
 * 출생시간 미상은 그대로 보존한다.
 */
public record UsefulGodAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        UsefulGodMethodAnalysis methodAnalysis,

        UsefulGodSelectionAnalysis selectionAnalysis,

        UsefulGodRelationAnalysis relationAnalysis,

        boolean completeChart

) {

    public UsefulGodAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                methodAnalysis,
                "7-1 용신법 분석이 필요합니다."
        );

        Objects.requireNonNull(
                selectionAnalysis,
                "7-8 용신 선택 분석이 필요합니다."
        );

        Objects.requireNonNull(
                relationAnalysis,
                "7-9 희신·기신 분석이 필요합니다."
        );

        /*
         * =================================================
         * 기본 원국 동일성 검증
         * =================================================
         */
        if (
                methodAnalysis.dayMaster()
                        != dayMaster

                        || selectionAnalysis.dayMaster()
                        != dayMaster

                        || relationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "7차 분석 결과들의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.dayMasterElement()
                        != dayMasterElement

                        || selectionAnalysis.dayMasterElement()
                        != dayMasterElement

                        || relationAnalysis.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "7차 분석 결과들의 일간 오행이 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.monthBranch()
                        != monthBranch

                        || selectionAnalysis.monthBranch()
                        != monthBranch

                        || relationAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "7차 분석 결과들의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.completeChart()
                        != completeChart

                        || selectionAnalysis.completeChart()
                        != completeChart

                        || relationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "7차 분석 결과들의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 단계 연결 검증
         * =================================================
         */
        if (
                !selectionAnalysis
                        .methodAnalysis()
                        .equals(
                                methodAnalysis
                        )
        ) {

            throw new IllegalArgumentException(
                    "7-8 분석이 참조하는 7-1 결과가 7-10 입력과 일치하지 않습니다."
            );
        }

        if (
                !relationAnalysis
                        .selectionAnalysis()
                        .equals(
                                selectionAnalysis
                        )
        ) {

            throw new IllegalArgumentException(
                    "7-9 분석이 참조하는 7-8 결과가 7-10 입력과 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 용신 일관성
         * =================================================
         *
         * 7-8 PRIMARY_USEFUL_GOD
         * =
         * 7-9 USEFUL_GOD
         *
         * 이어야 한다.
         */
        List<FiveElement> selectionPrimaryElements =
                selectionAnalysis
                        .primaryElements();

        List<FiveElement> relationUsefulElements =
                relationAnalysis
                        .usefulGodElements();

        if (
                selectionPrimaryElements.size()
                        != relationUsefulElements.size()

                        || !relationUsefulElements
                        .containsAll(
                                selectionPrimaryElements
                        )
        ) {

            throw new IllegalArgumentException(
                    "7-8 주용신과 7-9 용신 판정이 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 6차 원본
     * =================================================
     */

    public ClimateHealthAnalysis climateHealthAnalysis() {

        return methodAnalysis
                .climateHealthAnalysis();
    }

    /*
     * =================================================
     * 7-2 억부
     * =================================================
     */

    public BalancingUsefulGodAnalysis balancingAnalysis() {

        return selectionAnalysis
                .balancingAnalysis();
    }

    /*
     * =================================================
     * 7-3 조후
     * =================================================
     */

    public ClimateUsefulGodAnalysis climateAnalysis() {

        return selectionAnalysis
                .climateAnalysis();
    }

    /*
     * =================================================
     * 7-4 격국
     * =================================================
     */

    public StructureUsefulGodAnalysis structureAnalysis() {

        return selectionAnalysis
                .structureAnalysis();
    }

    /*
     * =================================================
     * 7-5 병약
     * =================================================
     */

    public RemedyUsefulGodAnalysis remedyAnalysis() {

        return selectionAnalysis
                .remedyAnalysis();
    }

    /*
     * =================================================
     * 7-6 통관
     * =================================================
     */

    public PassageUsefulGodAnalysis passageAnalysis() {

        return selectionAnalysis
                .passageAnalysis();
    }

    /*
     * =================================================
     * 7-7 종격
     * =================================================
     */

    public FollowStructureUsefulGodAnalysis
    followStructureAnalysis() {

        return selectionAnalysis
                .followStructureAnalysis();
    }

    /*
     * =================================================
     * 종격 모드
     * =================================================
     */

    public boolean followStructureMode() {

        return selectionAnalysis
                .followStructureMode();
    }

    /*
     * =================================================
     * 7-8 용신 후보
     * =================================================
     */

    public List<UsefulGodCandidate> usefulGodSelectionCandidates() {

        return selectionAnalysis
                .candidates();
    }

    public List<UsefulGodCandidate> primarySelectionCandidates() {

        return selectionAnalysis
                .primaryCandidates();
    }

    public List<UsefulGodCandidate> supportingSelectionCandidates() {

        return selectionAnalysis
                .supportingCandidates();
    }

    public List<UsefulGodCandidate> deferredSelectionCandidates() {

        return selectionAnalysis
                .deferredCandidates();
    }

    /*
     * =================================================
     * 최종 용신
     * =================================================
     */

    public List<UsefulGodRelationCandidate> usefulGodCandidates() {

        return relationAnalysis
                .usefulGodCandidates();
    }

    public List<FiveElement> usefulGodElements() {

        return relationAnalysis
                .usefulGodElements();
    }

    /*
     * =================================================
     * 최종 희신
     * =================================================
     */

    public List<UsefulGodRelationCandidate> favorableGodCandidates() {

        return relationAnalysis
                .favorableGodCandidates();
    }

    public List<FiveElement> favorableGodElements() {

        return relationAnalysis
                .favorableGodElements();
    }

    /*
     * =================================================
     * 최종 기신
     * =================================================
     */

    public List<UsefulGodRelationCandidate> unfavorableGodCandidates() {

        return relationAnalysis
                .unfavorableGodCandidates();
    }

    public List<FiveElement> unfavorableGodElements() {

        return relationAnalysis
                .unfavorableGodElements();
    }

    /*
     * =================================================
     * 조건부
     * =================================================
     */

    public List<UsefulGodRelationCandidate> conditionalCandidates() {

        return relationAnalysis
                .conditionalCandidates();
    }

    /*
     * =================================================
     * 중립
     * =================================================
     */

    public List<UsefulGodRelationCandidate> neutralCandidates() {

        return relationAnalysis
                .neutralCandidates();
    }

    /*
     * =================================================
     * 특정 五行 조회
     * =================================================
     */

    public Optional<UsefulGodCandidate> selectionCandidateOf(
            FiveElement element
    ) {

        return selectionAnalysis
                .candidateOf(
                        element
                );
    }

    public Optional<UsefulGodRelationCandidate> relationCandidateOf(
            FiveElement element
    ) {

        return relationAnalysis
                .candidateOf(
                        element
                );
    }

    /*
     * =================================================
     * 단일 주용신
     * =================================================
     */

    public Optional<UsefulGodCandidate> singlePrimarySelectionCandidate() {

        return selectionAnalysis
                .singlePrimaryCandidate();
    }

    public Optional<FiveElement> singlePrimaryElement() {

        return selectionAnalysis
                .singlePrimaryElement();
    }

    public Optional<UsefulGodRelationCandidate> singleUsefulGodCandidate() {

        List<UsefulGodRelationCandidate> usefulGods =
                usefulGodCandidates();

        if (
                usefulGods.size() != 1
        ) {

            return Optional.empty();
        }

        return Optional.of(
                usefulGods.get(0)
        );
    }

    /*
     * =================================================
     * 용신 상태
     * =================================================
     */

    public boolean hasUsefulGod() {

        return !usefulGodCandidates()
                .isEmpty();
    }

    public boolean hasSingleUsefulGod() {

        return usefulGodCandidates()
                .size() == 1;
    }

    public boolean hasMultipleUsefulGods() {

        return usefulGodCandidates()
                .size() >= 2;
    }

    /*
     * =================================================
     * 희신 상태
     * =================================================
     */

    public boolean hasFavorableGod() {

        return relationAnalysis
                .hasFavorableGod();
    }

    /*
     * =================================================
     * 기신 상태
     * =================================================
     */

    public boolean hasUnfavorableGod() {

        return relationAnalysis
                .hasUnfavorableGod();
    }

    /*
     * =================================================
     * 검토 상태
     * =================================================
     */

    /**
     * 주용신 자체가 아직 선택되지 않은 상태.
     */
    public boolean unresolvedUsefulGodSelection() {

        return selectionAnalysis
                .unresolvedUsefulGodSelection();
    }

    /**
     * 주용신이 복수로 남아 있는 상태.
     *
     * 오류가 아니다.
     */
    public boolean multiplePrimaryReview() {

        return selectionAnalysis
                .hasMultiplePrimaryUsefulGods();
    }

    /**
     * 용신 또는 희신·기신 관계에
     * 추가 검토가 필요한 Evidence가 있는가.
     */
    public boolean requiresReview() {

        return selectionAnalysis
                .requiresPrimaryReview()

                || relationAnalysis
                .requiresRelationReview();
    }

    /**
     * 주용신 기준축 자체가 없는 상태.
     */
    public boolean unresolvedPrimaryAnchor() {

        return relationAnalysis
                .unresolvedPrimaryAnchor();
    }

    /*
     * =================================================
     * 최종 역할별 조회
     * =================================================
     */

    public List<UsefulGodRelationCandidate> candidatesOf(
            UsefulGodRelationRole role
    ) {

        if (
                role == null
        ) {

            return List.of();
        }

        return relationAnalysis
                .candidates()
                .stream()
                .filter(
                        candidate ->
                                candidate.role() == role
                )
                .toList();
    }

    /*
     * =================================================
     * 출생시간
     * =================================================
     */

    public boolean hourUnknown() {

        return !completeChart;
    }
}