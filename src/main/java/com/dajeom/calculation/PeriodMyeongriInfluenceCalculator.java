package com.dajeom.calculation;

import com.dajeom.domain.PeriodMyeongriFacts;
import com.dajeom.domain.PeriodMyeongriInfluence;
import com.dajeom.domain.PeriodMyeongriInfluenceCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 기존 운 계산 Adapter가 만든
 * PeriodMyeongriFacts를
 *
 * PeriodMyeongriInfluence 목록으로 변환한다.
 *
 * =================================================
 *
 * 전체 흐름
 * =================================================
 *
 * 기존 Daewoon / Sewoon / Wolwoon
 *
 *      ↓ 실제 Adapter
 *
 * PeriodMyeongriFacts
 *
 *      ↓ 이 Calculator
 *
 * PeriodMyeongriInfluence
 *
 *      ↓
 *
 * PeriodLifeSignalCalculator
 *
 *      ↓
 *
 * PeriodLifeSignal
 *
 *      ↓
 *
 * PastFlowLifeClaimCalculator
 *
 *      ↓
 *
 * PeriodLifeClaim
 *
 * =================================================
 *
 * 이 Calculator는 명리 판단을 새로 하지 않는다.
 *
 * Adapter가 true/false로 확정한 구조 사실을
 * enum 형태로 변환하는 역할만 한다.
 */
@Component
public class PeriodMyeongriInfluenceCalculator {

    public List<PeriodMyeongriInfluence> calculate(
            PeriodMyeongriFacts facts
    ) {

        Objects.requireNonNull(
                facts,
                "PeriodMyeongriFacts는 필수입니다."
        );

        if (!facts.hasAnyInfluence()) {

            return List.of();
        }

        List<PeriodMyeongriInfluence> result =
                new ArrayList<>();

        /*
         * =================================================
         * 印
         * =================================================
         */

        if (
                facts.resourceActivated()
        ) {

            add(
                    result,
                    facts,
                    PeriodMyeongriInfluenceCode
                            .RESOURCE_ACTIVATED
            );
        }

        if (
                facts.resourceWeakened()
        ) {

            add(
                    result,
                    facts,
                    PeriodMyeongriInfluenceCode
                            .RESOURCE_WEAKENED
            );
        }

        /*
         * =================================================
         * 食傷
         * =================================================
         */

        if (
                facts.outputActivated()
        ) {

            add(
                    result,
                    facts,
                    PeriodMyeongriInfluenceCode
                            .OUTPUT_ACTIVATED
            );
        }

        /*
         * =================================================
         * 財
         * =================================================
         */

        if (
                facts.wealthActivated()
        ) {

            add(
                    result,
                    facts,
                    PeriodMyeongriInfluenceCode
                            .WEALTH_ACTIVATED
            );
        }

        /*
         * =================================================
         * 官殺
         * =================================================
         */

        if (
                facts.authorityActivated()
        ) {

            add(
                    result,
                    facts,
                    PeriodMyeongriInfluenceCode
                            .AUTHORITY_ACTIVATED
            );
        }

        /*
         * =================================================
         * 比劫
         * =================================================
         */

        if (
                facts.companionActivated()
        ) {

            add(
                    result,
                    facts,
                    PeriodMyeongriInfluenceCode
                            .COMPANION_ACTIVATED
            );
        }

        /*
         * =================================================
         * 원국 구조 교란
         * =================================================
         */

        if (
                facts
                        .natalStructureDisruptionActivated()
        ) {

            add(
                    result,
                    facts,
                    PeriodMyeongriInfluenceCode
                            .NATAL_STRUCTURE_DISRUPTION_ACTIVATED
            );
        }

        /*
         * 충돌이 발생했다는 사실과
         * 해소 route가 존재한다는 사실은
         * 서로 배타적이지 않다.
         */
        if (
                facts
                        .natalStructureDisruptionResolved()
        ) {

            add(
                    result,
                    facts,
                    PeriodMyeongriInfluenceCode
                            .NATAL_STRUCTURE_DISRUPTION_RESOLVED
            );
        }

        /*
         * =================================================
         * 天干合 등 실제 결속
         * =================================================
         */

        if (
                facts
                        .activeBindingEstablished()
        ) {

            add(
                    result,
                    facts,
                    PeriodMyeongriInfluenceCode
                            .ACTIVE_BINDING_ESTABLISHED
            );
        }

        /*
         * =================================================
         * 원국 중심 방향 강화
         * =================================================
         */

        if (
                facts
                        .natalDirectionReinforced()
        ) {

            add(
                    result,
                    facts,
                    PeriodMyeongriInfluenceCode
                            .NATAL_DIRECTION_REINFORCED
            );
        }

        /*
         * =================================================
         * 원국 중심 방향 도전
         * =================================================
         */

        if (
                facts
                        .natalDirectionChallenged()
        ) {

            add(
                    result,
                    facts,
                    PeriodMyeongriInfluenceCode
                            .NATAL_DIRECTION_CHALLENGED
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 여러 기간의 Fact를 한꺼번에 변환.
     */
    public List<PeriodMyeongriInfluence> calculateAll(
            List<PeriodMyeongriFacts> factsList
    ) {

        Objects.requireNonNull(
                factsList,
                "PeriodMyeongriFacts 목록은 필수입니다."
        );

        if (factsList.isEmpty()) {

            return List.of();
        }

        List<PeriodMyeongriInfluence> result =
                new ArrayList<>();

        for (
                PeriodMyeongriFacts facts
                : factsList
        ) {

            Objects.requireNonNull(
                    facts,
                    "PeriodMyeongriFacts 목록에 null이 포함될 수 없습니다."
            );

            result.addAll(
                    calculate(
                            facts
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    private void add(

            List<PeriodMyeongriInfluence> result,

            PeriodMyeongriFacts facts,

            PeriodMyeongriInfluenceCode code

    ) {

        result.add(
                new PeriodMyeongriInfluence(
                        facts.period(),
                        code
                )
        );
    }
}