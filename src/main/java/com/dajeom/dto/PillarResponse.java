package com.dajeom.dto;

import com.dajeom.type.FiveElement;
import com.dajeom.type.YinYang;

/**
 * 하나의 간지 출력값.
 */
public record PillarResponse(

        String koreanName,

        String chineseName,

        String stemKoreanName,

        String stemChineseName,

        FiveElement stemElement,

        YinYang stemYinYang,

        String branchKoreanName,

        String branchChineseName,

        FiveElement branchElement,

        YinYang branchYinYang

) {
}