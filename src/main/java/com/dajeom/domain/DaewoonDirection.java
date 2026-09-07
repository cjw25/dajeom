package com.dajeom.domain;

public enum DaewoonDirection {

    FORWARD(
            "순행"
    ),

    REVERSE(
            "역행"
    );

    private final String korean;

    DaewoonDirection(
            String korean
    ) {
        this.korean = korean;
    }

    public String korean() {
        return korean;
    }

    public boolean isForward() {
        return this == FORWARD;
    }

    public boolean isReverse() {
        return this == REVERSE;
    }
}