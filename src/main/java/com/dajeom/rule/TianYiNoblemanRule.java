package com.dajeom.rule;

import com.dajeom.constant.TianYiNoblemanTable;
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
 * 천을귀인 deterministic Rule.
 *
 * =================================================
 *
 * 다점 Convention:
 *
 * 원국 DAY STEM
 * ↓
 * 천을귀인 대상 지지 계산
 * ↓
 * 현재 ShinsalRuleContext에 존재하는
 * 모든 Branch Point 검사
 *
 * =================================================
 *
 * 따라서 동일한 하나의 Rule로:
 *
 * - 원국 천을귀인
 * - 대운에서 들어온 천을귀인
 * - 세운에서 들어온 천을귀인
 * - 월운에서 들어온 천을귀인
 *
 * 을 모두 검출할 수 있다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 운의 천간을 새로운 기준 일간처럼
 * 사용하지 않는다.
 *
 * 항상 원국 일간이 기준이다.
 */
@Component
public class TianYiNoblemanRule
        implements ShinsalRule {

    public static final String RULE_CODE =
            "TIAN_YI_NOBLEMAN_NATAL_DAY_STEM_V1";

    public static final String CONVENTION_ID =
            "DAJEOM_TIAN_YI_NATAL_DAY_STEM_TRADITIONAL_V1";

    private static final Set<ShinsalRuleBasis> BASES =
            Set.of(
                    ShinsalRuleBasis.NATAL_DAY_STEM
            );

    @Override
    public ShinsalType type() {

        return ShinsalType
                .TIAN_YI_NOBLEMAN;
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
                "천을귀인 Rule에는 ShinsalRuleContext가 필요합니다."
        );

        ShinsalPoint dayStemPoint =
                findNatalDayStemPoint(
                        context
                );

        HeavenlyStem dayMaster =
                dayStemPoint
                        .requireStem();

        Set<EarthlyBranch> targetBranches =
                TianYiNoblemanTable
                        .targetBranches(
                                dayMaster
                        );

        List<ShinsalRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 모든 실제 Branch Point 검사.
         *
         * NATAL
         * DECADE
         * ANNUAL
         * MONTHLY
         *
         * 모두 동일한 대상표를 사용한다.
         * =================================================
         */

        for (
                ShinsalPoint point
                : context.branchPoints()
        ) {

            EarthlyBranch branch =
                    point.requireBranch();

            if (
                    !targetBranches
                            .contains(
                                    branch
                            )
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
                                    "원국 DAY Stem을 천을귀인 계산 기준으로 사용했습니다.",
                                    "현재 Branch Point가 해당 일간의 천을귀인 대상 지지와 일치합니다.",
                                    "신살 Match는 보조 Evidence이며 기존 용신·격국·십성·운 Context를 변경하지 않습니다."
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

    /**
     * 원국 일간 Point 객체 자체를 확보한다.
     *
     * context.dayMaster()만 사용하지 않는 이유는
     * ShinsalRuleMatch.referencePoints에
     * 실제 원국 DAY STEM Point를 남기기 위해서다.
     */
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
                                        "천을귀인 계산에 필요한 원국 DAY STEM Point가 없습니다."
                                )
                );
    }
}