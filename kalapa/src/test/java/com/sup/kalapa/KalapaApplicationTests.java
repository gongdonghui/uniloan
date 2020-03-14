package com.sup.kalapa;

import com.sup.common.bean.paycenter.BankInfo;
import com.sup.common.bean.paycenter.PayInfo;
import com.sup.common.bean.paycenter.RepayInfo;
import com.sup.common.service.PayCenterService;
import com.sup.kalapa.bean.dto.GetCICBInfoParams;
import com.sup.kalapa.bean.dto.GetFBInfoParams;
import com.sup.kalapa.bean.dto.GetVSSInfoParams;
import com.sup.kalapa.controller.KalapaController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KalapaApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private KalapaController kalapa;

    @Test
    public void vssTest() {
        GetVSSInfoParams params = new GetVSSInfoParams();
        params.setId("221069377");
        params.setGetHRInfo(true);
        params.setGetMaxLatestJobs(true);
        System.out.println(kalapa.getVSSInfo(params));
    }

    @Test
    public void cicbTest() {
        GetCICBInfoParams params = new GetCICBInfoParams();
        params.setId("221069377");
        System.out.println(kalapa.getCICBInfo(params));
    }

    @Test
    public void fbTest() {
        GetFBInfoParams params = new GetFBInfoParams();
        params.setFbid("");
        System.out.println(kalapa.getFBInfo(params));
    }
}

