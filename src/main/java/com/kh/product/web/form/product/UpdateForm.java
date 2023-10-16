package com.kh.product.web.form.product;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateForm {
  private Long pid;
  @NotBlank // null, 빈문자열("")를 허용 안함, 문자열 타입만 사용
  @Size(min=1,max=10)
  private String pname;
  @NotNull
  @Positive   //양수
  private Long quantity;

  @NotNull
  @Positive
  @Min(100)
  @Max(1000000)
  private Long price;
}
