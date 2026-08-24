package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국의 정확한 10십성을 그대로 보존하는 Aggregate.
 *
 * =================================================
 *
 * 사용자 Meaning의 십성 기준은
 * 이 Aggregate처럼 정확한 StructureTenGodType
 * 단위에서 출발한다.
 *
 * =================================================
 *
 * 기존 5개 Family 기능축은
 * 내부 계산 호환용으로 남길 수 있다.
 *
 * 하지만:
 *
 * 정재 + 편재
 * -> 현실·자원이라는 하나의 성격
 *
 * 정관 + 편관
 * -> 책임·압력이라는 하나의 성격
 *
 * 식신 + 상관
 * -> 표현이라는 하나의 성격
 *
 * 같은 사용자 Meaning 자동 합성의
 * 공식 기준으로 사용하지 않는다.
 *
 * =================================================
 *
 * 이 Aggregate는 새로운 의미를 만들지 않는다.
 *
 * 다음 기존 계산값만 구조화한다.
 *
 * - 10-1 정확한 십성 세력
 * - 5차 주격 정확한 십성
 */
public record NatalExactTenGodAnalysis(

        HeavenlyStem dayMaster,

        TenGodDistributionAnalysis distributionAnalysis,

        Optional<StructureTenGodType> primaryStructureTenGod,

        List<NatalExactTenGodProfile> profiles,

        boolean completeChart

) {

    public NatalExactTenGodAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "Exact TenGod Analysis에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                distributionAnalysis,
                "Exact TenGod Analysis에는 TenGodDistributionAnalysis가 필요합니다."
        );

        primaryStructureTenGod =
                primaryStructureTenGod == null
                        ? Optional.empty()
                        : primaryStructureTenGod;

        profiles =
                profiles == null
                        ? List.of()
                        : List.copyOf(
                        profiles
                );

        if (
                distributionAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "Exact TenGod Analysis와 10-1 Distribution의 일간이 일치하지 않습니다."
            );
        }

        if (
                distributionAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Exact TenGod Analysis와 10-1 Distribution의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 정확한 10십성 전부 존재해야 함
         * =================================================
         */

        if (
                profiles.size()
                        != StructureTenGodType.values().length
        ) {

            throw new IllegalArgumentException(
                    "Exact TenGod Analysis에는 정확한 10십성 Profile이 모두 필요합니다."
            );
        }

        Map<
                StructureTenGodType,
                NatalExactTenGodProfile
                > indexed =
                new EnumMap<>(
                        StructureTenGodType.class
                );

        for (
                NatalExactTenGodProfile profile
                : profiles
        ) {

            Objects.requireNonNull(
                    profile,
                    "Exact TenGod Profile은 null일 수 없습니다."
            );

            if (
                    profile.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Exact TenGod Profile의 원국 완전성 상태가 일치하지 않습니다: "
                                + profile.tenGodType()
                );
            }

            if (
                    indexed.put(
                            profile.tenGodType(),
                            profile
                    ) != null
            ) {

                throw new IllegalArgumentException(
                        "Exact TenGod Profile이 중복되었습니다: "
                                + profile.tenGodType()
                );
            }

            /*
             * =================================================
             * 반드시 현재 10-1의 공식 Summary Identity 사용
             * =================================================
             */

            TenGodPowerSummary canonical =
                    distributionAnalysis
                            .summaryOf(
                                    profile.tenGodType()
                            )
                            .orElseThrow();

            if (
                    profile.sourceSummary()
                            != canonical
            ) {

                throw new IllegalArgumentException(
                        "Exact TenGod Profile이 현재 Distribution의 공식 Summary를 참조하지 않습니다: "
                                + profile.tenGodType()
                );
            }
        }

        for (
                StructureTenGodType tenGodType
                : StructureTenGodType.values()
        ) {

            if (
                    !indexed.containsKey(
                            tenGodType
                    )
            ) {

                throw new IllegalArgumentException(
                        "Exact TenGod Profile이 누락되었습니다: "
                                + tenGodType
                );
            }
        }

        /*
         * =================================================
         * 주격 정확성 검증
         * =================================================
         */

        long primaryCount =
                profiles
                        .stream()
                        .filter(
                                NatalExactTenGodProfile
                                        ::primaryStructure
                        )
                        .count();

        if (
                primaryStructureTenGod.isPresent()
        ) {

            if (
                    primaryCount != 1
            ) {

                throw new IllegalArgumentException(
                        "일반격 주격 십성이 존재하면 정확히 하나의 Exact TenGod Profile이 주격 중심이어야 합니다."
                );
            }

            StructureTenGodType primary =
                    primaryStructureTenGod
                            .orElseThrow();

            if (
                    !indexed
                            .get(
                                    primary
                            )
                            .primaryStructure()
            ) {

                throw new IllegalArgumentException(
                        "주격 십성과 Exact TenGod Profile의 중심성이 일치하지 않습니다."
                );
            }

        } else if (
                primaryCount != 0
        ) {

            throw new IllegalArgumentException(
                    "일반격 주격 십성이 없는데 Exact TenGod Profile에 주격 중심이 지정되었습니다."
            );
        }
    }

    public Optional<NatalExactTenGodProfile>
    profileOf(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return Optional.empty();
        }

        return profiles
                .stream()
                .filter(
                        profile ->
                                profile.tenGodType()
                                        == tenGodType
                )
                .findFirst();
    }

    public NatalExactTenGodProfile
    requireProfile(
            StructureTenGodType tenGodType
    ) {

        Objects.requireNonNull(
                tenGodType,
                "정확한 십성 종류는 필수입니다."
        );

        return profileOf(
                tenGodType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Exact TenGod Profile을 찾을 수 없습니다: "
                                                + tenGodType
                                )
                );
    }

    /**
     * 원국에 실제 존재하는 십성.
     */
    public List<NatalExactTenGodProfile>
    presentProfiles() {

        return sorted(
                profiles
                        .stream()
                        .filter(
                                NatalExactTenGodProfile
                                        ::present
                        )
                        .toList()
        );
    }

    /**
     * 기존 10-1 PowerLevel에서
     * meaningful로 이미 판정된 정확한 십성.
     *
     * 새로운 threshold를 만들지 않는다.
     */
    public List<NatalExactTenGodProfile>
    meaningfulProfiles() {

        return sorted(
                profiles
                        .stream()
                        .filter(
                                NatalExactTenGodProfile
                                        ::meaningful
                        )
                        .toList()
        );
    }

    /**
     * 기존 10-1에서
     * DOMINANT / STRONG으로 판정된 정확한 십성.
     */
    public List<NatalExactTenGodProfile>
    strongProfiles() {

        return sorted(
                profiles
                        .stream()
                        .filter(
                                NatalExactTenGodProfile
                                        ::strongSide
                        )
                        .toList()
        );
    }

    /**
     * 정렬 기준:
     *
     * 1. 실제 주격 중심
     * 2. 기존 10-1 PowerLevel
     * 3. 기존 allocatedPower
     * 4. enum 고정순서
     *
     * 새로운 성격 점수를 만들지 않는다.
     */
    private List<NatalExactTenGodProfile>
    sorted(
            List<NatalExactTenGodProfile> source
    ) {

        return source
                .stream()
                .sorted(
                        Comparator
                                .comparing(
                                        NatalExactTenGodProfile
                                                ::primaryStructure
                                )
                                .reversed()

                                .thenComparing(
                                        profile ->
                                                profile
                                                        .powerLevel()
                                                        .rank(),
                                        Comparator.reverseOrder()
                                )

                                .thenComparing(
                                        NatalExactTenGodProfile
                                                ::allocatedPower,
                                        Comparator.reverseOrder()
                                )

                                .thenComparing(
                                        profile ->
                                                profile
                                                        .tenGodType()
                                                        .ordinal()
                                )
                )
                .toList();
    }
}