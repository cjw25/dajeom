package com.dajeom.domain;

/**
 * 沖 / 刑 / 破 / 害에 대한
 * 회합 기반 해소 검토 상태.
 *
 * =================================================
 *
 * NO_RESOLUTION_CANDIDATE
 *
 * 해소할 六合 / 三合 / 三會 후보가 없음.
 *
 * =================================================
 *
 * RESOLUTION_CANDIDATE
 *
 * 해소 가능한 회합 후보가 있고
 * 현재 단계에서 명백한 경쟁 회합은 없음.
 *
 * 아직 "완전히 해소됐다"는 뜻은 아니다.
 *
 * =================================================
 *
 * CONTESTED_REVIEW_REQUIRED
 *
 * 해소 후보는 있으나:
 *
 * - 해소에 쓰이는 지지가 다른 회합에도 참여
 * - 복수의 해소 후보가 동시에 존재
 *
 * 등으로 우선순위 검토가 필요함.
 */
public enum PatternBranchDisruptionResolutionState {

    NO_RESOLUTION_CANDIDATE,

    RESOLUTION_CANDIDATE,

    CONTESTED_REVIEW_REQUIRED
}