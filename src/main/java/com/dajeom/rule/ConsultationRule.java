package com.dajeom.rule;

import com.dajeom.model.InterpretationEvidence;
import com.dajeom.model.InterpretationSignal;
import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.InterpretationIntensity;
import com.dajeom.type.InterpretationPolarity;

import java.util.List;
import java.util.Objects;

/**
 * 일반 사용자용 상담문 V1 Rule.
 *
 * 역할:
 *
 * InterpretationSignal
 *      ↓
 * 일반인이 이해할 수 있는
 * 기본 상담 표현
 *
 * 명리 공식이나 사주 계산은
 * 이 클래스에서 하지 않는다.
 *
 * 또한 미래의 특정 사건을
 * 확정적으로 예언하지 않는다.
 */
public final class ConsultationRule {

    public static final String RULE_VERSION =
            "DAJEOM_CONSULTATION_V1";

    private ConsultationRule() {

        throw new IllegalStateException(
                "ConsultationRule은 생성할 수 없습니다."
        );
    }

    /**
     * 전체 사주 상담 시작 문장.
     */
    public static String overallOpening(
            int targetYear
    ) {

        return targetYear
                + "년의 흐름까지 함께 보면서, "
                + "타고난 성향부터 현재와 앞으로의 흐름까지 "
                + "하나씩 살펴볼게요.";
    }

    /**
     * 전체 상담 마무리 문장.
     */
    public static String overallClosing() {

        return "사주는 한 가지 신호만으로 단정하기보다 "
                + "여러 흐름이 어떻게 겹치는지를 함께 보는 것이 중요해요. "
                + "지금 나온 내용은 선택과 준비에 참고할 수 있는 흐름으로 활용해 주세요.";
    }

    /**
     * 각 분야 시작 문장.
     */
    public static String openingFor(
            ConsultationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "상담 분야는 필수입니다."
        );

        return switch (category) {

            case PERSONALITY ->
                    "먼저 타고난 기본 성향부터 볼게요.";

            case PAST_LUCK ->
                    "지나온 흐름에서 반복되기 쉬운 패턴을 살펴볼게요.";

            case SOCIAL_RELATIONSHIP ->
                    "이번에는 주변 사람들과의 관계 흐름을 볼게요.";

            case LOVE_STYLE ->
                    "연애할 때 나타나기 쉬운 당신의 방식을 볼게요.";

            case PARTNER_LUCK ->
                    "인연과 배우자 흐름도 함께 살펴볼게요.";

            case WEALTH ->
                    "재물 흐름을 살펴볼게요.";

            case CAREER ->
                    "직업과 사업 쪽 흐름을 볼게요.";

            case LEGAL_RISK ->
                    "분쟁이나 주의가 필요한 흐름도 확인해 볼게요.";

            case HEALTH_TENDENCY ->
                    "건강은 질병을 예측하는 방식이 아니라, 관리 경향 중심으로 볼게요.";

            case CURRENT_YEAR_LUCK ->
                    "이제 현재와 올해의 흐름을 볼게요.";

            case FUTURE_LUCK ->
                    "마지막으로 앞으로 이어질 큰 흐름을 살펴볼게요.";
        };
    }

    /**
     * InterpretationSignal의 방향을
     * 일반 사용자가 이해할 수 있는
     * 기본 문장으로 변환한다.
     */
    public static String summaryFor(
            InterpretationSignal signal
    ) {

        Objects.requireNonNull(
                signal,
                "해석 신호는 필수입니다."
        );

        InterpretationPolarity polarity =
                signal.polarity();

        InterpretationIntensity intensity =
                signal.intensity();

        if (
                intensity
                        == InterpretationIntensity.NONE
                        || polarity
                        == InterpretationPolarity.NEUTRAL
        ) {

            return "현재 계산된 근거만으로는 "
                    + "이 분야를 한쪽 방향으로 강하게 단정하기보다 "
                    + "기본적인 성향과 상황을 함께 보는 편이 좋아요.";
        }

        return switch (polarity) {

            case SUPPORTIVE ->
                    supportiveSummary(
                            intensity
                    );

            case CAUTION ->
                    cautionSummary(
                            intensity
                    );

            case MIXED ->
                    mixedSummary(
                            intensity
                    );

            case NEUTRAL ->
                    "이 분야는 길흉보다 기본적인 성향과 구조를 중심으로 보는 것이 좋아요.";
        };
    }

    private static String supportiveSummary(
            InterpretationIntensity intensity
    ) {

        return switch (intensity) {

            case HIGH ->
                    "도움으로 활용할 수 있는 신호가 여러 겹으로 들어와 있어요. "
                            + "기회를 잘 잡으면 흐름을 적극적으로 활용하기 좋은 편이에요.";

            case MEDIUM ->
                    "도움이 되는 흐름이 비교적 분명하게 보이고 있어요. "
                            + "무리하지 않는 범위에서 기회를 활용해 볼 만해요.";

            case LOW ->
                    "작지만 도움이 되는 방향의 신호가 보여요. "
                            + "큰 변화보다는 작은 기회를 잘 활용하는 편이 좋아요.";

            case NONE ->
                    "현재는 뚜렷한 방향을 단정하기 어려워요.";
        };
    }

    private static String cautionSummary(
            InterpretationIntensity intensity
    ) {

        return switch (intensity) {

            case HIGH ->
                    "변동이나 부담으로 작용할 수 있는 신호가 여러 겹 겹쳐 있어요. "
                            + "서두르기보다 조건을 확인하면서 움직이는 편이 좋아요.";

            case MEDIUM ->
                    "주의해서 살펴볼 요소가 비교적 분명하게 보여요. "
                            + "결정을 내릴 때 한 번 더 확인하는 과정이 필요해요.";

            case LOW ->
                    "크지는 않지만 신경 써야 할 요소가 있어요. "
                            + "미리 점검하면 부담을 줄일 수 있는 수준으로 볼 수 있어요.";

            case NONE ->
                    "현재는 뚜렷한 주의 신호를 단정하기 어려워요.";
        };
    }

    private static String mixedSummary(
            InterpretationIntensity intensity
    ) {

        return switch (intensity) {

            case HIGH ->
                    "기회와 주의 신호가 모두 강하게 들어와 있어요. "
                            + "움직임이 커질 수 있는 만큼 무엇을 선택하느냐가 중요해지는 흐름이에요.";

            case MEDIUM ->
                    "좋게 활용할 수 있는 부분과 조심해야 할 부분이 함께 보여요. "
                            + "한쪽으로 단정하기보다 상황에 맞춰 선택하는 것이 중요해요.";

            case LOW ->
                    "서로 다른 방향의 신호가 조금씩 함께 있어요. "
                            + "큰 결론보다는 현재 상황을 보면서 조정하는 편이 좋아요.";

            case NONE ->
                    "현재는 특정 방향을 강하게 판단하기 어려워요.";
        };
    }

    /**
     * 상담 분야와 방향을 이용해
     * 현실적인 기본 조언을 생성한다.
     */
    public static String guidanceFor(
            ConsultationCategory category,
            InterpretationPolarity polarity
    ) {

        Objects.requireNonNull(
                category,
                "상담 분야는 필수입니다."
        );

        Objects.requireNonNull(
                polarity,
                "상담 방향은 필수입니다."
        );

        return switch (category) {

            case PERSONALITY ->
                    "자신의 장점을 살리되, 반복해서 나타나는 반응 방식은 상황에 맞게 조절해 보는 것이 좋아요.";

            case PAST_LUCK ->
                    "지나온 경험에서 반복됐던 선택과 관계 패턴을 돌아보면 앞으로의 판단에 도움이 될 수 있어요.";

            case SOCIAL_RELATIONSHIP ->
                    polarity
                            == InterpretationPolarity.CAUTION
                            ? "사람 사이에서는 말의 속도보다 확인과 조율을 먼저 두는 편이 좋아요."
                            : "혼자 해결하려 하기보다 좋은 인연과 협력 관계를 잘 활용해 보세요.";

            case LOVE_STYLE ->
                    "상대의 마음을 추측하기보다 원하는 관계 방식과 감정을 직접 확인하는 것이 중요해요.";

            case PARTNER_LUCK ->
                    polarity
                            == InterpretationPolarity.CAUTION
                            ? "인연을 서두르기보다 가치관과 생활 방식이 맞는지 충분히 살펴보는 편이 좋아요."
                            : "새로운 만남에서도 겉으로 보이는 조건보다 관계가 안정적으로 이어질 수 있는지를 살펴보세요.";

            case WEALTH ->
                    polarity
                            == InterpretationPolarity.CAUTION
                            ? "큰 지출이나 투자 판단은 분위기에 휩쓸리지 말고 금액과 조건을 먼저 확인해 보세요."
                            : "재물 기회가 보여도 수입과 지출의 균형을 함께 관리하면 흐름을 더 안정적으로 활용할 수 있어요.";

            case CAREER ->
                    polarity
                            == InterpretationPolarity.CAUTION
                            ? "직업이나 사업에서는 급하게 방향을 바꾸기보다 현재 조건과 위험을 먼저 정리해 보세요."
                            : "기회가 왔을 때 바로 움직일 수 있도록 실력과 준비를 쌓아두는 것이 좋아요.";

            case LEGAL_RISK ->
                    "계약이나 금전 약속처럼 기록이 중요한 일은 말로만 처리하지 말고 내용을 확인하고 남겨두는 습관이 좋아요.";

            case HEALTH_TENDENCY ->
                    "이 부분은 질병을 의미하지 않아요. 수면, 식사, 운동, 휴식 같은 생활 리듬을 꾸준히 관리하는 참고 자료로만 활용해 주세요.";

            case CURRENT_YEAR_LUCK ->
                    polarity
                            == InterpretationPolarity.CAUTION
                            ? "올해는 속도보다 타이밍과 확인 과정을 중요하게 두는 편이 좋아요."
                            : "올해 들어오는 기회를 활용하되 한 번에 너무 많은 것을 벌이기보다 우선순위를 정해 보세요.";

            case FUTURE_LUCK ->
                    "큰 흐름은 하루아침에 바뀌기보다 단계적으로 나타날 수 있으니 장기적인 준비 방향을 잡는 데 활용해 보세요.";
        };
    }

    /**
     * Evidence 신뢰도를 종합한다.
     *
     * 보수적으로:
     *
     * LOW가 하나라도 있으면 LOW
     *
     * 전부 HIGH이면 HIGH
     *
     * 나머지는 MEDIUM
     *
     * 근거 자체가 없으면 LOW
     */
    public static AnalysisConfidence
    determineConfidence(
            List<InterpretationEvidence> evidences
    ) {

        Objects.requireNonNull(
                evidences,
                "Evidence 목록은 필수입니다."
        );

        if (evidences.isEmpty()) {

            return AnalysisConfidence.LOW;
        }

        boolean hasLow =
                evidences
                        .stream()
                        .anyMatch(
                                evidence ->
                                        evidence.confidence()
                                                == AnalysisConfidence.LOW
                        );

        if (hasLow) {

            return AnalysisConfidence.LOW;
        }

        boolean allHigh =
                evidences
                        .stream()
                        .allMatch(
                                evidence ->
                                        evidence.confidence()
                                                == AnalysisConfidence.HIGH
                        );

        if (allHigh) {

            return AnalysisConfidence.HIGH;
        }

        return AnalysisConfidence.MEDIUM;
    }
}