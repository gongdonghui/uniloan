package com.sup.core;

import com.sup.common.service.CoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CreditApplicationTests {

	@Autowired
	private CoreService service;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testApplyService() {
		System.out.println(service.updateApplyInfo(null));
	}

}
