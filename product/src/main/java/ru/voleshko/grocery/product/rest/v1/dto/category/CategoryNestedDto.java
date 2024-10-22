package ru.voleshko.grocery.product.rest.v1.dto.category;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryNestedDto {

  @ApiModelProperty(example = "d4d36d16-1ab8-4479-8ad1-fbebfc2b19e8")
  private UUID id;

  @ApiModelProperty(example = "Vegetables", value = "")
  private String name;

}

