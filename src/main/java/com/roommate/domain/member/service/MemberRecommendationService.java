package com.roommate.domain.member.service;

import com.roommate.domain.member.dto.response.RecommendedRoommateResponse;

import java.util.List;

public interface MemberRecommendationService {
    List<RecommendedRoommateResponse> getRecommendedRoommates(String region,
                                                              Integer maxMonthlyRent,
                                                              String gender,
                                                              List<Long> workTypeIds,
                                                              List<Long> hobbyIds,
                                                              List<Long> preferenceIds,
                                                              List<Long> petIds);
}
