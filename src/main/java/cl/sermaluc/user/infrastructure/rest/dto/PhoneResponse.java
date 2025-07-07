package cl.sermaluc.user.infrastructure.rest.dto;

public record PhoneResponse(String number, 
		String citycode, 
		String contrycode
)
{}
