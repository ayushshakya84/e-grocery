package ru.voleshko.grocery.product.rest.v1.dto.product;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import ru.voleshko.grocery.product.rest.v1.dto.category.CategoryNestedDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSaveDto {

  @ApiModelProperty(example = "9ca1e332-fe69-4d2a-af69-1daebf62d061", value = "")
  private UUID id;

  @ApiModelProperty(example = "Milk", value = "")
  private String name;

  @ApiModelProperty(example = "Lactose-free milk", value = "")
  private String description;

  @ApiModelProperty(example = "100.0", value = "")
  private BigDecimal price;

  @ApiModelProperty(example = "10", value = "")
  private int qty;

  @ApiModelProperty(example = "10.5", value = "Weight, gram")
  private BigDecimal weight;

  @ApiModelProperty(example = "10", value = "Discount, %")
  private Integer discountPercent;

  @JsonRawValue
  @ApiModelProperty(example = "\"{ volume: 10 }\"", value = "")
  private Object attributes;

  private List<CategoryNestedDto> categories;

}

