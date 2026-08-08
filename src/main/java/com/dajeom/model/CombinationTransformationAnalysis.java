package com.dajeom.model;

import com.dajeom.type.TransformationStatus;

import java.util.List;
import java.util.Objects;

/**
 * 원국 전체의 합화 분석 결과.
 */
public record CombinationTransformationAnalysis(

        List<CombinationTransformation> transformations,

        String ruleVersion

) {

    public CombinationTransformationAnalysis {

        Objects.requireNonNull(
                transformations,
                "합화 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "Rule 버전은 필수입니다."
        );

        transformations =
                List.copyOf(
                        transformations
                );
    }

    public boolean hasCombination() {

        return !transformations.isEmpty();
    }

    public boolean hasEstablishedTransformation() {

        return transformations
                .stream()
                .anyMatch(
                        CombinationTransformation::isEstablished
                );
    }

    public boolean hasUndeterminedTransformation() {

        return transformations
                .stream()
                .anyMatch(
                        transformation ->
                                transformation.status()
                                        == TransformationStatus.UNDETERMINED
                );
    }

    /**
     * 실제 오행 구조에 대한 추가 검토가
     * 필요한 상태인지 확인한다.
     *
     * 합화가 확실히 불성립한 경우에는
     * 오행 구조 변경이 필요하지 않다.
     */
    public boolean requiresElementAdjustment() {

        return hasEstablishedTransformation()
                || hasUndeterminedTransformation();
    }
}