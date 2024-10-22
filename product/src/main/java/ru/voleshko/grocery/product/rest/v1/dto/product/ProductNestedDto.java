package ru.voleshko.grocery.product.rest.v1.dto.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductNestedDto {

  @ApiModelProperty(example = "9ca1e332-fe69-4d2a-af69-1daebf62d061", value = "")
  private UUID id;

  @ApiModelProperty(example = "Milk", value = "")
  private String name;

  @ApiModelProperty(example = "100.0", value = "")
  private BigDecimal price;

}

