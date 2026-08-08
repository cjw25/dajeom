package com.dajeom.model;

/**
 * 대운 시작 나이.
 *
 * 대운 시작시점을 단순한 문자열이 아니라
 * 계산 가능한 데이터로 보관한다.
 */
public record LuckStartAge(

        int years,

        int months,

        int days,

        double decimalYears

) {

    public LuckStartAge {

        if (years < 0) {

            throw new IllegalArgumentException(
                    "대운 시작 연수는 음수일 수 없습니다."
            );
        }

        if (months < 0) {

            throw new IllegalArgumentException(
                    "대운 시작 개월수는 음수일 수 없습니다."
            );
        }

        if (days < 0) {

            throw new IllegalArgumentException(
                    "대운 시작 일수는 음수일 수 없습니다."
            );
        }

        if (decimalYears < 0) {

            throw new IllegalArgumentException(
                    "대운 시작 나이는 음수일 수 없습니다."
            );
        }
    }

    /**
     * 사용자 표시용.
     *
     * 예:
     * 4년 2개월 10일
     */
    public String displayName() {

        return years
                + "년 "
                + months
                + "개월 "
                + days
                + "일";
    }
}