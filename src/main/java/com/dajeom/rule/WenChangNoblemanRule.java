package com.dajeom.rule;

import com.dajeom.constant.WenChangNoblemanTable;
import com.dajeom.model.ShinsalPoint;
import com.dajeom.model.ShinsalRuleContext;
import com.dajeom.model.ShinsalRuleMatch;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.ShinsalLayer;
import com.dajeom.type.ShinsalPointKind;
import com.dajeom.type.ShinsalRuleBasis;
import com.dajeom.type.ShinsalType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 17차-2.
 *
 * 문창귀인 deterministic Rule.
 *
 * =================================================
 *
 * 다점 Convention:
 *
 * 원국 DAY STEM
 * ↓
 * 문창귀인 대상 지지 1개 계산
 * ↓
 * 현재 Context의 모든 Branch Point 검사.
 *
 * =================================================
 *
 * 예:
 *
 * 丁 일간
 * ↓
 * 酉
 *
 * 따라서:
 *
 * 원국 酉
 * 대운 酉
 * 세운 酉
 * 월운 酉
 *
 * 가 각각 별도의 Match가 될 수 있다.
 *
 * =================================================
 *
 * 문창귀인 존재만으로:
 *
 * - 시험합격
 * - 학업성공
 * - 직업성공
 *
 * 등을 확정하지 않는다.
 */
@Component
public class WenChangNoblemanRule
        implements ShinsalRule {

    public static final String RULE_CODE =
            "WEN_CHANG_NOBLEMAN_NATAL_DAY_STEM_V1";

    public static final String CONVENTION_ID =
            "DAJEOM_WEN_CHANG_NATAL_DAY_STEM_TRADITIONAL_V1";

    private static final Set<ShinsalRuleBasis> BASES =
            Set.of(
                    ShinsalRuleBasis.NATAL_DAY_STEM
            );

    @Override
    public ShinsalType type() {

        return ShinsalType
                .WEN_CHANG_NOBLEMAN;
    }

    @Override
    public String code() {

        return RULE_CODE;
    }

    @Override
    public Set<ShinsalRuleBasis> bases() {

        return BASES;
    }

    @Override
    public String conventionId() {

        return CONVENTION_ID;
    }

    @Override
    public List<ShinsalRuleMatch> evaluate(
            ShinsalRuleContext context
    ) {

        Objects.requireNonNull(
                context,
                "문창귀인 Rule에는 ShinsalRuleContext가 필요합니다."
        );

        ShinsalPoint dayStemPoint =
                findNatalDayStemPoint(
                        context
                );

        HeavenlyStem dayMaster =
                dayStemPoint
                        .requireStem();

        EarthlyBranch targetBranch =
                WenChangNoblemanTable
                        .targetBranch(
                                dayMaster
                        );

        List<ShinsalRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 원국/대운/세운/월운의
         * 모든 실제 지지 Point 검사.
         * =================================================
         */

        for (
                ShinsalPoint point
                : context.branchPoints()
        ) {

            EarthlyBranch branch =
                    point.requireBranch();

            if (
                    branch != targetBranch
            ) {

                continue;
            }

            matches.add(
                    new ShinsalRuleMatch(
                            type(),
                            ShinsalRuleBasis.NATAL_DAY_STEM,
                            code(),
                            conventionId(),
                            List.of(
                                    dayStemPoint
                            ),
                            List.of(
                                    point
                            ),
                            List.of(
                                    "원국 DAY Stem을 문창귀인 계산 기준으로 사용했습니다.",
                                    "현재 Branch Point가 해당 일간의 문창귀인 대상 지지와 일치합니다.",
                                    "문창귀인은 보조 신살 Evidence이며 학업·시험·직업 결과를 단독 확정하지 않습니다."
                            )
                    )
            );
        }

        matches.sort(
                Comparator.comparing(
                        match ->
                                match
                                        .matchedPoints()
                                        .get(0)
                                        .locationKey()
                )
        );

        return List.copyOf(
                matches
        );
    }

    private ShinsalPoint findNatalDayStemPoint(
            ShinsalRuleContext context
    ) {

        return context
                .points()
                .stream()
                .filter(
                        point ->
                                point.layer()
                                        == ShinsalLayer.NATAL
                )
                .filter(
                        point ->
                                point.kind()
                                        == ShinsalPointKind.HEAVENLY_STEM
                )
                .filter(
                        point ->
                                point
                                        .palacePosition()
                                        .orElse(null)
                                        == PalacePosition.DAY
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "문창귀인 계산에 필요한 원국 DAY STEM Point가 없습니다."
                                )
                );
    }
}