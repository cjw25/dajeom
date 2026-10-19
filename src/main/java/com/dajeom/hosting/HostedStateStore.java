package com.dajeom.hosting;

import java.time.Instant;

public interface HostedStateStore {
    String get(String key);
    boolean acquire(String key,String owner,int seconds);
    boolean commit(String key,String json,Instant expires,String lockKey,String owner);
    void release(String key,String owner);
    boolean delete(String key,String lockKey,String owner);
    boolean consume(String bucket,int limit);
}
