package com.roommate.domain.member.service;

import com.roommate.domain.member.dto.response.RecommendedRoommateResponse;
import com.roommate.domain.member.entity.RecommendedRoommateEntity;
import com.roommate.domain.member.repository.MemberRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberRecommendationServiceImpl implements MemberRecommendationService {

    private final MemberRecommendationRepository memberRecommendationRepository;

    @Override
    public List<RecommendedRoommateResponse> getRecommendedRoommates(String region,
                                                                     Integer maxMonthlyRent,
                                                                     String gender,
                                                                     List<Long> workTypeIds,
                                                                     List<Long> hobbyIds,
                                                                     List<Long> preferenceIds,
                                                                     List<Long> petIds) {
        return memberRecommendationRepository.findRecommendedRoommates(
                        region,
                        maxMonthlyRent,
                        gender,
                        emptyToNull(workTypeIds),
                        emptyToNull(hobbyIds),
                        emptyToNull(preferenceIds),
                        emptyToNull(petIds)
                )
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private List<Long> emptyToNull(List<Long> values) {
        return values == null || values.isEmpty() ? null : values;
    }

    private RecommendedRoommateResponse toResponse(RecommendedRoommateEntity entity) {
        return new RecommendedRoommateResponse(
                entity.getRoomId(),
                entity.getRoomTitle(),
                entity.getMemberId(),
                entity.getName(),
                entity.getAge(),
                entity.getWorkTypeName(),
                entity.getLocation(),
                entity.getIntro(),
                entity.getBudget(),
                entity.getRating(),
                entity.getImageUrl(),
                toTags(entity.getTagText())
        );
    }

    private List<String> toTags(String tagText) {
        if (tagText == null || tagText.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(tagText.split(","))
                .map(String::trim)
                .filter(Objects::nonNull)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }
}
