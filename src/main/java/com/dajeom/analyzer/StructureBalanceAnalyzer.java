package com.dajeom.analyzer;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.StructureBalanceAnalysis;
import com.dajeom.model.StructureBalanceEvidence;
import com.dajeom.model.StructureCandidateEstablishmentAnalysis;
import com.dajeom.model.StructureEstablishmentEvidence;
import com.dajeom.rule.StructureBalanceRule;
import com.dajeom.type.StructureBalanceLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 5차-4.
 *
 * 5-3에서 성립도를 계산한 격 후보와
 * 4차의 일간 신강·신약을 결합한다.
 *
 * 중요한 원칙:
 *
 * 1.
 * 신강·신약을 다시 계산하지 않는다.
 *
 * 2.
 * 후보 오행 세력을 다시 계산하지 않는다.
 *
 * 3.
 * 격 성립도를 다시 계산하지 않는다.
 *
 * 4.
 * 길흉이나 용신을 판단하지 않는다.
 *
 * 5.
 * 주격 역시 아직 결정하지 않는다.
 */
@Component
public class StructureBalanceAnalyzer {

    private final StructureCandidateEstablishmentAnalyzer
            structureCandidateEstablishmentAnalyzer;

    private final StructureBalanceRule
            structureBalanceRule;

    public StructureBalanceAnalyzer(

            StructureCandidateEstablishmentAnalyzer
                    structureCandidateEstablishmentAnalyzer,

            StructureBalanceRule
                    structureBalanceRule

    ) {

        this.structureCandidateEstablishmentAnalyzer =
                Objects.requireNonNull(
                        structureCandidateEstablishmentAnalyzer,
                        "StructureCandidateEstablishmentAnalyzer가 필요합니다."
                );

        this.structureBalanceRule =
                Objects.requireNonNull(
                        structureBalanceRule,
                        "StructureBalanceRule이 필요합니다."
                );
    }

    /**
     * 5-1 ~ 5-3을 내부에서 연결한 뒤
     * 5-4를 수행한다.
     */
    public StructureBalanceAnalysis analyze(
            DayMasterStrengthAnalysis strength
    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        StructureCandidateEstablishmentAnalysis
                establishment =
                structureCandidateEstablishmentAnalyzer
                        .analyze(
                                strength
                        );

        return analyze(
                strength,
                establishment
        );
    }

    /**
     * 이미 계산된 5-3 결과를 재사용한다.
     *
     * 향후 5차 통합 Engine에서는
     * 이 오버로드를 사용한다.
     */
    public StructureBalanceAnalysis analyze(

            DayMasterStrengthAnalysis strength,

            StructureCandidateEstablishmentAnalysis
                    establishment

    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                establishment,
                "격 성립도 분석이 필요합니다."
        );

        validateConsistency(
                strength,
                establishment
        );

        List<StructureBalanceEvidence> evidences =
                new ArrayList<>();

        StructureBalanceEvidence mainQiEvidence =
                null;

        for (
                StructureEstablishmentEvidence
                        establishmentEvidence
                : establishment.orderedEvidences()
        ) {

            StructureBalanceLevel balanceLevel =
                    structureBalanceRule
                            .determineLevel(
                                    establishmentEvidence
                                            .tenGod(),

                                    establishmentEvidence
                                            .level(),

                                    strength
                                            .strengthBasisPoints(),

                                    establishmentEvidence
                                            .elementShareBasisPoints()
                            );

            StructureBalanceEvidence evidence =
                    new StructureBalanceEvidence(
                            establishmentEvidence,
                            strength.level(),
                            strength.strengthBasisPoints(),
                            balanceLevel
                    );

            evidences.add(
                    evidence
            );

            if (
                    evidence.mainQi()
            ) {

                if (
                        mainQiEvidence != null
                ) {

                    throw new IllegalStateException(
                            "월령 정기 Balance Evidence가 둘 이상 생성되었습니다."
                    );
                }

                mainQiEvidence =
                        evidence;
            }
        }

        if (
                mainQiEvidence == null
        ) {

            throw new IllegalStateException(
                    "월령 정기 Balance Evidence를 생성하지 못했습니다."
            );
        }

        /*
         * 여기서는 새로 정렬하지 않는다.
         *
         * 5-3에서 정한:
         *
         * 성립도
         * 월령 source 권위
         * 투출 우선순위
         *
         * 순서를 그대로 보존한다.
         *
         * 강약 적합도가 높다고 해서
         * 성립도가 낮은 후보를
         * 앞쪽으로 올려서는 안 된다.
         *
         * 실제 주격 결정은 5-9에서 한다.
         */
        return new StructureBalanceAnalysis(
                strength.dayMaster(),
                strength.dayMasterElement(),
                strength.level(),
                strength.strengthBasisPoints(),
                mainQiEvidence,
                evidences
        );
    }

    private void validateConsistency(

            DayMasterStrengthAnalysis strength,

            StructureCandidateEstablishmentAnalysis
                    establishment

    ) {

        if (
                strength.dayMaster()
                        != establishment.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "신강·신약 분석과 격 성립 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                establishment.orderedEvidences()
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "격 성립 Evidence가 존재하지 않습니다."
            );
        }
    }
}