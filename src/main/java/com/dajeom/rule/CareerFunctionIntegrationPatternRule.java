package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainSignalStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CAREER 카테고리의
 * 원국 직업 기능 교차 연결 Pattern Rule.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * CareerCoreRoleStructureRule과
 * 다른 질문을 담당한다.
 *
 * CareerCoreRoleStructureRule:
 *
 * - CAREER_PRIMARY_OFFICER_STRUCTURE
 * - CAREER_OFFICER_STRUCTURE
 *
 * 를 결합하여
 *
 * "관성이 원국의 역할·책임 중심축으로
 * 실제 성립하며 어떻게 작동하는가"
 *
 * 를 판단한다.
 *
 * -------------------------------------------------
 *
 * 이 Rule:
 *
 * CAREER_CROSS_VALIDATED_PATTERN에서
 * 이미 교차검증된 직업·사회 관련
 * 십성 Pattern Family를 사용하여
 *
 * "표현·산출·자원·역할·책임·학습 기능이
 * 원국 안에서 서로 어떤 방향으로
 * 연결되거나 긴장하는가"
 *
 * 를 별도의 CAREER 축으로 판단한다.
 *
 * =================================================
 *
 * 기존 Source가 포함할 수 있는 구조
 * =================================================
 *
 * CAREER_CROSS_VALIDATED_PATTERN은
 * 기존 18차에서:
 *
 * - 식신제살
 * - 상관견관
 * - 정관생인
 * - 칠살생인
 * - 정관·칠살 혼재
 * - 재생정관
 *
 * 및:
 *
 * - 식상 -> 관살
 * - 재성 -> 관살
 * - 관살 -> 인성
 * - 식상 -> 재성 -> 관살 -> 인성
 *
 * 등의 Family Chain을
 * 이미 계산한 결과다.
 *
 * =================================================
 *
 * Upstream 교차검증
 * =================================================
 *
 * 해당 Source는 기존 10차에서:
 *
 * - 핵심 십성 Pattern
 * - 격국
 * - 신강신약
 * - 용신
 * - 합화
 *
 * 등을 이미 교차검증한 상태를 사용한다.
 *
 * 따라서 이 Rule에서는
 * 위 내용을 다시 계산하지 않는다.
 *
 * =================================================
 *
 * 매우 중요한 제한
 * =================================================
 *
 * LifeDomainRuleMatch에는 현재:
 *
 * "이 Match가 정확히 식신제살인지"
 * "상관견관인지"
 * "정관생인인지"
 *
 * 를 나타내는 별도의 구조화된
 * patternType 필드가 없다.
 *
 * 따라서 이 Rule은:
 *
 * - summary 문자열
 * - reasons 문자열
 *
 * 을 파싱하여
 * 개별 Pattern을 재분류하지 않는다.
 *
 * 공식적으로 사용할 수 있는:
 *
 * - domain
 * - ruleCode
 * - status
 *
 * 만 사용한다.
 *
 * =================================================
 *
 * Status 의미
 * =================================================
 *
 * SUPPORTIVE
 *
 * ->
 *
 * 교차검증된 직업·사회 Pattern Family에서
 * 지지 방향이 확인되고
 * 이를 뒤집는 부담 방향이 없음.
 *
 * -------------------------------------------------
 *
 * PRESSURE
 *
 * ->
 *
 * 역할·규범과 표현·산출 등의 기능 사이에서
 * 교차검증된 부담 방향이 존재함.
 *
 * -------------------------------------------------
 *
 * MIXED
 *
 * ->
 *
 * 이미 MIXED Pattern이 존재하거나
 * SUPPORTIVE와 PRESSURE가
 * 동시에 존재함.
 *
 * -------------------------------------------------
 *
 * CONTEXTUAL
 *
 * ->
 *
 * 연결 구조는 존재하지만
 * 공식 근거만으로 방향을
 * 지지 또는 부담으로 확정할 수 없음.
 *
 * =================================================
 *
 * Match 개수
 * =================================================
 *
 * Pattern Match의 개수를 세어서:
 *
 * - 직업운이 강하다
 * - 직업 능력이 높다
 * - 성공 가능성이 높다
 * - 사업가 기질이 강하다
 *
 * 같은 점수를 만들지 않는다.
 *
 * 개수 비교가 아니라
 * 공식 Status의 존재 조합만 사용한다.
 *
 * =================================================
 *
 * 금지
 * =================================================
 *
 * 이 Rule은:
 *
 * - 취업한다
 * - 승진한다
 * - 퇴사한다
 * - 이직한다
 * - 창업한다
 * - 사업에 성공한다
 * - 조직에서 갈등한다
 *
 * 같은 실제 사건을 확정하지 않는다.
 *
 * 또한:
 *
 * 식신제살 -> 특정 직업
 * 정관생인 -> 공무원
 * 칠살생인 -> 군경
 *
 * 같은 직업 대응도 하지 않는다.
 */
@Component
public class CareerFunctionIntegrationPatternRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_FUNCTION_INTEGRATION_PATTERN";

    private static final String
            CROSS_VALIDATED_PATTERN_RULE_CODE =
            "CAREER_CROSS_VALIDATED_PATTERN";

    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.CAREER;
    }

    @Override
    public String code() {

        return RULE_CODE;
    }

    @Override
    public List<ConsultationCategoryDeterministicMatch>
    evaluate(
            ConsultationCategoryEvidenceView view
    ) {

        Objects.requireNonNull(
                view,
                "CareerFunctionIntegrationPatternRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category 검증
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerFunctionIntegrationPatternRule은 CAREER View만 평가할 수 있습니다."
            );
        }

        /*
         * =================================================
         * unavailable View
         * =================================================
         */

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 핵심 Source
         * =================================================
         *
         * CAREER_CROSS_VALIDATED_PATTERN만 사용한다.
         *
         * 이번 Rule은
         * 관성 중심성 자체를 다시 판단하지 않는다.
         */

        List<LifeDomainRuleMatch>
                patternMatches =
                findNatalMatches(
                        view,
                        CROSS_VALIDATED_PATTERN_RULE_CODE
                );

        /*
         * 교차검증된 직업 Pattern이 없다면
         * 이번 질문에 답할 근거가 없다.
         */

        if (
                patternMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Pattern Family Status
         * =================================================
         */

        ConsultationCategoryDeterministicStatus
                finalStatus =
                resolvePatternStatus(
                        patternMatches
                );

        /*
         * =================================================
         * Reasons
         * =================================================
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "직업·사회 영역에서 이미 교차검증된 십성 Pattern들을 하나의 사건으로 재해석하지 않고, 표현·산출·자원·역할·책임·학습 기능 사이의 구조적 연결 상태를 별도 CAREER 축으로 확인했습니다."
        );

        reasons.add(
                "CAREER_CROSS_VALIDATED_PATTERN 내부의 개별 Pattern subtype은 현재 LifeDomainRuleMatch의 별도 구조 필드로 노출되지 않으므로 summary 또는 reasons 문자열을 파싱해 재분류하지 않았습니다."
        );

        appendStatusReason(
                reasons,
                finalStatus
        );

        reasons.add(
                "Pattern Match 개수의 많고 적음을 직업 능력, 성취 규모, 승진 가능성, 사업 성공 가능성 또는 사회적 지위 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과는 원국의 직업·사회 기능들이 서로 연결되거나 긴장하는 구조를 설명하며 취업, 승진, 이직, 퇴사, 창업, 사업 성공 같은 실제 사건을 확정하지 않습니다."
        );

        /*
         * =================================================
         * Result
         * =================================================
         */

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory.CAREER,

                        ConsultationCategoryDeterministicLayer
                                .NATAL,

                        RULE_CODE,

                        finalStatus,

                        null,

                        null,

                        List.of(),

                        summaryOf(
                                finalStatus
                        ),

                        List.copyOf(
                                patternMatches
                        ),

                        List.copyOf(
                                reasons
                        )
                )
        );
    }

    /*
     * =================================================
     * Natal Source 조회
     * =================================================
     *
     * 공식 구조값:
     *
     * - domain
     * - ruleCode
     *
     * 로만 Source Family를 선택한다.
     *
     * summary / reasons 문자열은
     * Source 선택이나 Pattern 분류에
     * 사용하지 않는다.
     */

    private List<LifeDomainRuleMatch>
    findNatalMatches(

            ConsultationCategoryEvidenceView view,

            String sourceRuleCode

    ) {

        Objects.requireNonNull(
                view,
                "ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                sourceRuleCode == null
                        || sourceRuleCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "CAREER Source ruleCode가 필요합니다."
            );
        }

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : view.natalMatches()
        ) {

            Objects.requireNonNull(
                    match,
                    "CAREER Natal Match는 null일 수 없습니다."
            );

            /*
             * 다른 LifeDomain Evidence는
             * 이번 Rule에 사용하지 않는다.
             */

            if (
                    match.domain()
                            != LifeDomain.CAREER
            ) {

                continue;
            }

            /*
             * 공식 Rule Code로만
             * Source Family를 확인한다.
             */

            if (
                    !sourceRuleCode.equals(
                            match.ruleCode()
                    )
            ) {

                continue;
            }

            result.add(
                    match
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Pattern Family Status
     * =================================================
     *
     * Match 수를 비교하지 않는다.
     *
     * 해당 Family 내부에
     * 어떤 공식 Status가 존재하는지만 본다.
     *
     * -------------------------------------------------
     *
     * MIXED 존재
     * -> MIXED
     *
     * SUPPORTIVE + PRESSURE
     * -> MIXED
     *
     * PRESSURE 존재
     * -> PRESSURE
     *
     * SUPPORTIVE 존재
     * -> SUPPORTIVE
     *
     * 나머지
     * -> CONTEXTUAL
     */

    private ConsultationCategoryDeterministicStatus
    resolvePatternStatus(
            List<LifeDomainRuleMatch> matches
    ) {

        Objects.requireNonNull(
                matches,
                "CAREER Pattern Match 목록이 필요합니다."
        );

        boolean supportiveExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.MIXED
                );

        /*
         * 이미 상류 Pattern이
         * MIXED를 확정한 경우
         * 그대로 보존한다.
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * 서로 다른 직업 기능에서
         * SUPPORTIVE와 PRESSURE가
         * 동시에 확인되면
         * 한쪽으로 단순화하지 않는다.
         */

        if (
                supportiveExists
                        && pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        if (
                pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        if (
                supportiveExists
        ) {

            return ConsultationCategoryDeterministicStatus.SUPPORTIVE;
        }

        return ConsultationCategoryDeterministicStatus.CONTEXTUAL;
    }

    /*
     * =================================================
     * Status 존재 여부
     * =================================================
     */

    private boolean hasStatus(

            List<LifeDomainRuleMatch> matches,

            LifeDomainSignalStatus status

    ) {

        Objects.requireNonNull(
                matches,
                "CAREER Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "LifeDomainSignalStatus가 필요합니다."
        );

        return matches
                .stream()
                .anyMatch(
                        match ->
                                match.status()
                                        == status
                );
    }

    /*
     * =================================================
     * Status Reason
     * =================================================
     */

    private void appendStatusReason(

            List<String> reasons,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                reasons,
                "reasons가 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "CAREER deterministic status가 필요합니다."
        );

        switch (
                status
        ) {

            case SUPPORTIVE ->

                    reasons.add(
                            "교차검증된 직업·사회 Pattern Family에서 지지 방향이 확인되고 이를 뒤집는 PRESSURE 또는 MIXED 상태가 없어 SUPPORTIVE로 판단합니다."
                    );

            case PRESSURE ->

                    reasons.add(
                            "교차검증된 직업·사회 Pattern Family에서 역할·규범과 표현·산출 기능 사이의 부담 방향이 공식 PRESSURE로 확인되어 이를 보존합니다."
                    );

            case MIXED ->

                    reasons.add(
                            "교차검증된 직업·사회 Pattern Family에 이미 MIXED가 존재하거나 SUPPORTIVE와 PRESSURE가 함께 존재하여 한 방향으로 단순화하지 않고 MIXED로 판단합니다."
                    );

            case CONTEXTUAL ->

                    reasons.add(
                            "직업·사회 기능 사이의 연결 구조는 확인되지만 현재 공식 status만으로 지지 또는 부담 방향을 확정할 수 없어 CONTEXTUAL로 유지합니다."
                    );
        }
    }

    /*
     * =================================================
     * Internal Summary
     * =================================================
     *
     * 최종 사용자용 storyText가 아니다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "CAREER deterministic status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "원국에서 표현·산출·자원·역할·학습 기능이 직업·사회 영역에서 비교적 지지되는 방향으로 연결됩니다.";

            case PRESSURE ->
                    "원국의 직업·사회 기능 연결에서 역할·규범과 표현·산출 또는 다른 기능 사이의 구조적 부담이 확인됩니다.";

            case MIXED ->
                    "원국의 직업·사회 기능 연결에서 지지되는 흐름과 부담 또는 복수의 작동 방식이 함께 존재합니다.";

            case CONTEXTUAL ->
                    "원국에서 표현·산출·자원·역할·학습 기능 사이의 연결은 확인되지만 현재 근거만으로 직업·사회 구조의 방향을 단정하지 않습니다.";
        };
    }
}