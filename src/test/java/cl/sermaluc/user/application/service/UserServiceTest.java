package cl.sermaluc.user.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.sermaluc.common.exception.EmailException;
import cl.sermaluc.common.exception.UserException;
import cl.sermaluc.common.security.JwtUtil;
import cl.sermaluc.user.infrastructure.persistence.UserJpaRepository;
import cl.sermaluc.user.infrastructure.persistence.entity.UserEntity;
import cl.sermaluc.user.infrastructure.persistence.mapper.UserMapper;
import cl.sermaluc.user.infrastructure.rest.dto.PhoneResponse;
import cl.sermaluc.user.infrastructure.rest.dto.UserRequest;
import cl.sermaluc.user.infrastructure.rest.dto.UserResponse;
import cl.sermaluc.user.infrastructure.rest.mapper.UserDtoMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	private UserJpaRepository userRepo;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private UserMapper userMapper;

	@Mock
	private UserDtoMapper userDtoMapper;

	@InjectMocks
	private UserService userService;
	
	@PersistenceContext
	private EntityManager entityManager;

	private final UUID fakeId = UUID.randomUUID();
	private final String email = "juan@gmail.com";

	@Test
	void createUser_MailNOExiste() {
		UserRequest request = new UserRequest("juan", email, "1Aooo2344555", true, List.of());
		UserEntity userEntity = new UserEntity();
		UserResponse response = new UserResponse(
					UUID.randomUUID(),
					"juan",
					email,
					LocalDateTime.now(),
					LocalDateTime.now(),
					LocalDateTime.now(),
					"token-falso",
					true,
					List.of()
				);
		
		when(userRepo.findByEmail(email)).thenReturn(Optional.empty());
		when(jwtUtil.generateToken(any())).thenReturn("token-falso");
		when(userMapper.toEntity(any(), any(), any(), any())).thenReturn(userEntity);
		
		when(userRepo.save(userEntity)).thenReturn(userEntity);
		when(userDtoMapper.toResponseSucces(userEntity)).thenReturn(response);
		
		UserResponse result = userService.createUser(request);
		assertEquals(response, result);
		verify(userRepo).save(userEntity);
	}
	
	@Test
    void createUser_EmailSIExiste() {
		UserRequest request = new UserRequest("juan", email, "1Aooo2344555", true, List.of());
		
		when(userRepo.findByEmail(email)).thenReturn(Optional.of(new UserEntity()));
		EmailException ex = assertThrows(EmailException.class, () -> {
			userService.createUser(request);
		});
		
		assertTrue(ex.getMessage().contains("ya está registrado"));
		verify(userRepo, never()).save(any());
    }
	
	@Test
    void findById_deberiaRetornarUsuario() {
		UserEntity user = new UserEntity();
		List<PhoneResponse> listPhone = List.of(new PhoneResponse("1234567", "1", "57"));
		UserResponse response = new UserResponse(
				UUID.randomUUID(),
				"juan",
				email,
				LocalDateTime.now(),
				LocalDateTime.now(),
				LocalDateTime.now(),
				"",
				true,
				listPhone
		);
		
		when(userRepo.findById(fakeId)).thenReturn(Optional.of(user));
		when(userDtoMapper.toResponse(user)).thenReturn(response);
		
		UserResponse result = userService.findById(fakeId);
		assertEquals(response, result);
	}
	
	@Test
	void findById_deberiaLanzarExcepcionSiNoExiste() {
		when(userRepo.findById(fakeId)).thenReturn(Optional.empty());
		assertThrows(UserException.class, () -> userService.findById(fakeId));
	}
	
	
	@Test
	void delete_deberiaMarcarUsuarioInactivo() {
		UserEntity user = new UserEntity();
		when(userRepo.findById(fakeId)).thenReturn(Optional.of(user));
		userService.delete(fakeId);
		assertFalse(user.isActive());
		verify(userRepo).save(user);
	}
}
