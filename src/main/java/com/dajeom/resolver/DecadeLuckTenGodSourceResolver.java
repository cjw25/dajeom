package com.dajeom.resolver;

import com.dajeom.model.DecadeLuckResolvedTenGodSource;
import com.dajeom.model.SexagenaryPillar;
import com.dajeom.type.HeavenlyStem;

import java.util.List;

/**
 * 정밀화 13차-4.
 *
 * 기존:
 *
 * - 지장간 canonical source
 * - 십성 calculator
 *
 * 와 대운 분석 사이의 Boundary.
 *
 * =================================================
 *
 * 입력:
 *
 * dayMaster
 * +
 * 대운 간지
 *
 * =================================================
 *
 * 출력 예:
 *
 * 己未
 *
 * VISIBLE_STEM -> 己 -> 식신
 *
 * BRANCH_MAIN -> ...
 * BRANCH_MIDDLE -> ...
 * BRANCH_RESIDUAL -> ...
 *
 * =================================================
 *
 * 구현체에서는 반드시 기존 프로젝트의
 * 지장간표와 십성 계산기를 사용한다.
 *
 * 이 Resolver 때문에 기존 Calculator의
 * public API를 수정하지 않는다.
 */
public interface DecadeLuckTenGodSourceResolver {

    List<DecadeLuckResolvedTenGodSource> resolve(

            HeavenlyStem dayMaster,

            SexagenaryPillar luckPillar

    );
}