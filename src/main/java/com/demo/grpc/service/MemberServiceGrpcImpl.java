package com.demo.grpc.service;

import com.demo.grpc.entity.MemberEntity;
import com.demo.grpc.repository.MemberRepository;
import com.test.member.grpc.MemberProto;
import com.test.member.grpc.MemberServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class MemberServiceGrpcImpl extends MemberServiceGrpc.MemberServiceImplBase {

    private final MemberRepository memberRepository;

    /**
     * @param request          : 클라이언트로부터 받은 요청
     * @param responseObserver : 클라이언트로부터 받은 요청에 대한 응답을 전송하는 스트림
     * @apiNote 클라이언트로부터 받은 요청에 대한 응답을 전송하는 메서드
     */
    @Override
    public void getMemberById(MemberProto.MemberIdRequest request,
                              StreamObserver<MemberProto.MemberResponse> responseObserver) {
        // 메서드 진입 로깅 추가
        log.trace(("gRPC 서버의 getMemberById 메서드 실행 시작 - ID: {}"), request.getId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.trace("gRPC 서버 메서드 내부 시큐리티 인증 정보: {}", authentication.getPrincipal());

        try {
            MemberEntity member = memberRepository.findById(request.getId())
                    .orElseThrow(() -> {
                        log.error("회원을 찾을 수 없음 - ID: {}", request.getId());
                        return new StatusRuntimeException(
                                Status.NOT_FOUND.withDescription("Member not found")
                        );
                    });

            log.trace("회원 조회 성공 - 이메일: {}", member.getEmail());

            // 기존 응답 로직
            MemberProto.MemberResponse response = MemberProto.MemberResponse.newBuilder()
                    .setId(member.getId())
                    .setEmail(member.getEmail())
                    .setName(member.getName())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("getMemberById 메서드 실행 중 오류 발생", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

}