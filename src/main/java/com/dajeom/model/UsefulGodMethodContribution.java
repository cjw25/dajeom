package com.dajeom.model;

import com.dajeom.type.UsefulGodMethodApplicability;
import com.dajeom.type.UsefulGodMethodType;
import com.dajeom.type.UsefulGodSourceCandidateState;

import java.util.Objects;

/**
 * 정밀화 7차-8.
 *
 * 특정 五行에 대해
 * 하나의 용신법이 어떤 판단을 내렸는지를
 * 공통 구조로 정규화한 Evidence.
 *
 * =================================================
 *
 * directConflict
 *
 * 단순히 NOT_SELECTED라는 의미가 아니다.
 *
 * 해당 용신법 내부에서:
 *
 * - 신강신약 직접 모순
 * - 격국 직접 모순
 * - 다른 핵심 병 구조 악화
 * - 종격 흐름 파괴
 *
 * 등의 실제 반대 Evidence가 존재하는 경우다.
 *
 * =================================================
 *
 * caution
 *
 * 직접 모순까지는 아니지만:
 *
 * - STRAINED
 * - 오행 과다
 * - 혼잡
 * - 조건부 구조
 *
 * 등 추가 검토가 필요한 상태.
 *
 * =================================================
 *
 * followFlowBreaking
 *
 * 실제 종격 모드에서
 * 종하는 흐름을 깨는 오행인지.
 *
 * FOLLOW_STRUCTURE 방법에만 사용한다.
 */
public record UsefulGodMethodContribution(

        UsefulGodMethodType methodType,

        UsefulGodMethodApplicability methodApplicability,

        UsefulGodSourceCandidateState candidateState,

        boolean directConflict,

        boolean caution,

        boolean followFlowBreaking

) {

    public UsefulGodMethodContribution {

        Objects.requireNonNull(
                methodType,
                "용신법 유형이 필요합니다."
        );

        Objects.requireNonNull(
                methodApplicability,
                "용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                candidateState,
                "방법별 후보 상태가 필요합니다."
        );

        if (
                methodApplicability.inactive()
        ) {

            if (
                    !candidateState.notSelected()
            ) {

                throw new IllegalArgumentException(
                        "INACTIVE 용신법은 후보를 선택할 수 없습니다."
                );
            }

            if (
                    directConflict
                            || caution
                            || followFlowBreaking
            ) {

                throw new IllegalArgumentException(
                        "INACTIVE 용신법에는 활성 충돌·주의 Evidence를 지정할 수 없습니다."
                );
            }
        }

        if (
                followFlowBreaking

                        && methodType
                        != UsefulGodMethodType.FOLLOW_STRUCTURE
        ) {

            throw new IllegalArgumentException(
                    "종격 흐름 파괴 Evidence는 FOLLOW_STRUCTURE 방법에만 사용할 수 있습니다."
            );
        }

        if (
                directConflict
                        && candidateState.selected()
        ) {

            throw new IllegalArgumentException(
                    "직접 충돌이 존재하는 방법별 후보를 PRIMARY/SECONDARY 상태로 둘 수 없습니다."
            );
        }
    }

    public boolean coreMethod() {

        return methodApplicability.core();
    }

    public boolean activeMethod() {

        return methodApplicability.active();
    }

    public boolean conditionalMethod() {

        return methodApplicability.conditional();
    }

    public boolean meaningfulMethod() {

        return methodApplicability.meaningful();
    }

    public boolean applicableMethod() {

        return methodApplicability.applicable();
    }

    public boolean primary() {

        return candidateState.primary();
    }

    public boolean secondary() {

        return candidateState.secondary();
    }

    public boolean conditionalCandidate() {

        return candidateState.conditional();
    }

    public boolean selected() {

        return candidateState.selected();
    }

    public boolean candidate() {

        return candidateState.candidate();
    }

    public boolean corePrimary() {

        return coreMethod()
                && primary();
    }

    public boolean coreSelected() {

        return coreMethod()
                && selected();
    }

    public boolean meaningfulSelected() {

        return meaningfulMethod()
                && selected();
    }

    public boolean coreConflict() {

        return coreMethod()
                && directConflict;
    }

    public boolean activeConflict() {

        return activeMethod()
                && directConflict;
    }

    public boolean coreConditionalWarning() {

        return coreMethod()
                && conditionalCandidate();
    }
}