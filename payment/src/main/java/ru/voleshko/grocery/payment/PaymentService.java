package ru.voleshko.grocery.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.payment.entity.Payment;
import ru.voleshko.grocery.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public List<Payment> findByOrderId(UUID orderId) {
        return paymentRepository.findAllByOrderIdOrderByCreatedAtDesc(orderId);
    }

    public Payment doOrderPayment(
            UUID orderId,
            double totalPrice,
            String cardNumber,
            String cardValidTo,
            String cardCvv
    ) {

        if (isPaymentFailed(new BigDecimal(totalPrice), cardNumber, cardValidTo, cardCvv)) {
            throw new IllegalStateException("Payment failed!");
        }

        Payment payment = new Payment(
                null,
                Payment.PaymentStatus.SUCCESS,
                new BigDecimal(totalPrice),
                cardNumber,
                cardValidTo,
                orderId,
                ZonedDateTime.now()
        );
        return paymentRepository.save(payment);
    }

    @Transactional
    public void refund(UUID orderId) {
        paymentRepository.findLatestPaymentForOrder(orderId)
                .ifPresentOrElse(
                        payment -> {
                            doRefund(payment.getAmount(), payment.getCardNumber(), payment.getCardValidTo());
                            payment.setStatus(Payment.PaymentStatus.REFUNDED);
                        },
                        () -> {
                            throw new IllegalStateException("No payments were found for orderId=[" + orderId + "], so " +
                                    "nothing to refund");
                        });
    }

    private boolean isPaymentFailed(BigDecimal totalPrice,
                                    String cardNumber,
                                    String cardValidTo,
                                    String cardCvv) {

        // just mock 3rd party payment system RPC (1 of 10 requests would fail)
        return (new Random().nextInt(10) + 1) == 1;
    }

    private void doRefund(BigDecimal refundAmount,
                          String cardNumber,
                          String cardValidTo) {
        // just mock 3rd party payment system refund
        log.info(
                "Refund is done to Card: [number={}, validTo={}] with amount of [{}]",
                cardNumber, cardValidTo, refundAmount
        );
    }
}
