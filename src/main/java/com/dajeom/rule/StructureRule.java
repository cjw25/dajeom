package com.dajeom.rule;

import com.dajeom.type.StructureBalanceLevel;
import com.dajeom.type.StructureDamageLevel;
import com.dajeom.type.StructureEstablishmentLevel;
import com.dajeom.type.StructurePurityLevel;
import com.dajeom.type.StructureStatus;
import com.dajeom.type.StructureSupportLevel;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 5차-10.
 *
 * 최종 격국 상태 판정 Rule.
 *
 * 중요한 원칙:
 *
 * 이 Rule은 주격을 다시 선택하지 않는다.
 *
 * PRIMARY는 이미 5-9에서 결정됐다.
 *
 * 여기서는 그 PRIMARY의:
 *
 * - 성립도
 * - 강약 적합성
 * - 성격
 * - 파격
 * - 순도
 *
 * 를 이용해 최종 StructureStatus만 결정한다.
 */
@Component
public class StructureRule {

    /**
     * 일반 내격 PRIMARY의 최종 상태.
     */
    public StructureStatus determineStandardStatus(

            StructureEstablishmentLevel establishment,

            StructureBalanceLevel balance,

            StructureSupportLevel support,

            StructureDamageLevel damage,

            StructurePurityLevel purity

    ) {

        Objects.requireNonNull(
                establishment,
                "격 성립도가 필요합니다."
        );

        Objects.requireNonNull(
                balance,
                "격 강약 적합성이 필요합니다."
        );

        Objects.requireNonNull(
                support,
                "성격 요소 수준이 필요합니다."
        );

        Objects.requireNonNull(
                damage,
                "파격 요소 수준이 필요합니다."
        );

        Objects.requireNonNull(
                purity,
                "격 순도 수준이 필요합니다."
        );

        /*
         * PRIMARY 일반격은
         * 활성 후보여야 하지만,
         * 방어적으로 다시 확인한다.
         */
        if (
                establishment.inactive()
                        || balance.notApplicable()
                        || purity.notApplicable()
        ) {

            return StructureStatus
                    .UNSTABLE;
        }

        /*
         * ================================================
         * 1.
         * WEAK 성립 후보
         * ================================================
         *
         * 다른 후보가 모두 약하면
         * 5-9에서 PRIMARY로 선택될 수 있지만
         * 최종적으로 안정적인 격 성립으로
         * 확정하지 않는다.
         */
        if (
                establishment.weak()
        ) {

            return StructureStatus
                    .UNSTABLE;
        }

        /*
         * ================================================
         * 2.
         * 강한 파격
         * ================================================
         *
         * 강한 파격이 있어도
         * 강한 성격 요소가 동시에 존재하고,
         * 순도와 강약 상태가 비교적 안정적이라면
         * 아예 UNSTABLE로 버리지 않고
         * COMPROMISED로 보존한다.
         */
        if (
                damage.strong()
        ) {

            boolean strongCompensation =
                    support.strong()
                            && purity.relativelyPure()
                            && balance.acceptable();

            if (
                    strongCompensation
            ) {

                return StructureStatus
                        .COMPROMISED;
            }

            return StructureStatus
                    .UNSTABLE;
        }

        /*
         * ================================================
         * 3.
         * 의미 있는 파격
         * ================================================
         */
        if (
                damage.meaningful()
        ) {

            return StructureStatus
                    .COMPROMISED;
        }

        /*
         * ================================================
         * 4.
         * 강한 혼잡
         * ================================================
         *
         * 직접적인 파격이 아니더라도
         * 여러 격 구조가 강하게 혼재하면
         * 깨끗한 ESTABLISHED로 확정하지 않는다.
         */
        if (
                purity.highlyMixed()
        ) {

            return StructureStatus
                    .COMPROMISED;
        }

        /*
         * ================================================
         * 5.
         * 일간 강약 부담
         * ================================================
         *
         * 격 자체의 성립은 유지하지만
         * 현재 일간이 해당 격 세력을
         * 감당하는 데 부담이 있음을 보존한다.
         */
        if (
                balance.burdened()
        ) {

            return StructureStatus
                    .ESTABLISHED_WITH_STRAIN;
        }

        /*
         * ================================================
         * 6.
         * 의미 있는 성립
         * +
         * 심각한 손상 없음
         * ================================================
         */
        if (
                establishment.meaningfulCandidate()
        ) {

            return StructureStatus
                    .ESTABLISHED;
        }

        return StructureStatus
                .UNSTABLE;
    }

    /**
     * 5-8/5-9의 특수격 PRIMARY는
     * 아직 후보 상태다.
     *
     * 일반 내격용 성격/파격 Rule로
     * 강제로 확정하지 않는다.
     */
    public StructureStatus determineSpecialStatus() {

        return StructureStatus
                .SPECIAL_REVIEW;
    }
}