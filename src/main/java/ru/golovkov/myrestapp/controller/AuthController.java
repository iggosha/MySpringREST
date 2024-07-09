package ru.golovkov.myrestapp.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.exc.BadRequestException;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.security.JwtUtil;
import ru.golovkov.myrestapp.security.PersonDetails;
import ru.golovkov.myrestapp.service.PersonService;

@RestController
@RequestMapping("${app.base-url}")
@AllArgsConstructor
public class AuthController {

    private final PersonService personService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @SecurityRequirement(name = "Authorization")
    @GetMapping("/public/hello")
    public String getHello(Authentication authentication) {
        if (authentication != null) {
            PersonDetails principal = (PersonDetails) authentication.getPrincipal();
            return String.join(" ",
                    principal.getUsername(),
                    principal.getAuthorities().toString(),
                    authentication.getCredentials().toString()
            );
        }
        return "Hello";
    }

    @PostMapping("/public/login")
    public String postLogin(@RequestParam String username, @RequestParam String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Incorrect credentials");
        }
        return jwtUtil.generateToken(username);
    }

    @PostMapping("/public/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public String postPerson(@ParameterObject PersonRequestDto personRequestDto) {
        personService.create(personRequestDto);
        return jwtUtil.generateToken(personRequestDto.getName());
    }
}
