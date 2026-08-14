package com.dajeom.engine;

import com.dajeom.model.LifeDomainAnalysis;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainEvidenceResolution;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.LifeDomainSegmentAnalysis;
import com.dajeom.model.LifeDomainSegmentEvidence;
import com.dajeom.model.LuckIntegratedSegmentAnalysis;
import com.dajeom.resolver.LifeDomainEvidenceResolver;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 생활영역 분석 실행 Engine.
 *
 * =================================================
 *
 * 입력:
 *
 * LifeDomainEvidenceResolution
 *
 * =================================================
 *
 * 사용하는 기존 공식 Boundary:
 *
 * LifeDomainEvidenceResolver
 *
 * - resolveNatalBundle(...)
 * - resolveSegmentBundle(...)
 *
 * LifeDomainRuleEngine
 *
 * - evaluate(...)
 *
 * =================================================
 *
 * 이 Engine에서 하지 않는 것:
 *
 * - 3~17차 명리 재계산
 * - Evidence Source 재구성
 * - 신강신약 재판정
 * - 격국 재판정
 * - 용신 재선정
 * - 합충형파해 재계산
 * - 합화 재계산
 * - 대운/세운/월운 재계산
 * - 신살 재계산
 *
 * 이미 만들어진 Evidence Bundle에
 * 등록된 LifeDomainRule을 실행하고
 * 결과를 구조화하는 orchestration만 담당한다.
 *
 * =================================================
 *
 * 원국 실행:
 *
 * Domain별 Natal Bundle
 * ↓
 * LifeDomainRuleEngine.evaluate(...)
 * ↓
 * natalMatchesByDomain
 *
 * =================================================
 *
 * Segment 실행:
 *
 * 원국
 * +
 * 현재 Segment Temporal
 * +
 * 현재 Segment Shinsal
 *
 * Bundle
 *
 * ↓
 *
 * LifeDomainRuleEngine.evaluate(...)
 *
 * ↓
 *
 * 그중 현재 Segment의 Evidence를
 * 실제 사용한 Match만 보존
 *
 * =================================================
 *
 * 중요한 이유:
 *
 * Segment Bundle에는 원국 Evidence도 들어 있으므로
 * 단순히 RuleEngine 결과를 전부 저장하면:
 *
 * WealthCapacityRule
 * CareerOfficerStructureRule
 * ...
 *
 * 같은 원국 Match가
 * 매 월 Segment마다 반복된다.
 *
 * 따라서:
 *
 * natalMatches
 *
 * 와
 *
 * segment-local matches
 *
 * 를 분리한다.
 *
 * =================================================
 *
 * "segment-local" 기준:
 *
 * Match.evidences 안에
 *
 * 현재 LifeDomainSegmentEvidence가 가진:
 *
 * - temporalEvidences
 * - shinsalEvidences
 *
 * 중 최소 하나가 Object Identity로
 * 실제 포함되어 있어야 한다.
 *
 * =================================================
 *
 * 결과 상태는 합산하지 않는다.
 *
 * 서로 다른 Rule의:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 * CONTEXTUAL
 *
 * 이 동시에 존재할 수 있으며
 * 모두 그대로 보존한다.
 */
@Component
public class LifeDomainAnalysisEngine {

    private final LifeDomainEvidenceResolver
            evidenceResolver;

    private final LifeDomainRuleEngine
            ruleEngine;

    public LifeDomainAnalysisEngine(

            LifeDomainEvidenceResolver evidenceResolver,

            LifeDomainRuleEngine ruleEngine

    ) {

        this.evidenceResolver =
                Objects.requireNonNull(
                        evidenceResolver,
                        "LifeDomainEvidenceResolver가 필요합니다."
                );

        this.ruleEngine =
                Objects.requireNonNull(
                        ruleEngine,
                        "LifeDomainRuleEngine이 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 18차 실행 진입점
     * =================================================
     *
     * EvidenceResolution은 이미 18차 Evidence 연결까지
     * 끝난 상태여야 한다.
     *
     * 이 Engine은 Resolution을 다시 만들지 않는다.
     */
    public LifeDomainAnalysis analyze(
            LifeDomainEvidenceResolution resolution
    ) {

        Objects.requireNonNull(
                resolution,
                "LifeDomainAnalysisEngine에는 LifeDomainEvidenceResolution이 필요합니다."
        );

        /*
         * =================================================
         * 1.
         * 원국 Domain Rule 실행
         * =================================================
         */

        EnumMap<
                LifeDomain,
                List<LifeDomainRuleMatch>
                > natalMatchesByDomain =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            LifeDomainEvidenceBundle natalBundle =
                    evidenceResolver
                            .resolveNatalBundle(
                                    resolution,
                                    domain
                            );

            List<LifeDomainRuleMatch> matches =
                    ruleEngine
                            .evaluate(
                                    natalBundle
                            );

            natalMatchesByDomain.put(
                    domain,
                    matches
            );
        }

        /*
         * =================================================
         * 2.
         * Timeline Segment Rule 실행
         * =================================================
         */

        List<LifeDomainSegmentAnalysis>
                segmentAnalyses =
                new ArrayList<>(
                        resolution.segmentCount()
                );

        for (
                LifeDomainSegmentEvidence segmentEvidence
                : resolution.segmentEvidences()
        ) {

            LuckIntegratedSegmentAnalysis segmentAnalysis =
                    segmentEvidence
                            .segmentAnalysis();

            EnumMap<
                    LifeDomain,
                    List<LifeDomainRuleMatch>
                    > matchesByDomain =
                    new EnumMap<>(
                            LifeDomain.class
                    );

            for (
                    LifeDomain domain
                    : LifeDomain.values()
            ) {

                /*
                 * =================================================
                 * 현재 Segment용 전체 Bundle
                 *
                 * 원국
                 * +
                 * 대운/세운/월운
                 * +
                 * 해당 운 신살
                 * =================================================
                 */

                LifeDomainEvidenceBundle segmentBundle =
                        evidenceResolver
                                .resolveSegmentBundle(
                                        resolution,
                                        domain,
                                        segmentAnalysis
                                );

                List<LifeDomainRuleMatch> evaluated =
                        ruleEngine
                                .evaluate(
                                        segmentBundle
                                );

                /*
                 * =================================================
                 * 원국-only Match 제거
                 * =================================================
                 *
                 * Segment Bundle에서 다시 실행된
                 * 원국 Rule 결과는 natalMatchesByDomain에
                 * 이미 존재한다.
                 *
                 * 현재 Segment의 Evidence를
                 * 실제 사용한 Match만 남긴다.
                 */

                List<LifeDomainRuleMatch> localMatches =
                        filterSegmentLocalMatches(
                                evaluated,
                                segmentEvidence
                        );

                matchesByDomain.put(
                        domain,
                        localMatches
                );
            }

            segmentAnalyses.add(
                    new LifeDomainSegmentAnalysis(
                            segmentAnalysis,
                            matchesByDomain
                    )
            );
        }

        /*
         * =================================================
         * 3.
         * 최종 Aggregate
         * =================================================
         */

        return new LifeDomainAnalysis(
                resolution,
                natalMatchesByDomain,
                segmentAnalyses
        );
    }

    /*
     * =================================================
     * Segment-local Match 필터
     * =================================================
     *
     * Match가 현재 Segment 고유 Evidence를
     * 최소 하나 실제 사용했는지 Object Identity로 확인.
     *
     * 현재 Segment 고유 Source:
     *
     * temporalEvidences
     *
     * +
     *
     * shinsalEvidences
     *
     * =================================================
     *
     * 예:
     *
     * WealthCapacityRule
     *
     * TEN_GOD_COMPOSITE
     * DAY_MASTER_STRENGTH
     *
     * 만 사용
     *
     * -> 원국-only
     * -> Segment 결과에서는 제외
     *
     * =================================================
     *
     * WealthAnnualContextRule
     *
     * ANNUAL_CONTEXT 사용
     *
     * -> 현재 Segment Local
     * -> 포함
     *
     * =================================================
     *
     * CareerTravelHorseAuxiliaryRule
     *
     * TEN_GOD_COMPOSITE
     * +
     * ANNUAL_CONTEXT
     * +
     * SHINSAL
     *
     * -> 현재 Segment Local
     * -> 포함
     */
    private List<LifeDomainRuleMatch>
    filterSegmentLocalMatches(

            List<LifeDomainRuleMatch> evaluated,

            LifeDomainSegmentEvidence segmentEvidence

    ) {

        Objects.requireNonNull(
                evaluated,
                "Segment Rule 실행 결과가 필요합니다."
        );

        Objects.requireNonNull(
                segmentEvidence,
                "LifeDomainSegmentEvidence가 필요합니다."
        );

        Set<LifeDomainEvidenceReference<?>>
                localReferences =
                Collections.newSetFromMap(
                        new IdentityHashMap<>()
                );

        /*
         * =================================================
         * Temporal
         * =================================================
         */

        for (
                LifeDomainEvidenceReference<?> reference
                : segmentEvidence.temporalEvidences()
        ) {

            localReferences.add(
                    Objects.requireNonNull(
                            reference,
                            "Segment Temporal Evidence는 null일 수 없습니다."
                    )
            );
        }

        /*
         * =================================================
         * 운 Shinsal
         * =================================================
         */

        for (
                LifeDomainEvidenceReference<?> reference
                : segmentEvidence.shinsalEvidences()
        ) {

            localReferences.add(
                    Objects.requireNonNull(
                            reference,
                            "Segment Shinsal Evidence는 null일 수 없습니다."
                    )
            );
        }

        /*
         * 16차 Timeline Segment는
         * Annual/Monthly Context를 항상 가지므로
         * 일반적으로 localReferences는 비어 있지 않는다.
         *
         * 다만 이 Engine에서 별도의 가짜 Evidence를
         * 생성해서 보완하지 않는다.
         */

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : evaluated
        ) {

            Objects.requireNonNull(
                    match,
                    "LifeDomainRuleMatch는 null일 수 없습니다."
            );

            boolean usesSegmentLocalEvidence =
                    false;

            for (
                    LifeDomainEvidenceReference<?> evidence
                    : match.evidences()
            ) {

                if (
                        localReferences.contains(
                                evidence
                        )
                ) {

                    usesSegmentLocalEvidence =
                            true;

                    break;
                }
            }

            if (
                    usesSegmentLocalEvidence
            ) {

                result.add(
                        match
                );
            }
        }

        return List.copyOf(
                result
        );
    }
}