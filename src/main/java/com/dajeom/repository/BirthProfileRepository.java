package com.dajeom.repository;

import com.dajeom.entity.BirthProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BirthProfileRepository
        extends JpaRepository<BirthProfile, Long> {
}