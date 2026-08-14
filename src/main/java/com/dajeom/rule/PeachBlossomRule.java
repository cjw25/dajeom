package com.dajeom.rule;

import com.dajeom.constant.PeachBlossomTable;
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
 * 도화 deterministic Rule.
 *
 * =================================================
 *
 * 원국 YEAR Branch
 * +
 * 원국 DAY Branch
 *
 * 각각:
 *
 * 삼합 그룹
 * ->
 * 도화 대상 지지
 *
 * 를 계산한다.
 *
 * =================================================
 *
 * 이후:
 *
 * NATAL
 * DECADE
 * ANNUAL
 * MONTHLY
 *
 * 모든 실제 Branch Point를 검사한다.
 *
 * =================================================
 *
 * 도화 Match 자체는:
 *
 * 연애
 * 외도
 * 인기
 * 매력
 *
 * 같은 사건 또는 성향을 확정하지 않는다.
 */
@Component
public class PeachBlossomRule
        implements ShinsalRule {

    public static final String RULE_CODE =
            "PEACH_BLOSSOM_NATAL_YEAR_OR_DAY_BRANCH_V1";

    public static final String CONVENTION_ID =
            "DAJEOM_PEACH_BLOSSOM_NATAL_YEAR_OR_DAY_BRANCH_V1";

    private static final Set<ShinsalRuleBasis> BASES =
            Set.of(
                    ShinsalRuleBasis.NATAL_YEAR_OR_DAY_BRANCH
            );

    @Override
    public ShinsalType type() {

        return ShinsalType.PEACH_BLOSSOM;
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
                "도화 Rule에는 ShinsalRuleContext가 필요합니다."
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
                                    "원국 YEAR Branch와 DAY Branch를 각각 도화 계산 기준으로 사용했습니다.",
                                    "각 기준 지지의 삼합 그룹에서 도화 대상 지지를 조회했습니다.",
                                    "현재 Branch Point가 하나 이상의 기준에서 계산된 도화 대상 지지와 일치합니다.",
                                    "도화 Match는 연애·관계·인기 등의 결과를 단독 확정하지 않는 보조 Evidence입니다."
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
                PeachBlossomTable
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
                                        "도화 계산 기준 원국 지지가 없습니다: "
                                                + position
                                )
                );
    }
}