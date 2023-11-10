package com.projectx.codeecho.service.serviceImpl;

import com.projectx.codeecho.domain.entity.MemberEntity;
import com.projectx.codeecho.repository.MemberRepository;
import com.projectx.codeecho.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;


    @Override
    public void save(MemberEntity member) {
        memberRepository.save(member);
    }
}
