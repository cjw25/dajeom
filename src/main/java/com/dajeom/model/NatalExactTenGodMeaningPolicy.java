package com.dajeom.model;

import com.dajeom.type.StructureTenGodType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * 정확한 10십성 하나가
 * 사용자 설명에서 허용하는 Meaning 경계.
 *
 * 이 객체는 사주를 새로 계산하지 않는다.
 *
 * StructureTenGodType 하나에 대해:
 *
 * - 핵심 의미
 * - 같은 십성 안에서 허용되는 세부 의미
 * - 자동 확장하면 안 되는 현실 사건
 *
 * 만 고정한다.
 */
public record NatalExactTenGodMeaningPolicy(

        StructureTenGodType tenGodType,

        String coreMeaning,

        List<String> allowedDetails,

        List<String> forbiddenExtensions

) {

    public NatalExactTenGodMeaningPolicy {

        Objects.requireNonNull(
                tenGodType,
                "Exact TenGod Meaning Policy에는 정확한 십성 종류가 필요합니다."
        );

        coreMeaning =
                requireText(
                        coreMeaning,
                        "Exact TenGod Meaning Policy의 coreMeaning은 필수입니다."
                );

        allowedDetails =
                normalizeTexts(
                        allowedDetails
                );

        forbiddenExtensions =
                normalizeTexts(
                        forbiddenExtensions
                );
    }

    /**
     * 하나의 정확한 십성 Packet에서
     * 사용할 수 있는 전체 Meaning Claim.
     *
     * 모두 같은 십성 안의 의미이며
     * 다른 십성과 조합하지 않는다.
     */
    public List<String> claimMeanings() {

        LinkedHashSet<String> result =
                new LinkedHashSet<>();

        result.add(
                coreMeaning
        );

        result.addAll(
                allowedDetails
        );

        return List.copyOf(
                result
        );
    }

    private static List<String> normalizeTexts(
            List<String> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        LinkedHashSet<String> result =
                new LinkedHashSet<>();

        for (
                String raw
                : source
        ) {

            if (
                    raw == null
            ) {

                continue;
            }

            String value =
                    raw.trim();

            if (
                    !value.isBlank()
            ) {

                result.add(
                        value
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    private static String requireText(
            String value,
            String message
    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}