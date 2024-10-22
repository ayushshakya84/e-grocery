package ru.voleshko.grocery.product.rest.v1.dto.attribute;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeSaveDto {

  @ApiModelProperty(example = "Volume, l", value = "")
  private String name;

  @ApiModelProperty(example = "NUMERIC", value = "")
  private String type;

}

