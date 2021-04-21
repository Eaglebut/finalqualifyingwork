package ru.sfedu.finalqualifyingwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.sfedu.finalqualifyingwork.model.User;
import ru.sfedu.finalqualifyingwork.model.enums.Role;
import ru.sfedu.finalqualifyingwork.model.enums.Status;
import ru.sfedu.finalqualifyingwork.repository.HibernateUserDao;
import ru.sfedu.finalqualifyingwork.repository.UserDao;

@SpringBootApplication
public class FinalQualifyingWorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinalQualifyingWorkApplication.class, args);
	}

}
