package com.dajeom.analyzer;

import com.dajeom.model.UsefulGodCandidate;
import com.dajeom.model.UsefulGodPrimaryRelationEvidence;
import com.dajeom.model.UsefulGodRelationAnalysis;
import com.dajeom.model.UsefulGodRelationCandidate;
import com.dajeom.model.UsefulGodSelectionAnalysis;
import com.dajeom.rule.UsefulGodRelationRule;
import com.dajeom.type.FiveElement;
import com.dajeom.type.UsefulGodRelationRole;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-9.
 *
 * 7-8 UsefulGodSelectionAnalysis를 받아
 * 木火土金水 전체의:
 *
 * - 용신
 * - 희신
 * - 기신
 * - 조건부
 * - 중립
 *
 * 을 판정한다.
 *
 * =================================================
 *
 * 처리 순서
 *
 * 7-8 주용신 추출
 *
 * ↓
 *
 * 각 五行 × 각 주용신
 * 생극 관계 분석
 *
 * ↓
 *
 * 주용신 생조
 * 주용신 직접 극
 * 주용신 설기
 * 주용신 극제 부담
 * 주용신 공격자 제어
 *
 * ↓
 *
 * 7-8 보조 용신 여부
 *
 * ↓
 *
 * CORE / ACTIVE 충돌
 *
 * ↓
 *
 * 종격 흐름 파괴
 *
 * ↓
 *
 * 오행 과다
 *
 * ↓
 *
 * USEFUL_GOD
 * FAVORABLE_GOD
 * UNFAVORABLE_GOD
 * CONDITIONAL
 * NEUTRAL
 *
 * =================================================
 *
 * 숫자 점수 합산은 사용하지 않는다.
 */
@Component
public class UsefulGodRelationAnalyzer {

    private final UsefulGodRelationRule
            usefulGodRelationRule;

    public UsefulGodRelationAnalyzer(
            UsefulGodRelationRule usefulGodRelationRule
    ) {

        this.usefulGodRelationRule =
                Objects.requireNonNull(
                        usefulGodRelationRule,
                        "UsefulGodRelationRule이 필요합니다."
                );
    }

    public UsefulGodRelationAnalysis analyze(
            UsefulGodSelectionAnalysis selectionAnalysis
    ) {

        Objects.requireNonNull(
                selectionAnalysis,
                "7-8 주용신 통합 분석이 필요합니다."
        );

        List<FiveElement> primaryElements =
                selectionAnalysis
                        .primaryElements();

        boolean primaryAnchorPresent =
                !primaryElements
                        .isEmpty();

        List<UsefulGodRelationCandidate> candidates =
                new ArrayList<>();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            UsefulGodCandidate sourceCandidate =
                    selectionAnalysis
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "7-8 용신 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            List<UsefulGodPrimaryRelationEvidence> primaryRelations =
                    new ArrayList<>();

            for (
                    FiveElement primaryElement
                    : primaryElements
            ) {

                primaryRelations.add(
                        usefulGodRelationRule
                                .analyzePrimaryRelation(
                                        element,
                                        primaryElement
                                )
                );
            }

            UsefulGodRelationRole role =
                    usefulGodRelationRule
                            .determineRole(
                                    sourceCandidate,
                                    primaryRelations,
                                    primaryAnchorPresent
                            );

            candidates.add(
                    new UsefulGodRelationCandidate(
                            element,
                            role,
                            sourceCandidate,
                            primaryRelations,
                            usefulGodRelationRule
                                    .reasonsFor(
                                            sourceCandidate,
                                            primaryRelations,
                                            primaryAnchorPresent,
                                            role
                                    )
                    )
            );
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 1.
         * USEFUL_GOD
         * FAVORABLE_GOD
         * UNFAVORABLE_GOD
         * CONDITIONAL
         * NEUTRAL
         *
         * 2.
         * 지원하는 주용신 수
         *
         * 3.
         * 손상하는 주용신 수
         *
         * 4.
         * 7-8 원본 역할
         *
         * 순.
         *
         * 이 정렬 역시 점수 합산이 아니며
         * 동률이라고 하나를 임의 선택하지 않는다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        UsefulGodRelationCandidate candidate
                                ) ->
                                        candidate
                                                .role()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                UsefulGodRelationCandidate
                                                        ::supportedPrimaryCount
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                UsefulGodRelationCandidate
                                        ::harmedPrimaryCount
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        UsefulGodRelationCandidate candidate
                                                ) ->
                                                        candidate
                                                                .sourceCandidate()
                                                                .role()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        return new UsefulGodRelationAnalysis(
                selectionAnalysis.dayMaster(),
                selectionAnalysis.dayMasterElement(),
                selectionAnalysis.monthBranch(),
                selectionAnalysis,
                candidates,
                selectionAnalysis.completeChart()
        );
    }
}