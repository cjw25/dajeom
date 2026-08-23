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
 * LOVE_STYLE 원국 관계망 Anchor deterministic Rule.
 *
 * =================================================
 * 공식 Source Family
 * =================================================
 *
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 *
 * 하나만 사용한다.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LOVE_STYLE에서 참조할 수 있는
 * 원국 관계망 구조의 공식 상태를
 * Category 수준의 독립 Conclusion으로 보존한다.
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * 이 Rule은 하나의 Source Family만 소비한다.
 *
 * 다음 Source는 이 Rule에 결합하지 않는다.
 *
 * - RELATIONSHIP_TEN_GOD_CONTEXT
 * - RELATIONSHIP_SPOUSE_PALACE
 * - RELATIONSHIP_PEACH_BLOSSOM_AUXILIARY
 *
 * 이 Source들이 upstream LifeDomain 계산에서
 * 삭제되는 것은 아니다.
 *
 * 단지 이 LOVE_STYLE Conclusion의
 * 계산 Source가 아닐 뿐이다.
 *
 * =================================================
 * 공식 Status Anchor
 * =================================================
 *
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 *
 * Source Family 내부의
 * 공식 LifeDomainSignalStatus만 사용한다.
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
 * 동일 Source Family 내부의
 * 공식 상태를 Category 수준으로 정규화하는 것과
 *
 * 서로 다른 Source Family를
 * 하나의 연애 성향으로 합성하는 것은
 * 완전히 다른 작업이다.
 *
 * 이 Rule은 후자를 하지 않는다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 이 Rule은 다음을 만들지 않는다.
 *
 * - 관계가 가까워졌을 때
 * - 친밀도가 높아졌을 때
 * - 가까워질수록
 * - 마음이 커질수록
 * - 기대가 커질수록
 * - 역할 요구가 커질수록
 * - 관계가 깊어진 뒤
 * - 마찰이 생긴 뒤
 * - 거리 조절 과정
 * - 처음에는 A, 이후에는 B
 * - A 때문에 B
 * - A가 B로 이어진다
 *
 * 현재 Source에는
 * 이런 관계 단계나 시간순서를
 * 직접 계산한 데이터가 없다.
 *
 * =================================================
 * 행동 추론 금지
 * =================================================
 *
 * 이 Conclusion만으로 다음을 확정하지 않는다.
 *
 * - 먼저 고백하는가
 * - 감정을 직접 표현하는가
 * - 연락을 자주 하는가
 * - 밀고 당기는가
 * - 상대를 확인하는가
 * - 집착하는가
 * - 회피하는가
 *
 * 이런 항목은 해당 행동을 직접 계산하는
 * 별도 deterministic Rule이 있어야 한다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 연애가 잘 된다
 *
 * PRESSURE
 * != 연애가 어렵다
 *
 * MIXED
 * != 연애가 불안정하다
 *
 * CONTEXTUAL
 * != 연애운이 없다
 *
 * 어떤 Status도 다음 사건으로 변환하지 않는다.
 *
 * - 연애 시작
 * - 고백
 * - 재회
 * - 결혼
 * - 이별
 * - 외도
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 십성 재계산
 * - 합충형파해 재계산
 * - 배우자성 선택
 * - 성별 기반 배우자성 추론
 * - 궁위 재계산
 * - 용신 재계산
 * - 다른 Source Family 결합
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 관계 단계 생성
 * - 관계 인과 생성
 * - 관계 순서 생성
 */
@Component
public class LoveStyleClosenessResponseRule
        implements ConsultationCategoryDeterministicRule {

    /*
     * =================================================
     * Compatibility
     * =================================================
     *
     * 기존 Rule Code는 유지한다.
     *
     * 이름에 CLOSENESS_RESPONSE가 남아 있지만
     * 현재 Rule은 친밀도 변화나
     * 전후 반응을 계산하지 않는다.
     *
     * identifier 변경은
     * 전체 Grounding / Contract 참조를
     * 별도로 확인한 뒤 진행한다.
     */

    private static final String RULE_CODE =
            "LOVE_STYLE_CLOSENESS_RESPONSE";

    private static final String
            NATAL_RELATION_OVERLAP_RULE_CODE =
            "RELATIONSHIP_NATAL_RELATION_OVERLAP";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.LOVE_STYLE;
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
                "LoveStyleClosenessResponseRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.LOVE_STYLE
        ) {

            throw new IllegalArgumentException(
                    "LoveStyleClosenessResponseRule은 LOVE_STYLE View만 평가할 수 있습니다."
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
         * 단일 공식 Source Family
         * =================================================
         */

        List<LifeDomainRuleMatch> relationOverlapMatches =
                findNatalMatches(
                        view,
                        NATAL_RELATION_OVERLAP_RULE_CODE
                );

        if (
                relationOverlapMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Status
         * =================================================
         *
         * 오직
         * RELATIONSHIP_NATAL_RELATION_OVERLAP
         * Source Family 내부 상태만 사용한다.
         */

        ConsultationCategoryDeterministicStatus status =
                resolveAnchorStatus(
                        relationOverlapMatches
                );

        /*
         * =================================================
         * Reasons
         * =================================================
         *
         * 사용자 Story가 아니라
         * deterministic provenance 기록이다.
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "LOVE_STYLE의 이 원국 판단은 RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family만 사용했습니다."
        );

        reasons.add(
                statusReason(
                        relationOverlapMatches,
                        status
                )
        );

        reasons.add(
                "RELATIONSHIP_TEN_GOD_CONTEXT·RELATIONSHIP_SPOUSE_PALACE·RELATIONSHIP_PEACH_BLOSSOM_AUXILIARY를 이 Conclusion의 Source로 결합하지 않았습니다."
        );

        reasons.add(
                "다른 Source Family의 Status를 결합하여 새로운 SUPPORTIVE·PRESSURE·MIXED 상태를 만들지 않았습니다."
        );

        reasons.add(
                "Source Match의 반복 개수를 연애 강도·친밀도·매력·연애 가능성 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
        );

        reasons.add(
                "친밀도 상승·관계 진행·기대 증가·역할 증가 같은 가상 조건이나 시간순서를 생성하지 않았습니다."
        );

        reasons.add(
                "이 결과를 고백·연애 시작·결혼·이별·재회 같은 실제 사건으로 변환하지 않습니다."
        );

        /*
         * =================================================
         * Result
         * =================================================
         */

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory.LOVE_STYLE,

                        ConsultationCategoryDeterministicLayer.NATAL,

                        RULE_CODE,

                        status,

                        null,

                        null,

                        List.of(),

                        summaryOf(
                                status
                        ),

                        relationOverlapMatches,

                        List.copyOf(
                                reasons
                        )
                )
        );
    }


    /*
     * =================================================
     * Natal Match 조회
     * =================================================
     */

    private List<LifeDomainRuleMatch>
    findNatalMatches(

            ConsultationCategoryEvidenceView view,

            String sourceRuleCode

    ) {

        Objects.requireNonNull(
                view,
                "LOVE_STYLE Evidence View가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        sourceRuleCode,
                        "LOVE_STYLE Source ruleCode가 필요합니다."
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
                            "LOVE_STYLE Natal Match는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * RELATIONSHIP Domain만
             * =================================================
             */

            if (
                    normalized.domain()
                            != LifeDomain.RELATIONSHIP
            ) {

                continue;
            }

            /*
             * =================================================
             * 정확한 공식 RuleCode만
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
     * Anchor Status
     * =================================================
     *
     * 이 메서드에는
     * 하나의 Source Family만 들어온다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveAnchorStatus(

            List<LifeDomainRuleMatch> anchorMatches

    ) {

        Objects.requireNonNull(
                anchorMatches,
                "LOVE_STYLE Anchor Match 목록이 필요합니다."
        );

        if (
                anchorMatches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "LOVE_STYLE Anchor Status 계산에는 최소 하나의 Match가 필요합니다."
            );
        }

        boolean supportiveExists =
                hasStatus(
                        anchorMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        anchorMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        anchorMatches,
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
         * 동일 Source Family 안에서
         * SUPPORTIVE / PRESSURE 둘 다 실제 존재
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
     * Status 존재
     * =================================================
     */

    private boolean hasStatus(

            List<LifeDomainRuleMatch> matches,

            LifeDomainSignalStatus status

    ) {

        Objects.requireNonNull(
                matches,
                "LOVE_STYLE Match 목록이 필요합니다."
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

    private String statusReason(

            List<LifeDomainRuleMatch> anchorMatches,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                anchorMatches,
                "LOVE_STYLE Anchor Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "LOVE_STYLE deterministic status가 필요합니다."
        );

        boolean supportiveExists =
                hasStatus(
                        anchorMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        anchorMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        anchorMatches,
                        LifeDomainSignalStatus.MIXED
                );

        if (
                mixedExists
        ) {

            return "RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family의 upstream MIXED 상태를 LOVE_STYLE Anchor Status로 보존했습니다.";
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            return "RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family 내부에 SUPPORTIVE와 PRESSURE가 모두 존재하여 어느 한 방향을 삭제하지 않고 MIXED로 보존했습니다.";
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return "RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family의 공식 방향을 PRESSURE로 보존했습니다.";
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            return "RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family의 공식 방향을 SUPPORTIVE로 보존했습니다.";
        }

        return "RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family는 존재하지만 방향은 CONTEXTUAL로 유지했습니다.";
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     *
     * 최종 사용자 Story가 아니다.
     *
     * 다른 Source Context,
     * 연애 행동,
     * 관계 진행,
     * 조건,
     * 인과,
     * 순서를 추가하지 않는다.
     */

    private String summaryOf(

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                status,
                "LOVE_STYLE deterministic status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "LOVE_STYLE에서 참조하는 원국 관계망 구조에 SUPPORTIVE 상태가 확인됩니다.";

            case PRESSURE ->
                    "LOVE_STYLE에서 참조하는 원국 관계망 구조에 PRESSURE 상태가 확인됩니다.";

            case MIXED ->
                    "LOVE_STYLE에서 참조하는 원국 관계망 구조에 MIXED 상태가 확인됩니다.";

            case CONTEXTUAL ->
                    "LOVE_STYLE에서 참조하는 원국 관계망 구조가 존재하며 방향은 CONTEXTUAL로 유지됩니다.";
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