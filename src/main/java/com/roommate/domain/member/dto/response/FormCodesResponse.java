package com.roommate.domain.member.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class FormCodesResponse {
    private List<WorkTypeResponse> workTypes;
    private List<HobbyResponse> hobbies;
    private List<PreferenceResponse> preferences;
    private List<PetResponse> pets;
}
