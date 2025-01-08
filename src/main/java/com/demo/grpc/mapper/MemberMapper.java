package com.demo.grpc.mapper;

import com.demo.grpc.dto.MemberSignUpRequestDTO;
import com.demo.grpc.dto.ResponseMemberDTO;
import com.demo.grpc.entity.MemberEntity;
import com.test.member.grpc.MemberProto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * componentModel="spring"을 통해서 spring container에 Bean으로 등록 해 준다. (외부에서 주입받아서 사용하면 된다.)
 * unmappedTargetPolicy IGNORE 만약, target class에 매핑되지 않는 필드가 있으면, null로 넣게 되고, 따로 report하지 않는다.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {

    // gRPC request를 DTO로 변환
    MemberSignUpRequestDTO requestProtoToDto(MemberProto.MemberRequest member);

    // dto를 gRPC response로 변환
    MemberProto.MemberCreateResponse dtoToResponseProto(ResponseMemberDTO createdMember);

    // DTO를 Entity로 변환
    MemberEntity dtoToEntity(MemberSignUpRequestDTO memberDTO);

    // Entity를 DTO로 변환
    ResponseMemberDTO dtoToResponseDto(MemberEntity savedMemberEntity);

}