package com.dajeom.dto;

import java.util.Objects;

/**
 * 사용자에게 공개되는 사주팔자 4기둥.
 *
 * =================================================
 *
 * 화면에서는:
 *
 * 년주 | 월주 | 일주 | 시주
 *
 * 형태로 표시하면 된다.
 *
 * 각 기둥은:
 *
 * 천간
 * 지지
 * 천간 오행
 * 지지 오행
 *
 * 만 포함한다.
 */
public record PublicSajuPillarsDto(

        PublicPillarDto year,

        PublicPillarDto month,

        PublicPillarDto day,

        PublicPillarDto hour

) {

    public PublicSajuPillarsDto {

        Objects.requireNonNull(
                year,
                "년주는 필수입니다."
        );

        Objects.requireNonNull(
                month,
                "월주는 필수입니다."
        );

        Objects.requireNonNull(
                day,
                "일주는 필수입니다."
        );

        Objects.requireNonNull(
                hour,
                "시주는 필수입니다."
        );
    }
}