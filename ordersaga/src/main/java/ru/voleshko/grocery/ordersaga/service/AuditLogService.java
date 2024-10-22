package ru.voleshko.grocery.ordersaga.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.ordersaga.entity.AuditLogRecord;
import ru.voleshko.grocery.ordersaga.repository.AuditLogRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public List<AuditLogRecord> findByOrderId(UUID orderId) {
        return auditLogRepository.findAllByOrderIdOrderByCreatedAtAsc(orderId);
    }
}
