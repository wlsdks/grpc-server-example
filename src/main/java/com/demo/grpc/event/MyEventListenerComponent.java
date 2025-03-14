package com.demo.grpc.event;

import jakarta.servlet.http.HttpServletRequest;
import net.devh.boot.grpc.server.event.GrpcServerStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class MyEventListenerComponent {

    @EventListener
    public void onServerStarted(GrpcServerStartedEvent event) {
        System.out.println("gRPC Server started, listening on address: " + event.getAddress() + ", port: " + event.getPort());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMemberCreated(MemberCreatedEvent event) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        System.out.println("========== Member Created Event Start ==========");
        if (requestAttributes instanceof ServletRequestAttributes) {
            requestInfoLog((ServletRequestAttributes) requestAttributes);
        } else {
            System.out.println("로그에 기록할 Request 정보를 찾을 수 없습니다.");
        }
        System.out.println("========== Member Created Event End ==========");
        System.out.println("");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void recordAuditLogHandler(AuditMarkerEvent event) {
        // 현재 요청 정보를 가져옵니다.
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        // Request 정보를 로그에 기록
        System.out.println("========== Audit Log Start ==========");
        if (requestAttributes instanceof ServletRequestAttributes) {
            requestInfoLog((ServletRequestAttributes) requestAttributes);
        } else {
            System.out.println("로그에 기록할 Request 정보를 찾을 수 없습니다.");
        }
        System.out.println("========== Audit Log End ==========");
    }

    private void requestInfoLog(ServletRequestAttributes requestAttributes) {
        HttpServletRequest request = requestAttributes.getRequest();
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Client IP: " + request.getRemoteAddr());
        System.out.println("Request Method: " + request.getMethod());
        System.out.println("Request User-Agent: " + request.getHeader("User-Agent"));
        System.out.println("Request Referer: " + request.getHeader("Referer"));
    }

}