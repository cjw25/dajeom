package com.dajeom.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 사용자에게 최종 반환하는 AI 사주 종합풀이 응답.
 *
 * =================================================
 *
 * 공개 내용은 정확히 세 덩어리다.
 *
 * 1. 사주팔자 4기둥
 *
 * 2. 오행분포
 *
 * 3. 7개 장문풀이
 *
 * =================================================
 *
 * 절대 포함하지 않는 내부 정보:
 *
 * - 십성표
 * - 지장간
 * - 음양분포
 * - 신강신약 판정값
 * - 격국
 * - 월령
 * - 통근
 * - 투간
 * - 합충형파해
 * - 삼합
 * - 방합
 * - 천간합
 * - 공망
 * - 납음
 * - 귀인
 * - 신살
 * - 12운성
 * - 대운표
 * - 세운표
 * - 월운표
 * - 절입시각
 * - 시간보정
 * - 대운수
 * - LifeClaim
 * - NarrativeDirective
 *
 * =================================================
 *
 * Front-end는 이 DTO만 보고 화면을 구성하면 된다.
 */
public record SajuInterpretationResponse(

        PublicSajuPillarsDto pillars,

        FiveElementDistributionDto fiveElements,

        List<LifeInterpretationSectionDto> interpretations

) {

    public SajuInterpretationResponse {

        Objects.requireNonNull(
                pillars,
                "공개 사주팔자는 필수입니다."
        );

        Objects.requireNonNull(
                fiveElements,
                "오행분포는 필수입니다."
        );

        Objects.requireNonNull(
                interpretations,
                "7개 종합풀이는 필수입니다."
        );

        List<LifeInterpretationSectionDto> normalized =
                new ArrayList<>();

        for (
                LifeInterpretationSectionDto interpretation
                : interpretations
        ) {

            normalized.add(
                    Objects.requireNonNull(
                            interpretation,
                            "종합풀이 목록에 null이 포함될 수 없습니다."
                    )
            );
        }

        /*
         * 반드시 7개.
         */
        if (
                normalized.size() != 7
        ) {

            throw new IllegalArgumentException(
                    "최종 사주 종합풀이는 반드시 7개 영역이어야 합니다. 현재="
                            + normalized.size()
            );
        }

        /*
         * order 중복 방지.
         */
        Set<Integer> orders =
                new HashSet<>();

        for (
                LifeInterpretationSectionDto section
                : normalized
        ) {

            if (
                    !orders.add(
                            section.order()
                    )
            ) {

                throw new IllegalArgumentException(
                        "중복된 풀이 순서가 존재합니다: "
                                + section.order()
                );
            }
        }

        /*
         * 1~7이 하나씩 모두 존재해야 한다.
         */
        for (
                int order = 1;
                order <= 7;
                order++
        ) {

            if (
                    !orders.contains(
                            order
                    )
            ) {

                throw new IllegalArgumentException(
                        "누락된 풀이 영역이 있습니다. order="
                                + order
                );
            }
        }

        /*
         * 프론트가 별도 정렬하지 않아도 되도록
         * 항상 1 → 7 순으로 반환.
         */
        normalized.sort(
                Comparator.comparingInt(
                        LifeInterpretationSectionDto::order
                )
        );

        interpretations =
                List.copyOf(
                        normalized
                );
    }
}