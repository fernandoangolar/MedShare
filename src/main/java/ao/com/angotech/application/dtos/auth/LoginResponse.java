package ao.com.angotech.application.dtos.auth;

public record LoginResponse (

        String id,
        String name,
        String email,
        String token
) { }
