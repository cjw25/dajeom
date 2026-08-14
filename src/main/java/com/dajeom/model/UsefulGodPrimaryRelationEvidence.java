package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.UsefulGodPrimaryRelationType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-9.
 *
 * 하나의 평가 대상 五行과
 * 하나의 주용신 사이의 관계.
 *
 * =================================================
 *
 * relations
 *
 * 하나만 존재한다고 가정하지 않는다.
 *
 * 예:
 *
 * Fire가 주용신이고 Earth를 평가:
 *
 * Fire -> Earth
 * -> DRAINS_PRIMARY
 *
 * Earth -> Water
 * Water -> Fire 극
 *
 * -> CONTROLS_PRIMARY_THREAT
 *
 * 두 Evidence가 동시에 존재할 수 있다.
 */
public record UsefulGodPrimaryRelationEvidence(

        FiveElement candidateElement,

        FiveElement primaryElement,

        List<UsefulGodPrimaryRelationType> relations

) {

    public UsefulGodPrimaryRelationEvidence {

        Objects.requireNonNull(
                candidateElement,
                "평가 대상 오행이 필요합니다."
        );

        Objects.requireNonNull(
                primaryElement,
                "주용신 오행이 필요합니다."
        );

        relations =
                relations == null
                        ? List.of()
                        : List.copyOf(
                        relations
                );

        if (
                relations.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "주용신과의 관계 Evidence가 하나 이상 필요합니다."
            );
        }

        if (
                relations
                        .stream()
                        .distinct()
                        .count()
                        != relations.size()
        ) {

            throw new IllegalArgumentException(
                    "주용신 관계 Evidence가 중복되었습니다."
            );
        }

        if (
                candidateElement == primaryElement

                        && !relations.contains(
                        UsefulGodPrimaryRelationType
                                .SAME_AS_PRIMARY
                )
        ) {

            throw new IllegalArgumentException(
                    "주용신 자체 오행에는 SAME_AS_PRIMARY 관계가 필요합니다."
            );
        }

        if (
                candidateElement != primaryElement

                        && relations.contains(
                        UsefulGodPrimaryRelationType
                                .SAME_AS_PRIMARY
                )
        ) {

            throw new IllegalArgumentException(
                    "서로 다른 오행에 SAME_AS_PRIMARY를 지정할 수 없습니다."
            );
        }
    }

    public boolean sameAsPrimary() {

        return relations.contains(
                UsefulGodPrimaryRelationType
                        .SAME_AS_PRIMARY
        );
    }

    public boolean generatesPrimary() {

        return relations.contains(
                UsefulGodPrimaryRelationType
                        .GENERATES_PRIMARY
        );
    }

    public boolean controlsPrimary() {

        return relations.contains(
                UsefulGodPrimaryRelationType
                        .CONTROLS_PRIMARY
        );
    }

    public boolean drainsPrimary() {

        return relations.contains(
                UsefulGodPrimaryRelationType
                        .DRAINS_PRIMARY
        );
    }

    public boolean controlledByPrimary() {

        return relations.contains(
                UsefulGodPrimaryRelationType
                        .CONTROLLED_BY_PRIMARY
        );
    }

    public boolean controlsPrimaryThreat() {

        return relations.contains(
                UsefulGodPrimaryRelationType
                        .CONTROLS_PRIMARY_THREAT
        );
    }

    public boolean supportive() {

        return relations
                .stream()
                .anyMatch(
                        UsefulGodPrimaryRelationType
                                ::supportive
                );
    }

    public boolean harmful() {

        return relations
                .stream()
                .anyMatch(
                        UsefulGodPrimaryRelationType
                                ::harmful
                );
    }

    public boolean burdening() {

        return relations
                .stream()
                .anyMatch(
                        UsefulGodPrimaryRelationType
                                ::burdening
                );
    }

    public boolean mixed() {

        return supportive()
                && (
                harmful()
                        || burdening()
        );
    }

    public boolean hasRelation(
            UsefulGodPrimaryRelationType relationType
    ) {

        if (
                relationType == null
        ) {

            return false;
        }

        return relations.contains(
                relationType
        );
    }
}