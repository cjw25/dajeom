package com.dajeom.resolver;

import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckCandidate;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LuckIntegratedSegmentAnalysis;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 18차-2.
 *
 * 현재 16차 Timeline Segment가 사용하는
 * 13-6 DecadeLuckPeriodContextAnalysis를
 * 생활영역 Evidence Source로 연결한다.
 *
 * =================================================
 *
 * 책임:
 *
 * ACTIVE Segment
 * ->
 * DecadeLuckCandidate
 * ->
 * 동일 sequenceNumber의
 * DecadeLuckPeriodContextAnalysis
 * ->
 * DECADE_CONTEXT Evidence Reference
 *
 * =================================================
 *
 * ACTIVE가 아닌 Segment:
 *
 * BEFORE_FIRST
 * AFTER_GENERATED_RANGE
 * UNAVAILABLE
 *
 * ->
 * Optional.empty()
 *
 * =================================================
 *
 * ACTIVE 상태더라도
 * 13-6 Context가 실제로 존재하지 않는 경우에는
 * 없는 Context를 만들어내지 않고
 * Optional.empty()를 반환한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Resolver에서는
 * 대운 분석을 다시 계산하지 않는다.
 *
 * DecadeLuckAnalysis에 이미 존재하는
 * 13-6 Context 객체를 source에 그대로 연결한다.
 *
 * 또한 Segment의 시간범위나
 * DEFINITE/POSSIBLE 상태를
 * DECADE_CONTEXT Reference 안에 복제하지 않는다.
 *
 * 그것은 LuckIntegratedSegmentAnalysis와
 * LuckTimelineSegment가 이미 보존하고 있다.
 */
@Component
public class DefaultLifeDomainDecadeContextSourceResolver
        implements LifeDomainDecadeContextSourceResolver {

    /**
     * =================================================
     * 공식 18-2 Boundary 구현
     * =================================================
     */
    @Override
    public Optional<LifeDomainEvidenceReference<?>>
    resolveDecadeContext(
            LuckIntegratedSegmentAnalysis segmentAnalysis
    ) {

        Objects.requireNonNull(
                segmentAnalysis,
                "대운 Context Evidence 연결에는 LuckIntegratedSegmentAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * ACTIVE가 아닌 Timeline Segment
         * =================================================
         *
         * 첫 대운 이전,
         * 생성 대운 범위 이후,
         * 대운 Sequence 사용 불가
         *
         * 등의 Segment에서는
         * DECADE_CONTEXT를 만들지 않는다.
         */

        if (
                !segmentAnalysis
                        .hasActiveDecade()
        ) {

            return Optional.empty();
        }

        /*
         * =================================================
         * ACTIVE Segment라면
         * 실제 DecadeLuckCandidate가 존재해야 한다.
         * =================================================
         */

        DecadeLuckCandidate candidate =
                segmentAnalysis
                        .decadeCandidate()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "ACTIVE Timeline Segment에 DecadeLuckCandidate가 없습니다."
                                        )
                        );

        DecadeLuckAnalysis decadeLuckAnalysis =
                Objects.requireNonNull(
                        segmentAnalysis
                                .decadeLuckAnalysis(),
                        "ACTIVE Segment에는 DecadeLuckAnalysis가 필요합니다."
                );

        /*
         * =================================================
         * 13-6 분석 자체를 사용할 수 없는 경우
         * =================================================
         *
         * Boundary 계약상 ACTIVE 상태더라도
         * 기존 대운 Context를 사용할 수 없다면
         * Optional.empty()를 허용한다.
         *
         * 없는 Context를 새로 계산하지 않는다.
         */

        if (
                !decadeLuckAnalysis
                        .analysisAvailable()
        ) {

            return Optional.empty();
        }

        /*
         * =================================================
         * 현재 Candidate의 sequenceNumber로
         * 기존 13-6 Context 조회
         * =================================================
         */

        Optional<DecadeLuckPeriodContextAnalysis>
                contextOptional =
                decadeLuckAnalysis
                        .contextOf(
                                candidate
                                        .sequenceNumber()
                        );

        if (
                contextOptional.isEmpty()
        ) {

            return Optional.empty();
        }

        DecadeLuckPeriodContextAnalysis decadeContext =
                contextOptional
                        .orElseThrow();

        /*
         * =================================================
         * Period Identity 검증
         * =================================================
         *
         * 같은 sequenceNumber라는 이유만으로
         * 다른 DecadeLuckPeriod 객체를
         * 허용하지 않는다.
         */

        if (
                decadeContext
                        .period()
                        != candidate
                        .period()
        ) {

            throw new IllegalArgumentException(
                    "16차 DecadeLuckCandidate와 13-6 DecadeLuckPeriodContextAnalysis가 "
                            + "서로 다른 DecadeLuckPeriod를 참조합니다. sequenceNumber="
                            + candidate.sequenceNumber()
            );
        }

        /*
         * =================================================
         * 일간 Identity 검증
         * =================================================
         */

        if (
                decadeContext
                        .dayMaster()
                        != decadeLuckAnalysis
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "13-6 DecadeLuckPeriodContextAnalysis와 DecadeLuckAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                decadeLuckAnalysis
                        .dayMaster()
                        != segmentAnalysis
                        .annualLuckAnalysis()
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "13차 대운 분석과 현재 16차 Timeline Segment의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 실제 upstream 객체 연결
         * =================================================
         *
         * sourceKey는 Segment별 키가 아니다.
         *
         * 동일 DecadeLuckPeriodContextAnalysis가
         * 여러 Timeline Segment에서 재사용될 때
         * 동일 Reference로 canonicalize될 수 있도록
         * 대운 sequenceNumber만 사용한다.
         */

        LifeDomainEvidenceReference<
                DecadeLuckPeriodContextAnalysis
                > reference =
                new LifeDomainEvidenceReference<>(
                        LifeDomainEvidenceSourceType
                                .DECADE_CONTEXT,

                        LifeDomainEvidenceSourceType
                                .DECADE_CONTEXT
                                .tier(),

                        "DECADE_CONTEXT:"
                                + candidate
                                .sequenceNumber(),

                        decadeContext,

                        candidate
                                .sequenceNumber()
                                + "대운 Context",

                        List.of(
                                "13-6 DecadeLuckPeriodContextAnalysis 원본 객체를 생활영역 시간축 Context로 연결합니다.",

                                "현재 16차 ACTIVE Timeline Segment의 DecadeLuckCandidate와 동일 DecadeLuckPeriod를 참조합니다.",

                                "대운의 교운시점 불확실성 및 DEFINITE/POSSIBLE 여부는 LifeDomain Evidence로 복제하지 않고 16차 Timeline 구조에 그대로 보존합니다."
                        )
                );

        return Optional.of(
                reference
        );
    }
}