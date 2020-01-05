package com.sup.market.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkySmsResponse implements Serializable  {
  Integer status;
  Integer success;
  String desc;
}
