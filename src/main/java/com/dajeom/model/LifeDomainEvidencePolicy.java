package com.dajeom.model;

import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainEvidenceTier;

import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차-1.
 *
 * 특정 기존 분석 Source를
 * 어느 생활영역에서 사용할 수 있는지 정의한다.
 *
 * =================================================
 *
 * standaloneOutcomeAllowed=false
 *
 * 하나의 Evidence Source만으로:
 *
 * - 돈을 번다
 * - 취업한다
 * - 결혼한다
 * - 합격한다
 * - 병이 생긴다
 *
 * 등의 사건 결과를 단정할 수 없다는 의미.
 */
public record LifeDomainEvidencePolicy(

        LifeDomainEvidenceSourceType sourceType,

        LifeDomainEvidenceTier tier,

        Set<LifeDomain> supportedDomains,

        boolean standaloneOutcomeAllowed

) {

    public LifeDomainEvidencePolicy {

        Objects.requireNonNull(
                sourceType,
                "생활영역 Evidence Policy에는 SourceType이 필요합니다."
        );

        Objects.requireNonNull(
                tier,
                "생활영역 Evidence Policy에는 Tier가 필요합니다."
        );

        if (
                tier != sourceType.tier()
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainEvidenceSourceType의 Tier와 Policy Tier가 일치하지 않습니다: "
                            + sourceType
            );
        }

        supportedDomains =
                supportedDomains == null
                        ? Set.of()
                        : Set.copyOf(
                        supportedDomains
                );

        if (
                supportedDomains.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "생활영역 Evidence Source는 최소 하나의 Domain을 지원해야 합니다."
            );
        }
    }

    public boolean supports(
            LifeDomain domain
    ) {

        return domain != null
                && supportedDomains.contains(
                domain
        );
    }

    public boolean auxiliaryOnly() {
        return tier.auxiliary();
    }
}