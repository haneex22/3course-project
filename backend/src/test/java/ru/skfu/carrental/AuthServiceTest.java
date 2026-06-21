package ru.skfu.carrental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skfu.carrental.dto.request.LoginRequest;
import ru.skfu.carrental.dto.request.RegisterRequest;
import ru.skfu.carrental.dto.response.AuthResponse;
import ru.skfu.carrental.entity.ClientProfile;
import ru.skfu.carrental.entity.User;
import ru.skfu.carrental.entity.enums.UserRole;
import ru.skfu.carrental.exception.EmailAlreadyExistsException;
import ru.skfu.carrental.foundation.ClientProfileRepository;
import ru.skfu.carrental.foundation.UserRepository;
import ru.skfu.carrental.mediator.AuthServiceImpl;
import ru.skfu.carrental.security.JwtTokenProvider;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ClientProfileRepository clientProfileRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@test.com");
        testUser.setPasswordHash("encodedPassword");
        testUser.setRole(UserRole.CLIENT);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("new@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
    }

    @Test
    void login_success_returnsAuthResponse() {
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(testUser)).thenReturn("jwt.token.here");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt.token.here");
        assertThat(response.getEmail()).isEqualTo("test@test.com");
        assertThat(response.getRole()).isEqualTo("CLIENT");
        assertThat(response.getUserId()).isEqualTo(testUser.getId().toString());
    }

    @Test
    void login_invalidCredentials_throwsBadCredentialsException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void register_success_createsUserAndProfile() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });
        when(clientProfileRepository.save(any(ClientProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        authService.register(registerRequest);

        verify(userRepository).save(any(User.class));
        verify(clientProfileRepository).save(any(ClientProfile.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void register_passwordsDoNotMatch_throwsIllegalArgumentException() {
        registerRequest.setConfirmPassword("differentPassword");

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пароли не совпадают");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_emailAlreadyExists_throwsEmailAlreadyExistsException() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("уже зарегистрирован");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_newUserGetsClientRoleAndVerifiedProfile() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });
        when(clientProfileRepository.save(any(ClientProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        authService.register(registerRequest);

        verify(userRepository).save(argThat(user ->
                user.getRole() == UserRole.CLIENT &&
                user.getEmail().equals("new@test.com") &&
                user.getPasswordHash().equals("encodedPassword")
        ));
        verify(clientProfileRepository).save(argThat(profile ->
                profile.isVerified() &&
                profile.getUser() != null
        ));
    }
}
