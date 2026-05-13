package com.roommate.domain.member.service;

import com.roommate.domain.member.dto.response.RecommendedRoommateResponse;
import com.roommate.domain.member.entity.RecommendedRoommateEntity;
import com.roommate.domain.member.repository.MemberRecommendationRepository;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MemberRecommendationServiceImplTest {

    @Test
    public void getRecommendedRoommatesMapsTagsAndPassesFilters() {
        FakeRepository repository = new FakeRepository(List.of(new RecommendedRoommateEntity(
                10L,
                "테스트 방",
                20L,
                "테스트 회원",
                28,
                "직장인",
                "마포구",
                "소개",
                "월 50만원",
                null,
                "profile.png",
                "요리, 영화, 요리"
        )));
        MemberRecommendationService service = new MemberRecommendationServiceImpl(repository);

        List<RecommendedRoommateResponse> responses = service.getRecommendedRoommates(
                "마포",
                60,
                "FEMALE",
                List.of(1L),
                List.of(2L),
                List.of(3L),
                List.of(4L)
        );

        assertEquals("마포", repository.region);
        assertEquals(Integer.valueOf(60), repository.maxMonthlyRent);
        assertEquals("FEMALE", repository.gender);
        assertEquals(List.of(1L), repository.workTypeIds);
        assertEquals(List.of(2L), repository.hobbyIds);
        assertEquals(List.of(3L), repository.preferenceIds);
        assertEquals(List.of(4L), repository.petIds);
        assertEquals(1, responses.size());
        assertEquals(List.of("요리", "영화"), responses.get(0).getTags());
    }

    @Test
    public void getRecommendedRoommatesConvertsEmptyFilterListsToNull() {
        FakeRepository repository = new FakeRepository(Collections.emptyList());
        MemberRecommendationService service = new MemberRecommendationServiceImpl(repository);

        service.getRecommendedRoommates(null, null, null, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        assertNull(repository.workTypeIds);
        assertNull(repository.hobbyIds);
        assertNull(repository.preferenceIds);
        assertNull(repository.petIds);
    }

    private static class FakeRepository implements MemberRecommendationRepository {
        private final List<RecommendedRoommateEntity> result;
        private String region;
        private Integer maxMonthlyRent;
        private String gender;
        private List<Long> workTypeIds;
        private List<Long> hobbyIds;
        private List<Long> preferenceIds;
        private List<Long> petIds;

        private FakeRepository(List<RecommendedRoommateEntity> result) {
            this.result = result;
        }

        @Override
        public List<RecommendedRoommateEntity> findRecommendedRoommates(String region,
                                                                        Integer maxMonthlyRent,
                                                                        String gender,
                                                                        List<Long> workTypeIds,
                                                                        List<Long> hobbyIds,
                                                                        List<Long> preferenceIds,
                                                                        List<Long> petIds) {
            this.region = region;
            this.maxMonthlyRent = maxMonthlyRent;
            this.gender = gender;
            this.workTypeIds = workTypeIds;
            this.hobbyIds = hobbyIds;
            this.preferenceIds = preferenceIds;
            this.petIds = petIds;
            return result;
        }
    }
}
