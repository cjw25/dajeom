package com.dajeom.type;

/**
 * 하나의 Consultation Section을 Packet 단위로 조립할 때
 * Java가 검증하는 위반 유형.
 */
public enum ConsultationSectionAssemblyViolationType {

    MISSING_PACKET,

    UNEXPECTED_PACKET,

    DUPLICATE_PACKET,

    PACKET_ORDER_CHANGED,

    EMPTY_PACKET_TEXT,

    /**
     * 서로 다른 Packet이 사실상 같은 문단으로 반복된 경우.
     */
    REPEATED_PACKET_TEXT,

    /**
     * 독립 Packet 문단이 이전/다음 Packet과의 관계를 전제하는
     * 접속 표현으로 시작한 경우.
     */
    CROSS_PACKET_OPENING_CONNECTOR,

    /**
     * Packet 강조 구절이 해당 Packet 본문에 존재하지 않는 경우.
     */
    EMPHASIS_NOT_IN_PACKET_TEXT
}