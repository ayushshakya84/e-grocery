package ru.voleshko.grocery.product.rest.v1.dto.category;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import ru.voleshko.grocery.product.rest.v1.dto.attribute.AttributeNestedDto;

import java.util.List;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategorySaveDto {

  @ApiModelProperty(example = "Vegetables", value = "")
  private String name;

  @ApiModelProperty(example = "Vegetables", value = "")
  private String description;

  @ApiModelProperty(example = "Grocery", value = "")
  private CategoryNestedDto parentCategory;

  private List<AttributeNestedDto> attributes;

  @ApiModelProperty(example = "true", value = "Defines if a category is leaf (is not a parent for any other category)")
  private boolean isLeaf;

}

