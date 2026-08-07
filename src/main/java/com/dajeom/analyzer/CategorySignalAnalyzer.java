package com.dajeom.analyzer;

import com.dajeom.model.CategoryInterpretation;
import com.dajeom.model.FourPillars;
import com.dajeom.model.InterpretationSignal;
import com.dajeom.model.LuckAnalysis;
import com.dajeom.model.LuckInteractionAnalysis;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.StructureResult;
import com.dajeom.model.TenGodAssignment;
import com.dajeom.type.FiveElement;
import com.dajeom.type.LuckEvaluationLevel;
import com.dajeom.type.SajuReportCategory;
import com.dajeom.type.SignalPolarity;
import com.dajeom.type.StrengthLevel;
import com.dajeom.type.StructureType;
import com.dajeom.type.TenGod;
import com.dajeom.type.UsefulGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 지금까지 계산된 명리 데이터를 보고서 카테고리별 해석 신호로 변환한다.
 *
 * 이 클래스는 긴 자연어 보고서를 생성하지 않는다.
 * 보고서 생성에 사용할 사실, 방향, 강조점만 구조화한다.
 */
@Component
public class CategorySignalAnalyzer {

    public List<CategoryInterpretation> analyze(
            FourPillars fourPillars,
            NatalChartAnalysis natalChartAnalysis,
            LuckAnalysis luckAnalysis,
            LuckInteractionAnalysis luckInteractionAnalysis
    ) {
        Map<TenGod, Integer> tenGodCounts =
                countTenGods(
                        natalChartAnalysis
                                .tenGodAssignments()
                );

        EnumMap<
                SajuReportCategory,
                List<InterpretationSignal>
                > categorySignals =
                createEmptyCategoryMap();

        addPersonalitySignals(
                categorySignals,
                fourPillars,
                natalChartAnalysis,
                tenGodCounts
        );

        addPastLuckSignals(
                categorySignals,
                natalChartAnalysis,
                tenGodCounts
        );

        addParentSignals(
                categorySignals,
                natalChartAnalysis,
                tenGodCounts
        );

        addSiblingSignals(
                categorySignals,
                natalChartAnalysis,
                tenGodCounts
        );

        addLoveStyleSignals(
                categorySignals,
                natalChartAnalysis,
                tenGodCounts
        );

        addPartnerSignals(
                categorySignals,
                natalChartAnalysis,
                tenGodCounts
        );

        addWealthSignals(
                categorySignals,
                natalChartAnalysis,
                tenGodCounts
        );

        addCareerSignals(
                categorySignals,
                natalChartAnalysis,
                tenGodCounts
        );

        addLegalRiskSignals(
                categorySignals,
                natalChartAnalysis,
                tenGodCounts
        );

        addHealthSignals(
                categorySignals,
                natalChartAnalysis
        );

        addCurrentLuckSignals(
                categorySignals,
                luckAnalysis,
                luckInteractionAnalysis
        );

        addYearLuckSignals(
                categorySignals,
                luckAnalysis,
                luckInteractionAnalysis
        );

        addFutureLuckSignals(
                categorySignals,
                luckAnalysis,
                natalChartAnalysis
        );

        return categorySignals.entrySet()
                .stream()
                .map(entry ->
                        createCategoryInterpretation(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();
    }

    private void addPersonalitySignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            FourPillars fourPillars,
            NatalChartAnalysis natal,
            Map<TenGod, Integer> counts
    ) {
        FiveElement dayMasterElement =
                fourPillars
                        .dayPillar()
                        .stem()
                        .getElement();

        add(
                destination,
                InterpretationSignal.of(
                        "PERSONALITY_DAY_MASTER_"
                                + dayMasterElement.name(),

                        SajuReportCategory.PERSONALITY,

                        "일간 중심 기질",

                        dayMasterElement.getKoreanName()
                                + " 일간의 기질이 성격의 중심에 놓여 있음",

                        SignalPolarity.NEUTRAL,

                        65.0,

                        List.of(
                                "DAY_MASTER_ELEMENT_"
                                        + dayMasterElement.name()
                        ),

                        List.of(
                                "일간은 "
                                        + fourPillars
                                        .dayPillar()
                                        .stem()
                                        .getChineseCharacter()
                                        + "이며 오행은 "
                                        + dayMasterElement
                                        .getKoreanName()
                                        + "임"
                        )
                )
        );

        StrengthLevel strengthLevel =
                natal.strengthAnalysis().level();

        if (isWeak(strengthLevel)) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "PERSONALITY_WEAK_DAY_MASTER",

                            SajuReportCategory.PERSONALITY,

                            "환경 영향을 크게 받는 성향",

                            "자신의 의지는 분명하지만 환경과 사람의 영향에 민감하게 반응하는 경향이 있음",

                            SignalPolarity.NEUTRAL,

                            70.0,

                            List.of(
                                    "STRENGTH_LEVEL_"
                                            + strengthLevel.name()
                            ),

                            List.of(
                                    "일간 강약이 "
                                            + strengthLevel
                                            .getKoreanName()
                                            + "으로 판정됨"
                            )
                    )
            );
        }

        int officerCount =
                officerCount(counts);

        if (officerCount >= 2) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "PERSONALITY_OFFICER_PRESSURE",

                            SajuReportCategory.PERSONALITY,

                            "책임과 긴장감이 강한 성향",

                            "규칙과 책임을 의식하며 스스로를 쉽게 느슨하게 두지 않는 편임",

                            SignalPolarity.NEUTRAL,

                            Math.min(
                                    90.0,
                                    40.0 + officerCount * 10.0
                            ),

                            List.of(
                                    "OFFICER_COUNT_"
                                            + officerCount
                            ),

                            List.of(
                                    "관성 계열 십성이 "
                                            + officerCount
                                            + "개 확인됨"
                            )
                    )
            );
        }

        int resourceCount =
                resourceCount(counts);

        if (resourceCount >= 2) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "PERSONALITY_RESOURCE_THINKING",

                            SajuReportCategory.PERSONALITY,

                            "생각과 분석이 앞서는 성향",

                            "경험을 바로 행동으로 옮기기보다 먼저 이해하고 판단하려는 경향이 강함",

                            SignalPolarity.FAVORABLE,

                            Math.min(
                                    90.0,
                                    35.0 + resourceCount * 10.0
                            ),

                            List.of(
                                    "RESOURCE_COUNT_"
                                            + resourceCount
                            ),

                            List.of(
                                    "인성 계열 십성이 "
                                            + resourceCount
                                            + "개 확인됨"
                            )
                    )
            );
        }
    }

    private void addPastLuckSignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            NatalChartAnalysis natal,
            Map<TenGod, Integer> counts
    ) {
        if (isWeak(
                natal.strengthAnalysis().level()
        )) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "PAST_ENVIRONMENT_DEPENDENCY",

                            SajuReportCategory.PAST_LUCK,

                            "초기 환경의 영향이 큼",

                            "어린 시절이나 과거에는 자신의 선택보다 주변 환경과 타인의 결정에 영향을 받는 일이 많았을 가능성이 있음",

                            SignalPolarity.CHALLENGING,

                            65.0,

                            List.of(
                                    "WEAK_DAY_MASTER"
                            ),

                            List.of(
                                    "일간이 신약 계열로 판정됨"
                            )
                    )
            );
        }

        if (officerCount(counts) >= 3) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "PAST_EARLY_RESPONSIBILITY",

                            SajuReportCategory.PAST_LUCK,

                            "일찍 책임을 체감한 흐름",

                            "과거부터 해야 할 일이나 지켜야 할 기준을 강하게 의식했을 가능성이 있음",

                            SignalPolarity.CHALLENGING,

                            70.0,

                            List.of(
                                    "OFFICER_DOMINANT"
                            ),

                            List.of(
                                    "관성 계열 십성이 다수 존재함"
                            )
                    )
            );
        }
    }

    private void addParentSignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            NatalChartAnalysis natal,
            Map<TenGod, Integer> counts
    ) {
        int resource =
                resourceCount(counts);

        if (resource == 0) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "PARENTS_RESOURCE_ABSENT",

                            SajuReportCategory.PARENTS,

                            "보호를 체감하는 방식의 차이",

                            "부모의 보호가 없었다고 단정할 수는 없지만, 자신이 기대한 방식으로 보호와 지지를 체감하기 어려웠을 수 있음",

                            SignalPolarity.CHALLENGING,

                            75.0,

                            List.of(
                                    "NO_RESOURCE"
                            ),

                            List.of(
                                    "원국에서 인성 계열 십성이 확인되지 않음"
                            )
                    )
            );

        } else {
            add(
                    destination,
                    InterpretationSignal.of(
                            "PARENTS_RESOURCE_PRESENT",

                            SajuReportCategory.PARENTS,

                            "부모·보호자 영향이 존재함",

                            "부모나 보호자에게서 지식, 조언, 돌봄의 영향을 받는 흐름이 존재함",

                            SignalPolarity.NEUTRAL,

                            Math.min(
                                    85.0,
                                    45.0 + resource * 8.0
                            ),

                            List.of(
                                    "RESOURCE_COUNT_"
                                            + resource
                            ),

                            List.of(
                                    "인성 계열 십성이 "
                                            + resource
                                            + "개 확인됨"
                            )
                    )
            );
        }

        if (natal.relationAnalysis()
                .branchRelations()
                .stream()
                .anyMatch(relation ->
                        relation.positions()
                                .stream()
                                .anyMatch(position ->
                                        position.name()
                                                .startsWith("YEAR")
                                )
                                && isChallengingRelation(
                                relation.relationType()
                        )
                )) {

            add(
                    destination,
                    InterpretationSignal.of(
                            "PARENTS_YEAR_PILLAR_CONFLICT",

                            SajuReportCategory.PARENTS,

                            "가족 환경의 변화 신호",

                            "년주에 충·형·파·해가 연결되어 가족 환경이나 부모와의 관계에서 거리감과 변화가 나타날 수 있음",

                            SignalPolarity.CHALLENGING,

                            70.0,

                            List.of(
                                    "YEAR_PILLAR_RELATION"
                            ),

                            List.of(
                                    "년지에 부담 관계가 연결됨"
                            )
                    )
            );
        }
    }

    private void addSiblingSignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            NatalChartAnalysis natal,
            Map<TenGod, Integer> counts
    ) {
        int peers =
                peerCount(counts);

        if (peers >= 3) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "SIBLING_PEER_COMPETITION",

                            SajuReportCategory.SIBLINGS,

                            "가까운 관계에서 경쟁이 강함",

                            "형제·친구·동료와 가까워질수록 협력과 경쟁이 동시에 강해지는 경향이 있음",

                            SignalPolarity.CHALLENGING,

                            Math.min(
                                    90.0,
                                    45.0 + peers * 10.0
                            ),

                            List.of(
                                    "PEER_COUNT_"
                                            + peers
                            ),

                            List.of(
                                    "비겁 계열 십성이 "
                                            + peers
                                            + "개 확인됨"
                            )
                    )
            );

        } else if (peers > 0) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "SIBLING_PEER_SUPPORT",

                            SajuReportCategory.SIBLINGS,

                            "동료 관계의 도움",

                            "혼자 해결하기보다 비슷한 위치의 사람과 힘을 합칠 때 성과가 커질 수 있음",

                            SignalPolarity.FAVORABLE,

                            55.0,

                            List.of(
                                    "PEER_PRESENT"
                            ),

                            List.of(
                                    "비겁 계열 십성이 존재함"
                            )
                    )
            );
        }
    }

    private void addLoveStyleSignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            NatalChartAnalysis natal,
            Map<TenGod, Integer> counts
    ) {
        int wealth =
                wealthCount(counts);

        int officer =
                officerCount(counts);

        int output =
                outputCount(counts);

        if (output >= 2) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "LOVE_EXPRESSIVE_STYLE",

                            SajuReportCategory.LOVE_STYLE,

                            "표현과 반응을 중시하는 연애",

                            "상대의 반응을 빠르게 확인하고 감정이나 생각을 표현하며 관계를 움직이려는 성향이 있음",

                            SignalPolarity.NEUTRAL,

                            Math.min(
                                    85.0,
                                    45.0 + output * 8.0
                            ),

                            List.of(
                                    "OUTPUT_COUNT_"
                                            + output
                            ),

                            List.of(
                                    "식상 계열 십성이 "
                                            + output
                                            + "개 확인됨"
                            )
                    )
            );
        }

        if (wealth + officer >= 3) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "LOVE_REALISTIC_SELECTION",

                            SajuReportCategory.LOVE_STYLE,

                            "감정만으로 관계를 선택하지 않음",

                            "호감뿐 아니라 상대의 책임감, 생활력, 현실 조건을 함께 보는 경향이 강함",

                            SignalPolarity.FAVORABLE,

                            65.0,

                            List.of(
                                    "WEALTH_OFFICER_TOTAL_"
                                            + (wealth + officer)
                            ),

                            List.of(
                                    "재성과 관성의 합계가 "
                                            + (wealth + officer)
                                            + "개임"
                            )
                    )
            );
        }

        if (isWeak(
                natal.strengthAnalysis().level()
        )) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "LOVE_PARTNER_INFLUENCE",

                            SajuReportCategory.LOVE_STYLE,

                            "상대에게 영향을 많이 받음",

                            "연애를 시작하면 상대의 감정과 상황을 자신의 문제처럼 받아들이기 쉬움",

                            SignalPolarity.CHALLENGING,

                            60.0,

                            List.of(
                                    "WEAK_DAY_MASTER"
                            ),

                            List.of(
                                    "일간이 신약 계열로 판정됨"
                            )
                    )
            );
        }
    }

    private void addPartnerSignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            NatalChartAnalysis natal,
            Map<TenGod, Integer> counts
    ) {
        int wealth =
                wealthCount(counts);

        int officer =
                officerCount(counts);

        int partnerCount =
                wealth + officer;

        if (partnerCount == 0) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "PARTNER_STAR_ABSENT",

                            SajuReportCategory.PARTNER_LUCK,

                            "인연이 생활 속에서 늦게 드러남",

                            "배우자 별이 원국에서 약하므로 인연이 없다는 뜻이 아니라, 대운과 세운에서 관계가 구체화되는 경향이 큼",

                            SignalPolarity.NEUTRAL,

                            70.0,

                            List.of(
                                    "PARTNER_STAR_COUNT_0"
                            ),

                            List.of(
                                    "재성과 관성 계열 십성이 확인되지 않음"
                            )
                    )
            );

        } else {
            add(
                    destination,
                    InterpretationSignal.of(
                            "PARTNER_STAR_PRESENT",

                            SajuReportCategory.PARTNER_LUCK,

                            "현실적인 역할을 가진 인연",

                            "책임감이나 경제 감각처럼 현실적인 기능이 분명한 사람과 인연이 연결되기 쉬움",

                            SignalPolarity.FAVORABLE,

                            Math.min(
                                    90.0,
                                    45.0 + partnerCount * 7.0
                            ),

                            List.of(
                                    "PARTNER_STAR_COUNT_"
                                            + partnerCount
                            ),

                            List.of(
                                    "재성과 관성 합계가 "
                                            + partnerCount
                                            + "개임"
                            )
                    )
            );
        }

        boolean dayBranchConflict =
                natal.relationAnalysis()
                        .branchRelations()
                        .stream()
                        .anyMatch(relation ->
                                relation.positions()
                                        .stream()
                                        .anyMatch(position ->
                                                position.name()
                                                        .equals(
                                                                "DAY_BRANCH"
                                                        )
                                        )
                                        && isChallengingRelation(
                                        relation.relationType()
                                )
                        );

        if (dayBranchConflict) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "PARTNER_PALACE_RELATION",

                            SajuReportCategory.PARTNER_LUCK,

                            "관계 안에서 조정이 필요함",

                            "일지에 부담 관계가 연결되어 가까운 관계일수록 감정과 생활 방식의 조정이 중요함",

                            SignalPolarity.CHALLENGING,

                            75.0,

                            List.of(
                                    "DAY_BRANCH_CHALLENGING_RELATION"
                            ),

                            List.of(
                                    "일지에 충·형·파·해 관계가 존재함"
                            )
                    )
            );
        }
    }

    private void addWealthSignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            NatalChartAnalysis natal,
            Map<TenGod, Integer> counts
    ) {
        int wealth =
                wealthCount(counts);

        if (wealth >= 3) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "WEALTH_OPPORTUNITY_HIGH",

                            SajuReportCategory.WEALTH,

                            "돈을 다룰 기회가 많은 구조",

                            "재성의 비중이 높아 수익, 거래, 자산, 현실 성과와 연결되는 일이 반복될 가능성이 큼",

                            SignalPolarity.FAVORABLE,

                            Math.min(
                                    95.0,
                                    50.0 + wealth * 8.0
                            ),

                            List.of(
                                    "WEALTH_COUNT_"
                                            + wealth
                            ),

                            List.of(
                                    "재성 계열 십성이 "
                                            + wealth
                                            + "개 확인됨"
                            )
                    )
            );
        }

        if (hasStructure(
                natal,
                StructureType
                        .WEALTH_HEAVY_WEAK_SELF
        )) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "WEALTH_HEAVY_WEAK_SELF",

                            SajuReportCategory.WEALTH,

                            "돈의 기회보다 관리 능력이 중요함",

                            "재물 기회는 생기지만 일간의 힘이 약하면 수익을 지키거나 감당하는 과정에서 부담이 커질 수 있음",

                            SignalPolarity.CHALLENGING,

                            90.0,

                            List.of(
                                    "STRUCTURE_WEALTH_HEAVY_WEAK_SELF"
                            ),

                            List.of(
                                    "재다신약 구조가 성립함"
                            )
                    )
            );
        }

        if (hasStructure(
                natal,
                StructureType.OUTPUT_GENERATES_WEALTH
        )) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "WEALTH_OUTPUT_GENERATION",

                            SajuReportCategory.WEALTH,

                            "기술과 결과물이 돈으로 연결됨",

                            "말, 기술, 콘텐츠, 생산 결과를 수익으로 전환하는 방식이 적합함",

                            SignalPolarity.FAVORABLE,

                            80.0,

                            List.of(
                                    "STRUCTURE_OUTPUT_GENERATES_WEALTH"
                            ),

                            List.of(
                                    "식상생재 구조가 성립함"
                            )
                    )
            );
        }
    }

    private void addCareerSignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            NatalChartAnalysis natal,
            Map<TenGod, Integer> counts
    ) {
        int officer =
                officerCount(counts);

        int resource =
                resourceCount(counts);

        int output =
                outputCount(counts);

        if (officer >= 2) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "CAREER_ORGANIZATION_RULE",

                            SajuReportCategory.CAREER,

                            "책임과 기준이 분명한 직업",

                            "조직의 역할과 책임이 명확하거나 자격·규정·관리 능력을 활용하는 직업에 강점이 있음",

                            SignalPolarity.FAVORABLE,

                            Math.min(
                                    90.0,
                                    45.0 + officer * 8.0
                            ),

                            List.of(
                                    "OFFICER_COUNT_"
                                            + officer
                            ),

                            List.of(
                                    "관성 계열 십성이 "
                                            + officer
                                            + "개 확인됨"
                            )
                    )
            );
        }

        if (resource >= 2) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "CAREER_KNOWLEDGE_SPECIALIST",

                            SajuReportCategory.CAREER,

                            "지식과 분석을 쓰는 전문직",

                            "학습, 문서, 기획, 분석, 자격, 기술 축적이 필요한 분야에 적합함",

                            SignalPolarity.FAVORABLE,

                            Math.min(
                                    90.0,
                                    40.0 + resource * 8.0
                            ),

                            List.of(
                                    "RESOURCE_COUNT_"
                                            + resource
                            ),

                            List.of(
                                    "인성 계열 십성이 "
                                            + resource
                                            + "개 확인됨"
                            )
                    )
            );
        }

        if (output >= 2) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "CAREER_OUTPUT_CREATION",

                            SajuReportCategory.CAREER,

                            "직접 만들고 표현하는 업무",

                            "단순 반복보다 결과물을 만들고 문제를 해결하며 자신의 방식을 드러내는 업무가 적합함",

                            SignalPolarity.FAVORABLE,

                            Math.min(
                                    85.0,
                                    40.0 + output * 8.0
                            ),

                            List.of(
                                    "OUTPUT_COUNT_"
                                            + output
                            ),

                            List.of(
                                    "식상 계열 십성이 "
                                            + output
                                            + "개 확인됨"
                            )
                    )
            );
        }
    }

    private void addLegalRiskSignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            NatalChartAnalysis natal,
            Map<TenGod, Integer> counts
    ) {
        if (hasStructure(
                natal,
                StructureType
                        .HURTING_OFFICER_SEES_OFFICER
        )) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "LEGAL_HURTING_OFFICER_OFFICER",

                            SajuReportCategory.LEGAL_RISK,

                            "규칙과 표현의 충돌",

                            "자신의 말이나 행동 방식이 제도·상사·기관의 기준과 충돌하지 않도록 관리할 필요가 있음",

                            SignalPolarity.CHALLENGING,

                            90.0,

                            List.of(
                                    "STRUCTURE_HURTING_OFFICER_SEES_OFFICER"
                            ),

                            List.of(
                                    "상관견관 구조가 성립함"
                            )
                    )
            );
        }

        int challengingRelations =
                (int) natal.relationAnalysis()
                        .branchRelations()
                        .stream()
                        .filter(relation ->
                                isChallengingRelation(
                                        relation.relationType()
                                )
                        )
                        .count();

        if (challengingRelations >= 2) {
            add(
                    destination,
                    InterpretationSignal.of(
                            "LEGAL_RELATION_CONFLICT",

                            SajuReportCategory.LEGAL_RISK,

                            "계약과 분쟁 관리 필요",

                            "충·형·파·해가 반복되므로 감정적 대응보다 기록과 계약 조건을 분명히 하는 것이 중요함",

                            SignalPolarity.CHALLENGING,

                            Math.min(
                                    90.0,
                                    45.0
                                            + challengingRelations
                                            * 10.0
                            ),

                            List.of(
                                    "CHALLENGING_RELATION_COUNT_"
                                            + challengingRelations
                            ),

                            List.of(
                                    "부담 관계가 "
                                            + challengingRelations
                                            + "개 확인됨"
                            )
                    )
            );
        }

        if (officerCount(counts) > 0
                && !hasStructure(
                natal,
                StructureType
                        .HURTING_OFFICER_SEES_OFFICER
        )) {

            add(
                    destination,
                    InterpretationSignal.of(
                            "LEGAL_RULE_AWARENESS",

                            SajuReportCategory.LEGAL_RISK,

                            "규칙을 의식하는 성향",

                            "관성이 존재해 기본적으로 규칙과 책임을 의식하는 경향이 있음",

                            SignalPolarity.FAVORABLE,

                            50.0,

                            List.of(
                                    "OFFICER_PRESENT"
                            ),

                            List.of(
                                    "관성 계열 십성이 존재함"
                            )
                    )
            );
        }
    }

    private void addHealthSignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            NatalChartAnalysis natal
    ) {
        FiveElement weakest =
                natal.elementStrength()
                        .weakestElement();

        FiveElement strongest =
                natal.elementStrength()
                        .strongestElement();

        add(
                destination,
                InterpretationSignal.of(
                        "HEALTH_WEAKEST_ELEMENT_"
                                + weakest.name(),

                        SajuReportCategory
                                .HEALTH_TENDENCY,

                        "부족한 오행 관리",

                        weakest.getKoreanName()
                                + " 오행의 세력이 가장 약하므로 생활 리듬과 균형 관리가 중요함",

                        SignalPolarity.NEUTRAL,

                        60.0,

                        List.of(
                                "WEAKEST_ELEMENT_"
                                        + weakest.name()
                        ),

                        List.of(
                                "가장 약한 오행은 "
                                        + weakest.getKoreanName()
                                        + "임"
                        )
                )
        );

        if (natal.elementStrength()
                .percentageOf(strongest) >= 35.0) {

            add(
                    destination,
                    InterpretationSignal.of(
                            "HEALTH_EXCESS_ELEMENT_"
                                    + strongest.name(),

                            SajuReportCategory
                                    .HEALTH_TENDENCY,

                            "과다한 오행의 소모 관리",

                            strongest.getKoreanName()
                                    + " 기운이 과도하게 작용하지 않도록 휴식과 생활 습관을 조정할 필요가 있음",

                            SignalPolarity.CHALLENGING,

                            65.0,

                            List.of(
                                    "STRONGEST_ELEMENT_"
                                            + strongest.name()
                            ),

                            List.of(
                                    strongest.getKoreanName()
                                            + " 오행 비율이 "
                                            + natal
                                            .elementStrength()
                                            .percentageOf(strongest)
                                            + "%임"
                            )
                    )
            );
        }
    }

    private void addCurrentLuckSignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            LuckAnalysis luckAnalysis,
            LuckInteractionAnalysis interaction
    ) {
        if (interaction.decadeEvaluation() == null) {
            return;
        }

        double score =
                interaction
                        .decadeEvaluation()
                        .totalScore();

        add(
                destination,
                InterpretationSignal.of(
                        "CURRENT_DECADE_SCORE",

                        SajuReportCategory.CURRENT_LUCK,

                        "현재 대운의 장기 환경",

                        "현재 대운은 "
                                + interaction
                                .decadeEvaluation()
                                .level()
                                .getKoreanName()
                                + "으로 평가됨",

                        polarityFromLuckScore(score),

                        score,

                        List.of(
                                "CURRENT_DECADE_"
                                        + interaction
                                        .decadeEvaluation()
                                        .pillar()
                                        .chineseName()
                        ),

                        interaction
                                .decadeEvaluation()
                                .evidence()
                )
        );
    }

    private void addYearLuckSignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            LuckAnalysis luckAnalysis,
            LuckInteractionAnalysis interaction
    ) {
        double annualScore =
                interaction
                        .annualEvaluation()
                        .totalScore();

        add(
                destination,
                InterpretationSignal.of(
                        "YEAR_LUCK_SCORE_"
                                + luckAnalysis
                                .annualLuck()
                                .targetYear(),

                        SajuReportCategory.YEAR_LUCK,

                        luckAnalysis
                                .annualLuck()
                                .targetYear()
                                + "년 운의 직접 작용",

                        "선택 연도의 세운은 "
                                + interaction
                                .annualEvaluation()
                                .level()
                                .getKoreanName()
                                + "으로 평가됨",

                        polarityFromLuckScore(
                                annualScore
                        ),

                        annualScore,

                        List.of(
                                "ANNUAL_LUCK_"
                                        + luckAnalysis
                                        .annualLuck()
                                        .pillar()
                                        .chineseName()
                        ),

                        interaction
                                .annualEvaluation()
                                .evidence()
                )
        );

        double combinedScore =
                interaction.combinedScore();

        add(
                destination,
                InterpretationSignal.of(
                        "YEAR_COMBINED_LUCK_SCORE",

                        SajuReportCategory.YEAR_LUCK,

                        "대운과 세운의 종합 결과",

                        "장기 대운과 해당 연도 세운을 합산한 종합 점수는 "
                                + combinedScore
                                + "점임",

                        polarityFromLuckScore(
                                combinedScore
                        ),

                        combinedScore,

                        List.of(
                                "COMBINED_LUCK_LEVEL_"
                                        + interaction
                                        .combinedLevel()
                                        .name()
                        ),

                        interaction.summarySignals()
                )
        );
    }

    private void addFutureLuckSignals(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            LuckAnalysis luckAnalysis,
            NatalChartAnalysis natal
    ) {
        List<FiveElement> favorableElements =
                natal.usefulGodAnalysis()
                        .usefulCandidates()
                        .stream()
                        .map(candidate ->
                                candidate.element()
                        )
                        .toList();

        luckAnalysis.decadeLucks()
                .stream()
                .filter(luck ->
                        !luck.current()
                )
                .limit(3)
                .forEach(luck -> {
                    FiveElement stemElement =
                            luck.pillar()
                                    .stem()
                                    .getElement();

                    FiveElement branchElement =
                            luck.pillar()
                                    .branch()
                                    .getElement();

                    boolean favorable =
                            favorableElements.contains(
                                    stemElement
                            )
                                    || favorableElements.contains(
                                    branchElement
                            );

                    add(
                            destination,
                            InterpretationSignal.of(
                                    "FUTURE_DECADE_"
                                            + luck.sequence(),

                                    SajuReportCategory
                                            .FUTURE_LUCK,

                                    luck.sequence()
                                            + "번째 대운 "
                                            + luck.pillar()
                                            .koreanName(),

                                    favorable
                                            ? "용신·희신 후보 오행이 포함되어 장기 발전 기회를 만들 수 있음"
                                            : "기신 후보 오행이 강해질 경우 선택과 관리가 중요함",

                                    favorable
                                            ? SignalPolarity
                                            .FAVORABLE
                                            : SignalPolarity
                                            .CHALLENGING,

                                    favorable
                                            ? 65.0
                                            : 55.0,

                                    List.of(
                                            "DECADE_PILLAR_"
                                                    + luck
                                                    .pillar()
                                                    .chineseName()
                                    ),

                                    List.of(
                                            "대운 기간은 "
                                                    + luck.startDate()
                                                    + "부터 "
                                                    + luck.endDate()
                                                    + "까지임"
                                    )
                            )
                    );
                });
    }

    private CategoryInterpretation
    createCategoryInterpretation(
            SajuReportCategory category,
            List<InterpretationSignal> signals
    ) {
        double favorableScore =
                signals.stream()
                        .filter(signal ->
                                signal.polarity()
                                        == SignalPolarity
                                        .FAVORABLE
                        )
                        .mapToDouble(
                                InterpretationSignal::score
                        )
                        .sum();

        double challengingScore =
                signals.stream()
                        .filter(signal ->
                                signal.polarity()
                                        == SignalPolarity
                                        .CHALLENGING
                        )
                        .mapToDouble(signal ->
                                Math.abs(signal.score())
                        )
                        .sum();

        double netScore =
                favorableScore
                        - challengingScore;

        return new CategoryInterpretation(
                category,
                favorableScore,
                challengingScore,
                netScore,
                signals
        );
    }

    private EnumMap<
            SajuReportCategory,
            List<InterpretationSignal>
            > createEmptyCategoryMap() {
        EnumMap<
                SajuReportCategory,
                List<InterpretationSignal>
                > result =
                new EnumMap<>(
                        SajuReportCategory.class
                );

        for (SajuReportCategory category
                : SajuReportCategory.values()) {

            result.put(
                    category,
                    new ArrayList<>()
            );
        }

        return result;
    }

    private Map<TenGod, Integer> countTenGods(
            List<TenGodAssignment> assignments
    ) {
        EnumMap<TenGod, Integer> counts =
                new EnumMap<>(TenGod.class);

        for (TenGod tenGod : TenGod.values()) {
            counts.put(tenGod, 0);
        }

        for (TenGodAssignment assignment
                : assignments) {

            counts.compute(
                    assignment.tenGod(),
                    (key, value) ->
                            value == null
                                    ? 1
                                    : value + 1
            );
        }

        return counts;
    }

    private int peerCount(
            Map<TenGod, Integer> counts
    ) {
        return count(
                counts,
                TenGod.FRIEND
        ) + count(
                counts,
                TenGod.ROB_WEALTH
        );
    }

    private int outputCount(
            Map<TenGod, Integer> counts
    ) {
        return count(
                counts,
                TenGod.EATING_GOD
        ) + count(
                counts,
                TenGod.HURTING_OFFICER
        );
    }

    private int wealthCount(
            Map<TenGod, Integer> counts
    ) {
        return count(
                counts,
                TenGod.INDIRECT_WEALTH
        ) + count(
                counts,
                TenGod.DIRECT_WEALTH
        );
    }

    private int officerCount(
            Map<TenGod, Integer> counts
    ) {
        return count(
                counts,
                TenGod.SEVEN_KILLINGS
        ) + count(
                counts,
                TenGod.DIRECT_OFFICER
        );
    }

    private int resourceCount(
            Map<TenGod, Integer> counts
    ) {
        return count(
                counts,
                TenGod.INDIRECT_RESOURCE
        ) + count(
                counts,
                TenGod.DIRECT_RESOURCE
        );
    }

    private int count(
            Map<TenGod, Integer> counts,
            TenGod tenGod
    ) {
        return counts.getOrDefault(
                tenGod,
                0
        );
    }

    private boolean hasStructure(
            NatalChartAnalysis natal,
            StructureType structureType
    ) {
        return natal.structureAnalysis()
                .matchedResults()
                .stream()
                .map(StructureResult::structureType)
                .anyMatch(type ->
                        type == structureType
                );
    }

    private boolean isWeak(
            StrengthLevel level
    ) {
        return level
                == StrengthLevel.EXTREMELY_WEAK
                || level == StrengthLevel.WEAK
                || level
                == StrengthLevel.SLIGHTLY_WEAK;
    }

    private boolean isChallengingRelation(
            com.dajeom.type.RelationType relationType
    ) {
        return switch (relationType) {
            case CLASH,
                 PUNISHMENT,
                 BREAK,
                 HARM,
                 STEM_CONTROL -> true;

            default -> false;
        };
    }

    private SignalPolarity polarityFromLuckScore(
            double score
    ) {
        if (score >= 56.0) {
            return SignalPolarity.FAVORABLE;
        }

        if (score <= 44.0) {
            return SignalPolarity.CHALLENGING;
        }

        return SignalPolarity.NEUTRAL;
    }

    private void add(
            Map<SajuReportCategory, List<InterpretationSignal>>
                    destination,
            InterpretationSignal signal
    ) {
        destination
                .get(signal.category())
                .add(signal);
    }
}