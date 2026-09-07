package com.dajeom.domain;

import java.util.Objects;
import java.util.Set;

/**
 * AI 해석에 전달하는 하나의 의미 Claim.
 *
 * =================================================
 *
 * code
 *
 * Java 명리 규칙 엔진이 확정한
 * 삶의 의미 단위.
 *
 * =================================================
 *
 * bases
 *
 * 왜 이 Claim이 생성됐는지를 나타내는
 * 내부 구조적 근거.
 *
 * =================================================
 *
 * periodKey
 *
 * 기존 MyeongriRuleEngine과의 호환을 위한
 * 기간 구분값.
 *
 * 현재 이 LifeClaim은 원국 분석용이므로
 * 기본값은 NATAL이다.
 *
 * 향후 대운 / 세운 Claim을 별도로 도입할 때는
 * 기간 Claim 모델을 분리하거나 확장한다.
 *
 * =================================================
 *
 * 중요:
 *
 * claimCode(), periodKey()는
 * 기존 엔진 호환 API다.
 *
 * 신규 코드에서는 가능하면
 *
 * code()
 * bases()
 *
 * 를 기준으로 사용한다.
 */
public record LifeClaim(

        LifeClaimCode code,

        Set<LifeClaimBasisCode> bases

) {

    private static final String NATAL_PERIOD_KEY =
            "NATAL";

    public LifeClaim {

        Objects.requireNonNull(
                code,
                "LifeClaimCode는 필수입니다."
        );

        bases =
                bases == null
                        ? Set.of()
                        : Set.copyOf(bases);

        if (bases.isEmpty()) {

            throw new IllegalArgumentException(
                    "LifeClaim에는 최소 하나의 구조적 근거가 필요합니다."
            );
        }

        for (
                LifeClaimBasisCode basis
                : bases
        ) {

            Objects.requireNonNull(
                    basis,
                    "LifeClaim bases에 null이 포함될 수 없습니다."
            );
        }
    }

    /**
     * 특정 구조적 근거가 포함되어 있는가.
     */
    public boolean hasBasis(
            LifeClaimBasisCode basis
    ) {

        Objects.requireNonNull(
                basis,
                "LifeClaimBasisCode는 필수입니다."
        );

        return bases.contains(
                basis
        );
    }

    /*
     * =================================================
     * 기존 MyeongriRuleEngine 호환 API
     * =================================================
     */

    /**
     * 기존 엔진에서 사용하던 claimCode().
     *
     * 신규 LifeClaimCode enum의 이름을
     * 기존 key 문자열로 제공한다.
     *
     * 예:
     *
     * RECOVERY_BEFORE_OVEREXTENSION
     */
    public String claimCode() {

        return code.name();
    }

    /**
     * 기존 엔진에서 사용하던 periodKey().
     *
     * 현재 LifeClaim 계층은 원국 Claim이므로
     * NATAL을 반환한다.
     *
     * 대운/세운은 이후 별도의 기간 Claim 계층에서
     * 실제 periodKey를 갖도록 확장한다.
     */
    public String periodKey() {

        return NATAL_PERIOD_KEY;
    }
}