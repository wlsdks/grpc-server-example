package com.demo.grpc.mapper;

import com.demo.grpc.entity.Address;
import com.demo.grpc.entity.Contact;
import com.demo.grpc.entity.MemberEntity;
import com.test.member.grpc.MemberProto;
import org.springframework.stereotype.Component;

@Component
public class GrpcMemberMapper {

    public MemberProto.MemberResponse entityToProto(MemberEntity entity) {
        MemberProto.MemberResponse.Builder builder = MemberProto.MemberResponse.newBuilder()
                .setId(entity.getId())
                .setEmail(entity.getEmail())
                .setName(entity.getName())
                .setProfileImageBase64(entity.getProfileImageBase64());

//        if (entity.getEtcInfo() != null) {
//            builder.setAddress(buildAddress(entity.getEtcInfo().getAddress()))
//                    .setContact(buildContact(entity.getEtcInfo().getContact()))
//                    .addAllInterests(entity.getEtcInfo().getInterests())
//                    .addAllSkills(entity.getEtcInfo().getSkills())
//                    .setMetadata(entity.getEtcInfo().getMetadata());
//        }

        return builder.build();
    }

    private MemberProto.Address buildAddress(Address address) {
        if (address == null) return MemberProto.Address.getDefaultInstance();

        return MemberProto.Address.newBuilder()
                .setStreet(address.getStreet())
                .setCity(address.getCity())
                .setCountry(address.getCountry())
                .setPostalCode(address.getPostalCode())
                .putAllAdditionalInfo(address.getAdditionalInfo())
                .build();
    }

    private MemberProto.Contact buildContact(Contact contact) {
        if (contact == null) return MemberProto.Contact.getDefaultInstance();

        return MemberProto.Contact.newBuilder()
                .setPhone(contact.getPhone())
                .setMobile(contact.getMobile())
                .setWorkPhone(contact.getWorkPhone())
                .addAllEmails(contact.getEmails())
                .putAllSocialMedia(contact.getSocialMedia())
                .build();
    }

}