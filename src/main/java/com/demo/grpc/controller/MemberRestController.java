package com.demo.grpc.controller;

import com.demo.grpc.dto.request.MemberSignUpRequestDTO;
import com.demo.grpc.dto.response.ResponseMemberDTO;
import com.demo.grpc.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<ResponseMemberDTO> createMember(@RequestBody MemberSignUpRequestDTO dto) {
        // DB에 저장
        ResponseMemberDTO saved = memberService.createMember(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/members/{memberId}")
    public ResponseEntity<ResponseMemberDTO> getMemberById(@PathVariable Long memberId) {
        // DB에서 조회
        ResponseMemberDTO member = memberService.getMemberById(memberId);
        return ResponseEntity.ok(member);
    }

}
