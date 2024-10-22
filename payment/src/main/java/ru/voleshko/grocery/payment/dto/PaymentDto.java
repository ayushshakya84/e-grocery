package ru.voleshko.grocery.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentDto {

    @JsonProperty("id")
    private UUID orderId;
    private double totalPrice;
    private String cardNumber;
    private String cardValidTo;
    private String cardCvv;

}
