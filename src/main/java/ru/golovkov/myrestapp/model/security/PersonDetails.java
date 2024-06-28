package ru.golovkov.myrestapp.model.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.golovkov.myrestapp.model.entity.Person;

import java.util.Collection;
import java.util.Set;

@AllArgsConstructor
public class PersonDetails implements UserDetails {

    private final transient Person person;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority(person.getRole()));
    }

    @Override
    public String getPassword() {
        return person.getPassword();
    }

    @Override
    public String getUsername() {
        return person.getName();
    }
}
