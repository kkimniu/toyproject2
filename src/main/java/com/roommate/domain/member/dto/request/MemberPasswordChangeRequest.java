package com.roommate.domain.member.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
@NoArgsConstructor
public class MemberPasswordChangeRequest {

    @NotBlank
    private String currentPassword;
    @NotBlank
    @Size(min = 8 , max = 64)
    private String newPassword;
    @NotBlank
    private String confirmPassword;
}
