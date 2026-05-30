package com.roommate.domain.admin.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AdminSettingRepository {
    Optional<String> findValue(@Param("key") String key);

    int upsert(@Param("key") String key, @Param("value") String value);
}
