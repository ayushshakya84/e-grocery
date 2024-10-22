package ru.voleshko.grocery.ordersaga.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.voleshko.grocery.ordersaga.entity.AuditLogRecord;
import ru.voleshko.grocery.ordersaga.service.AuditLogService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class OrderSagaController {

    private final AuditLogService auditLogService;

    @GetMapping("/logs/{orderId}")
    public List<AuditLogRecord> findByOrderId(@PathVariable UUID orderId) {
        return auditLogService.findByOrderId(orderId);
    }
}
