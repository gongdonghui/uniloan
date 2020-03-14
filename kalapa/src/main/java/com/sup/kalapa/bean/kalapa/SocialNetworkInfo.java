package com.sup.kalapa.bean.kalapa;

import lombok.Data;

import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2020/2/10 12:04
 */
@Data
public class SocialNetworkInfo {
    private String mobile;
    private String fbLink;
    private String fbName;
    private List<FacebookInfo> topFriends;
}
