package com.dajeom.analyzer;

import com.dajeom.model.PalacePositionAnalysis;
import com.dajeom.model.PalaceResolvedTenGodSource;
import com.dajeom.model.PalaceTenGodAnalysis;
import com.dajeom.model.PalaceTenGodEvidence;
import com.dajeom.resolver.PalaceTenGodSourceResolver;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalacePositionContextType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 12차-3.
 *
 * 궁위 × 십성 Analyzer.
 *
 * =================================================
 *
 * 12-2:
 *
 * PalacePositionAnalysis
 *
 * +
 *
 * 기존 2차/10차와 연결되는:
 *
 * PalaceTenGodSourceResolver
 *
 * ↓
 *
 * PalaceTenGodAnalysis
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Analyzer 자체는 지장간표와
 * 십성 계산 공식을 다시 구현하지 않는다.
 *
 * 기존 canonical engine을
 * Resolver 구현체에서 사용한다.
 */
@Component
public class PalaceTenGodAnalyzer {

    /**
     * =================================================
     * 공식 12-3 API
     * =================================================
     *
     * Resolver는 기존 프로젝트의
     * HiddenStem/TenGod 엔진을 연결한 구현체를 받는다.
     */
    public PalaceTenGodAnalysis analyze(

            PalacePositionAnalysis positionAnalysis,

            PalaceTenGodSourceResolver sourceResolver

    ) {

        Objects.requireNonNull(
                positionAnalysis,
                "12-2 PalacePositionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                sourceResolver,
                "PalaceTenGodSourceResolver가 필요합니다."
        );

        List<PalaceResolvedTenGodSource> resolvedSources =
                sourceResolver.resolve(
                        positionAnalysis
                );

        resolvedSources =
                resolvedSources == null
                        ? List.of()
                        : List.copyOf(
                        resolvedSources
                );

        List<PalaceTenGodEvidence> evidences =
                new ArrayList<>();

        for (
                PalaceResolvedTenGodSource source
                : resolvedSources
        ) {

            Objects.requireNonNull(
                    source,
                    "해결된 Palace 십성 Source는 null일 수 없습니다."
            );

            /*
             * =================================================
             * 존재하지 않는 Palace 방어
             * =================================================
             */

            if (
                    !positionAnalysis
                            .containsPosition(
                                    source.position()
                            )
            ) {

                throw new IllegalArgumentException(
                        "Resolver가 존재하지 않는 Palace의 십성을 반환했습니다: "
                                + source.position()
                );
            }

            /*
             * =================================================
             * DAY 천간 Anchor 방어
             * =================================================
             *
             * Resolver 구현체에서 실수하더라도
             * 여기서 한 번 더 막는다.
             */

            if (
                    source.position()
                            == PalacePosition.DAY

                            && source.sourceType()
                            .visibleStem()
            ) {

                throw new IllegalArgumentException(
                        "DAY 천간은 일간 Anchor이므로 외부 십성 Source로 반환할 수 없습니다."
                );
            }

            PalacePositionContextType positionContext =
                    positionAnalysis
                            .contextOf(
                                    source.position()
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "Palace Context를 찾을 수 없습니다: "
                                                            + source.position()
                                            )
                            );

            evidences.add(
                    new PalaceTenGodEvidence(
                            positionAnalysis.dayMaster(),
                            source.position(),
                            positionContext,
                            source.sourceType(),
                            source.sourceStem(),
                            source.tenGodType()
                    )
            );
        }

        /*
         * =================================================
         * 정렬
         *
         * Palace:
         * YEAR -> MONTH -> DAY -> HOUR
         *
         * Source:
         * Visible -> Main -> Middle -> Residual
         *
         * 중요도 점수가 아니라
         * 출력 안정성을 위한 순서다.
         * =================================================
         */

        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        PalaceTenGodEvidence evidence
                                ) ->
                                        evidence.position()
                                                .order()
                        )
                        .thenComparingInt(
                                evidence ->
                                        sourceOrder(
                                                evidence
                                                        .sourceType()
                                        )
                        )
        );

        return new PalaceTenGodAnalysis(
                positionAnalysis.dayMaster(),
                positionAnalysis,
                evidences,
                positionAnalysis.knownPalaceCount(),
                positionAnalysis.completeChart()
        );
    }

    private int sourceOrder(
            com.dajeom.type.PalaceTenGodSourceType sourceType
    ) {

        return switch (
                sourceType
                ) {

            case VISIBLE_STEM ->
                    0;

            case BRANCH_MAIN ->
                    1;

            case BRANCH_MIDDLE ->
                    2;

            case BRANCH_RESIDUAL ->
                    3;
        };
    }
}