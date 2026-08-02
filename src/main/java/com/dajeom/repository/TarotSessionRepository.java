package com.dajeom.repository;

import com.dajeom.entity.TarotSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TarotSessionRepository
        extends JpaRepository<TarotSession, Long> {

    Optional<TarotSession> findBySessionKey(
            String sessionKey
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select session
            from TarotSession session
            join fetch session.spread
            where session.sessionKey = :sessionKey
            """)
    Optional<TarotSession> findLockedBySessionKey(
            @Param("sessionKey") String sessionKey
    );
}