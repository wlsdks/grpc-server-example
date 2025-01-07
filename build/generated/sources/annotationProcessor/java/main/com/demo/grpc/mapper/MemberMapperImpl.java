package com.demo.grpc.mapper;

import com.demo.grpc.dto.MemberSignUpRequestDTO;
import com.demo.grpc.entity.Member;
import com.test.member.grpc.MemberProto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-07T21:12:38+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.1.jar, environment: Java 21.0.3 (Eclipse Adoptium)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public MemberSignUpRequestDTO requestProtoToDto(MemberProto.MemberRequest member) {
        if ( member == null ) {
            return null;
        }

        MemberSignUpRequestDTO memberSignUpRequestDTO = new MemberSignUpRequestDTO();

        memberSignUpRequestDTO.setId( member.getId() );
        memberSignUpRequestDTO.setEmail( member.getEmail() );
        memberSignUpRequestDTO.setPassword( member.getPassword() );
        memberSignUpRequestDTO.setName( member.getName() );
        memberSignUpRequestDTO.setProfileImageBase64( member.getProfileImageBase64() );
        memberSignUpRequestDTO.setEtcInfo( member.getEtcInfo() );

        return memberSignUpRequestDTO;
    }

    @Override
    public MemberProto.MemberCreateResponse dtoToResponseProto(Member createdMember) {
        if ( createdMember == null ) {
            return null;
        }

        MemberProto.MemberCreateResponse.Builder memberCreateResponse = MemberProto.MemberCreateResponse.newBuilder();

        if ( createdMember.getId() != null ) {
            memberCreateResponse.setId( createdMember.getId() );
        }
        memberCreateResponse.setEmail( createdMember.getEmail() );
        memberCreateResponse.setPassword( createdMember.getPassword() );
        memberCreateResponse.setName( createdMember.getName() );
        memberCreateResponse.setProfileImageBase64( createdMember.getProfileImageBase64() );
        memberCreateResponse.setEtcInfo( createdMember.getEtcInfo() );

        return memberCreateResponse.build();
    }

    @Override
    public Member dtoToEntity(MemberSignUpRequestDTO memberDTO) {
        if ( memberDTO == null ) {
            return null;
        }

        Long id = null;
        String email = null;
        String password = null;
        String name = null;
        String profileImageBase64 = null;
        String etcInfo = null;

        id = memberDTO.getId();
        email = memberDTO.getEmail();
        password = memberDTO.getPassword();
        name = memberDTO.getName();
        profileImageBase64 = memberDTO.getProfileImageBase64();
        etcInfo = memberDTO.getEtcInfo();

        Member member = new Member( id, email, password, name, profileImageBase64, etcInfo );

        return member;
    }
}
