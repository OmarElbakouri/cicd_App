package com.cicd.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CicdApplicationTests {

	@Test
	void contextLoads() {
		// Ce test vérifie simplement que le contexte de l'application se charge correctement
	}

}
