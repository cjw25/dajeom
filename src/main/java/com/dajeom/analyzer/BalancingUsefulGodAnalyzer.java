package com.dajeom.analyzer;

import com.dajeom.model.BalancingUsefulGodAnalysis;
import com.dajeom.model.BalancingUsefulGodCandidate;
import com.dajeom.model.ClimateHealthAnalysis;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.PathologyAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.UsefulGodMethodAnalysis;
import com.dajeom.model.UsefulGodMethodEvidence;
import com.dajeom.rule.BalancingUsefulGodRule;
import com.dajeom.type.BalancingUsefulGodCandidateRole;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.UsefulGodMethodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 7차-2.
 *
 * 7-1에서 분리된 BALANCING 용신법을 이용해
 * 실제 木火土金水 억부용신 후보를 계산한다.
 *
 * =================================================
 *
 * 처리
 *
 * 신강·신약
 *
 * ↓
 *
 * 五行의 일간 기준 역할
 *
 * ↓
 *
 * 실제 五行 세력
 *
 * ↓
 *
 * 6-4 병 구조
 *
 * ↓
 *
 * 우선 억부 역할 선택
 *
 * ↓
 *
 * PRIMARY
 * SECONDARY
 * CONDITIONAL
 * NOT_NEEDED
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 신약 = 인비 모두 PRIMARY
 *    로 만들지 않는다.
 *
 * 2.
 * 신강 = 식재관 모두 PRIMARY
 *    로 만들지 않는다.
 *
 * 3.
 * 실제 병목이 있으면
 *    병목을 직접 완화하는 억부 역할을 우선한다.
 *
 * 4.
 * 병목이 명확하지 않으면
 *    현재 실제 오행 세력을 이용한다.
 *
 * 5.
 * 종격 주격에서는
 *    일반 억부 후보를 조건부로 제한한다.
 *
 * 6.
 * 최종 용신 결정은 하지 않는다.
 */
@Component
public class BalancingUsefulGodAnalyzer {

    private final BalancingUsefulGodRule
            balancingUsefulGodRule;

    public BalancingUsefulGodAnalyzer(
            BalancingUsefulGodRule
                    balancingUsefulGodRule
    ) {

        this.balancingUsefulGodRule =
                Objects.requireNonNull(
                        balancingUsefulGodRule,
                        "BalancingUsefulGodRule이 필요합니다."
                );
    }

    public BalancingUsefulGodAnalysis analyze(
            UsefulGodMethodAnalysis methodAnalysis
    ) {

        Objects.requireNonNull(
                methodAnalysis,
                "7-1 용신법 분석이 필요합니다."
        );

        UsefulGodMethodEvidence methodEvidence =
                methodAnalysis
                        .methodOf(
                                UsefulGodMethodType.BALANCING
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "BALANCING 용신법 Evidence가 없습니다."
                                        )
                        );

        ClimateHealthAnalysis climateHealth =
                methodAnalysis
                        .climateHealthAnalysis();

        StructureAnalysis structure =
                climateHealth
                        .structureAnalysis();

        ElementStrengthAnalysis elementStrength =
                structure
                        .dayMasterStrengthAnalysis()
                        .elementStrengthAnalysis();

        PathologyAnalysis pathology =
                climateHealth
                        .pathologyAnalysis();

        DayMasterStrengthLevel strengthLevel =
                structure
                        .dayMasterStrengthAnalysis()
                        .level();

        FiveElement dayMasterElement =
                structure
                        .dayMasterStrengthAnalysis()
                        .dayMasterElement();

        boolean followStructureSuppressed =
                methodEvidence
                        .suppressedByFollowStructure();

        /*
         * ================================================
         * 五行별 실제 비중
         * ================================================
         */
        Map<FiveElement, Integer> shares =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            shares.put(
                    element,
                    elementStrength
                            .elementPowerAnalysis()
                            .shareBasisPointsOf(
                                    element
                            )
            );
        }

        /*
         * ================================================
         * 五行별 일간 기준 역할
         * ================================================
         */
        Map<FiveElement, DayMasterElementRole> roles =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            roles.put(
                    element,
                    balancingUsefulGodRule
                            .determineDayMasterRole(
                                    dayMasterElement,
                                    element
                            )
            );
        }

        int companionShare =
                shareOfRole(
                        shares,
                        roles,
                        DayMasterElementRole.COMPANION
                );

        int resourceShare =
                shareOfRole(
                        shares,
                        roles,
                        DayMasterElementRole.RESOURCE
                );

        int outputShare =
                shareOfRole(
                        shares,
                        roles,
                        DayMasterElementRole.OUTPUT
                );

        int wealthShare =
                shareOfRole(
                        shares,
                        roles,
                        DayMasterElementRole.WEALTH
                );

        int officerShare =
                shareOfRole(
                        shares,
                        roles,
                        DayMasterElementRole.OFFICER
                );

        /*
         * ================================================
         * 현재 억부 우선 역할
         * ================================================
         */
        Set<DayMasterElementRole> preferredRoles =
                balancingUsefulGodRule
                        .determinePreferredRoles(
                                strengthLevel,
                                pathology,
                                companionShare,
                                resourceShare,
                                outputShare,
                                wealthShare,
                                officerShare
                        );

        List<BalancingUsefulGodCandidate> candidates =
                new ArrayList<>();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            DayMasterElementRole role =
                    roles.get(
                            element
                    );

            int share =
                    shares.get(
                            element
                    );

            ClimateElementPresenceLevel presence =
                    ClimateElementPresenceLevel
                            .fromShareBasisPoints(
                                    share
                            );

            boolean strengthDirectionMatch =
                    balancingUsefulGodRule
                            .strengthDirectionMatch(
                                    strengthLevel,
                                    role
                            );

            boolean directPathologyMatch =
                    balancingUsefulGodRule
                            .directPathologyMatch(
                                    role,
                                    pathology
                            );

            boolean preferredRole =
                    preferredRoles.contains(
                            role
                    );

            BalancingUsefulGodCandidateRole candidateRole =
                    balancingUsefulGodRule
                            .determineCandidateRole(
                                    strengthLevel,
                                    methodEvidence.applicability(),
                                    role,
                                    strengthDirectionMatch,
                                    directPathologyMatch,
                                    preferredRole,
                                    presence,
                                    followStructureSuppressed
                            );

            candidates.add(
                    new BalancingUsefulGodCandidate(
                            element,
                            role,
                            candidateRole,
                            methodEvidence.applicability(),
                            presence,
                            share,
                            strengthDirectionMatch,
                            directPathologyMatch,
                            preferredRole,
                            followStructureSuppressed,
                            balancingUsefulGodRule
                                    .reasonsFor(
                                            strengthLevel,
                                            role,
                                            presence,
                                            strengthDirectionMatch,
                                            directPathologyMatch,
                                            preferredRole,
                                            followStructureSuppressed,
                                            pathology
                                    )
                    )
            );
        }

        /*
         * ================================================
         * 결과 정렬
         * ================================================
         *
         * 1.
         * PRIMARY
         * SECONDARY
         * CONDITIONAL
         * NOT_NEEDED
         *
         * 2.
         * 실제 병 구조 직접 대응
         *
         * 3.
         * 우선 억부 역할
         *
         * 4.
         * 현재 세력이 적은 오행
         *
         * 순.
         *
         * 이것은 용신 종합점수가 아니다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        BalancingUsefulGodCandidate candidate
                                ) ->
                                        candidate
                                                .role()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                BalancingUsefulGodCandidate
                                                        ::directPathologyMatch
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparing(
                                                BalancingUsefulGodCandidate
                                                        ::preferredRole
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                BalancingUsefulGodCandidate
                                        ::existingShareBasisPoints
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        return new BalancingUsefulGodAnalysis(
                methodAnalysis.dayMaster(),
                dayMasterElement,
                methodAnalysis.monthBranch(),
                methodAnalysis,
                methodEvidence,
                candidates,
                followStructureSuppressed,
                methodAnalysis.completeChart()
        );
    }

    /**
     * 五行과 DayMasterElementRole은
     * 일대일 관계이므로
     * 해당 역할의 오행 비중을 가져온다.
     */
    private int shareOfRole(

            Map<FiveElement, Integer> shares,

            Map<FiveElement, DayMasterElementRole> roles,

            DayMasterElementRole targetRole

    ) {

        return roles
                .entrySet()
                .stream()
                .filter(
                        entry ->
                                entry.getValue()
                                        == targetRole
                )
                .map(
                        Map.Entry::getKey
                )
                .mapToInt(
                        element ->
                                shares.getOrDefault(
                                        element,
                                        0
                                )
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "일간 기준 오행 역할을 찾을 수 없습니다: "
                                                + targetRole
                                )
                );
    }
}