package ru.voleshko.grocery.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.voleshko.grocery.payment.entity.Payment;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/payments/{orderId}")
    public List<Payment> findByOrderId(@PathVariable UUID orderId) {
        return paymentService.findByOrderId(orderId);
    }
}
