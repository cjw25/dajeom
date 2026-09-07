package com.dajeom.domain;

/**
 * 하나의 일간 통근 지점에 걸린
 * 지지 관계 신호.
 *
 * 이 객체는 관계의 존재 여부만 보존한다.
 *
 * 중요:
 *
 * clash = true
 * != 뿌리 소멸
 *
 * combination = true
 * != 뿌리 강화
 *
 * punishment / break / harm
 * != 뿌리 삭제
 *
 * 실제 명리적 상태는 별도 Rule에서 판단한다.
 */
public record DayMasterRootRelationSignals(

        boolean clash,

        boolean combination,

        boolean punishment,

        boolean selfPunishment,

        boolean breakRelation,

        boolean harm

) {

    public boolean hasAnyRelation() {

        return clash
                || combination
                || punishment
                || selfPunishment
                || breakRelation
                || harm;
    }

    public boolean hasDisturbingRelation() {

        return clash
                || punishment
                || selfPunishment
                || breakRelation
                || harm;
    }

    public boolean hasCombination() {

        return combination;
    }

    public boolean hasClash() {

        return clash;
    }

    /**
     * 합과 충/형/파/해 등이
     * 동시에 걸려 있는 복합 Context.
     */
    public boolean mixedCombinationAndDisturbance() {

        return combination
                && hasDisturbingRelation();
    }

    /**
     * 충 외에도 형/파/해 등이
     * 함께 존재하는가.
     */
    public boolean clashWithAdditionalDisturbance() {

        if (!clash) {
            return false;
        }

        return punishment
                || selfPunishment
                || breakRelation
                || harm;
    }
}