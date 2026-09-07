package com.dajeom.solar;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class SolarTermRepository {

    private final SolarTermCalculator calculator;

    private final ConcurrentMap<
            Integer,
            Map<SolarTerm, SolarTermInstant>
            > yearlyCache =
            new ConcurrentHashMap<>();

    public SolarTermRepository(
            SolarTermCalculator calculator
    ) {

        this.calculator =
                Objects.requireNonNull(
                        calculator,
                        "SolarTermCalculator가 필요합니다."
                );
    }

    /**
     * 특정 연도의 특정 절기.
     */
    public SolarTermInstant get(
            int gregorianYear,
            SolarTerm term
    ) {

        Objects.requireNonNull(
                term,
                "조회할 절기가 필요합니다."
        );

        return getYearMap(
                gregorianYear
        ).get(
                term
        );
    }

    /**
     * 특정 연도 입춘.
     *
     * 년주 계산에서 바로 사용한다.
     */
    public SolarTermInstant getLichun(
            int gregorianYear
    ) {

        return get(
                gregorianYear,
                SolarTerm.LICHUN
        );
    }

    /**
     * 해당 연도의 24절기 전체.
     */
    public List<SolarTermInstant> findAll(
            int gregorianYear
    ) {

        List<SolarTermInstant> result =
                new ArrayList<>(
                        getYearMap(
                                gregorianYear
                        ).values()
                );

        result.sort(
                Comparator.comparing(
                        SolarTermInstant::instant
                )
        );

        return List.copyOf(
                result
        );
    }

    /**
     * 월주 경계에 실제 사용하는 12절입만 반환.
     */
    public List<SolarTermInstant> findMonthBoundaries(
            int gregorianYear
    ) {

        List<SolarTermInstant> result =
                new ArrayList<>();

        for (
                SolarTermInstant value
                : getYearMap(gregorianYear).values()
        ) {

            if (value.term().isMonthBoundary()) {
                result.add(
                        value
                );
            }
        }

        result.sort(
                Comparator.comparing(
                        SolarTermInstant::instant
                )
        );

        return List.copyOf(
                result
        );
    }

    /**
     * 특정 순간 직전 또는 정확히 그 순간에 시작한
     * 가장 최근의 사주 월 경계 절기를 찾는다.
     *
     * 예:
     *
     * 1993-08-24
     * → 가장 최근 절입 = 입추
     *
     * 1993-09-08 백로 이후
     * → 가장 최근 절입 = 백로
     */
    public SolarTermInstant findLatestMonthBoundaryAtOrBefore(
            Instant target
    ) {

        Objects.requireNonNull(
                target,
                "기준 시간이 필요합니다."
        );

        int koreanYear =
                target
                        .atZone(
                                SolarTermInstant.KOREA_ZONE
                        )
                        .getYear();

        List<SolarTermInstant> candidates =
                collectBoundaryCandidates(
                        koreanYear
                );

        return candidates
                .stream()
                .filter(value ->
                        !value
                                .instant()
                                .isAfter(
                                        target
                                )
                )
                .max(
                        Comparator.comparing(
                                SolarTermInstant::instant
                        )
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "이전 절입을 찾을 수 없습니다. target="
                                        + target
                        )
                );
    }

    /**
     * 특정 순간 이후에 시작되는
     * 다음 사주 월 경계 절기를 찾는다.
     *
     * 대운 시작거리 계산에서도 나중에 재사용할 수 있다.
     */
    public SolarTermInstant findNextMonthBoundaryAfter(
            Instant target
    ) {

        Objects.requireNonNull(
                target,
                "기준 시간이 필요합니다."
        );

        int koreanYear =
                target
                        .atZone(
                                SolarTermInstant.KOREA_ZONE
                        )
                        .getYear();

        List<SolarTermInstant> candidates =
                collectBoundaryCandidates(
                        koreanYear
                );

        return candidates
                .stream()
                .filter(value ->
                        value
                                .instant()
                                .isAfter(
                                        target
                                )
                )
                .min(
                        Comparator.comparing(
                                SolarTermInstant::instant
                        )
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "다음 절입을 찾을 수 없습니다. target="
                                        + target
                        )
                );
    }

    public void clearCache() {
        yearlyCache.clear();
    }

    public void clearCache(
            int gregorianYear
    ) {

        yearlyCache.remove(
                gregorianYear
        );
    }

    private Map<SolarTerm, SolarTermInstant> getYearMap(
            int gregorianYear
    ) {

        return yearlyCache.computeIfAbsent(
                gregorianYear,
                this::calculateYear
        );
    }

    private Map<SolarTerm, SolarTermInstant> calculateYear(
            int gregorianYear
    ) {

        EnumMap<SolarTerm, SolarTermInstant> values =
                new EnumMap<>(
                        SolarTerm.class
                );

        for (SolarTerm term : SolarTerm.values()) {

            SolarTermInstant instant =
                    calculator.calculate(
                            gregorianYear,
                            term
                    );

            values.put(
                    term,
                    instant
            );
        }

        return Map.copyOf(
                values
        );
    }

    /**
     * 연말/연초 경계를 안전하게 찾기 위해
     * 기준연도 앞뒤 1년까지 같이 조회한다.
     */
    private List<SolarTermInstant> collectBoundaryCandidates(
            int centerYear
    ) {

        List<SolarTermInstant> result =
                new ArrayList<>(
                        36
                );

        result.addAll(
                findMonthBoundaries(
                        centerYear - 1
                )
        );

        result.addAll(
                findMonthBoundaries(
                        centerYear
                )
        );

        result.addAll(
                findMonthBoundaries(
                        centerYear + 1
                )
        );

        result.sort(
                Comparator.comparing(
                        SolarTermInstant::instant
                )
        );

        return result;
    }
}