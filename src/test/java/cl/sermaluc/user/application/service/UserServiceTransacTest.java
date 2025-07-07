package cl.sermaluc.user.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import cl.sermaluc.common.exception.EmailException;
import cl.sermaluc.user.infrastructure.persistence.UserJpaRepository;
import cl.sermaluc.user.infrastructure.persistence.entity.UserEntity;
import cl.sermaluc.user.infrastructure.rest.dto.PhoneRequest;
import cl.sermaluc.user.infrastructure.rest.dto.UserRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTransacTest {
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserJpaRepository userRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Test
	@Transactional
	void createUser_rollbackTransaction_duplicateEmail() {
		// Usuario OK
		UserRequest request1 = new UserRequest();
		request1.setEmail("test@example.com");
		request1.setName("Test Uno");
		request1.setPassword("Password123");
		request1.setPhones(List.of(new PhoneRequest("12345678", "1", "56")));
		userService.createUser(request1);
		entityManager.flush(); // Forzar la insercion en BD
		
		// Usuario con error, mail duplicado
		UserRequest request2 = new UserRequest();
		request2.setEmail("test@example.com");
		request2.setName("Test Dos");
		request2.setPassword("Password123");
		request2.setPhones(List.of(new PhoneRequest("87654321", "2", "56")));
		
		assertThrows(EmailException.class, () -> userService.createUser(request2));
		
		// Verificar que solo hay un usuario en la base de datos
		List<UserEntity> users = userRepo.findAll();
		assertEquals(1, users.size(), "Solo debería existir un usuario en la base de datos");
	}
}
