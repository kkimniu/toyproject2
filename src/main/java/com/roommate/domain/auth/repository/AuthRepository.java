package com.roommate.domain.auth.repository;

import com.roommate.domain.auth.entity.TokenRefreshEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthRepository {

    void save(TokenRefreshEntity tokenRefreshEntity);
}
