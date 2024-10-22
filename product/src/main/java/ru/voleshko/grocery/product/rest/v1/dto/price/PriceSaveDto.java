package ru.voleshko.grocery.product.rest.v1.dto.price;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import ru.voleshko.grocery.product.rest.v1.dto.product.ProductNestedDto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceSaveDto {

  private ZonedDateTime startDate;

  @ApiModelProperty(example = "100.0", value = "")
  private BigDecimal value;

  private ProductNestedDto product;
}

