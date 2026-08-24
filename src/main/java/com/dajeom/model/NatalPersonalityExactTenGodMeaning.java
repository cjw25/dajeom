package com.dajeom.model;

import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;

/**
 * 정확한 10십성 Fact 하나와
 * 사용자용 Meaning Policy를 결합한
 * 최종 표현 경계.
 *
 * =================================================
 *
 * sourceFact
 *
 * 실제 원국 계산에서 선택된
 * 정확한 10십성 Fact.
 *
 * =================================================
 *
 * policy
 *
 * 해당 십성 자체가 허용하는
 * 사용자용 의미 범위.
 *
 * =================================================
 *
 * 서로 다른 ExactTenGodMeaning끼리
 * 자동 합성하지 않는다.
 */
public record NatalPersonalityExactTenGodMeaning(

        NatalPersonalityExactTenGodFact sourceFact,

        NatalExactTenGodMeaningPolicy policy,

        boolean strongExpressionAllowed,

        boolean qualificationRequired

) {

    public NatalPersonalityExactTenGodMeaning {

        Objects.requireNonNull(
                sourceFact,
                "Exact TenGod Meaning에는 Source Fact가 필요합니다."
        );

        Objects.requireNonNull(
                policy,
                "Exact TenGod Meaning에는 Meaning Policy가 필요합니다."
        );

        if (
                sourceFact.tenGodType()
                        != policy.tenGodType()
        ) {

            throw new IllegalArgumentException(
                    "Exact TenGod Fact와 Meaning Policy의 십성 종류가 일치하지 않습니다."
            );
        }

        /*
         * PRIMARY가 아닌 십성에
         * 강한 단정 표현을 허용하지 않는다.
         */
        if (
                strongExpressionAllowed
                        && !sourceFact.primary()
        ) {

            throw new IllegalArgumentException(
                    "강한 표현은 PRIMARY Exact TenGod Fact에만 허용할 수 있습니다."
            );
        }
    }

    public StructureTenGodType tenGodType() {

        return sourceFact
                .tenGodType();
    }

    public boolean primary() {

        return sourceFact
                .primary();
    }

    public boolean supporting() {

        return sourceFact
                .supporting();
    }

    public boolean directOutput() {

        return sourceFact
                .directOutput();
    }

    public List<String> claimMeanings() {

        return policy
                .claimMeanings();
    }

    public List<String> forbiddenExtensions() {

        return policy
                .forbiddenExtensions();
    }
}