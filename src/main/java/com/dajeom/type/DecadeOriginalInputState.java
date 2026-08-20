package com.dajeom.type;

/**
 * 대운에서 직접 들어온
 * 원래 Function Input의
 * Transformation 이후 보존 상태.
 *
 * =================================================
 *
 * ABSENT
 *
 * 해당 Function의 원래 십성 Input이 없음.
 *
 * =================================================
 *
 * RETAINED
 *
 * 직접 Input은 존재하며
 * 대운 Participant 원기능 감소가
 * 확인되지 않음.
 *
 * =================================================
 *
 * REDUCED
 *
 * 직접 Input의 원기능 감소가 확인됨.
 *
 * =================================================
 *
 * STRONGLY_REDUCED
 *
 * 직접 Input에서 강한 원기능 감소가 확인됨.
 *
 * =================================================
 *
 * MIXED
 *
 * 같은 Function 안의 여러 Input 또는
 * 여러 Transformation에서
 *
 * - 유지
 * - 감소
 * - 강한 감소
 *
 * 상태가 서로 다르게 존재함.
 *
 * =================================================
 *
 * 중요:
 *
 * REDUCED는:
 *
 * 기능이 사라졌다
 *
 * 는 뜻이 아니다.
 *
 * 원래 대운 Input의 기능이
 * 그대로 발현되는 데 제한 Context가
 * 존재한다는 의미다.
 */
public enum DecadeOriginalInputState {

    ABSENT,

    RETAINED,

    REDUCED,

    STRONGLY_REDUCED,

    MIXED;

    public boolean present() {

        return this != ABSENT;
    }

    public boolean retained() {

        return this == RETAINED;
    }

    public boolean reduced() {

        return this == REDUCED
                || this == STRONGLY_REDUCED
                || this == MIXED;
    }

    public boolean stronglyReduced() {

        return this == STRONGLY_REDUCED;
    }

    public boolean mixed() {

        return this == MIXED;
    }
}