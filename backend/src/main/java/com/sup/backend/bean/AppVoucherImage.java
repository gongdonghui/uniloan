package com.sup.backend.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AppVoucherImage {
  private Integer id;
  private String info_id;
  private Integer apply_id;
  private String image_key;
  private Integer image_object;
}
