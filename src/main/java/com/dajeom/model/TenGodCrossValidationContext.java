package com.dajeom.model;

import com.dajeom.type.TenGodCorePatternType;

import java.util.Set;

/**
 * 정밀화 10차-9.
 *
 * 7차 용신 분석과 9차 합화 분석이
 * 10-8 Pattern에 어떤 영향을 주는지를
 * Pattern 단위로 전달하는 안정된 Boundary.
 *
 * =================================================
 *
 * 왜 별도 Context인가?
 *
 * 7차와 9차 내부에는:
 *
 * - 후보
 * - 방법론
 * - 경쟁
 * - 원기능 감소
 * - 목표 오행 활성화
 *
 * 등의 세부 Evidence가 많다.
 *
 * 10-9가 그 내부 구조에 직접 강하게 결합하면
 * 앞 단계 내부 Model 변경 때마다
 * 10-9 전체가 깨진다.
 *
 * 따라서 10-9에서는 최종적으로 필요한:
 *
 * "이 Pattern을 지지하는가?"
 * "충돌하는가?"
 * "합화 때문에 약화되는가?"
 *
 * 만 받는다.
 *
 * =================================================
 *
 * 서로 다른 Set에 같은 Pattern이 동시에
 * 포함되는 것을 허용한다.
 *
 * 예:
 *
 * usefulGodSupportedPatterns
 * +
 * usefulGodConflictedPatterns
 *
 * 양쪽에 모두 존재할 수 있다.
 *
 * 이는 오류가 아니라
 * 억부와 조후 등 방법론 사이의
 * 불일치를 보존하기 위함이다.
 */
public record TenGodCrossValidationContext(

        Set<TenGodCorePatternType> usefulGodSupportedPatterns,

        Set<TenGodCorePatternType> usefulGodConflictedPatterns,

        Set<TenGodCorePatternType> transformationSupportedPatterns,

        Set<TenGodCorePatternType> transformationWeakenedPatterns,

        Set<TenGodCorePatternType> transformationConflictedPatterns

) {

    public TenGodCrossValidationContext {

        usefulGodSupportedPatterns =
                safeCopy(
                        usefulGodSupportedPatterns
                );

        usefulGodConflictedPatterns =
                safeCopy(
                        usefulGodConflictedPatterns
                );

        transformationSupportedPatterns =
                safeCopy(
                        transformationSupportedPatterns
                );

        transformationWeakenedPatterns =
                safeCopy(
                        transformationWeakenedPatterns
                );

        transformationConflictedPatterns =
                safeCopy(
                        transformationConflictedPatterns
                );
    }

    public static TenGodCrossValidationContext neutral() {

        return new TenGodCrossValidationContext(
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of(),
                Set.of()
        );
    }

    public boolean usefulGodSupports(
            TenGodCorePatternType patternType
    ) {

        return patternType != null
                && usefulGodSupportedPatterns
                .contains(
                        patternType
                );
    }

    public boolean usefulGodConflicts(
            TenGodCorePatternType patternType
    ) {

        return patternType != null
                && usefulGodConflictedPatterns
                .contains(
                        patternType
                );
    }

    public boolean usefulGodDisagreement(
            TenGodCorePatternType patternType
    ) {

        return usefulGodSupports(
                patternType
        )
                && usefulGodConflicts(
                patternType
        );
    }

    public boolean transformationSupports(
            TenGodCorePatternType patternType
    ) {

        return patternType != null
                && transformationSupportedPatterns
                .contains(
                        patternType
                );
    }

    public boolean transformationWeakens(
            TenGodCorePatternType patternType
    ) {

        return patternType != null
                && transformationWeakenedPatterns
                .contains(
                        patternType
                );
    }

    public boolean transformationConflicts(
            TenGodCorePatternType patternType
    ) {

        return patternType != null
                && transformationConflictedPatterns
                .contains(
                        patternType
                );
    }

    public boolean transformationMixed(
            TenGodCorePatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return false;
        }

        return transformationSupports(
                patternType
        )
                && (
                transformationWeakens(
                        patternType
                )
                        || transformationConflicts(
                        patternType
                )
        );
    }

    public boolean neutralFor(
            TenGodCorePatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return true;
        }

        return !usefulGodSupports(
                patternType
        )
                && !usefulGodConflicts(
                patternType
        )
                && !transformationSupports(
                patternType
        )
                && !transformationWeakens(
                patternType
        )
                && !transformationConflicts(
                patternType
        );
    }

    private static Set<TenGodCorePatternType> safeCopy(
            Set<TenGodCorePatternType> source
    ) {

        return source == null
                ? Set.of()
                : Set.copyOf(
                source
        );
    }
}