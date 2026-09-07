package com.dajeom.calculation;

import com.dajeom.domain.OfficerKillingsNodeRef;
import com.dajeom.domain.PatternOfficerKillingsFinalPurificationInput;
import com.dajeom.domain.PatternOfficerKillingsStandardPurificationSnapshot;
import com.dajeom.domain.PatternOfficerKillingsYangBladeNodeSnapshot;

import java.util.Objects;

/**
 * 기존 프로젝트의 실제 官殺 분석 객체와
 * 羊刃合殺 분석 객체를
 *
 * PatternOfficerKillingsFinalPurificationInput
 *
 * 으로 변환하기 위한 Adapter 계약.
 *
 * =================================================
 * 타입
 * =================================================
 *
 * S
 *
 * 기존 표준 官殺 purification 분석 객체 타입.
 *
 * 예:
 *
 * PatternOfficerKillingsPurificationAnalysis
 *
 * -------------------------------------------------
 *
 * Y
 *
 * 기존 羊刃合殺 분석 객체 타입.
 *
 * =================================================
 * 구현 예시
 * =================================================
 *
 * class ExistingOfficerKillingsAdapter
 *         implements
 *         PatternOfficerKillingsFinalPurificationInputAdapter<
 *                 PatternOfficerKillingsPurificationAnalysis,
 *                 SomeYangBladeAnalysis
 *         >
 *
 * 처럼 실제 프로젝트 객체에 맞춰
 * 구현하면 된다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 Adapter 구현에서만
 * 기존 클래스의 구체적인 accessor를 알아야 한다.
 *
 * 그 아래의:
 *
 * - CompletionCalculator
 * - FinalPurificationCalculator
 * - LifeClaim
 * - Narrative
 *
 * 계층은 기존 분석 객체의 내부 API를 알 필요가 없다.
 */
public interface PatternOfficerKillingsFinalPurificationInputAdapter<S, Y> {

    /**
     * 기존 표준 官殺 분석 객체를
     * 정규화 Snapshot으로 변환한다.
     */
    PatternOfficerKillingsStandardPurificationSnapshot
    adaptStandard(
            S source
    );

    /**
     * 기존 羊刃合殺 분석 객체를
     * 개별 七殺 node Snapshot으로 변환한다.
     */
    PatternOfficerKillingsYangBladeNodeSnapshot
    adaptYangBlade(
            Y source
    );

    /**
     * 두 실제 분석 객체를 받아
     * 최종 purification pipeline 입력까지 조립한다.
     */
    default PatternOfficerKillingsFinalPurificationInput adapt(

            S standardSource,

            Y yangBladeSource

    ) {

        Objects.requireNonNull(
                standardSource,
                "표준 官殺 분석 객체는 필수입니다."
        );

        Objects.requireNonNull(
                yangBladeSource,
                "羊刃合殺 분석 객체는 필수입니다."
        );

        PatternOfficerKillingsStandardPurificationSnapshot standard =
                Objects.requireNonNull(
                        adaptStandard(
                                standardSource
                        ),
                        "adaptStandard() 결과는 null일 수 없습니다."
                );

        PatternOfficerKillingsYangBladeNodeSnapshot yangBlade =
                Objects.requireNonNull(
                        adaptYangBlade(
                                yangBladeSource
                        ),
                        "adaptYangBlade() 결과는 null일 수 없습니다."
                );

        /*
         * =================================================
         * 표준 정리가 이미 끝난 경우
         * =================================================
         *
         * 羊刃合殺 결과가 존재하면 안 된다.
         */

        if (
                standard.cleared()
                        &&
                        !yangBlade.empty()
        ) {

            throw new IllegalArgumentException(
                    "표준 官殺 정리가 이미 완료된 경우 후속 羊刃合殺 처리 결과를 가질 수 없습니다."
            );
        }

        /*
         * =================================================
         * 官殺混雜 자체가 아닌 경우
         * =================================================
         */

        if (
                !standard.mixed()
                        &&
                        !yangBlade.empty()
        ) {

            throw new IllegalArgumentException(
                    "官殺混雜이 아닌 경우 羊刃合殺 purification 결과를 가질 수 없습니다."
            );
        }

        /*
         * =================================================
         * resolved/review는 retained 七殺의 부분집합이어야 함
         * =================================================
         */

        if (
                !standard
                        .retainedKillingNodes()
                        .containsAll(
                                yangBlade.resolvedKillingNodes()
                        )
        ) {

            throw new IllegalArgumentException(
                    "羊刃合殺 resolved 노드 중 retained 七殺이 아닌 노드가 존재합니다."
            );
        }

        if (
                !standard
                        .retainedKillingNodes()
                        .containsAll(
                                yangBlade.reviewKillingNodes()
                        )
        ) {

            throw new IllegalArgumentException(
                    "羊刃合殺 review 노드 중 retained 七殺이 아닌 노드가 존재합니다."
            );
        }

        /*
         * =================================================
         * 표준 REVIEW 상태
         * =================================================
         *
         * retained 대상 자체가 확정되지 않은 단계라면
         * 후속 羊刃합살 resolved를 확정값으로 넘기지 않는다.
         */

        if (
                standard.reviewRequired()
                        &&
                        !yangBlade.empty()
        ) {

            throw new IllegalArgumentException(
                    "표준 官殺 purification이 REVIEW_REQUIRED라면 羊刃合殺 후속 결과를 확정값으로 사용할 수 없습니다."
            );
        }

        /*
         * =================================================
         * 최종 입력 조립
         * =================================================
         */

        return new PatternOfficerKillingsFinalPurificationInput(

                standard.mixed(),

                standard.cleared(),

                standard.reviewRequired(),

                standard.retainedKillingNodes(),

                yangBlade.resolvedKillingNodes(),

                yangBlade.reviewKillingNodes()
        );
    }

    /**
     * nodeKey 생성 시 사용할 수 있는
     * 최소 보조 helper.
     *
     * 실제 node 객체 전체의 toString()을
     * key로 사용하는 것은 피한다.
     *
     * 위치 + 역할처럼
     * 재실행해도 동일한 안정적인 값으로 만들어야 한다.
     */
    default OfficerKillingsNodeRef nodeRef(
            String stableNodeKey
    ) {

        return new OfficerKillingsNodeRef(
                stableNodeKey
        );
    }
}