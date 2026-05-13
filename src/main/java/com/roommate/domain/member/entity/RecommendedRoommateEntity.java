package com.roommate.domain.member.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedRoommateEntity {
    private Long roomId;
    private String roomTitle;
    private Long memberId;
    private String name;
    private Integer age;
    private String workTypeName;
    private String location;
    private String intro;
    private String budget;
    private String rating;
    private String imageUrl;
    private String tagText;
}
