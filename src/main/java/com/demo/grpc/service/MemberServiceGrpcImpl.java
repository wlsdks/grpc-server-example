package com.demo.grpc.service;

import com.demo.grpc.dto.MemberSignUpRequestDTO;
import com.demo.grpc.dto.ResponseMemberDTO;
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

    /**
     * @param responseObserver : 클라이언트로부터 받은 요청에 대한 응답을 전송하는 스트림
     * @return StreamObserver<MemberProto.MemberRequest> : 클라이언트로부터 받은 요청을 처리하는 스트림
     * @apiNote 클라이언트로부터 스트리밍 요청을 받아서 응답을 스트리밍으로 전송하는 메서드
     */
    @Override
    public StreamObserver<MemberProto.MemberRequest> streamCreateMember(StreamObserver<MemberProto.MemberCreateResponse> responseObserver) {
        return new StreamObserver<MemberProto.MemberRequest>() {
            @Override
            public void onNext(MemberProto.MemberRequest request) {
                // 클라이언트 요청 처리 (예시로 받은 데이터를 그대로 응답)
                MemberProto.MemberCreateResponse response = MemberProto.MemberCreateResponse.newBuilder()
                        .setId(request.getId())
                        .setEmail(request.getEmail())
                        .setPassword("EncryptedPassword") // 예시로 비밀번호를 처리한 상태로 응답
                        .setName(request.getName())
                        .setProfileImageBase64(request.getProfileImageBase64())
                        .setEtcInfo(request.getEtcInfo())
                        .build();

                // 클라이언트로 응답 스트림 전송
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {
                log.error("Streaming 요청 처리 중 에러 발생: ", t);
            }

            @Override
            public void onCompleted() {
                // 스트리밍 완료
                log.info("Streaming 요청 완료");
                responseObserver.onCompleted();
            }
        };
    }

}