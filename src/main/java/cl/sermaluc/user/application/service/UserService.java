package cl.sermaluc.user.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.sermaluc.common.exception.EmailException;
import cl.sermaluc.common.exception.UserNotFoundException;
import cl.sermaluc.common.security.JwtUtil;
import cl.sermaluc.user.infrastructure.persistence.UserJpaRepository;
import cl.sermaluc.user.infrastructure.persistence.entity.UserEntity;
import cl.sermaluc.user.infrastructure.persistence.mapper.UserMapper;
import cl.sermaluc.user.infrastructure.rest.dto.UserRequest;
import cl.sermaluc.user.infrastructure.rest.dto.UserResponse;
import cl.sermaluc.user.infrastructure.rest.mapper.UserDtoMapper;

@Service
public class UserService implements UserServicePort{
	private final UserJpaRepository userRepo;
	private final JwtUtil jwtService;
	private final UserMapper userMapper;
	private final UserDtoMapper userDtoMapper;

	public UserService(UserJpaRepository userRepo, JwtUtil jwtService, UserMapper userMapper,
			UserDtoMapper userDtoMapper) {
		this.userRepo = userRepo;
		this.jwtService = jwtService;
		this.userMapper = userMapper;
		this.userDtoMapper = userDtoMapper;
	}


	@Override
	public UserResponse createUser(UserRequest request) {
		userRepo.findByEmail(request.getEmail()).ifPresent(u -> {
			throw new EmailException("El correo " + request.getEmail() + ", ya está registrado ");
		});
		
		String token = jwtService.generateToken(UUID.randomUUID());
		UserEntity userEntity = userMapper.toEntity(request, UUID.randomUUID(), token, LocalDateTime.now());
		UserEntity entity =  userRepo.save(userEntity);
		return userDtoMapper.toResponseSucces(entity);
	}


	@Override
	public UserResponse update(UUID id, UserRequest request) {
		UserEntity existing = userRepo.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
		
		userDtoMapper.mapUpdate(existing, request);
		
		UserEntity entity = userRepo.save(existing);
		
		return userDtoMapper.toResponseSucces(entity);
	}
	
	

	@Override
	public void delete(UUID id) {
		UserEntity user = userRepo.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
		
		user.setActive(false);
		user.setModified(LocalDateTime.now());
		
		userRepo.save(user);
	}


	@Override
	public UserResponse findById(UUID id) {
		UserEntity entity = userRepo.findById(id)
				.orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + id));
		
		return userDtoMapper.toResponse(entity);
	}

	
	@Override
	public List<UserResponse> findAll() {
		return userRepo.findAll().stream()
				.map(userDtoMapper::toResponse)
				.collect(Collectors.toList());
	}
}
