package com.sup.paycenter;

import com.sup.common.bean.paycenter.BankInfo;
import com.sup.common.bean.paycenter.PayInfo;
import com.sup.common.bean.paycenter.RepayInfo;
import com.sup.common.service.PayCenterService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaycenterApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private PayCenterService payCenterService;

    @Test
    public void getBankList() {
        System.out.println(payCenterService.getBankList("0"));
    }

    @Test
    public void verifyBankInfo(){
        BankInfo b = new BankInfo();
        b.setAccountName("HAHA");
        b.setAccountNo("3489");
        b.setAccountType(0);
        b.setBankNo("1");
        System.out.println(payCenterService.verifyBankInfo(b));
    }

    @Test
    public void pay() {
        PayInfo p = new PayInfo();
        p.setUserId("11223332");
        p.setOrderNo("33221212");
        p.setAmount(500000);
        p.setRemark("Hello Test3");
        p.setBankNo("1");
        p.setAccountNo("348963");
        p.setAccountType(0);
        p.setAccountName("HA");
        p.setTransferTime("");
        System.out.println(payCenterService.pay(p));
    }

    @Test
    public void repay() {
        RepayInfo r = new RepayInfo();
        r.setAmount(500000);
        r.setApplyId("33221111");
        r.setName("ABCDE");
        r.setPhone("123098421232");
        r.setUserId("1122323");
        System.out.println(payCenterService.repay(r));
    }
}

