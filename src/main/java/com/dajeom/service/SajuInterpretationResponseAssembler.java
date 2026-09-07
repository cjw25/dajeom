package com.dajeom.service;

import com.dajeom.domain.LifeInterpretationCategory;
import com.dajeom.domain.LifeInterpretationTextBundle;
import com.dajeom.dto.FiveElementDistributionDto;
import com.dajeom.dto.LifeInterpretationSectionDto;
import com.dajeom.dto.PublicSajuPillarsDto;
import com.dajeom.dto.SajuInterpretationResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 사주 종합풀이 최종 공개 응답 조립기.
 *
 * =================================================
 *
 * 입력
 * =================================================
 *
 * 1. PublicSajuPillarsDto
 *
 * 사용자 공개용 사주팔자 4기둥.
 *
 * 2. FiveElementDistributionDto
 *
 * 사주팔자 8글자 기준 오행분포.
 *
 * 3. LifeInterpretationTextBundle
 *
 * AI가 최종 생성한 7개 장문풀이.
 *
 * =================================================
 *
 * 출력
 * =================================================
 *
 * SajuInterpretationResponse
 *
 * =================================================
 *
 * 이 Assembler는:
 *
 * - 명리 계산을 하지 않는다.
 * - AI를 호출하지 않는다.
 * - LifeClaim을 다시 판단하지 않는다.
 * - 내부 계산값을 공개 응답에 추가하지 않는다.
 *
 * 최종 공개 데이터 조립만 담당한다.
 */
@Component
public class SajuInterpretationResponseAssembler {

    public SajuInterpretationResponse assemble(

            PublicSajuPillarsDto pillars,

            FiveElementDistributionDto fiveElements,

            LifeInterpretationTextBundle interpretations

    ) {

        Objects.requireNonNull(
                pillars,
                "PublicSajuPillarsDto는 필수입니다."
        );

        Objects.requireNonNull(
                fiveElements,
                "FiveElementDistributionDto는 필수입니다."
        );

        Objects.requireNonNull(
                interpretations,
                "LifeInterpretationTextBundle은 필수입니다."
        );

        if (
                !interpretations.complete()
        ) {

            throw new IllegalArgumentException(
                    "최종 응답을 생성하려면 7개 해석 본문이 모두 필요합니다."
            );
        }

        List<LifeInterpretationSectionDto> sections =
                new ArrayList<>();

        /*
         * =================================================
         * 공개 순서는 여기서 고정한다.
         * =================================================
         *
         * enum 내부 accessor 이름에 의존하지 않는다.
         *
         * 따라서 LifeInterpretationCategory enum의
         * 내부 필드 구조가 변경되더라도
         * 공개 응답 순서는 영향을 받지 않는다.
         */

        sections.add(
                section(
                        1,
                        "성향·기질",
                        LifeInterpretationCategory.TEMPERAMENT,
                        interpretations
                )
        );

        sections.add(
                section(
                        2,
                        "지나온 흐름",
                        LifeInterpretationCategory.PAST_FLOW,
                        interpretations
                )
        );

        sections.add(
                section(
                        3,
                        "가족·인간관계",
                        LifeInterpretationCategory.FAMILY_RELATIONSHIPS,
                        interpretations
                )
        );

        sections.add(
                section(
                        4,
                        "연애·결혼",
                        LifeInterpretationCategory.LOVE_MARRIAGE,
                        interpretations
                )
        );

        sections.add(
                section(
                        5,
                        "재물",
                        LifeInterpretationCategory.WEALTH,
                        interpretations
                )
        );

        sections.add(
                section(
                        6,
                        "일·성취",
                        LifeInterpretationCategory.WORK_ACHIEVEMENT,
                        interpretations
                )
        );

        sections.add(
                section(
                        7,
                        "건강·생활",
                        LifeInterpretationCategory.HEALTH_LIFESTYLE,
                        interpretations
                )
        );

        return new SajuInterpretationResponse(
                pillars,
                fiveElements,
                sections
        );
    }

    private LifeInterpretationSectionDto section(

            int order,

            String title,

            LifeInterpretationCategory category,

            LifeInterpretationTextBundle interpretations

    ) {

        return new LifeInterpretationSectionDto(
                order,
                title,
                interpretations.textFor(
                        category
                )
        );
    }
}