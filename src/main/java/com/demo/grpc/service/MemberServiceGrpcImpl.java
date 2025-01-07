package com.demo.grpc.service;

import com.demo.grpc.dto.MemberSignUpRequestDTO;
import com.demo.grpc.dto.ResponseMemberDTO;
import com.demo.grpc.entity.Member;
import com.demo.grpc.mapper.MemberMapper;
import com.test.member.grpc.MemberProto;
import com.test.member.grpc.MemberServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class MemberServiceGrpcImpl extends MemberServiceGrpc.MemberServiceImplBase {

    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @Override
    public void createMember(MemberProto.MemberRequest request,
                             StreamObserver<MemberProto.MemberCreateResponse> responseObserver) {
        // 1. 클라이언트로부터 전달받은 request 데이터를 DTO로 변환한다.
        MemberSignUpRequestDTO memberDTO = memberMapper.requestProtoToDto(request);

        // 2. 서비스 레이어에서 request 데이터를 사용해서 RDB에 저장하는 로직을 수행하고 결과를 받는다.
        ResponseMemberDTO createdMember = memberService.createMember(memberDTO);

        // 3. RDB에 저장된 데이터를 gRPC response 데이터로 변환한다.
        MemberProto.MemberCreateResponse response = memberMapper.dtoToResponseProto(createdMember);

        // 4. 응답을 클라이언트에게 전달한다.
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}