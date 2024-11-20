package tn.esprit.spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import tn.esprit.spring.services.CourseServicesImplTest;
import tn.esprit.spring.services.SkierServicesImplTest;

@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class, TransactionalTestExecutionListener.class })
@ContextConfiguration(classes = {SkierServicesImplTest.class, CourseServicesImplTest.class})

@SpringBootTest
class GestionStationSkiApplicationTests {


	@Test
	void contextLoads() {
//loadcontext

	}

}
