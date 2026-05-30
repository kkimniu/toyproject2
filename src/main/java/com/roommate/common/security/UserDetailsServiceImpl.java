package com.roommate.common.security;

import com.roommate.domain.member.entity.MemberEntity;
import com.roommate.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        MemberEntity memberEntity = memberRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("해당 이메일 사용자가 없습니다. email="+email));
        return new UserDetailsImpl(memberEntity);
    }
}
