package com.demo.grpc.event;

public record MemberCreatedEvent(
        Long memberId,
        String email
) implements AuditMarkerEvent {

    public static MemberCreatedEvent of(Long memberId, String email) {
        return new MemberCreatedEvent(memberId, email);
    }
}
