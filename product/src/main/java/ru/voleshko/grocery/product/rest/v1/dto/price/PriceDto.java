package ru.voleshko.grocery.product.rest.v1.dto.price;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.processing.Generated;
import java.util.UUID;

/**
 * Price
 */
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2021-01-09T16:36:59.377Z")



@EqualsAndHashCode(of = "id", callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto extends PriceSaveDto {

  @ApiModelProperty(example = "9ca1e332-fe69-4d2a-af69-1daebf62d061", value = "")
  private UUID id;

}

