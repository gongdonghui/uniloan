package com.sup.kalapa.bean.kalapa;

import lombok.Data;

import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2020/2/10 11:43
 */
@Data
public class FamilyInfo {
    private String houseOwner;
    private List<FamilyMember> familyMember;
}
