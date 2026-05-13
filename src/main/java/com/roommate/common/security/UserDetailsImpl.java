package com.roommate.common.security;

import com.roommate.domain.member.entity.MemberEntity;
import com.roommate.domain.member.entity.MemberRoleEnum;
import com.roommate.domain.member.entity.MemberStatusEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {

    private final MemberRoleEnum role;
    private final Long memberId;
    private final String email;
    private final String password;
    private final MemberStatusEnum status;
    private final int deleted;

    public UserDetailsImpl(MemberEntity memberEntity){
        this.memberId = memberEntity.getMemberId();
        this.email = memberEntity.getEmail();
        this.password = memberEntity.getPassword();
        this.role = memberEntity.getRole();
        this.status = memberEntity.getStatus();
        this.deleted = memberEntity.getDeleted();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getAuthority()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != MemberStatusEnum.BANNED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return deleted == 0 && status != MemberStatusEnum.DELETED;
    }
}
