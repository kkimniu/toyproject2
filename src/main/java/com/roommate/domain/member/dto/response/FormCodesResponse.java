package com.roommate.domain.member.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class FormCodesResponse {
    private List<WorkTypeResponse> workTypes;
    private List<HobbyResponse> hobbies;
    private List<PreferenceResponse> preferences;
    private List<PetResponse> pets;
}
