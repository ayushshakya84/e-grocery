package ru.voleshko.grocery.ordersaga.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.voleshko.grocery.ordersaga.entity.AuditLogRecord;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLogRecord, UUID> {

    List<AuditLogRecord> findAllByOrderIdOrderByCreatedAtAsc(UUID orderId);
}
