package com.dominikdev.ecommerceshop.auth;

import com.dominikdev.ecommerceshop.email.EmailService;
import com.dominikdev.ecommerceshop.email.EmailTemplateName;
import com.dominikdev.ecommerceshop.role.Role;
import com.dominikdev.ecommerceshop.role.RoleRepository;
import com.dominikdev.ecommerceshop.security.JwtService;
import com.dominikdev.ecommerceshop.security.MessageResponse;
import com.dominikdev.ecommerceshop.user.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Value("${application.mailing.frontend.restart-password-url}")
    private String resetPasswordUrl;

    public ResponseEntity<MessageResponse> register(RegistrationRequest request) throws MessagingException {

        boolean existingEmail = userRepository.existsByEmail(request.getEmail());
        if (existingEmail) {
            throw new RuntimeException("This email is already taken!");
        }

        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
        return null;
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token
                .builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws MessagingException {
        var checkUser = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));
        if (!checkUser.isEnabled()) {
            sendValidationEmail(checkUser);
            throw new RuntimeException("Account is disabled. A new activation email has been sent.");
        }

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();

        var user = ((User) auth.getPrincipal());
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        claims.put("id", user.getId());
        claims.put("fullName", user.getFullName());
        claims.put("email", user.getEmail());
        claims.put("roleName", roleNames);
        claims.put("createdDate", user.getCreatedDate() != null ? user.getCreatedDate().toString() : null);
        claims.put("dateOfBirth", user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);
        claims.put("accountLocked", user.isAccountLocked());
        claims.put("enabled", user.isEnabled());

        var jwtToken = jwtService.generateToken(claims, user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(UserResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .roleNames(roleNames)
                        .createdDate(user.getCreatedDate())
                        .dateOfBirth(user.getDateOfBirth())
                        .accountLocked(user.isAccountLocked())
                        .enabled(user.isEnabled())
                        .build()
                ).build();
    }

    @Transactional
    public AuthenticationResponse activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent to the same address email.");
        }
        var user = userRepository.findById(savedToken.getUser()
                        .getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public ResponseEntity<Map<String, String>> forgotPassword(ForgotPasswordRequest request) throws MessagingException {
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User with this email not found"));

        String token = generateAndSaveResetToken(user);
        sendResetPasswordEmail(user, token);

        return ResponseEntity.ok(Map.of(
                "message", "Reset password link has been sent to your email",
                "token", token
        ));
    }

    private String generateAndSaveResetToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);
        return generatedToken;
    }

    private void sendResetPasswordEmail(User user, String token) throws MessagingException {

        String urlWithToken = resetPasswordUrl + "/" + token;

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.RESET_PASSWORD,
                urlWithToken,
                token,
                "Reset your password"
        );
    }

    private void sendPasswordChangedConfirmation(User user) throws MessagingException {

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.PASSWORD_CHANGED,
                null,
                null,
                "Your Password Has Been Changed"
        );

    }

    public ResponseEntity<String> resetPassword(ResetPasswordRequest request) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(request.getToken()).orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            throw new RuntimeException("Token has expired");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        var user = userRepository.findById(savedToken.getUser().getId()).orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        sendPasswordChangedConfirmation(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);

        return ResponseEntity.ok("Password has been reset successfully");
    }
}
