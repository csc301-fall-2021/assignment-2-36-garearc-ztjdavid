package com.csc301group36.Covid19API;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class Covid19ApiApplicationTests {

	@Test
	void contextLoads() throws Exception {
		File dir = new File("LOL");
		System.out.println(dir.mkdirs());
//		System.out.println(.createNewFile());
	}

}
