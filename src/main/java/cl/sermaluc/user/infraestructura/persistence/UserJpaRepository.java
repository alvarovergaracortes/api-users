package cl.sermaluc.user.infraestructura.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.sermaluc.user.infraestructura.persistence.entity.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
	boolean existsByEmail(String email);
	Optional<UserEntity> findByEmail(String email);
}
