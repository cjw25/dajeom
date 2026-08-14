package com.dajeom.analyzer;

import com.dajeom.model.ClimateElementCandidate;
import com.dajeom.model.ClimateHealthAnalysis;
import com.dajeom.model.ClimateHealthCrossValidationAnalysis;
import com.dajeom.model.ClimateHealthValidatedCandidate;
import com.dajeom.model.ExtremeStrengthStructureAnalysis;
import com.dajeom.model.FollowStructureUsefulGodAnalysis;
import com.dajeom.model.FollowStructureUsefulGodCandidate;
import com.dajeom.model.RemedyCandidate;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.UsefulGodMethodAnalysis;
import com.dajeom.model.UsefulGodMethodEvidence;
import com.dajeom.rule.FollowStructureUsefulGodRule;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.ExtremeStrengthStructureType;
import com.dajeom.type.FiveElement;
import com.dajeom.type.FollowStructureUsefulGodCandidateRole;
import com.dajeom.type.FollowWeakDirection;
import com.dajeom.type.UsefulGodMethodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 7차-7.
 *
 * 7-1 FOLLOW_STRUCTURE 용신법을 이용해
 * 木火土金水 종격용신 후보를 계산한다.
 *
 * =================================================
 *
 * 조건
 *
 * 반드시:
 *
 * 5-9에서 실제 PRIMARY가
 * 종격으로 선택된 경우에만 활성.
 *
 * =================================================
 *
 * 종약
 *
 * FollowWeakDirection을 그대로 재사용:
 *
 * OUTPUT
 * WEALTH
 * OFFICER
 * MIXED
 *
 * =================================================
 *
 * 종강
 *
 * COMPANION
 * RESOURCE
 *
 * =================================================
 *
 * 일반 억부 공식을 재사용하지 않는다.
 *
 * =================================================
 *
 * 현재 세력이 강하다는 이유로
 * 종격 후보를 자동 강등하지 않는다.
 *
 * 종격에서는 편중 흐름 자체가
 * 성립 근거일 수 있기 때문이다.
 *
 * =================================================
 *
 * 아직 최종 주용신을 결정하지 않는다.
 */
@Component
public class FollowStructureUsefulGodAnalyzer {

    private static final int DOMINANCE_TOLERANCE_BASIS_POINTS =
            500;

    private final FollowStructureUsefulGodRule
            followStructureUsefulGodRule;

    public FollowStructureUsefulGodAnalyzer(
            FollowStructureUsefulGodRule
                    followStructureUsefulGodRule
    ) {

        this.followStructureUsefulGodRule =
                Objects.requireNonNull(
                        followStructureUsefulGodRule,
                        "FollowStructureUsefulGodRule이 필요합니다."
                );
    }

    public FollowStructureUsefulGodAnalysis analyze(
            UsefulGodMethodAnalysis methodAnalysis
    ) {

        Objects.requireNonNull(
                methodAnalysis,
                "7-1 용신법 분석이 필요합니다."
        );

        UsefulGodMethodEvidence methodEvidence =
                methodAnalysis
                        .methodOf(
                                UsefulGodMethodType.FOLLOW_STRUCTURE
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "FOLLOW_STRUCTURE 용신법 Evidence가 없습니다."
                                        )
                        );

        ClimateHealthAnalysis climateHealth =
                methodAnalysis
                        .climateHealthAnalysis();

        StructureAnalysis structure =
                climateHealth
                        .structureAnalysis();

        ClimateHealthCrossValidationAnalysis crossValidation =
                climateHealth
                        .crossValidationAnalysis();

        /*
         * =================================================
         * 4-7 극단구조 결과
         * =================================================
         *
         * 4차에서 이미 판정한 종격 유형과
         * FollowWeakDirection을 그대로 사용한다.
         *
         * 7-7에서 종격을 다시 판정하지 않는다.
         */
        ExtremeStrengthStructureAnalysis extremeStructure =
                structure
                        .dayMasterStrengthAnalysis()
                        .extremeStructureAnalysis();

        ExtremeStrengthStructureType structureType =
                extremeStructure
                        .primaryType();

        FollowWeakDirection followWeakDirection =
                extremeStructure
                        .followWeakDirection();

        boolean followStructureMode =
                methodAnalysis
                        .followStructureMode();

        /*
         * =================================================
         * 5-9 실제 주격과 4-7 종격 상태 일치 확인
         * =================================================
         */
        boolean primaryFollow =
                structure
                        .primaryCandidate()
                        .followStructureCandidate();

        if (
                primaryFollow
                        != followStructureMode
        ) {

            throw new IllegalStateException(
                    "5-9 주격과 7-1 종격 모드가 일치하지 않습니다."
            );
        }

        if (
                followStructureMode
        ) {

            boolean validType =
                    structureType
                            == ExtremeStrengthStructureType
                            .FOLLOW_WEAK_CANDIDATE

                            || structureType
                            == ExtremeStrengthStructureType
                            .FOLLOW_STRONG_CANDIDATE;

            if (
                    !validType
            ) {

                throw new IllegalStateException(
                        "실제 종격 주격인데 4-7 종격 구조 유형이 존재하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * 五行별 역할 / 세력 확보
         * =================================================
         *
         * 6-5 RemedyCandidate에 이미:
         *
         * - FiveElement
         * - DayMasterElementRole
         * - existingShareBasisPoints
         *
         * 가 있으므로 중복 계산하지 않는다.
         */
        Map<FiveElement, RemedyCandidate> remedyCandidates =
                new EnumMap<>(
                        FiveElement.class
                );

        Map<DayMasterElementRole, Integer> roleShares =
                new EnumMap<>(
                        DayMasterElementRole.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            RemedyCandidate remedyCandidate =
                    climateHealth
                            .remedyAnalysis()
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "6-5 오행 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            remedyCandidates.put(
                    element,
                    remedyCandidate
            );

            roleShares.put(
                    remedyCandidate.dayMasterRole(),
                    remedyCandidate.existingShareBasisPoints()
            );
        }

        int maximumRelevantShare =
                determineMaximumRelevantShare(
                        structureType,
                        followWeakDirection,
                        roleShares
                );

        List<FollowStructureUsefulGodCandidate> candidates =
                new ArrayList<>();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            RemedyCandidate remedyCandidate =
                    remedyCandidates.get(
                            element
                    );

            DayMasterElementRole dayMasterRole =
                    remedyCandidate
                            .dayMasterRole();

            int existingShare =
                    remedyCandidate
                            .existingShareBasisPoints();

            ClimateElementCandidate climateCandidate =
                    climateHealth
                            .climateElementCandidateAnalysis()
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "6-3 조후 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            if (
                    climateCandidate
                            .existingShareBasisPoints()

                            != existingShare
            ) {

                throw new IllegalStateException(
                        "6-3과 6-5의 오행 비중이 일치하지 않습니다: "
                                + element
                );
            }

            ClimateElementPresenceLevel presenceLevel =
                    climateCandidate
                            .presenceLevel();

            ClimateHealthValidatedCandidate validatedCandidate =
                    crossValidation
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "6-9 교차검증 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            if (
                    validatedCandidate
                            .dayMasterRole()

                            != dayMasterRole
            ) {

                throw new IllegalStateException(
                        "6-5와 6-9의 일간 기준 오행 역할이 일치하지 않습니다: "
                                + element
                );
            }

            boolean directFollowDirection =
                    followStructureUsefulGodRule
                            .directFollowDirection(
                                    structureType,
                                    followWeakDirection,
                                    dayMasterRole
                            );

            boolean flowSupport =
                    followStructureUsefulGodRule
                            .flowSupport(
                                    structureType,
                                    followWeakDirection,
                                    dayMasterRole
                            );

            boolean breaksFollowFlow =
                    followStructureUsefulGodRule
                            .breaksFollowFlow(
                                    structureType,
                                    followWeakDirection,
                                    dayMasterRole
                            );

            /*
             * direct 방향이 아닌 단순 보조 후보가
             * 우세하다는 이유만으로 PRIMARY가 되면 안 된다.
             */
            boolean dominantFlowElement =
                    directFollowDirection

                            && followStructureUsefulGodRule
                            .dominantFlowElement(
                                    existingShare,
                                    maximumRelevantShare
                            );

            FollowStructureUsefulGodCandidateRole role =
                    followStructureUsefulGodRule
                            .determineRole(
                                    methodEvidence.applicability(),
                                    followStructureMode,
                                    structureType,
                                    directFollowDirection,
                                    flowSupport,
                                    dominantFlowElement,
                                    breaksFollowFlow,
                                    validatedCandidate.strengthState(),
                                    validatedCandidate.structureState()
                            );

            candidates.add(
                    new FollowStructureUsefulGodCandidate(
                            element,
                            dayMasterRole,
                            role,
                            methodEvidence.applicability(),
                            structureType,
                            followWeakDirection,
                            presenceLevel,
                            existingShare,
                            directFollowDirection,
                            flowSupport,
                            dominantFlowElement,
                            breaksFollowFlow,
                            validatedCandidate.strengthState(),
                            validatedCandidate.structureState(),
                            followStructureUsefulGodRule
                                    .reasonsFor(
                                            methodEvidence.applicability(),
                                            followStructureMode,
                                            structureType,
                                            followWeakDirection,
                                            dayMasterRole,
                                            directFollowDirection,
                                            flowSupport,
                                            dominantFlowElement,
                                            breaksFollowFlow,
                                            presenceLevel,
                                            validatedCandidate.strengthState(),
                                            validatedCandidate.structureState(),
                                            role
                                    )
                    )
            );
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 1.
         * PRIMARY
         * SECONDARY
         * CONDITIONAL
         * NOT_NEEDED
         *
         * 2.
         * 종격 직접 방향
         *
         * 3.
         * 현재 종격 우세 흐름
         *
         * 4.
         * 흐름 보조
         *
         * 5.
         * 흐름 파괴 요소는 뒤로
         *
         * 6.
         * 현재 실제 세력은 큰 쪽 우선
         *
         * 종격에서는 일반 억부처럼
         * "부족한 오행 우선" 정렬을 하지 않는다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        FollowStructureUsefulGodCandidate candidate
                                ) ->
                                        candidate
                                                .role()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                FollowStructureUsefulGodCandidate
                                                        ::directFollowDirection
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                FollowStructureUsefulGodCandidate
                                                        ::dominantFlowElement
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                FollowStructureUsefulGodCandidate
                                                        ::flowSupport
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                FollowStructureUsefulGodCandidate
                                        ::breaksFollowFlow
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                FollowStructureUsefulGodCandidate
                                                        ::existingShareBasisPoints
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        return new FollowStructureUsefulGodAnalysis(
                methodAnalysis.dayMaster(),
                methodAnalysis.dayMasterElement(),
                methodAnalysis.monthBranch(),
                methodAnalysis,
                methodEvidence,
                structureType,
                followWeakDirection,
                candidates,
                followStructureMode,
                methodAnalysis.completeChart()
        );
    }

    /**
     * 현재 종격의 직접 방향에 해당하는 역할 중
     * 가장 강한 실제 세력을 구한다.
     *
     * 종약 MIXED:
     *
     * OUTPUT / WEALTH / OFFICER
     *
     * 종강:
     *
     * COMPANION / RESOURCE
     *
     * 를 비교한다.
     */
    private int determineMaximumRelevantShare(

            ExtremeStrengthStructureType structureType,

            FollowWeakDirection followWeakDirection,

            Map<DayMasterElementRole, Integer> roleShares

    ) {

        if (
                structureType
                        == ExtremeStrengthStructureType
                        .FOLLOW_STRONG_CANDIDATE
        ) {

            return Math.max(
                    shareOf(
                            roleShares,
                            DayMasterElementRole.COMPANION
                    ),
                    shareOf(
                            roleShares,
                            DayMasterElementRole.RESOURCE
                    )
            );
        }

        if (
                structureType
                        != ExtremeStrengthStructureType
                        .FOLLOW_WEAK_CANDIDATE
        ) {

            return 0;
        }

        return switch (
                followWeakDirection
                ) {

            case OUTPUT ->
                    shareOf(
                            roleShares,
                            DayMasterElementRole.OUTPUT
                    );

            case WEALTH ->
                    shareOf(
                            roleShares,
                            DayMasterElementRole.WEALTH
                    );

            case OFFICER ->
                    shareOf(
                            roleShares,
                            DayMasterElementRole.OFFICER
                    );

            case MIXED ->
                    Math.max(
                            shareOf(
                                    roleShares,
                                    DayMasterElementRole.OUTPUT
                            ),
                            Math.max(
                                    shareOf(
                                            roleShares,
                                            DayMasterElementRole.WEALTH
                                    ),
                                    shareOf(
                                            roleShares,
                                            DayMasterElementRole.OFFICER
                                    )
                            )
                    );

            case NONE ->
                    0;
        };
    }

    private int shareOf(

            Map<DayMasterElementRole, Integer> roleShares,

            DayMasterElementRole role

    ) {

        return roleShares
                .getOrDefault(
                        role,
                        0
                );
    }
}