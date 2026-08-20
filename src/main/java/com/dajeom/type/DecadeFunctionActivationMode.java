package com.dajeom.type;

/**
 * 하나의 대운 Period에서
 * 특정 Function이 어떤 경로로 활성되었는지 나타낸다.
 *
 * =================================================
 *
 * INACTIVE
 *
 * 원래 대운 십성 Input도 없고
 * Transformation Target 활성도 없다.
 *
 * =================================================
 *
 * ORIGINAL_INPUT_ONLY
 *
 * 대운의 원래 십성으로 직접 들어오지만
 * 해당 Function을 목표로 하는
 * 활성 Transformation은 없다.
 *
 * =================================================
 *
 * TRANSFORMED_TARGET_ONLY
 *
 * 원래 십성으로는 직접 들어오지 않지만
 * 다른 Function의 Transformation 결과로
 * 이 Function이 활성된다.
 *
 * =================================================
 *
 * ORIGINAL_AND_TRANSFORMED_TARGET
 *
 * 원래 십성 Input도 존재하고
 * Transformation Target으로도 활성된다.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 enum은 강약이나 길흉이 아니다.
 *
 * 오직 Function 활성 경로를 구분한다.
 */
public enum DecadeFunctionActivationMode {

    INACTIVE(
            false,
            false
    ),

    ORIGINAL_INPUT_ONLY(
            true,
            false
    ),

    TRANSFORMED_TARGET_ONLY(
            false,
            true
    ),

    ORIGINAL_AND_TRANSFORMED_TARGET(
            true,
            true
    );

    private final boolean originalInput;

    private final boolean transformedTarget;

    DecadeFunctionActivationMode(
            boolean originalInput,
            boolean transformedTarget
    ) {

        this.originalInput =
                originalInput;

        this.transformedTarget =
                transformedTarget;
    }

    public boolean active() {

        return originalInput
                || transformedTarget;
    }

    public boolean originalInput() {

        return originalInput;
    }

    public boolean transformedTarget() {

        return transformedTarget;
    }

    public boolean mixedActivationSource() {

        return originalInput
                && transformedTarget;
    }

    public static DecadeFunctionActivationMode of(

            boolean originalInput,

            boolean transformedTarget

    ) {

        if (
                originalInput
                        && transformedTarget
        ) {

            return ORIGINAL_AND_TRANSFORMED_TARGET;
        }

        if (
                originalInput
        ) {

            return ORIGINAL_INPUT_ONLY;
        }

        if (
                transformedTarget
        ) {

            return TRANSFORMED_TARGET_ONLY;
        }

        return INACTIVE;
    }
}