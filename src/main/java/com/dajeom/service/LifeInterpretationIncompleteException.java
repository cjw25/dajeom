package com.dajeom.service;

import com.dajeom.domain.LifeInterpretationCategory;

import java.util.List;
import java.util.Objects;

/**
 * 7개 사주 종합풀이 중 하나 이상이
 * 생성되지 않아 최종 공개 응답을 만들 수 없을 때 발생한다.
 *
 * =================================================
 *
 * 예:
 *
 * - PAST_FLOW generationAllowed=false
 * - 특정 카테고리 AI 결과 누락
 * - 일부 Prompt만 생성된 상태
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 누락된 영역을 임의 기본 문장으로 채우지 않는다.
 */
public class LifeInterpretationIncompleteException
        extends RuntimeException {

    private final List<LifeInterpretationCategory>
            missingCategories;

    private final List<LifeInterpretationCategory>
            blockedCategories;

    public LifeInterpretationIncompleteException(

            List<LifeInterpretationCategory> missingCategories,

            List<LifeInterpretationCategory> blockedCategories

    ) {

        super(
                buildMessage(
                        missingCategories,
                        blockedCategories
                )
        );

        this.missingCategories =
                copy(
                        missingCategories,
                        "missingCategories"
                );

        this.blockedCategories =
                copy(
                        blockedCategories,
                        "blockedCategories"
                );
    }

    public List<LifeInterpretationCategory>
    missingCategories() {

        return missingCategories;
    }

    public List<LifeInterpretationCategory>
    blockedCategories() {

        return blockedCategories;
    }

    private static String buildMessage(

            List<LifeInterpretationCategory> missing,

            List<LifeInterpretationCategory> blocked

    ) {

        return "최종 사주 종합풀이를 생성할 수 없습니다. "
                + "미생성 카테고리="
                + missing
                + ", 생성 차단 카테고리="
                + blocked;
    }

    private static List<LifeInterpretationCategory> copy(

            List<LifeInterpretationCategory> source,

            String name

    ) {

        Objects.requireNonNull(
                source,
                name + "은 필수입니다."
        );

        for (
                LifeInterpretationCategory category
                : source
        ) {

            Objects.requireNonNull(
                    category,
                    name + "에 null이 포함될 수 없습니다."
            );
        }

        return List.copyOf(
                source
        );
    }
}