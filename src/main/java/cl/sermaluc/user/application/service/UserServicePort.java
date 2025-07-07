package cl.sermaluc.user.application.service;

import java.util.List;
import java.util.UUID;

import cl.sermaluc.user.infraestructura.rest.dto.UserRequest;
import cl.sermaluc.user.infraestructura.rest.dto.UserResponse;

public interface UserServicePort {
	UserResponse createUser(UserRequest request);
	UserResponse update(UUID id, UserRequest request);
	void delete(UUID id);
	UserResponse findById(UUID id);
	List<UserResponse> findAll();
}
