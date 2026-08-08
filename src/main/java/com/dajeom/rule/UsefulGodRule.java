package com.dajeom.rule;

import com.dajeom.type.FiveElement;
import com.dajeom.type.StrengthLevel;

import java.util.List;
import java.util.Objects;

/**
 * 억부법 기반 용신 후보 Rule.
 *
 * V1의 목적은
 * 최종 용신 확정이 아니라
 * 강약을 기준으로 유리/불리할 가능성이 있는
 * 오행 후보군을 구성하는 것이다.
 *
 * 최종 용신 판정에는 추후:
 *
 * - 조후
 * - 통관
 * - 격국
 * - 합화
 * - 대운 상호작용
 *
 * 등을 추가 반영한다.
 */
public final class UsefulGodRule {

    public static final String RULE_VERSION =
            "DAJEOM_USEFUL_GOD_V1";

    private UsefulGodRule() {

        throw new IllegalStateException(
                "UsefulGodRule은 생성할 수 없습니다."
        );
    }

    /**
     * 강약에 따른 억부 후보군을 생성한다.
     */
    public static CandidateSet createCandidates(
            FiveElement dayMasterElement,
            StrengthLevel strengthLevel
    ) {

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행은 필수입니다."
        );

        Objects.requireNonNull(
                strengthLevel,
                "강약 단계는 필수입니다."
        );

        FiveElement resource =
                resourceElementOf(
                        dayMasterElement
                );

        FiveElement output =
                outputElementOf(
                        dayMasterElement
                );

        FiveElement wealth =
                wealthElementOf(
                        dayMasterElement
                );

        FiveElement officer =
                officerElementOf(
                        dayMasterElement
                );

        return switch (strengthLevel) {

            case VERY_WEAK,
                 WEAK ->

                    new CandidateSet(
                            List.of(
                                    resource
                            ),
                            List.of(
                                    dayMasterElement
                            ),
                            List.of(
                                    output,
                                    wealth,
                                    officer
                            )
                    );

            case VERY_STRONG,
                 STRONG ->

                    new CandidateSet(
                            List.of(
                                    output
                            ),
                            List.of(
                                    wealth,
                                    officer
                            ),
                            List.of(
                                    dayMasterElement,
                                    resource
                            )
                    );

            case BALANCED ->

                /*
                 * 중화 상태에서는 강약 하나만으로
                 * 특정 용신을 선택하지 않는다.
                 *
                 * 조후, 격국, 통관 등의
                 * 추가 판단을 기다린다.
                 */
                    new CandidateSet(
                            List.of(),
                            List.of(),
                            List.of()
                    );
        };
    }

    /**
     * 일간을 생하는 오행.
     *
     * 인성 계열.
     */
    public static FiveElement resourceElementOf(
            FiveElement element
    ) {

        Objects.requireNonNull(
                element,
                "오행은 필수입니다."
        );

        return switch (element) {

            case WOOD ->
                    FiveElement.WATER;

            case FIRE ->
                    FiveElement.WOOD;

            case EARTH ->
                    FiveElement.FIRE;

            case METAL ->
                    FiveElement.EARTH;

            case WATER ->
                    FiveElement.METAL;
        };
    }

    /**
     * 일간이 생하는 오행.
     *
     * 식상 계열.
     */
    public static FiveElement outputElementOf(
            FiveElement element
    ) {

        Objects.requireNonNull(
                element,
                "오행은 필수입니다."
        );

        return switch (element) {

            case WOOD ->
                    FiveElement.FIRE;

            case FIRE ->
                    FiveElement.EARTH;

            case EARTH ->
                    FiveElement.METAL;

            case METAL ->
                    FiveElement.WATER;

            case WATER ->
                    FiveElement.WOOD;
        };
    }

    /**
     * 일간이 극하는 오행.
     *
     * 재성 계열.
     */
    public static FiveElement wealthElementOf(
            FiveElement element
    ) {

        Objects.requireNonNull(
                element,
                "오행은 필수입니다."
        );

        return switch (element) {

            case WOOD ->
                    FiveElement.EARTH;

            case FIRE ->
                    FiveElement.METAL;

            case EARTH ->
                    FiveElement.WATER;

            case METAL ->
                    FiveElement.WOOD;

            case WATER ->
                    FiveElement.FIRE;
        };
    }

    /**
     * 일간을 극하는 오행.
     *
     * 관성 계열.
     */
    public static FiveElement officerElementOf(
            FiveElement element
    ) {

        Objects.requireNonNull(
                element,
                "오행은 필수입니다."
        );

        return switch (element) {

            case WOOD ->
                    FiveElement.METAL;

            case FIRE ->
                    FiveElement.WATER;

            case EARTH ->
                    FiveElement.WOOD;

            case METAL ->
                    FiveElement.FIRE;

            case WATER ->
                    FiveElement.EARTH;
        };
    }

    /**
     * 억부 후보군.
     */
    public record CandidateSet(

            List<FiveElement> yongshinCandidates,

            List<FiveElement> huishinCandidates,

            List<FiveElement> gishinCandidates

    ) {

        public CandidateSet {

            Objects.requireNonNull(
                    yongshinCandidates,
                    "용신 후보 목록은 필수입니다."
            );

            Objects.requireNonNull(
                    huishinCandidates,
                    "희신 후보 목록은 필수입니다."
            );

            Objects.requireNonNull(
                    gishinCandidates,
                    "기신 후보 목록은 필수입니다."
            );

            yongshinCandidates =
                    List.copyOf(
                            yongshinCandidates
                    );

            huishinCandidates =
                    List.copyOf(
                            huishinCandidates
                    );

            gishinCandidates =
                    List.copyOf(
                            gishinCandidates
                    );
        }
    }
}