package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.StructureDamageLevel;
import com.dajeom.type.StructureDamagePatternType;

import java.util.Objects;

/**
 * 정밀화 5차-6.
 *
 * 하나의 파격 패턴에 대한
 * 실제 Evidence.
 *
 * sourceBased 패턴:
 *
 * - 파격 오행
 * - 오행 최종세력
 * - 오행 비중
 * - 정확히 일치하는 천간/지장간 source 수
 *
 * 를 보존한다.
 *
 * absenceBased 패턴:
 *
 * 예:
 * 칠살 제화 부족
 *
 * 특정 공격 source가 아니라
 * 필요한 성격 구조가 부족한 상태이므로
 * damagingElement는 null이고
 * 오행 세력값은 0으로 유지한다.
 */
public record StructureDamageSignal(

        StructureDamagePatternType patternType,

        FiveElement damagingElement,

        int elementFinalPower,

        int elementShareBasisPoints,

        int visibleSourceCount,

        int hiddenSourceCount,

        int exposedHiddenSourceCount,

        boolean absenceBased,

        StructureDamageLevel level

) {

    public StructureDamageSignal {

        Objects.requireNonNull(
                patternType,
                "파격 패턴이 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "파격 수준이 필요합니다."
        );

        if (
                absenceBased
                        != patternType.absenceBased()
        ) {

            throw new IllegalArgumentException(
                    "파격 패턴의 absenceBased 상태가 일치하지 않습니다."
            );
        }

        if (
                elementFinalPower < 0
                        || elementShareBasisPoints < 0
                        || elementShareBasisPoints > 10_000
                        || visibleSourceCount < 0
                        || hiddenSourceCount < 0
                        || exposedHiddenSourceCount < 0
        ) {

            throw new IllegalArgumentException(
                    "파격 Evidence 값이 올바르지 않습니다."
            );
        }

        if (
                exposedHiddenSourceCount
                        > hiddenSourceCount
        ) {

            throw new IllegalArgumentException(
                    "투출 지장간 source 수가 전체 지장간 source 수보다 클 수 없습니다."
            );
        }

        if (
                absenceBased
        ) {

            if (
                    damagingElement != null
                            || elementFinalPower != 0
                            || elementShareBasisPoints != 0
                            || visibleSourceCount != 0
                            || hiddenSourceCount != 0
                            || exposedHiddenSourceCount != 0
            ) {

                throw new IllegalArgumentException(
                        "부재 기반 파격 Signal에는 공격 오행/source 값을 넣을 수 없습니다."
                );
            }

        } else {

            Objects.requireNonNull(
                    damagingElement,
                    "source 기반 파격에는 공격 오행이 필요합니다."
            );

            int sourceCount =
                    visibleSourceCount
                            + hiddenSourceCount;

            if (
                    sourceCount <= 0
                            && !level.none()
            ) {

                throw new IllegalArgumentException(
                        "일치하는 파격 source가 없는데 파격 수준이 존재할 수 없습니다."
                );
            }

            if (
                    sourceCount > 0
                            && level.none()
            ) {

                throw new IllegalArgumentException(
                        "일치하는 파격 source가 있는데 파격 수준이 NONE일 수 없습니다."
                );
            }
        }
    }

    public int totalSourceCount() {

        return visibleSourceCount
                + hiddenSourceCount;
    }

    public boolean sourceBased() {

        return !absenceBased;
    }

    public boolean hasVisibleSource() {

        return visibleSourceCount > 0;
    }

    public boolean hasHiddenSource() {

        return hiddenSourceCount > 0;
    }

    public boolean hasExposedHiddenSource() {

        return exposedHiddenSourceCount > 0;
    }

    public boolean expressed() {

        return hasVisibleSource()
                || hasExposedHiddenSource();
    }

    public boolean strong() {

        return level.strong();
    }

    public boolean meaningful() {

        return level.meaningful();
    }

    public boolean exists() {

        return level.exists();
    }

    public double elementSharePercent() {

        return elementShareBasisPoints
                / 100.0;
    }
}