package com.dajeom.rule;

import com.dajeom.constant.TravelHorseTable;
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
 * 역마 deterministic Rule.
 *
 * =================================================
 *
 * 다점 V1 기준:
 *
 * 원국 YEAR Branch
 * +
 * 원국 DAY Branch
 *
 * 를 각각 독립 기준점으로 사용한다.
 *
 * =================================================
 *
 * 각각의 기준지지
 * ↓
 * 삼합 그룹
 * ↓
 * 역마 대상 지지
 * ↓
 * 현재 Context의 모든 Branch Point 검사
 *
 * =================================================
 *
 * YEAR와 DAY가 같은 역마 대상지지를 만들 경우:
 *
 * 동일 matchedPoint에 대해
 * Match를 두 번 만들지 않고
 *
 * referencePoints:
 *
 * YEAR
 * DAY
 *
 * 두 기준을 하나의 Match에 함께 보존한다.
 */
@Component
public class TravelHorseRule
        implements ShinsalRule {

    public static final String RULE_CODE =
            "TRAVEL_HORSE_NATAL_YEAR_OR_DAY_BRANCH_V1";

    public static final String CONVENTION_ID =
            "DAJEOM_TRAVEL_HORSE_NATAL_YEAR_OR_DAY_BRANCH_V1";

    private static final Set<ShinsalRuleBasis> BASES =
            Set.of(
                    ShinsalRuleBasis.NATAL_YEAR_OR_DAY_BRANCH
            );

    @Override
    public ShinsalType type() {

        return ShinsalType.TRAVEL_HORSE;
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
                "역마 Rule에는 ShinsalRuleContext가 필요합니다."
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
                buildReferencesByTarget(
                        yearBranchPoint,
                        dayBranchPoint
                );

        List<ShinsalRuleMatch> matches =
                new ArrayList<>();

        for (
                ShinsalPoint candidate
                : context.branchPoints()
        ) {

            EarthlyBranch candidateBranch =
                    candidate.requireBranch();

            List<ShinsalPoint> references =
                    referencesByTarget.get(
                            candidateBranch
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
                                    "원국 YEAR Branch와 DAY Branch를 각각 역마 계산 기준으로 사용했습니다.",
                                    "기준 지지를 삼합 그룹으로 분류한 뒤 해당 그룹의 역마 대상 지지를 조회했습니다.",
                                    "현재 Branch Point가 하나 이상의 기준점에서 계산된 역마 대상 지지와 일치합니다.",
                                    "역마 Match는 이동·변동 사건을 단독으로 확정하지 않는 보조 Evidence입니다."
                            )
                    )
            );
        }

        return List.copyOf(
                matches
        );
    }

    private Map<
            EarthlyBranch,
            List<ShinsalPoint>
            > buildReferencesByTarget(

            ShinsalPoint yearBranchPoint,

            ShinsalPoint dayBranchPoint

    ) {

        Map<
                EarthlyBranch,
                List<ShinsalPoint>
                > result =
                new LinkedHashMap<>();

        addReference(
                result,
                yearBranchPoint
        );

        addReference(
                result,
                dayBranchPoint
        );

        return result;
    }

    private void addReference(

            Map<
                    EarthlyBranch,
                    List<ShinsalPoint>
                    > result,

            ShinsalPoint referencePoint

    ) {

        EarthlyBranch target =
                TravelHorseTable
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
                                        "역마 계산 기준 원국 지지가 없습니다: "
                                                + position
                                )
                );
    }
}