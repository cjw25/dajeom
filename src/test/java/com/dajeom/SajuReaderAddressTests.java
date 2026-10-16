package com.dajeom;

import com.dajeom.service.SajuReaderAddress;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SajuReaderAddressTests {
    @Test void usesTheSameFullNameForAllFormsOfTheReaderAddress() {
        assertEquals("최정원님은 최정원님과 최정원님께 말씀드립니다.",
            SajuReaderAddress.normalize("최정원님은 정원님과 [[상담자호칭]]께 말씀드립니다.","최정원"));
    }

    @Test void preservesOtherNamesAndOrdinaryWords() {
        assertEquals("김정원님과 정원을 보며 최정원님이 이야기합니다.",
            SajuReaderAddress.normalize("김정원님과 정원을 보며 정원님이 이야기합니다.","최정원"));
        assertEquals("Alex님",SajuReaderAddress.normalize("[[상담자호칭]]","Alex"));
    }

    @Test void doesNotDuplicateTheHonorific() {
        assertEquals("최정원님",SajuReaderAddress.of("최정원님"));
        assertEquals("최정원님과 최정원님",SajuReaderAddress.normalize("최정원 님님과 정원 님","최정원님"));
    }
}
