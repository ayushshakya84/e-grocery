package ru.voleshko.grocery.product.rest.v1.dto.attribute;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeNestedDto {

  private UUID id;

  @ApiModelProperty(example = "Volume", value = "")
  private String name;

  @ApiModelProperty(example = "liter", value = "")
  private String measureUnit;

  @ApiModelProperty(example = "NUMERIC", value = "")
  private String type;
}

