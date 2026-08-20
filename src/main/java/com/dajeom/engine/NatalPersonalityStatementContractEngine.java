package com.dajeom.engine;

import com.dajeom.model.NatalPersonalityStatementBundle;
import com.dajeom.model.NatalPersonalityStatementBundleAnalysis;
import com.dajeom.model.NatalPersonalityStatementBundleContract;
import com.dajeom.model.NatalPersonalityStatementContract;
import com.dajeom.model.NatalPersonalityStatementSelection;
import com.dajeom.type.PersonalityStatementBundleObligation;
import com.dajeom.type.PersonalityStatementContractGuardrail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Personality Statement Bundle을
 * 최종 문장생성 Contract로 변환한다.
 *
 * =================================================
 *
 * 이 Engine은 새로운 명리판정을 하지 않는다.
 *
 * =================================================
 *
 * 하는 일:
 *
 * PRIMARY
 * →
 * Mandatory
 *
 * SUPPORTING
 * →
 * Supporting
 *
 * Strong Fact
 * →
 * Strong Expression 허용 Source
 *
 * INTERNAL_REFERENCE
 * →
 * 내부 참고
 *
 * BACKGROUND_CONTEXT
 * →
 * 전역 Context
 *
 * =================================================
 *
 * 그리고 최종적으로
 * Personality Statement 생성에서
 * 반드시 적용해야 하는 Guardrail을
 * Contract에 고정한다.
 */
@Component
public class NatalPersonalityStatementContractEngine {

    public NatalPersonalityStatementContract analyze(

            NatalPersonalityStatementBundleAnalysis
                    bundleAnalysis

    ) {

        Objects.requireNonNull(
                bundleAnalysis,
                "Personality Statement Bundle Analysis는 필수입니다."
        );

        List<NatalPersonalityStatementBundleContract>
                bundleContracts =
                new ArrayList<>();

        /*
         * =================================================
         * 모든 Bundle을
         * 정확히 하나의 Contract로 변환
         * =================================================
         */

        for (
                NatalPersonalityStatementBundle bundle
                : bundleAnalysis.bundles()
        ) {

            bundleContracts.add(
                    createBundleContract(
                            bundle
                    )
            );
        }

        /*
         * =================================================
         * Guardrail 전체 적용
         * =================================================
         */

        List<PersonalityStatementContractGuardrail> guardrails =
                List.copyOf(
                        Arrays.asList(
                                PersonalityStatementContractGuardrail
                                        .values()
                        )
                );

        return new NatalPersonalityStatementContract(

                bundleAnalysis,

                bundleContracts,

                bundleAnalysis.globalBackgroundContexts(),

                guardrails,

                bundleAnalysis.completeChart()
        );
    }

    /**
     * Bundle 하나의 Contract 생성.
     */
    private NatalPersonalityStatementBundleContract
    createBundleContract(

            NatalPersonalityStatementBundle bundle

    ) {

        PersonalityStatementBundleObligation obligation =
                bundle.primaryConnected()

                        ? PersonalityStatementBundleObligation
                        .REQUIRED

                        : PersonalityStatementBundleObligation
                        .OPTIONAL_SUPPORT;

        /*
         * =================================================
         * REQUIRED일 때만
         * PRIMARY를 Mandatory로 보존.
         *
         * SUPPORTING_CONNECTED는
         * PRIMARY를 임의 생성하지 않는다.
         * =================================================
         */

        List<NatalPersonalityStatementSelection>
                mandatorySelections =
                bundle.primaryConnected()

                        ? bundle.primarySelections()

                        : List.of();

        /*
         * =================================================
         * Strong Selection
         *
         * Bundle 전체가 아니라
         * 실제 Strong Fact만 보존한다.
         *
         * 그래야:
         *
         * 강한 Fact 1개 때문에
         * 같은 Bundle의 다른 약한 Fact까지
         * 강하게 표현되는 것을 막을 수 있다.
         * =================================================
         */

        List<NatalPersonalityStatementSelection>
                strongSelections =
                bundle
                        .directSelections()
                        .stream()
                        .filter(
                                selection ->
                                        selection
                                                .fact()
                                                .strongUserFacingCandidate()
                        )
                        .toList();

        return new NatalPersonalityStatementBundleContract(

                bundle,

                obligation,

                mandatorySelections,

                bundle.supportingSelections(),

                strongSelections,

                bundle.relatedInternalReferences(),

                bundle.involvedFunctions(),

                bundle.situations(),

                bundle.qualificationRequired(),

                bundle.completeChart()
        );
    }
}