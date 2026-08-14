package com.dajeom.model;

import com.dajeom.type.UsefulGodMethodApplicability;
import com.dajeom.type.UsefulGodMethodReasonType;
import com.dajeom.type.UsefulGodMethodType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-1.
 *
 * 하나의 용신법이 현재 명식에서
 * 어느 정도 적용되는지에 대한 Evidence.
 *
 * =================================================
 *
 * directEvidencePresent
 *
 * 해당 용신법을 실제로 적용해야 할
 * 직접적인 구조 근거가 있는가.
 *
 * 예:
 *
 * 조후 HIGH
 * -> true
 *
 * 통관 NONE
 * -> false
 *
 * =================================================
 *
 * suppressedByFollowStructure
 *
 * 종격 후보 때문에
 * 일반 억부 또는 일반 격국 판단을
 * 우선 적용하지 않는 상태인가.
 *
 * 현재는 주로 BALANCING / STRUCTURE에서 사용한다.
 */
public record UsefulGodMethodEvidence(

        UsefulGodMethodType methodType,

        UsefulGodMethodApplicability applicability,

        List<UsefulGodMethodReasonType> reasons,

        boolean directEvidencePresent,

        boolean suppressedByFollowStructure

) {

    public UsefulGodMethodEvidence {

        Objects.requireNonNull(
                methodType,
                "용신법 유형이 필요합니다."
        );

        Objects.requireNonNull(
                applicability,
                "용신법 적용도가 필요합니다."
        );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                reasons
                        .stream()
                        .distinct()
                        .count()
                        != reasons.size()
        ) {

            throw new IllegalArgumentException(
                    "용신법 적용 근거가 중복되었습니다."
            );
        }

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "용신법 적용도에는 하나 이상의 근거가 필요합니다."
            );
        }

        if (
                applicability.inactive()
                        && directEvidencePresent
        ) {

            throw new IllegalArgumentException(
                    "INACTIVE 용신법에 직접 적용 근거를 지정할 수 없습니다."
            );
        }

        if (
                suppressedByFollowStructure

                        && methodType
                        != UsefulGodMethodType.BALANCING

                        && methodType
                        != UsefulGodMethodType.STRUCTURE
        ) {

            throw new IllegalArgumentException(
                    "종격에 의한 일반 방법 억제는 억부·격국용신법에만 적용합니다."
            );
        }
    }

    public boolean core() {

        return applicability
                .core();
    }

    public boolean active() {

        return applicability
                .active();
    }

    public boolean conditional() {

        return applicability
                .conditional();
    }

    public boolean inactive() {

        return applicability
                .inactive();
    }

    public boolean applicable() {

        return applicability
                .applicable();
    }

    public boolean meaningful() {

        return applicability
                .meaningful();
    }

    public boolean hasReason(
            UsefulGodMethodReasonType reason
    ) {

        if (
                reason == null
        ) {

            return false;
        }

        return reasons.contains(
                reason
        );
    }
}