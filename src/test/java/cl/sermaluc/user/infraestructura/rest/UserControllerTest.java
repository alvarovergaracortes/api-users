package cl.sermaluc.user.infraestructura.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import cl.sermaluc.common.security.CustomAccessDeniedHandler;
import cl.sermaluc.common.security.JwtUtil;
import cl.sermaluc.common.security.SecurityConfig;
import cl.sermaluc.user.application.service.UserServicePort;
import cl.sermaluc.user.infrastructure.rest.UserController;
import cl.sermaluc.user.infrastructure.rest.dto.UserRequest;
import cl.sermaluc.user.infrastructure.rest.dto.UserResponse;



@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, CustomAccessDeniedHandler.class})
public class UserControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockitoBean
	private UserServicePort service;
	
	@MockitoBean
	private JwtUtil jwtUtil;

	private final String email = "juan@gmail.com";
	
	@Test
	void insertUser_return201() throws Exception{
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
		
		when(service.createUser(any())).thenReturn(response);
		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"name": "juan",
						    "email": "juan@gmail.com",
						    "password": "mIcontraseña123",
						    "phones": [
						        {
						            "number": "12345678",
						            "citycode": "1",
						            "contrycode": "59"
						        },
						        {
						            "number": "87654321",
						            "citycode": "1",
						            "contrycode": "59"
						        }
						    ]
						}
						"""))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.email").value(email));

	}

	
	@Test
    void getUserById_return200() throws Exception {
		final UUID id = UUID.randomUUID();
		
		UserResponse response = new UserResponse(
				id,
				"juan",
				email,
				LocalDateTime.now(),
				LocalDateTime.now(),
				LocalDateTime.now(),
				"token-falso",
                true,
                List.of()
        );
        
        when(jwtUtil.validateToken("token-falso")).thenReturn(true);
        when(jwtUtil.extractUsername("token-falso")).thenReturn("usuario-test");

        when(service.findById(id)).thenReturn(response);
        

        mockMvc.perform(get("/users/{id}", id)
        		.header("Authorization", "Bearer token-falso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }
	
	@Test
	void getAllUsers_return200() throws Exception {
	    UUID id = UUID.randomUUID();
	    
	    List<UserResponse> userList = List.of(
	        new UserResponse(
	            id,
	            "juan",
	            email,
	            LocalDateTime.now(),
	            LocalDateTime.now(),
	            LocalDateTime.now(),
	            "token-falso",true,
	            List.of()
	        )
	    );

	    when(jwtUtil.validateToken("token-falso")).thenReturn(true);
	    when(jwtUtil.extractUsername("token-falso")).thenReturn("juan");

	    when(service.findAll()).thenReturn(userList);

	    mockMvc.perform(get("/users")
	    		.header("Authorization", "Bearer token-falso"))
	    	.andExpect(status().isOk())
	    	.andExpect(jsonPath("$[0].id").value(id.toString()))
	    	.andExpect(jsonPath("$[0].name").value("juan"))
	    	.andExpect(jsonPath("$[0].email").value(email));
	}
	
	@Test
	void updateUser_return200() throws Exception {
		UUID id = UUID.randomUUID();

		UserResponse response = new UserResponse(
				id,
				"juan",
				email,
				LocalDateTime.now(),
				LocalDateTime.now(),
				LocalDateTime.now(),
				"token-falso",
				true,
				List.of()
		);
		
		when(jwtUtil.validateToken("token-falso")).thenReturn(true);
		when(jwtUtil.extractUsername("token-falso")).thenReturn("juan");

		when(service.update(eq(id), any(UserRequest.class))).thenReturn(response);

		mockMvc.perform(put("/users/{id}", id)
				.header("Authorization", "Bearer token-falso")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"name": "juan",
						"email": "juan@gmail.com",
						"password": "NuevaClave123",
						"phones": []
					}
				"""))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(id.toString()))
		.andExpect(jsonPath("$.email").value(email))
		.andExpect(jsonPath("$.name").value("juan"));
	}
	
	
	@Test
	void createUser_badRequest_400_invalidEmail() throws Exception {
		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"juan\", \"email\": \"correo-no-valido\", \"password\": \"Clave12345\", \"phones\": [] }"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.mensaje").exists());
	}
	
	
	@Test
	void accessDenied_return403Message() throws Exception{
		UUID id = UUID.randomUUID();
		
		when(jwtUtil.validateToken("token-falso")).thenReturn(true);
		when(jwtUtil.extractUsername("token-falso")).thenReturn("usuario-prueba");
		
		doThrow(new AccessDeniedException("No Autorizado")).when(service).findById(id);
		
		mockMvc.perform(get("/users/{id}", id)
				.header("Authorization", "Bearer token-falso"))
				.andExpect(status().isForbidden())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.mensaje").value("No tienes permisos para acceder a este recurso"));
	}
}