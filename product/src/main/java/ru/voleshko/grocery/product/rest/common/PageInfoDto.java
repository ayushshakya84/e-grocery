package ru.voleshko.grocery.product.rest.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@ApiModel(description = "Информация о страницы пагинации")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageInfoDto {

    @ApiModelProperty(value = "Размер страницы", example = "20")
    private Integer size;

    @ApiModelProperty(value = "Общее число элементов", example = "50")
    private Long totalElements;

    @ApiModelProperty(value = "Общее число страниц", example = "3")
    private Integer totalPages;

    @ApiModelProperty(value = "Номер текущей страницы", example = "0")
    private Integer number;

    public static PageInfoDto pageInfoDtoOf(Integer size, Long totalElements, Integer totalPages, Integer number) {
        return new PageInfoDto(size, totalElements, totalPages, number);
    }

    public static PageInfoDto pageInfoDtoOf(Page<?> page) {
        return new PageInfoDto(page.getSize(), page.getTotalElements(), page.getTotalPages(), page.getNumber());
    }
}
