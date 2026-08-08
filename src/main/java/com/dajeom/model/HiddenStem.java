package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;

import java.util.Objects;

/**
 * 지지 내부에 숨어 있는 천간을 표현한다.
 *
 * 지장간의 비율이나 강도는
 * 이 모델에서 결정하지 않는다.
 *
 * 여기서는 어떤 천간이
 * 여기/중기/정기인지에 대한
 * 구조만 보관한다.
 */
public record HiddenStem(

        HeavenlyStem stem,

        HiddenStemRole role

) {

    public HiddenStem {

        Objects.requireNonNull(
                stem,
                "지장간 천간은 필수입니다."
        );

        Objects.requireNonNull(
                role,
                "지장간 역할은 필수입니다."
        );
    }

    public String chineseName() {

        return stem.getChineseCharacter();
    }

    public String koreanName() {

        return stem.getKoreanName();
    }

    public String roleKoreanName() {

        return role.getKoreanName();
    }
}