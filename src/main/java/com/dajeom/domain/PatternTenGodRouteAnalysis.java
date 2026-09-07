package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 원국의 격국용 십성 route 분석.
 */
public record PatternTenGodRouteAnalysis(

        HeavenlyStem dayMaster,

        List<PatternTenGodRouteOccurrence> routes

) {

    public PatternTenGodRouteAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        routes =
                routes == null
                        ? List.of()
                        : List.copyOf(routes);

        for (
                PatternTenGodRouteOccurrence route
                : routes
        ) {

            Objects.requireNonNull(
                    route,
                    "route 목록에 null이 포함될 수 없습니다."
            );
        }
    }

    public boolean hasRoute(
            PatternTenGodRouteType type
    ) {

        return routes
                .stream()
                .anyMatch(
                        route ->
                                route.type() == type
                );
    }

    public List<PatternTenGodRouteOccurrence> routesOf(
            PatternTenGodRouteType type
    ) {

        return routes
                .stream()
                .filter(
                        route ->
                                route.type() == type
                )
                .toList();
    }
}