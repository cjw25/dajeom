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
 * CAREER 원국 주격 역할 구조 deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.CAREER에서 이미 계산된:
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 *
 * 의 공식 상태만
 * ConsultationCategory.CAREER의
 * 독립 deterministic 결과로 보존한다.
 *
 * =================================================
 * 공식 Status Anchor
 * =================================================
 *
 * 이 Rule의 공식 Anchor는:
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 *
 * 하나뿐이다.
 *
 * =================================================
 * Upstream 계산
 * =================================================
 *
 * CareerPrimaryOfficerStructureRule은
 * 5차 StructureAnalysis를 사용하여:
 *
 * - 실제 주격이 일반격인지
 * - 실제 주격 십성이 DIRECT_OFFICER인지
 * - 실제 주격 십성이 SEVEN_KILLINGS인지
 * - 주격이 성립 계열인지
 * - 부담을 동반하는지
 * - 손상되어 있는지
 * - 강한 손상이 있는지
 * - 혼잡 상태인지
 *
 * 등을 이미 계산한다.
 *
 * 따라서 이 Category Rule에서는
 * 격국을 다시 계산하지 않는다.
 *
 * =================================================
 * CAREER_OFFICER_STRUCTURE와의 분리
 * =================================================
 *
 * 기존에는:
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 *
 * +
 *
 * CAREER_OFFICER_STRUCTURE
 *
 * 를 결합하여 하나의 finalStatus를 만들었다.
 *
 * 이제 그렇게 하지 않는다.
 *
 * -------------------------------------------------
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 *
 * =
 *
 * 관성이 실제 원국의 주격 중심축으로
 * 선택되었는지와 그 주격 상태
 *
 * -------------------------------------------------
 *
 * CAREER_OFFICER_STRUCTURE
 *
 * =
 *
 * 관살 Family가 원국 전체에서 가지는
 * 발달·압박·중재·지원 Context
 *
 * -------------------------------------------------
 *
 * 두 계산은 서로 다른 deterministic 결과다.
 *
 * =================================================
 * 삭제되는 Cross-Source Status 합성
 * =================================================
 *
 * 다음 계산을 하지 않는다.
 *
 * PRIMARY SUPPORTIVE
 * +
 * OFFICER PRESSURE
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * PRIMARY PRESSURE
 * +
 * OFFICER SUPPORTIVE
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * PRIMARY CONTEXTUAL
 * +
 * OFFICER PRESSURE
 * =
 * PRESSURE
 *
 * 금지.
 *
 * =================================================
 * 이유
 * =================================================
 *
 * 두 Source Family가 각각 존재한다는 사실만으로:
 *
 * - 중심 역할 구조가 실제 관살 작동에 의해
 *   강화된다
 *
 * - 관살 수용 부담이 주격을 약화시킨다
 *
 * - 주격 성립이 관살 부담을 상쇄한다
 *
 * - 두 구조가 함께 작동하여
 *   새로운 직업 상태가 된다
 *
 * 같은 관계를 만들 수 없다.
 *
 * 그런 판단이 필요하면
 * 두 계산 사이의 관계 자체를
 * 별도의 deterministic Java Analysis로
 * 직접 계산해야 한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 동일한:
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 *
 * Source Family 내부의 공식
 * LifeDomainSignalStatus만 사용한다.
 *
 * MIXED 존재
 * -> MIXED
 *
 * SUPPORTIVE + PRESSURE가
 * 같은 Source Family 안에 실제 존재
 * -> MIXED
 *
 * PRESSURE만 존재
 * -> PRESSURE
 *
 * SUPPORTIVE만 존재
 * -> SUPPORTIVE
 *
 * 그 외
 * -> CONTEXTUAL
 *
 * =================================================
 * 중요한 경계
 * =================================================
 *
 * 위 Status 정규화는
 * 서로 다른 계산 결과를 합성하는 것이 아니다.
 *
 * 동일 Source Family 안에서
 * upstream Java가 이미 확정한
 * 공식 상태를 Category 수준으로
 * 보존하는 처리다.
 *
 * =================================================
 * CAREER 의미 경계
 * =================================================
 *
 * SUPPORTIVE
 *
 * != 취업 성공
 * != 승진
 * != 높은 직급
 * != 좋은 직업
 *
 * PRESSURE
 *
 * != 실직
 * != 직장 갈등
 * != 승진 실패
 *
 * MIXED
 *
 * != 직업 기복
 *
 * CONTEXTUAL
 *
 * != 직업운 없음
 *
 * =================================================
 * 특정 직종 생성 금지
 * =================================================
 *
 * DIRECT_OFFICER
 * != 공무원
 *
 * SEVEN_KILLINGS
 * != 군인
 * != 경찰
 * != 법조인
 *
 * 격국을 실제 직종으로 직접 변환하지 않는다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 다음을 생성하지 않는다.
 *
 * - 책임을 맡기 전
 * - 책임을 맡은 뒤
 * - 역할이 반복될수록
 * - 요구가 증가할수록
 * - 조직 안에서 시간이 지나면서
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 작동한다
 * - 먼저 A이고 이후 B가 된다
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 관성 세력 재계산
 * - 정관 / 칠살 재선택
 * - 격국 재계산
 * - 신강신약 재계산
 * - 십성 Composite 재계산
 * - 용신 재계산
 * - 합충형파해 재계산
 * - CAREER_OFFICER_STRUCTURE 조회
 * - 다른 Source Family와 Status 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 실제 직업 사건 생성
 * - 특정 직업군 생성
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 */
@Component
public class CareerCoreRoleStructureRule
        implements ConsultationCategoryDeterministicRule {

    /*
     * =================================================
     * Compatibility
     * =================================================
     *
     * 기존 Contract / Grounding 연결 확인 전까지
     * class명과 Rule Code는 유지한다.
     *
     * 실제 계산 의미는 이제
     * CAREER_PRIMARY_OFFICER_STRUCTURE만 담당한다.
     */

    private static final String RULE_CODE =
            "CAREER_CORE_ROLE_STRUCTURE";

    private static final String
            PRIMARY_OFFICER_STRUCTURE_RULE_CODE =
            "CAREER_PRIMARY_OFFICER_STRUCTURE";


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
         * Category
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
         * unavailable
         * =================================================
         */

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 공식 Status Anchor
         * =================================================
         */

        List<LifeDomainRuleMatch> primaryStructureMatches =
                findNatalMatches(
                        view,
                        PRIMARY_OFFICER_STRUCTURE_RULE_CODE
                );

        /*
         * 실제 주격 관성 구조가 없으면
         * 다른 CAREER Evidence를 대신 사용하여
         * "관성이 원국의 중심 역할 구조다"
         * 라고 생성하지 않는다.
         */

        if (
                primaryStructureMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Status
         * =================================================
         */

        ConsultationCategoryDeterministicStatus status =
                resolvePrimaryStructureStatus(
                        primaryStructureMatches
                );

        /*
         * =================================================
         * Reasons
         * =================================================
         *
         * 최종 사용자 Story가 아니다.
         * deterministic provenance 기록이다.
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "CAREER의 원국 주격 역할 구조는 CAREER_PRIMARY_OFFICER_STRUCTURE Source Family만 공식 Status Anchor로 사용했습니다."
        );

        appendStatusReason(
                reasons,
                primaryStructureMatches,
                status
        );

        reasons.add(
                "CAREER_OFFICER_STRUCTURE는 이 Rule의 Source 또는 Status 계산에 사용하지 않았으며 별도의 CAREER deterministic 결과로 분리합니다."
        );

        reasons.add(
                "주격 관성 구조와 관살 복합 작동 구조의 Status를 결합하여 새로운 SUPPORTIVE·PRESSURE·MIXED 상태를 만들지 않았습니다."
        );

        reasons.add(
                "CAREER_PRIMARY_OFFICER_STRUCTURE Match의 반복 개수를 직업운 강도·사회적 지위·직위·성취 가능성의 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
        );

        reasons.add(
                "DIRECT_OFFICER 또는 SEVEN_KILLINGS 주격을 특정 직업군으로 직접 변환하지 않았습니다."
        );

        reasons.add(
                "이 결과는 정관·칠살 계열이 실제 원국의 주격 중심축으로 관여하는 구조와 그 공식 상태만 보존하며 취업·승진·이직·퇴사 같은 실제 직업 사건을 생성하지 않습니다."
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

                        status,

                        null,

                        null,

                        List.of(),

                        summaryOf(
                                status
                        ),

                        List.copyOf(
                                primaryStructureMatches
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
     * Source 선택에는:
     *
     * - domain
     * - ruleCode
     *
     * 만 사용한다.
     *
     * summary / reasons 문자열을
     * Source 판별에 사용하지 않는다.
     */

    private List<LifeDomainRuleMatch>
    findNatalMatches(

            ConsultationCategoryEvidenceView view,

            String sourceRuleCode

    ) {

        Objects.requireNonNull(
                view,
                "CAREER Evidence View가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        sourceRuleCode,
                        "CAREER Primary Officer Structure sourceRuleCode가 필요합니다."
                );

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : view.natalMatches()
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            match,
                            "CAREER Natal Match는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * CAREER Domain만
             * =================================================
             */

            if (
                    normalized.domain()
                            != LifeDomain.CAREER
            ) {

                continue;
            }

            /*
             * =================================================
             * 공식 Rule Code만
             * =================================================
             */

            if (
                    !normalizedRuleCode.equals(
                            normalized.ruleCode()
                    )
            ) {

                continue;
            }

            result.add(
                    normalized
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Primary Structure Status
     * =================================================
     *
     * 다른 Source Family는
     * 이 메서드에 들어오지 않는다.
     *
     * 오직:
     *
     * CAREER_PRIMARY_OFFICER_STRUCTURE
     *
     * 내부의 공식 Status만 정규화한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolvePrimaryStructureStatus(
            List<LifeDomainRuleMatch> primaryStructureMatches
    ) {

        Objects.requireNonNull(
                primaryStructureMatches,
                "CAREER Primary Officer Structure Match 목록이 필요합니다."
        );

        if (
                primaryStructureMatches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "CAREER Primary Officer Structure Status 계산에는 최소 하나의 Match가 필요합니다."
            );
        }

        boolean supportiveExists =
                hasStatus(
                        primaryStructureMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        primaryStructureMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        primaryStructureMatches,
                        LifeDomainSignalStatus.MIXED
                );

        /*
         * =================================================
         * upstream MIXED 보존
         * =================================================
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * =================================================
         * 동일 Source Family 내부에
         * 두 공식 방향이 모두 실제 존재
         * =================================================
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
                "CAREER Primary Officer Structure Match 목록이 필요합니다."
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
     * Status Provenance
     * =================================================
     */

    private void appendStatusReason(

            List<String> reasons,

            List<LifeDomainRuleMatch> primaryStructureMatches,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                reasons,
                "CAREER reasons가 필요합니다."
        );

        Objects.requireNonNull(
                primaryStructureMatches,
                "CAREER Primary Officer Structure Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "CAREER Primary Officer Structure Status가 필요합니다."
        );

        boolean supportiveExists =
                hasStatus(
                        primaryStructureMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        primaryStructureMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        primaryStructureMatches,
                        LifeDomainSignalStatus.MIXED
                );

        if (
                mixedExists
        ) {

            reasons.add(
                    "CAREER_PRIMARY_OFFICER_STRUCTURE Source Family에 upstream MIXED 상태가 존재하므로 CAREER 주격 역할 구조 Status를 MIXED로 보존합니다."
            );

            return;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            reasons.add(
                    "CAREER_PRIMARY_OFFICER_STRUCTURE Source Family 내부에 SUPPORTIVE와 PRESSURE가 모두 실제 존재하므로 어느 한 방향을 삭제하지 않고 MIXED로 보존합니다."
            );

            return;
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "CAREER_PRIMARY_OFFICER_STRUCTURE Source Family에 PRESSURE 상태가 존재하고 SUPPORTIVE 또는 MIXED 상태가 없어 CAREER 주격 역할 구조 Status를 PRESSURE로 확정합니다."
            );

            return;
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "CAREER_PRIMARY_OFFICER_STRUCTURE Source Family에 SUPPORTIVE 상태가 존재하고 PRESSURE 또는 MIXED 상태가 없어 CAREER 주격 역할 구조 Status를 SUPPORTIVE로 확정합니다."
            );

            return;
        }

        reasons.add(
                "CAREER_PRIMARY_OFFICER_STRUCTURE Source Family는 존재하지만 SUPPORTIVE·PRESSURE·MIXED 방향이 확정되지 않아 CAREER 주격 역할 구조 Status를 CONTEXTUAL로 유지합니다."
        );
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     *
     * 최종 사용자 Story가 아니다.
     *
     * CAREER_OFFICER_STRUCTURE와 연결하지 않는다.
     * 실제 직업 사건으로 변환하지 않는다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "CAREER Primary Officer Structure Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "원국에서 정관·칠살 계열이 주격 역할 구조로 관여하며 해당 구조의 공식 상태가 SUPPORTIVE로 확인됩니다.";

            case PRESSURE ->
                    "원국에서 정관·칠살 계열이 주격 역할 구조로 관여하며 해당 구조의 공식 상태가 PRESSURE로 확인됩니다.";

            case MIXED ->
                    "원국에서 정관·칠살 계열이 주격 역할 구조로 관여하며 해당 구조의 공식 상태가 MIXED로 확인됩니다.";

            case CONTEXTUAL ->
                    "원국에서 정관·칠살 계열이 주격 역할 구조로 관여하며 방향은 CONTEXTUAL로 유지됩니다.";
        };
    }


    /*
     * =================================================
     * Text
     * =================================================
     */

    private static String requireText(

            String value,

            String message

    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}