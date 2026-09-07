package com.dajeom.calculation;

import com.dajeom.domain.LifeClaim;
import com.dajeom.domain.LifeClaimBasisCode;
import com.dajeom.domain.LifeClaimCode;
import com.dajeom.domain.PatternOfficerKillingsFinalPurificationAnalysis;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 官殺 관련 LifeClaim을
 * 최종 권위 판정에 맞춰 마지막으로 정리한다.
 *
 * =================================================
 *
 * 왜 필요한가
 * =================================================
 *
 * 기존 여러 Calculator가 이미:
 *
 * - 官殺混雜
 * - 官殺取清
 * - review
 *
 * 관련 Claim을 만들고 있다.
 *
 * 하지만 과거 generic stem binding이나
 * node-level이 아닌 羊刃合殺 판정이
 * 개입했을 가능성을 완전히 차단하려면,
 *
 * Pipeline 마지막에
 * PatternOfficerKillingsFinalPurificationAnalysis
 * 를 기준으로 官殺 관련 basis를 다시 정리해야 한다.
 *
 * =================================================
 *
 * 최종 권위
 * =================================================
 *
 * PatternOfficerKillingsFinalPurificationAnalysis
 *
 * 만 사용한다.
 *
 * =================================================
 *
 * PARTIALLY_CLEARED는
 * mixtureCleared로 승격하지 않는다.
 */
@Component
public class OfficerKillingsFinalLifeClaimCorrectionCalculator {

    public List<LifeClaim> correct(

            List<LifeClaim> existingClaims,

            PatternOfficerKillingsFinalPurificationAnalysis
                    finalPurification

    ) {

        Objects.requireNonNull(
                existingClaims,
                "기존 LifeClaim 목록은 필수입니다."
        );

        Objects.requireNonNull(
                finalPurification,
                "官殺 최종 purification 분석은 필수입니다."
        );

        Map<
                LifeClaimCode,
                EnumSet<LifeClaimBasisCode>
                > claims =
                new EnumMap<>(
                        LifeClaimCode.class
                );

        /*
         * =================================================
         * 1. 기존 Claim 복사
         * =================================================
         *
         * 이때 官殺 최종 판정용 basis는 제거한다.
         *
         * 이후 finalPurification 기준으로
         * 다시 정확하게 추가한다.
         */

        for (
                LifeClaim claim
                : existingClaims
        ) {

            Objects.requireNonNull(
                    claim,
                    "기존 LifeClaim에 null이 포함될 수 없습니다."
            );

            EnumSet<LifeClaimBasisCode> bases =
                    EnumSet.noneOf(
                            LifeClaimBasisCode.class
                    );

            for (
                    LifeClaimBasisCode basis
                    : claim.bases()
            ) {

                if (
                        isOfficerKillingsFinalBasis(
                                basis
                        )
                ) {

                    continue;
                }

                bases.add(
                        basis
                );
            }

            /*
             * 官殺 basis만으로 존재했던 Claim이라면
             * 일단 제거한다.
             *
             * 아래에서 최종 판정에 따라 다시 생성.
             */
            if (
                    bases.isEmpty()
            ) {

                continue;
            }

            claims
                    .computeIfAbsent(
                            claim.code(),
                            ignored ->
                                    EnumSet.noneOf(
                                            LifeClaimBasisCode.class
                                    )
                    )
                    .addAll(
                            bases
                    );
        }

        /*
         * =================================================
         * 2. 官殺混雜 존재
         * =================================================
         */

        if (
                finalPurification.mixed()
        ) {

            add(
                    claims,

                    LifeClaimCode
                            .COMPLEX_AUTHORITY_EXPECTATIONS_REQUIRE_CLARITY,

                    LifeClaimBasisCode
                            .OFFICER_KILLINGS_MIXED
            );
        }

        /*
         * =================================================
         * 3. 전체 정리 완료
         * =================================================
         *
         * 이것만
         *
         * AUTHORITY_COMPLEXITY_CAN_BE_CHANNELLED
         *
         * 생성 가능.
         */

        if (
                finalPurification
                        .mixtureCleared()
        ) {

            add(
                    claims,

                    LifeClaimCode
                            .AUTHORITY_COMPLEXITY_CAN_BE_CHANNELLED,

                    LifeClaimBasisCode
                            .OFFICER_KILLINGS_MIXTURE_CLEARED
            );
        }

        /*
         * =================================================
         * 4. 검토 필요
         * =================================================
         */

        if (
                finalPurification
                        .reviewRequired()
        ) {

            add(
                    claims,

                    LifeClaimCode
                            .MULTIPLE_STRUCTURAL_DIRECTIONS_REQUIRE_CONTEXT,

                    LifeClaimBasisCode
                            .OFFICER_KILLINGS_REVIEW_REQUIRED
            );
        }

        /*
         * =================================================
         * PARTIALLY_CLEARED
         * =================================================
         *
         * 별도의 "부분 정리 완료" LifeClaim은
         * 현재 만들지 않는다.
         *
         * 왜냐하면 현재 LifeClaimBasisCode에
         * 부분 정리를 정확히 표현하는 basis가 없기 때문이다.
         *
         * 따라서:
         *
         * 官殺混雜 존재
         *
         * 까지만 남고,
         *
         * AUTHORITY_COMPLEXITY_CAN_BE_CHANNELLED
         *
         * 은 생성되지 않는다.
         */

        return toClaims(
                claims
        );
    }

    /*
     * =================================================
     * 기존 官殺 판정 basis 여부
     * =================================================
     */

    private boolean isOfficerKillingsFinalBasis(
            LifeClaimBasisCode basis
    ) {

        return basis
                == LifeClaimBasisCode.OFFICER_KILLINGS_MIXED

                ||

                basis
                        == LifeClaimBasisCode
                        .OFFICER_KILLINGS_MIXTURE_CLEARED

                ||

                basis
                        == LifeClaimBasisCode
                        .OFFICER_KILLINGS_REVIEW_REQUIRED

                ||

                basis
                        == LifeClaimBasisCode
                        .OFFICER_KILLINGS_CLEARED_BY_STEM_COMBINATION;
    }

    private void add(

            Map<
                    LifeClaimCode,
                    EnumSet<LifeClaimBasisCode>
                    > claims,

            LifeClaimCode code,

            LifeClaimBasisCode basis

    ) {

        claims
                .computeIfAbsent(
                        code,
                        ignored ->
                                EnumSet.noneOf(
                                        LifeClaimBasisCode.class
                                )
                )
                .add(
                        basis
                );
    }

    private List<LifeClaim> toClaims(

            Map<
                    LifeClaimCode,
                    EnumSet<LifeClaimBasisCode>
                    > claims

    ) {

        List<LifeClaim> result =
                new ArrayList<>();

        /*
         * LifeClaimCode enum 순서 유지.
         */
        for (
                LifeClaimCode code
                : LifeClaimCode.values()
        ) {

            EnumSet<LifeClaimBasisCode> bases =
                    claims.get(
                            code
                    );

            if (
                    bases == null
                            ||
                            bases.isEmpty()
            ) {

                continue;
            }

            result.add(
                    new LifeClaim(
                            code,
                            EnumSet.copyOf(
                                    bases
                            )
                    )
            );
        }

        return List.copyOf(
                result
        );
    }
}