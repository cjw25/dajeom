package com.dajeom.model;

import com.dajeom.type.LuckUncertaintyCause;
import com.dajeom.type.LuckUncertaintyKind;
import com.dajeom.type.LuckUncertaintyScope;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 16차-6.
 *
 * 운 분석 불확실성의 최소 Evidence.
 *
 * =================================================
 *
 * affectsCurrentResult
 *
 * true
 *
 * 현재 조회 결과를 실제로
 * 확정할 수 없게 만드는 원인.
 *
 * false
 *
 * 입력 또는 원국에 제한은 존재하지만
 * 현재 운 선택 결과 자체에는
 * 직접적인 복수 Candidate를 만들지 않은 경우.
 *
 * =================================================
 *
 * 예:
 *
 * 날짜만 입력했지만
 * 입춘/절입/교운이 전혀 없는 날짜라면:
 *
 * REFERENCE_TIME_UNSPECIFIED
 * affectsCurrentResult = false
 *
 * =================================================
 *
 * 반대로:
 *
 * 입춘일에 날짜만 입력했다면:
 *
 * REFERENCE_TIME_UNSPECIFIED
 * affectsCurrentResult = true
 *
 * +
 *
 * ANNUAL_BOUNDARY_WITHOUT_REFERENCE_TIME
 * affectsCurrentResult = true
 */
public record LuckUncertaintyEvidence(

        LuckUncertaintyCause cause,

        Set<LuckUncertaintyScope> scopes,

        boolean affectsCurrentResult,

        String summary,

        List<String> reasons

) {

    public LuckUncertaintyEvidence {

        Objects.requireNonNull(
                cause,
                "불확실성 Evidence에는 Cause가 필요합니다."
        );

        scopes =
                scopes == null
                        ? Set.of()
                        : Set.copyOf(
                        scopes
                );

        if (
                scopes.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "불확실성 Evidence에는 최소 하나의 Scope가 필요합니다."
            );
        }

        for (
                LuckUncertaintyScope scope
                : scopes
        ) {

            Objects.requireNonNull(
                    scope,
                    "LuckUncertaintyScope는 null일 수 없습니다."
            );
        }

        if (
                summary == null
                        || summary.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "불확실성 Evidence에는 summary가 필요합니다."
            );
        }

        summary =
                summary.trim();

        reasons =
                reasons == null
                        ? List.of()
                        : reasons
                        .stream()
                        .filter(
                                Objects::nonNull
                        )
                        .map(
                                String::trim
                        )
                        .filter(
                                reason ->
                                        !reason.isBlank()
                        )
                        .toList();

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "불확실성 Evidence에는 최소 하나의 reason이 필요합니다."
            );
        }
    }

    public LuckUncertaintyKind kind() {

        return cause
                .kind();
    }

    public boolean involves(
            LuckUncertaintyScope scope
    ) {

        return scope != null
                && scopes.contains(
                scope
        );
    }

    public boolean currentResultAffected() {

        return affectsCurrentResult;
    }

    public boolean informationalOnly() {

        return !affectsCurrentResult;
    }
}