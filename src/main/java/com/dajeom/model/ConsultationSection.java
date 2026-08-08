package com.dajeom.model;

import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.InterpretationIntensity;
import com.dajeom.type.InterpretationPolarity;

import java.util.List;
import java.util.Objects;

/**
 * 하나의 상담 분야에 대한
 * 사용자용 상담 구조.
 *
 * 예:
 *
 * - 나의 기본 성향
 * - 재물운
 * - 직업·사업운
 * - 현재와 올해의 흐름
 *
 * InterpretationSignal을
 * 실제 화면/TTS에서 사용할 수 있는
 * 상담 구조로 변환한 결과다.
 *
 * 아직 OpenAI가 자연스럽게 다듬은
 * 최종 캐릭터 대사는 아니다.
 */
public record ConsultationSection(

        int order,

        ConsultationCategory category,

        String title,

        InterpretationPolarity polarity,

        InterpretationIntensity intensity,

        AnalysisConfidence confidence,

        /**
         * 해당 분야를 시작할 때
         * 캐릭터가 말할 수 있는 기본 문장.
         */
        String openingMessage,

        /**
         * 현재 명리 신호를
         * 일반 사용자가 이해할 수 있도록
         * 변환한 핵심 요약.
         */
        String summaryMessage,

        /**
         * 현실적인 대응 방향.
         *
         * 미래 사건을 확정하거나
         * 의료/법률 등의 전문적 판단을
         * 대신하지 않는다.
         */
        String guidanceMessage,

        /**
         * 어떤 종류의 명리 근거가
         * 사용됐는지 표시한다.
         *
         * 예:
         *
         * - 운의 십성
         * - 원국·대운·세운 관계
         * - 용신 후보 오행 유입
         */
        List<String> basisLabels,

        /**
         * 실제 사용된 Evidence 개수.
         */
        int evidenceCount

) {

    public ConsultationSection {

        if (order < 1) {

            throw new IllegalArgumentException(
                    "상담 순서는 1 이상이어야 합니다."
            );
        }

        Objects.requireNonNull(
                category,
                "상담 분야는 필수입니다."
        );

        Objects.requireNonNull(
                title,
                "상담 제목은 필수입니다."
        );

        Objects.requireNonNull(
                polarity,
                "상담 방향은 필수입니다."
        );

        Objects.requireNonNull(
                intensity,
                "상담 강도는 필수입니다."
        );

        Objects.requireNonNull(
                confidence,
                "상담 신뢰도는 필수입니다."
        );

        Objects.requireNonNull(
                openingMessage,
                "상담 시작 문장은 필수입니다."
        );

        Objects.requireNonNull(
                summaryMessage,
                "상담 요약 문장은 필수입니다."
        );

        Objects.requireNonNull(
                guidanceMessage,
                "상담 조언 문장은 필수입니다."
        );

        Objects.requireNonNull(
                basisLabels,
                "상담 근거 종류 목록은 필수입니다."
        );

        if (
                title.isBlank()
                        || openingMessage.isBlank()
                        || summaryMessage.isBlank()
                        || guidanceMessage.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "상담 문장은 비어 있을 수 없습니다."
            );
        }

        if (evidenceCount < 0) {

            throw new IllegalArgumentException(
                    "Evidence 개수는 음수일 수 없습니다."
            );
        }

        basisLabels =
                List.copyOf(
                        basisLabels
                );
    }

    public boolean hasEvidence() {

        return evidenceCount > 0;
    }

    /**
     * 기본 TTS용 문자열.
     *
     * 향후 OpenAI 자연어 생성 결과가 들어오면
     * 별도의 narration 필드/모델로 확장할 수 있다.
     */
    public String narrationText() {

        return String.join(
                " ",
                openingMessage,
                summaryMessage,
                guidanceMessage
        );
    }
}