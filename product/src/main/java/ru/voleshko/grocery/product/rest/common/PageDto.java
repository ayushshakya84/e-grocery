package ru.voleshko.grocery.product.rest.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

import static ru.voleshko.grocery.product.rest.common.PageInfoDto.pageInfoDtoOf;

@ApiModel(description = "Страница пагинации, содержащая список сущностей")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T> {

    @ApiModelProperty(value = "Список сущностей")
    private List<T> data;

    @ApiModelProperty(value = "Информация о странице")
    private PageInfoDto page;

    public static<T> PageDto<T> pageDtoOf(List<T> data, PageInfoDto page) {
        return new PageDto<>(data, page);
    }

    public static<T> PageDto<T> pageDtoOf(Page<T> page) {
        return pageDtoOf(
                page.getContent(),
                pageInfoDtoOf(
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.getNumber()
                )
        );
    }
}
