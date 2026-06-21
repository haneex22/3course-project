package ru.skfu.carrental.mediator;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.carrental.dto.request.LoginRequest;
import ru.skfu.carrental.dto.request.RegisterRequest;
import ru.skfu.carrental.dto.response.AuthResponse;
import ru.skfu.carrental.entity.ClientProfile;
import ru.skfu.carrental.entity.User;
import ru.skfu.carrental.entity.enums.UserRole;
import ru.skfu.carrental.exception.EmailAlreadyExistsException;
import ru.skfu.carrental.foundation.ClientProfileRepository;
import ru.skfu.carrental.foundation.UserRepository;
import ru.skfu.carrental.security.JwtTokenProvider;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                            ClientProfileRepository clientProfileRepository,
                            PasswordEncoder passwordEncoder,
                            JwtTokenProvider jwtTokenProvider,
                            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = (User) auth.getPrincipal();
        String token = jwtTokenProvider.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), user.getId().toString());
    }

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Пользователь с таким email уже зарегистрирован");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CLIENT);
        userRepository.save(user);

        ClientProfile profile = new ClientProfile();
        profile.setUser(user);
        profile.setVerified(true);
        clientProfileRepository.save(profile);
    }

}
