package com.dajeom.dto;

import com.dajeom.type.FiveElement;

import java.util.Map;

/**
 * 목·화·토·금·수의 개수를 반환하는 Response다.
 */
public record ElementCountResponse(
        int wood,
        int fire,
        int earth,
        int metal,
        int water,
        int total
) {

    public static ElementCountResponse from(
            Map<FiveElement, Integer> counts
    ) {
        int wood =
                counts.getOrDefault(
                        FiveElement.WOOD,
                        0
                );

        int fire =
                counts.getOrDefault(
                        FiveElement.FIRE,
                        0
                );

        int earth =
                counts.getOrDefault(
                        FiveElement.EARTH,
                        0
                );

        int metal =
                counts.getOrDefault(
                        FiveElement.METAL,
                        0
                );

        int water =
                counts.getOrDefault(
                        FiveElement.WATER,
                        0
                );

        int total =
                wood
                        + fire
                        + earth
                        + metal
                        + water;

        return new ElementCountResponse(
                wood,
                fire,
                earth,
                metal,
                water,
                total
        );
    }
}