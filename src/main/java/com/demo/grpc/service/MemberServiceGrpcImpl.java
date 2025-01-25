package com.demo.grpc.service;

import com.demo.grpc.entity.MemberEntity;
import com.demo.grpc.mapper.GrpcMemberMapper;
import com.demo.grpc.repository.MemberRepository;
import com.test.member.grpc.MemberProto;
import com.test.member.grpc.MemberServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true) // 트랜잭션 추가하여 지연 로딩 문제 해결
@RequiredArgsConstructor
@GrpcService
public class MemberServiceGrpcImpl extends MemberServiceGrpc.MemberServiceImplBase {

    private final MemberRepository memberRepository;
    private final GrpcMemberMapper grpcMemberMapper;

    /**
     * @param request          : 클라이언트로부터 받은 요청
     * @param responseObserver : 클라이언트로부터 받은 요청에 대한 응답을 전송하는 스트림
     * @apiNote 클라이언트로부터 받은 요청에 대한 응답을 전송하는 메서드
     */
    @Override
    public void getMemberById(MemberProto.MemberIdRequest request,
                              StreamObserver<MemberProto.MemberResponse> responseObserver) {
        try {
            MemberEntity member = memberRepository.findById(request.getId())
                    .orElseThrow(() -> {
                        log.error("회원을 찾을 수 없음 - ID: {}", request.getId());
                        return new StatusRuntimeException(
                                Status.NOT_FOUND.withDescription("Member not found")
                        );
                    });

            // 기존 응답 로직
            responseObserver.onNext(grpcMemberMapper.entityToProto(member));
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