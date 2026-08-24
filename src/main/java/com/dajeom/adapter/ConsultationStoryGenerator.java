package com.dajeom.adapter;

import com.dajeom.model.ConsultationNarrativePlan;
import com.dajeom.model.ConsultationStoryGeneration;

import java.util.Optional;

/**
 * Java가 확정한 Narrative Plan을 외부 LLM으로 문장화하는 Adapter 경계.
 *
 * AI는 Narrative Plan의 Packet/Claim 의미를 변경하거나 새 명리 판단을 만들 수 없다.
 */
public interface ConsultationStoryGenerator {

    Optional<ConsultationStoryGeneration> generate(
            ConsultationNarrativePlan narrativePlan
    );
}