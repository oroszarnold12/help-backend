package com.bbte.styoudent.service.impl;

import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.repository.PersonRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PersonRepository personRepository;

    public UserDetailsServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Person currentUser;
        currentUser = personRepository.findPersonByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found!")
        );

        return new User(email, currentUser.getPassword(),
                true, true, true, true,
                AuthorityUtils.createAuthorityList(currentUser.getRole().toString()));
    }
}
