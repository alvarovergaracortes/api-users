package cl.sermaluc.user.infraestructura.rest.dto;

import java.util.List;

import cl.sermaluc.common.annotation.PasswordPattern;
import cl.sermaluc.common.annotation.ValidEmail;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class UserRequest {
	@NotEmpty(message = "El nombre no puede estar vacío")
	private String name;
	
	@NotBlank(message = "El correo no puede estar vacio")
	@ValidEmail
	private String email;
	
	@NotBlank(message = "La contraseña no puede estar vacía")
	@PasswordPattern
	private String password;
	
	private boolean isActive;
	
	@NotEmpty(message = "Debe incluir al menos un teléfono")
	@Valid
    private List<PhoneRequest> phones;
	
	public UserRequest() {
	}
	
	public UserRequest(@NotEmpty(message = "El nombre no puede estar vacío") String name, String email, String password, boolean isActive, List<PhoneRequest> phones) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.isActive = isActive;
		this.phones = phones;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public List<PhoneRequest> getPhones() {
		return phones;
	}

	public void setPhones(List<PhoneRequest> phones) {
		this.phones = phones;
	}
}
