package com.dajeom.rule;

import com.dajeom.domain.InterpretationLayer;
import com.dajeom.domain.LifeClaim;
import com.dajeom.domain.MyeongriFact;

import java.util.List;
import java.util.Optional;

/**
 * 하나의 명리 해석 규칙.
 *
 * 수치 계산이 아니라:
 *
 * "어떤 명리 사실들이 성립할 때
 * 어떤 LifeClaim이 성립하는가"
 *
 * 를 Java로 정의한다.
 */
public interface InterpretationRule {

    String ruleCode();

    InterpretationLayer layer();

    Optional<LifeClaim> apply(
            List<MyeongriFact> facts
    );
}