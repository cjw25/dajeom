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
 * 원국 핵심 역할·책임 구조 Rule.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * 기존 LifeDomain.CAREER 결과를
 * ConsultationCategory.CAREER에
 * 그대로 복사하지 않는다.
 *
 * 이 Rule은 서로 다른 두 deterministic 축:
 *
 * 1.
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 *
 * 2.
 * CAREER_OFFICER_STRUCTURE
 *
 * 를 함께 사용하여:
 *
 * "정관/칠살의 역할·책임·규범 기능이
 * 원국의 중심 구조로 실제 관여하고 있으며,
 * 그 관살 기능이 원국 전체에서
 * 어떤 방식으로 작동하고 있는가"
 *
 * 를 CAREER 카테고리 관점에서
 * 다시 판단한다.
 *
 * =================================================
 *
 * Source 1
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 * =================================================
 *
 * 5차 StructureAnalysis에서
 * 실제 primaryStandardTenGod가:
 *
 * - DIRECT_OFFICER
 * - SEVEN_KILLINGS
 *
 * 인 경우에만 생성된다.
 *
 * 따라서 단순히 원국에
 * 정관/칠살이 존재한다는 의미가 아니다.
 *
 * 원국의 실제 주격 중심축에서:
 *
 * - 역할
 * - 책임
 * - 규범
 * - 통제
 * - 요구
 * - 사회적 역할 수행
 *
 * 기능이 어느 정도 중심적으로
 * 관여하는지를 나타낸다.
 *
 * 이 Source는:
 *
 * - 성립
 * - 부담
 * - 손상
 * - 혼잡
 *
 * 상태를 이미 계산한 결과다.
 *
 * =================================================
 *
 * Source 2
 * CAREER_OFFICER_STRUCTURE
 * =================================================
 *
 * TenGodCompositeAnalysis
 * +
 * DayMasterStrengthAnalysis
 *
 * 에서 이미 계산된:
 *
 * - 관성 발달
 * - 정관 중심
 * - 칠살 중심
 * - 관살 혼재
 * - 신약 관살 압박
 * - 식상의 관살 대응
 * - 인성의 관살 중재
 * - 재성의 관살 지원
 *
 * 등의 복합 Context를 사용한다.
 *
 * =================================================
 *
 * 중요한 역할 분리
 * =================================================
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 *
 * ->
 *
 * "관성이 원국의 중심 역할 구조인가?"
 *
 * -------------------------------------------------
 *
 * CAREER_OFFICER_STRUCTURE
 *
 * ->
 *
 * "그 관살 기능이 실제 원국 전체에서
 * 어떤 수용·압박·중재 Context를 가지는가?"
 *
 * -------------------------------------------------
 *
 * 따라서 같은 관성을 다루더라도
 * 두 Source는 서로 대체하지 않는다.
 *
 * =================================================
 *
 * Match 생성 조건
 * =================================================
 *
 * 두 Source Family가
 * 모두 실제로 존재해야 한다.
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE가 없는데:
 *
 * "관성이 원국의 핵심 직업 중심축이다"
 *
 * 라고 추정하지 않는다.
 *
 * CAREER_OFFICER_STRUCTURE가 없는데:
 *
 * 관살의 실제 작동 상태를
 * 임의 생성하지 않는다.
 *
 * =================================================
 *
 * Source Family Status
 * =================================================
 *
 * 각 Source Family 내부에서는
 * Match 개수를 사용하지 않는다.
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
 * 그 외
 * -> CONTEXTUAL
 *
 * =================================================
 *
 * CAREER 전용 결합
 * =================================================
 *
 * 중심 역할 구조 SUPPORTIVE
 * +
 * 관살 작동 CONTEXTUAL
 * -> SUPPORTIVE
 *
 * 중심 역할 구조는 실제 성립되어 있고,
 * 복합 관살 Context에서 이를 뒤집는
 * 부담 방향이 확인되지 않은 상태다.
 *
 * -------------------------------------------------
 *
 * 중심 역할 구조 SUPPORTIVE
 * +
 * 관살 작동 PRESSURE
 * -> MIXED
 *
 * 중심 역할 구조는 성립하지만
 * 실제 역할 수행 과정에는
 * 수용 부담이 존재한다.
 *
 * -------------------------------------------------
 *
 * 중심 역할 구조 PRESSURE
 * +
 * 관살 작동 SUPPORTIVE
 * -> MIXED
 *
 * 중심 구조 자체에는 부담이 있으나
 * 복합 작동에서 지지 방향이 존재한다.
 *
 * -------------------------------------------------
 *
 * 중심 역할 구조 PRESSURE
 * +
 * 관살 작동 CONTEXTUAL/PRESSURE
 * -> PRESSURE
 *
 * -------------------------------------------------
 *
 * 중심 역할 구조 CONTEXTUAL
 * +
 * 관살 작동 PRESSURE
 * -> PRESSURE
 *
 * -------------------------------------------------
 *
 * 중심 역할 구조 CONTEXTUAL
 * +
 * 관살 작동 SUPPORTIVE
 * -> CONTEXTUAL
 *
 * 복합 작동의 SUPPORTIVE만으로
 * 관성이 원국의 중심 역할 구조로
 * 성립했다고 승격하지 않는다.
 *
 * =================================================
 *
 * 매우 중요한 제한
 * =================================================
 *
 * 이 Rule은:
 *
 * - 관성 세력을 다시 계산하지 않는다.
 * - 신강신약을 다시 계산하지 않는다.
 * - 격국을 다시 계산하지 않는다.
 * - 십성 Pattern을 다시 계산하지 않는다.
 * - Match 개수를 점수화하지 않는다.
 * - summary 문자열을 파싱하지 않는다.
 * - reasons 문자열을 파싱하지 않는다.
 *
 * =================================================
 *
 * 금지
 * =================================================
 *
 * SUPPORTIVE
 * -> 취업 성공
 *
 * SUPPORTIVE
 * -> 승진
 *
 * PRESSURE
 * -> 실직
 *
 * PRESSURE
 * -> 직장 갈등 발생
 *
 * 정관
 * -> 공무원
 *
 * 칠살
 * -> 군인/경찰
 *
 * 같은 실제 직업 사건이나
 * 특정 직종을 생성하지 않는다.
 */
@Component
public class CareerCoreRoleStructureRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_CORE_ROLE_STRUCTURE";

    private static final String
            PRIMARY_OFFICER_STRUCTURE_RULE_CODE =
            "CAREER_PRIMARY_OFFICER_STRUCTURE";

    private static final String
            OFFICER_STRUCTURE_RULE_CODE =
            "CAREER_OFFICER_STRUCTURE";

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
                "CareerCoreRoleStructureRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CareerCoreRoleStructureRule은 CAREER View만 평가할 수 있습니다."
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
         * Source 1
         * 주격 관성 중심 구조
         * =================================================
         */

        List<LifeDomainRuleMatch>
                primaryStructureMatches =
                findNatalMatches(
                        view,
                        PRIMARY_OFFICER_STRUCTURE_RULE_CODE
                );

        /*
         * =================================================
         * Source 2
         * 관살 실제 복합 작동 구조
         * =================================================
         */

        List<LifeDomainRuleMatch>
                officerStructureMatches =
                findNatalMatches(
                        view,
                        OFFICER_STRUCTURE_RULE_CODE
                );

        /*
         * =================================================
         * 두 Source Family 모두 필수
         * =================================================
         *
         * 주격이 관성이 아닌데
         * "관성이 직업 중심축"
         * 이라고 생성하지 않는다.
         *
         * 또한 관살 복합 분석이 없는데
         * 실제 역할 수행 상태를
         * 추정하지 않는다.
         */

        if (
                primaryStructureMatches.isEmpty()
                        || officerStructureMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Source Family별 상태
         * =================================================
         */

        ConsultationCategoryDeterministicStatus
                primaryStructureStatus =
                resolveFamilyStatus(
                        primaryStructureMatches
                );

        ConsultationCategoryDeterministicStatus
                officerStructureStatus =
                resolveFamilyStatus(
                        officerStructureMatches
                );

        /*
         * =================================================
         * CAREER 전용 최종 결합
         * =================================================
         */

        ConsultationCategoryDeterministicStatus
                finalStatus =
                resolveCombinedStatus(
                        primaryStructureStatus,
                        officerStructureStatus
                );

        /*
         * =================================================
         * Source Provenance
         * =================================================
         */

        List<LifeDomainRuleMatch> sourceMatches =
                new ArrayList<>();

        sourceMatches.addAll(
                primaryStructureMatches
        );

        sourceMatches.addAll(
                officerStructureMatches
        );

        /*
         * =================================================
         * Reasons
         * =================================================
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "정관·칠살이 원국의 주격 중심 구조로 관여하는 상태와 관살 기능의 실제 복합 작동 상태를 서로 다른 deterministic 축으로 확인했습니다."
        );

        reasons.add(
                "CAREER_PRIMARY_OFFICER_STRUCTURE Source Family의 상태는 "
                        + primaryStructureStatus.name()
                        + "입니다."
        );

        reasons.add(
                "CAREER_OFFICER_STRUCTURE Source Family의 상태는 "
                        + officerStructureStatus.name()
                        + "입니다."
        );

        appendCombinationReason(
                reasons,
                primaryStructureStatus,
                officerStructureStatus,
                finalStatus
        );

        reasons.add(
                "관성의 존재량이나 Match 개수를 직업운의 강도, 직위, 성취 가능성 또는 직업 안정성 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "정관과 칠살을 특정 직업군에 직접 대응시키지 않았습니다."
        );

        reasons.add(
                "이 결과는 원국의 역할·책임 중심축과 관살 기능의 수용·압박·중재 구조를 설명하며 취업, 승진, 이직, 퇴사, 사업 성공 같은 실제 사건을 확정하지 않습니다."
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
                                sourceMatches
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
     * 만 사용한다.
     *
     * summary / reasons 문자열은
     * Source 구분에 사용하지 않는다.
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
             * 다른 Domain의 Evidence를
             * 이번 Rule에 끌어오지 않는다.
             */

            if (
                    match.domain()
                            != LifeDomain.CAREER
            ) {

                continue;
            }

            /*
             * 공식 Rule Code 기준으로만
             * Source Family를 구분한다.
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
     * Source Family Status
     * =================================================
     *
     * Match 개수를 세지 않는다.
     *
     * Family 내부에
     * 어떤 공식 LifeDomainSignalStatus가
     * 존재하는지만 본다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveFamilyStatus(
            List<LifeDomainRuleMatch> matches
    ) {

        Objects.requireNonNull(
                matches,
                "CAREER Source Match 목록이 필요합니다."
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
         * 이미 Source가 MIXED를
         * 확정했다면 그대로 보존한다.
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * 같은 Family 안에
         * SUPPORTIVE와 PRESSURE가
         * 함께 존재해도 MIXED다.
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
     * CAREER 전용 결합 Matrix
     * =================================================
     *
     * primaryStructureStatus
     *
     * =
     *
     * 관성이 원국 주격의
     * 중심 역할·책임 구조로
     * 어떤 상태에 있는가.
     *
     * -------------------------------------------------
     *
     * officerStructureStatus
     *
     * =
     *
     * 관살 기능이 실제 원국 전체에서
     * 어떤 수용·압박·중재 Context로
     * 작동하는가.
     */

    private ConsultationCategoryDeterministicStatus
    resolveCombinedStatus(

            ConsultationCategoryDeterministicStatus
                    primaryStructureStatus,

            ConsultationCategoryDeterministicStatus
                    officerStructureStatus

    ) {

        Objects.requireNonNull(
                primaryStructureStatus,
                "CAREER primaryStructureStatus가 필요합니다."
        );

        Objects.requireNonNull(
                officerStructureStatus,
                "CAREER officerStructureStatus가 필요합니다."
        );

        /*
         * =================================================
         * 어느 한 축이 이미 MIXED
         * =================================================
         */

        if (
                primaryStructureStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
                        || officerStructureStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * =================================================
         * 주격 관성 구조 SUPPORTIVE
         * =================================================
         */

        if (
                primaryStructureStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            /*
             * 중심 역할 구조는 성립하지만
             * 실제 관살 작동에는
             * 부담이 확인된다.
             */

            if (
                    officerStructureStatus
                            == ConsultationCategoryDeterministicStatus.PRESSURE
            ) {

                return ConsultationCategoryDeterministicStatus.MIXED;
            }

            /*
             * 관살 복합구조가
             * SUPPORTIVE 또는 CONTEXTUAL이고
             * 별도의 부담이 없으면
             * 주격의 성립 방향을 보존한다.
             */

            return ConsultationCategoryDeterministicStatus.SUPPORTIVE;
        }

        /*
         * =================================================
         * 주격 관성 구조 PRESSURE
         * =================================================
         */

        if (
                primaryStructureStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            /*
             * 중심 구조 부담과
             * 실제 관살 작동의 지지 방향이
             * 동시에 존재하면 MIXED.
             */

            if (
                    officerStructureStatus
                            == ConsultationCategoryDeterministicStatus.SUPPORTIVE
            ) {

                return ConsultationCategoryDeterministicStatus.MIXED;
            }

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        /*
         * =================================================
         * 주격 관성 구조 CONTEXTUAL
         * =================================================
         */

        if (
                officerStructureStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            /*
             * 주격 방향은 확정되지 않았지만
             * 실제 관살 수용 부담은
             * 공식적으로 확인되어 있다.
             */

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        /*
         * 관살 복합 작동이 SUPPORTIVE라고 해도
         * 주격 자체가 CONTEXTUAL인 상태를
         * 원국의 핵심 역할구조 SUPPORTIVE로
         * 자동 승격하지 않는다.
         */

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
     * Combination Reason
     * =================================================
     */

    private void appendCombinationReason(

            List<String> reasons,

            ConsultationCategoryDeterministicStatus
                    primaryStructureStatus,

            ConsultationCategoryDeterministicStatus
                    officerStructureStatus,

            ConsultationCategoryDeterministicStatus
                    finalStatus

    ) {

        Objects.requireNonNull(
                reasons,
                "reasons가 필요합니다."
        );

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
        ) {

            reasons.add(
                    "역할·책임의 중심 구조와 실제 관살 작동 구조가 한 방향으로 일치하지 않거나 두 축 중 하나에 이미 혼재가 존재하여 MIXED로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "역할·책임의 중심 구조 또는 실제 관살 수용 구조에서 확인된 부담이 다른 축에 의해 해소된 것으로 볼 공식 근거가 없어 PRESSURE로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "정관·칠살의 역할·책임 기능이 원국의 중심 구조로 비교적 성립되어 있고 실제 관살 작동에서 이를 뒤집는 부담 방향이 확인되지 않아 SUPPORTIVE로 판단합니다."
            );

            return;
        }

        reasons.add(
                "역할·책임 기능이 원국의 중심축으로 관여하고 관살 복합구조도 확인되지만 현재 공식 status만으로 직업·사회 역할 구조의 방향을 한쪽으로 확정하지 않아 CONTEXTUAL로 유지합니다."
        );
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
                    "원국에서 역할·책임 기능이 중심 구조로 비교적 성립되어 있으며 이를 수행하는 관살 구조에서 이를 뒤집는 뚜렷한 부담 방향이 확인되지 않습니다.";

            case PRESSURE ->
                    "원국에서 역할·책임 기능이 중요한 축으로 관여하지만 중심 구조 또는 실제 관살 수용 과정에 부담이 존재합니다.";

            case MIXED ->
                    "원국에서 역할·책임의 중심성은 존재하지만 실제 수용·압박·중재 구조가 한 방향으로 단순화되지 않는 복합적인 직업·사회 구조입니다.";

            case CONTEXTUAL ->
                    "원국에서 정관·칠살의 역할·책임 기능과 관살 복합구조가 관여하지만 현재 근거만으로 직업·사회 역할 구조를 한 방향으로 단정하지 않습니다.";
        };
    }
}