package com.sup.cms.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/2 16:16
 */
@Data
public class CollectionRecordsExportParam {
    /**
     * 进件id
     */
    private Integer applyId;

    private Date start;

    private Date end;
}
