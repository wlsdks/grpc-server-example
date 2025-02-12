package com.demo.grpc.mapper;

import com.demo.grpc.dto.request.AddressDTO;
import com.demo.grpc.dto.request.ContactDTO;
import com.demo.grpc.dto.request.MemberSignUpRequestDTO;
import com.demo.grpc.dto.response.ResponseMemberDTO;
import com.demo.grpc.entity.Address;
import com.demo.grpc.entity.Contact;
import com.demo.grpc.entity.MemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * componentModel="spring"을 통해서 spring container에 Bean으로 등록 해 준다. (외부에서 주입받아서 사용하면 된다.)
 * unmappedTargetPolicy IGNORE 만약, target class에 매핑되지 않는 필드가 있으면, null로 넣게 되고, 따로 report하지 않는다.
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {

    // DTO -> Entity 변환 (생략된 매핑은 IGNORE 처리)
//    @Mapping(target = "etcInfo.address", source = "address")
//    @Mapping(target = "etcInfo.contact", source = "contact")
//    @Mapping(target = "etcInfo.interests", source = "interests")
//    @Mapping(target = "etcInfo.skills", source = "skills")
//    @Mapping(target = "etcInfo.metadata", source = "metadata")
    MemberEntity dtoToEntity(MemberSignUpRequestDTO dto);

    // Entity -> DTO 변환
//    @Mapping(target = "address", source = "etcInfo.address")
//    @Mapping(target = "contact", source = "etcInfo.contact")
//    @Mapping(target = "interests", source = "etcInfo.interests")
//    @Mapping(target = "skills", source = "etcInfo.skills")
//    @Mapping(target = "metadata", source = "etcInfo.metadata")
    ResponseMemberDTO entityToDto(MemberEntity entity);

    // 하위 객체 변환 메서드
    AddressDTO addressToAddressDTO(Address address);
    ContactDTO contactToContactDTO(Contact contact);

}
