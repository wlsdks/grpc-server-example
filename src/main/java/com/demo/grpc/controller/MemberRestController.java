package com.demo.grpc.controller;

import com.demo.grpc.dto.MemberSignUpRequestDTO;
import com.demo.grpc.dto.ResponseMemberDTO;
import com.demo.grpc.entity.Member;
import com.demo.grpc.mapper.MemberMapper;
import com.demo.grpc.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @PostMapping("/members")
    public ResponseEntity<ResponseMemberDTO> createMember(@RequestBody MemberSignUpRequestDTO dto) {
        // DB에 저장
        ResponseMemberDTO saved = memberService.createMember(dto);
        return ResponseEntity.ok(saved);
    }

}
