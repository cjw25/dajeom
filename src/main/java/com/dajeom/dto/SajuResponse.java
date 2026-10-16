package com.dajeom.dto;

import com.dajeom.domain.FourPillars;
import com.dajeom.domain.LifeInterpretationCategory;
import com.dajeom.domain.LifeInterpretationTextBundle;
import com.dajeom.domain.Pillar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 최종 공개 사주 응답.
 *
 * =================================================
 *
 * 외부에 공개하는 값은 세 종류뿐이다.
 *
 * 1. 사주 4기둥
 * 2. 오행 분포
 * 3. 7개 해석 항목과 생성 상태
 *
 * READY 항목만 실제 생성된 풀이이며 UNAVAILABLE은 미생성 안내다.
 * 출생시간이 없을 때에는 세 기둥과 여섯 글자의 오행만 공개한다.
 *
 * =================================================
 *
 * 다음 내부 계산값은 공개하지 않는다.
 *
 * - 신강·신약 판정
 * - 격국 판정
 * - 천간합/지지합 판정
 * - 관살/재인/양인 중간 결과
 * - LifeClaim
 * - Narrative
 * - Prompt
 * - 대운 내부 분석값
 */
public record SajuResponse(

        PillarsResponse pillars,

        ElementCountResponse fiveElements,

        List<InterpretationResponse> interpretations

) {

    public static SajuResponse available(FourPillars pillars, ElementCountResponse elements,
            com.dajeom.domain.LifeInterpretationGenerationResult generation, String unavailableMessage) {
        List<InterpretationResponse> interpretations = new ArrayList<>();
        for (LifeInterpretationCategory category : LifeInterpretationCategory.values()) {
            String text = generation.generatedTexts().get(category);
            interpretations.add(new InterpretationResponse(category.ordinal() + 1, titleOf(category),
                    text == null ? unavailableMessage : text, text == null ? "UNAVAILABLE" : "READY"));
        }
        return new SajuResponse(new PillarsResponse(PillarResponse.from(pillars.year()),
                PillarResponse.from(pillars.month()), PillarResponse.from(pillars.day()),
                pillars.hasKnownHour() ? PillarResponse.from(pillars.hour()) : null), elements, interpretations);
    }

    public SajuResponse {

        Objects.requireNonNull(
                pillars,
                "사주 4기둥 응답이 필요합니다."
        );

        Objects.requireNonNull(
                fiveElements,
                "오행 분포 응답이 필요합니다."
        );

        Objects.requireNonNull(
                interpretations,
                "7개 해석 응답이 필요합니다."
        );

        interpretations =
                List.copyOf(
                        interpretations
                );

        if (
                interpretations.size()
                        != LifeInterpretationCategory.values().length
        ) {

            throw new IllegalArgumentException(
                    "최종 사주 응답에는 7개 해석이 모두 필요합니다. actual="
                            + interpretations.size()
            );
        }

        for (
                int index = 0;
                index < interpretations.size();
                index++
        ) {

            InterpretationResponse interpretation =
                    Objects.requireNonNull(
                            interpretations.get(index),
                            "해석 목록에는 null이 들어갈 수 없습니다."
                    );

            int expectedOrder =
                    index + 1;

            if (
                    interpretation.order()
                            != expectedOrder
            ) {

                throw new IllegalArgumentException(
                        "해석 순서가 올바르지 않습니다. expected="
                                + expectedOrder
                                + ", actual="
                                + interpretation.order()
                );
            }
        }
    }

    public static SajuResponse of(

            FourPillars fourPillars,

            ElementCountResponse fiveElements,

            LifeInterpretationTextBundle interpretationTexts

    ) {

        Objects.requireNonNull(
                fourPillars,
                "FourPillars가 필요합니다."
        );

        Objects.requireNonNull(
                fiveElements,
                "ElementCountResponse가 필요합니다."
        );

        Objects.requireNonNull(
                interpretationTexts,
                "LifeInterpretationTextBundle이 필요합니다."
        );

        List<InterpretationResponse> interpretations =
                new ArrayList<>();

        int order =
                1;

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            interpretations.add(
                    new InterpretationResponse(
                            order,
                            titleOf(
                                    category
                            ),
                            interpretationTexts
                                    .textFor(
                                            category
                                    )
                    )
            );

            order++;
        }

        return new SajuResponse(

                new PillarsResponse(
                        PillarResponse.from(
                                fourPillars.year()
                        ),
                        PillarResponse.from(
                                fourPillars.month()
                        ),
                        PillarResponse.from(
                                fourPillars.day()
                        ),
                        fourPillars.hasKnownHour()
                                ? PillarResponse.from(
                                fourPillars.hour()
                        )
                                : null
                ),

                fiveElements,

                interpretations
        );
    }

    private static String titleOf(
            LifeInterpretationCategory category
    ) {

        return switch (category) {

            case TEMPERAMENT ->
                    "성향·기질";

            case PAST_FLOW ->
                    "지나온 흐름";

            case FAMILY_RELATIONSHIPS ->
                    "가족·인간관계";

            case LOVE_MARRIAGE ->
                    "연애·결혼";

            case WEALTH ->
                    "재물";

            case WORK_ACHIEVEMENT ->
                    "일·성취";

            case HEALTH_LIFESTYLE ->
                    "건강·생활";
        };
    }

    public record PillarsResponse(

            PillarResponse year,

            PillarResponse month,

            PillarResponse day,

            PillarResponse hour

    ) {

        public PillarsResponse {

            Objects.requireNonNull(
                    year,
                    "년주가 필요합니다."
            );

            Objects.requireNonNull(
                    month,
                    "월주가 필요합니다."
            );

            Objects.requireNonNull(
                    day,
                    "일주가 필요합니다."
            );
        }
    }

    public record PillarResponse(

            String stem,

            String branch,

            String hanja,

            String korean

    ) {

        public PillarResponse {

            stem =
                    requireText(
                            stem,
                            "천간"
                    );

            branch =
                    requireText(
                            branch,
                            "지지"
                    );

            hanja =
                    requireText(
                            hanja,
                            "간지 한자"
                    );

            korean =
                    requireText(
                            korean,
                            "간지 한글"
                    );
        }

        public static PillarResponse from(
                Pillar pillar
        ) {

            Objects.requireNonNull(
                    pillar,
                    "Pillar가 필요합니다."
            );

            String hanja =
                    requireText(
                            pillar
                                    .ganji()
                                    .hanja(),
                            "간지 한자"
                    );

            int[] codePoints =
                    hanja
                            .codePoints()
                            .toArray();

            if (
                    codePoints.length
                            != 2
            ) {

                throw new IllegalStateException(
                        "간지 한자는 정확히 두 글자여야 합니다: "
                                + hanja
                );
            }

            String stem =
                    new String(
                            codePoints,
                            0,
                            1
                    );

            String branch =
                    new String(
                            codePoints,
                            1,
                            1
                    );

            return new PillarResponse(
                    stem,
                    branch,
                    hanja,
                    pillar
                            .ganji()
                            .korean()
            );
        }
    }

    public record InterpretationResponse(

            int order,

            String title,

            String content,

            String status

    ) {

        public InterpretationResponse(int order, String title, String content) {
            this(order, title, content, "READY");
        }

        public InterpretationResponse {

            if (!"READY".equals(status) && !"UNAVAILABLE".equals(status)) {
                throw new IllegalArgumentException("알 수 없는 해석 상태입니다.");
            }

            if (
                    order < 1
                            || order > 7
            ) {

                throw new IllegalArgumentException(
                        "해석 순서는 1~7이어야 합니다. order="
                                + order
                );
            }

            title =
                    requireText(
                            title,
                            "해석 제목"
                    );

            content =
                    requireText(
                            content,
                            "해석 본문"
                    );
        }
    }

    private static String requireText(

            String value,

            String name

    ) {

        Objects.requireNonNull(
                value,
                name + "이 필요합니다."
        );

        String normalized =
                value.trim();

        if (normalized.isEmpty()) {

            throw new IllegalArgumentException(
                    name + "은 비어 있을 수 없습니다."
            );
        }

        return normalized;
    }
}
