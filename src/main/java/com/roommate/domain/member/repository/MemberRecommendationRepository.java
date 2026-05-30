package com.roommate.domain.member.repository;

import com.roommate.domain.member.entity.RecommendedRoommateEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberRecommendationRepository {
    List<RecommendedRoommateEntity> findRecommendedRoommates(@Param("region") String region,
                                                             @Param("maxMonthlyRent") Integer maxMonthlyRent,
                                                             @Param("gender") String gender,
                                                             @Param("workTypeIds") List<Long> workTypeIds,
                                                             @Param("hobbyIds") List<Long> hobbyIds,
                                                             @Param("preferenceIds") List<Long> preferenceIds,
                                                             @Param("petIds") List<Long> petIds);
}
