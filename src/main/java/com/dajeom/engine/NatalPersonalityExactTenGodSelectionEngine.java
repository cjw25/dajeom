package com.dajeom.engine;

import com.dajeom.model.NatalExactTenGodAnalysis;
import com.dajeom.model.NatalExactTenGodProfile;
import com.dajeom.model.NatalPersonalityExactTenGodSelection;
import com.dajeom.type.TenGodPowerLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Personality 사용자 Meaning에 사용할
 * 정확한 10십성의 설명 우선순위를 결정한다.
 *
 * =================================================
 *
 * 새 점수나 심리 의미를 만들지 않는다.
 *
 * 사용하는 값:
 *
 * - 5차 실제 주격 중심성
 * - 10-1 기존 TenGodPowerLevel
 *
 * 뿐이다.
 *
 * =================================================
 *
 * PRIMARY
 *
 * - 실제 주격 중심 십성
 * - DOMINANT
 * - STRONG
 *
 * =================================================
 *
 * SUPPORTING
 *
 * - MODERATE
 *
 * =================================================
 *
 * REFERENCE
 *
 * - WEAK
 *
 * =================================================
 *
 * ABSENT
 *
 * 사용자 Personality Selection에 넣지 않는다.
 *
 * =================================================
 *
 * 주의:
 *
 * PRIMARY에 여러 십성이 존재해도
 *
 * A + B = 새로운 성격 C
 *
 * 로 합성하지 않는다.
 *
 * 각 십성을 각각 독립적으로 설명한다.
 */
@Component
public class NatalPersonalityExactTenGodSelectionEngine {

    public NatalPersonalityExactTenGodSelection analyze(

            NatalExactTenGodAnalysis exactTenGodAnalysis

    ) {

        Objects.requireNonNull(
                exactTenGodAnalysis,
                "Personality Exact TenGod Selection에는 NatalExactTenGodAnalysis가 필요합니다."
        );

        List<NatalExactTenGodProfile> primary =
                new ArrayList<>();

        List<NatalExactTenGodProfile> supporting =
                new ArrayList<>();

        List<NatalExactTenGodProfile> reference =
                new ArrayList<>();

        /*
         * =================================================
         * 이미 정렬된 정확한 십성 Profile 사용
         * =================================================
         */

        for (
                NatalExactTenGodProfile profile
                : exactTenGodAnalysis
                .presentProfiles()
        ) {

            /*
             * =================================================
             * PRIMARY
             * =================================================
             */

            if (
                    profile.primaryStructure()

                            || profile.powerLevel()
                            == TenGodPowerLevel.DOMINANT

                            || profile.powerLevel()
                            == TenGodPowerLevel.STRONG
            ) {

                primary.add(
                        profile
                );

                continue;
            }

            /*
             * =================================================
             * SUPPORTING
             * =================================================
             */

            if (
                    profile.powerLevel()
                            == TenGodPowerLevel.MODERATE
            ) {

                supporting.add(
                        profile
                );

                continue;
            }

            /*
             * =================================================
             * REFERENCE
             * =================================================
             */

            if (
                    profile.powerLevel()
                            == TenGodPowerLevel.WEAK
            ) {

                reference.add(
                        profile
                );
            }
        }

        return new NatalPersonalityExactTenGodSelection(

                exactTenGodAnalysis,

                List.copyOf(
                        primary
                ),

                List.copyOf(
                        supporting
                ),

                List.copyOf(
                        reference
                ),

                exactTenGodAnalysis
                        .completeChart()
        );
    }
}