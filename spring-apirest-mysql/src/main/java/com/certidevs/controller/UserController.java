package com.certidevs.controller;

import com.certidevs.dto.Login;
import com.certidevs.dto.Register;
import com.certidevs.dto.Token;
import com.certidevs.model.Role;
import com.certidevs.model.User;
import com.certidevs.repository.UserRepository;
import com.certidevs.security.SecurityUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@CrossOrigin("*") // Permitir acceso desde cualquier dominio desde el exterior
@RestController
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("users/register")
    public void register(@RequestBody Register register) {

        if (this.userRepository.existsByEmail(register.email())){
            throw new BadCredentialsException("Email ocupado. Elija otro email.");
        }

        User user = User.builder()
                .email(register.email())
                .password(passwordEncoder.encode(register.password()))
                .role(Role.USER)
                .build();

        this.userRepository.save(user);
    }

    @PostMapping("users/login")
    public Token login(@RequestBody Login login) {

        if (!this.userRepository.existsByEmail(login.email())) {
            throw new NoSuchElementException("User not found");
        }

        User user = this.userRepository.findByEmail(login.email()).orElseThrow();

        //if (!passwordEncoder.matches(login.password(), user.getPassword()))
        boolean correctPassword = passwordEncoder.matches(login.password(), user.getPassword());
        boolean incorrectPassword = !correctPassword;
        if (incorrectPassword){
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        // JWT Json Web Token: jwt.io
        // Generar token de acceso: eyJhbGciOiJIUzI1NiIsIn......
        // Generar el token: https://github.com/jwtk/jjwt?tab=readme-ov-file#creating-a-jwt
        Date issuedDate = new Date();
        long nextWeekMillis = TimeUnit.DAYS.toMillis(7);
        Date expirationDate = new Date(issuedDate.getTime() + nextWeekMillis);
        byte[] key = Base64.getDecoder().decode("FZD5maIaX04mYCwsgckoBh1NJp6T3t62h2MVyEtdo3w="); // Clave secreta

        String token = Jwts.builder()
                // id del usuario
                .subject(String.valueOf(user.getId()))
                // La clave secreta para firmar el token y saber que es nuestro cuando lleguen las peticiones del frontend
                .signWith(Keys.hmacShaKeyFor(key))
                // Fecha emisión del token
                .issuedAt(issuedDate)
                // Fecha de expiración del token
                .expiration(expirationDate)
                // información personalizada: rol, username, email, avatar...
                .claim("role", user.getRole())
                .claim("email", user.getEmail())
                //.claim("avatar", user.getAvatarUrl())
                // Construye el token
                .compact();

        return new Token(token);
    }

    // Get account
    @GetMapping("users/account")
    public User getCurrentUser() {
        return SecurityUtils.getCurrentUser().orElseThrow();
    }

    // Put account
    @PutMapping("users/account")
    public User update(@RequestBody User user) {
        // Si está autenticado, y el usuario autenticado es ADMIN o es el mismo usuario que la variable user
        // entonces actualizar, en caso contrario no actualizamos
        SecurityUtils.getCurrentUser().ifPresent(currentUser -> {
            if (currentUser.getRole() == Role.ADMIN || Objects.equals(currentUser.getId(), user.getId())) {
                this.userRepository.save(user);
            } else {
                throw new RuntimeException("No puede actualizar"); // Reemplazar por Excepción personalizada
            }
        });

        return user;
    }


}