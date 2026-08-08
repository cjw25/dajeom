package com.dajeom.rule;

import com.dajeom.type.StructureType;
import com.dajeom.type.TenGod;

import java.util.Objects;

/**
 * 원국 구조/격국 분류 Rule.
 *
 * V1에서는 월령 정기의 십성을 기준으로
 * 일반적인 8격 후보를 분류한다.
 *
 * 비견/겁재 계열은
 * 여기서 억지로 특정 격국으로 확정하지 않는다.
 */
public final class StructureRule {

    public static final String RULE_VERSION =
            "DAJEOM_STRUCTURE_V1";

    private StructureRule() {

        throw new IllegalStateException(
                "StructureRule은 생성할 수 없습니다."
        );
    }

    public static StructureType classify(
            TenGod tenGod
    ) {

        Objects.requireNonNull(
                tenGod,
                "십성은 필수입니다."
        );

        return switch (tenGod) {

            case JEONG_GWAN ->
                    StructureType.DIRECT_OFFICER;

            case PYEON_GWAN ->
                    StructureType.SEVEN_KILLINGS;

            case JEONG_JAE ->
                    StructureType.DIRECT_WEALTH;

            case PYEON_JAE ->
                    StructureType.INDIRECT_WEALTH;

            case SIK_SIN ->
                    StructureType.EATING_GOD;

            case SANG_GWAN ->
                    StructureType.HURTING_OFFICER;

            case JEONG_IN ->
                    StructureType.DIRECT_RESOURCE;

            case PYEON_IN ->
                    StructureType.INDIRECT_RESOURCE;

            case BI_GYEON,
                 GEOP_JAE ->
                    StructureType.UNDETERMINED;
        };
    }
}