package com.dajeom.rule;

import com.dajeom.constant.CanopyTable;
import com.dajeom.model.ShinsalPoint;
import com.dajeom.model.ShinsalRuleContext;
import com.dajeom.model.ShinsalRuleMatch;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.ShinsalLayer;
import com.dajeom.type.ShinsalPointKind;
import com.dajeom.type.ShinsalRuleBasis;
import com.dajeom.type.ShinsalType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 17차-3.
 *
 * 화개 deterministic Rule.
 *
 * =================================================
 *
 * 원국 YEAR Branch
 * +
 * 원국 DAY Branch
 *
 * 각각의 삼합 그룹에서
 * 화개 대상 지지를 계산한다.
 *
 * =================================================
 *
 * 모든 NATAL / DECADE / ANNUAL / MONTHLY
 * Branch Point를 대상으로 Match를 찾는다.
 *
 * =================================================
 *
 * 화개 Match 자체는:
 *
 * 예술성
 * 종교성
 * 고독
 * 출가
 *
 * 등을 단정하지 않는다.
 */
@Component
public class CanopyRule
        implements ShinsalRule {

    public static final String RULE_CODE =
            "CANOPY_NATAL_YEAR_OR_DAY_BRANCH_V1";

    public static final String CONVENTION_ID =
            "DAJEOM_CANOPY_NATAL_YEAR_OR_DAY_BRANCH_V1";

    private static final Set<ShinsalRuleBasis> BASES =
            Set.of(
                    ShinsalRuleBasis.NATAL_YEAR_OR_DAY_BRANCH
            );

    @Override
    public ShinsalType type() {

        return ShinsalType.CANOPY;
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
                "화개 Rule에는 ShinsalRuleContext가 필요합니다."
        );

        ShinsalPoint yearBranchPoint =
                findNatalBranchPoint(
                        context,
                        PalacePosition.YEAR
                );

        ShinsalPoint dayBranchPoint =
                findNatalBranchPoint(
                        context,
                        PalacePosition.DAY
                );

        Map<
                EarthlyBranch,
                List<ShinsalPoint>
                > referencesByTarget =
                new LinkedHashMap<>();

        addReference(
                referencesByTarget,
                yearBranchPoint
        );

        addReference(
                referencesByTarget,
                dayBranchPoint
        );

        List<ShinsalRuleMatch> matches =
                new ArrayList<>();

        for (
                ShinsalPoint candidate
                : context.branchPoints()
        ) {

            List<ShinsalPoint> references =
                    referencesByTarget.get(
                            candidate.requireBranch()
                    );

            if (
                    references == null
                            || references.isEmpty()
            ) {

                continue;
            }

            matches.add(
                    new ShinsalRuleMatch(
                            type(),
                            ShinsalRuleBasis
                                    .NATAL_YEAR_OR_DAY_BRANCH,
                            code(),
                            conventionId(),
                            references,
                            List.of(
                                    candidate
                            ),
                            List.of(
                                    "원국 YEAR Branch와 DAY Branch를 각각 화개 계산 기준으로 사용했습니다.",
                                    "각 기준 지지를 삼합 그룹으로 분류하여 화개 대상 지지를 계산했습니다.",
                                    "현재 Branch Point가 하나 이상의 기준에서 계산된 화개 대상 지지와 일치합니다.",
                                    "화개 Match는 성향·직업·종교·예술 결과를 단독으로 확정하지 않는 보조 Evidence입니다."
                            )
                    )
            );
        }

        return List.copyOf(
                matches
        );
    }

    private void addReference(

            Map<
                    EarthlyBranch,
                    List<ShinsalPoint>
                    > result,

            ShinsalPoint referencePoint

    ) {

        EarthlyBranch target =
                CanopyTable
                        .targetBranch(
                                referencePoint
                                        .requireBranch()
                        );

        result.computeIfAbsent(
                        target,
                        ignored ->
                                new ArrayList<>()
                )
                .add(
                        referencePoint
                );
    }

    private ShinsalPoint findNatalBranchPoint(

            ShinsalRuleContext context,

            PalacePosition position

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
                                        == ShinsalPointKind.EARTHLY_BRANCH
                )
                .filter(
                        point ->
                                point
                                        .palacePosition()
                                        .orElse(null)
                                        == position
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "화개 계산 기준 원국 지지가 없습니다: "
                                                + position
                                )
                );
    }
}