package com.dajeom.domain;

/**
 * 천간오합에 의한 화기격 검토 상태.
 *
 * 후보 상태일 뿐
 * 화기격 확정이 아니다.
 */
public enum DayMasterStemTransformationCandidateType {

    /**
     * 진화 조건을 본격적으로 검토할 가치가 있는 구조.
     *
     * 아직 TRUE_TRANSFORMATION 확정이 아니다.
     */
    TRUE_TRANSFORMATION_REVIEW,

    /**
     * 천간오합은 존재하지만:
     *
     * - 일간 근이 남아 있거나
     * - 인성/비겁 생부가 남거나
     * - 화신이 월령에 통하지 않거나
     *
     * 하는 구조.
     *
     * 假化 또는 불화 가능성을
     * 다음 단계에서 세밀하게 검토한다.
     */
    FAKE_OR_INCOMPLETE_TRANSFORMATION_REVIEW,

    /**
     * 일간의 합 상대가 둘 이상 존재해
     * 독합이 아니거나 쟁합 Context가 있는 경우.
     *
     * 바로 화기격으로 확정하면 안 된다.
     */
    CONTESTED_TRANSFORMATION_REVIEW
}