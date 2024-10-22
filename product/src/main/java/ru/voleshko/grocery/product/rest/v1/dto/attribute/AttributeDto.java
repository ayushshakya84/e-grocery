package ru.voleshko.grocery.product.rest.v1.dto.attribute;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@EqualsAndHashCode(of = "id", callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeDto extends AttributeSaveDto {

  @ApiModelProperty(example = "9ca1e332-fe69-4d2a-af69-1daebf62d061", value = "")
  private UUID id;

}

