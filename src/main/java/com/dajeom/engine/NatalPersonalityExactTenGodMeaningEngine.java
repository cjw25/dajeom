package com.dajeom.engine;

import com.dajeom.constant.NatalExactTenGodMeaningPolicyRegistry;
import com.dajeom.model.NatalExactTenGodMeaningPolicy;
import com.dajeom.model.NatalPersonalityExactTenGodFact;
import com.dajeom.model.NatalPersonalityExactTenGodMeaning;
import com.dajeom.model.NatalPersonalityStatementContract;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 최종 Personality Contract에 보존된
 * 정확한 10십성 Fact를
 * 사용자 Meaning 경계로 변환한다.
 *
 * =================================================
 *
 * 하지 않는 것
 *
 * - 새로운 십성 세력 계산
 * - 새 점수 계산
 * - 십성 둘 이상 합성
 * - 사건 예측
 * - 5개 Family로 재통합
 *
 * =================================================
 *
 * 정확한 십성 하나
 * ->
 * 해당 십성 하나의 Meaning Policy
 *
 * 만 연결한다.
 */
@Component
public class NatalPersonalityExactTenGodMeaningEngine {

    public List<NatalPersonalityExactTenGodMeaning> analyze(
            NatalPersonalityStatementContract contract
    ) {

        Objects.requireNonNull(
                contract,
                "Exact TenGod Meaning 분석에는 Personality Statement Contract가 필요합니다."
        );

        if (
                !contract.hasExactTenGodContract()
        ) {

            return List.of();
        }

        List<NatalPersonalityExactTenGodMeaning> result =
                new ArrayList<>();

        for (
                NatalPersonalityExactTenGodFact fact
                : contract.exactTenGodFacts()
        ) {

            if (
                    fact == null
                            || !fact.directOutput()
            ) {

                continue;
            }

            NatalExactTenGodMeaningPolicy policy =
                    NatalExactTenGodMeaningPolicyRegistry
                            .require(
                                    fact.tenGodType()
                            );

            /*
             * PRIMARY이고
             * 기존 명리 계산에서 구조적으로 강한 경우만
             * 강한 표현을 허용한다.
             */
            boolean strongExpressionAllowed =
                    fact.primary()
                            && fact.structurallyStrong();

            /*
             * 출생시간 미상 등으로
             * 원국이 완전하지 않은 경우에는
             * 제한 표현이 필요하다.
             */
            boolean qualificationRequired =
                    !fact.completeChart();

            result.add(
                    new NatalPersonalityExactTenGodMeaning(
                            fact,
                            policy,
                            strongExpressionAllowed,
                            qualificationRequired
                    )
            );
        }

        return List.copyOf(
                result
        );
    }
}