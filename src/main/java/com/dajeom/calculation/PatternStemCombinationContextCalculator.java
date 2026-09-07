package com.dajeom.calculation;

import com.dajeom.domain.PatternStemCombinationAnalysis;
import com.dajeom.domain.PatternStemCombinationContext;
import com.dajeom.domain.PatternStemCombinationOccurrence;
import com.dajeom.domain.PatternStemCombinationParticipation;
import com.dajeom.domain.PatternTenGodNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 天干五合 occurrence들을
 * 격국 판정에서 사용하기 쉬운 Context로 변환한다.
 *
 * =================================================
 *
 * 주요 기능:
 *
 * 1. 십성별 합 여부
 *
 * 2. 日主와의 합 여부
 *
 * 3. 동일 천간 노드가
 *    복수의 五合 후보에 걸리는지 확인
 *
 * =================================================
 *
 * 아직:
 *
 * - 合化
 * - 合去
 * - 合留
 * - 爭合 확정
 *
 * 은 판단하지 않는다.
 */
@Component
public class PatternStemCombinationContextCalculator {

    public PatternStemCombinationContext analyze(
            PatternStemCombinationAnalysis combinationAnalysis
    ) {

        Objects.requireNonNull(
                combinationAnalysis,
                "PatternStemCombinationAnalysis는 필수입니다."
        );

        /*
         * 같은 노드가 몇 개의 합 occurrence에
         * 참여하는지 모은다.
         */
        Map<
                PatternTenGodNode,
                List<PatternStemCombinationOccurrence>
                > occurrenceMap =
                new LinkedHashMap<>();

        for (
                PatternStemCombinationOccurrence occurrence
                : combinationAnalysis.combinations()
        ) {

            occurrenceMap
                    .computeIfAbsent(
                            occurrence.left(),
                            key -> new ArrayList<>()
                    )
                    .add(
                            occurrence
                    );

            occurrenceMap
                    .computeIfAbsent(
                            occurrence.right(),
                            key -> new ArrayList<>()
                    )
                    .add(
                            occurrence
                    );
        }

        List<PatternStemCombinationParticipation> participations =
                occurrenceMap
                        .entrySet()
                        .stream()
                        .map(
                                entry ->
                                        new PatternStemCombinationParticipation(
                                                entry.getKey(),
                                                entry.getValue()
                                        )
                        )
                        .toList();

        return new PatternStemCombinationContext(
                combinationAnalysis.dayMaster(),
                combinationAnalysis.combinations(),
                participations
        );
    }
}