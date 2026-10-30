package org.acme.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.acme.dto.LoginRequest;
import org.acme.dto.LoginResponse;
import org.acme.model.EmployeeCredential;
import org.acme.repository.EmployeeCredentialRepository;
import org.acme.repository.EmployeeRepository;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/** Verifies employee credentials and creates signed JWTs for successful logins. */
@ApplicationScoped
public class AuthenticationService {

    /** Creates the authentication service. */
    public AuthenticationService() {
    }

    @Inject
    EmployeeCredentialRepository credentialRepository;

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    @ConfigProperty(name = "security.jwt.secret")
    private String jwtSecret;

    /*
     * public AuthenticationService( String jwtSecret) {
     * this.jwtSecret = jwtSecret;
     * }
     */

    /**
     * Authenticates the supplied email and password.
     *
     * @param request login credentials
     * @return login details when an active employee has matching credentials; otherwise empty
     */
    public Optional<LoginResponse> authenticate(LoginRequest request) {
        var employee = employeeRepository.findActiveByEmail(request.email().toLowerCase(Locale.ROOT));
        if (employee == null) {
            return Optional.empty();
        }
        EmployeeCredential credential = credentialRepository.findById(employee.id);
        if (credential == null || !BcryptUtil.matches(request.password(), credential.passwordHash)) {
            return Optional.empty();
        }

        String role = employee.role.toLowerCase(Locale.ROOT);
        String seniority = employee.seniority.toLowerCase(Locale.ROOT);
        String token = Jwt.issuer("people-api")
                .expiresIn(Duration.ofMinutes(60))
                .subject(employee.id.toString())
                .upn(employee.email)
                .claim("id", employee.id.toString())
                .claim("name", employee.name)
                .groups(Set.of(role))
                .signWithSecret(jwtSecret);

        return Optional.of(new LoginResponse(token, employee.name, seniority));
    }

    /**
     * Hashes a password using BCrypt.
     *
     * @param password plaintext password
     * @return BCrypt password hash
     */
    public static String hashPassword(String password) {
        return BcryptUtil.bcryptHash(password, 12);
    }
}