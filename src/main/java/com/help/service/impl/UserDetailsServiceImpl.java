package com.help.service.impl;

import com.help.model.person.Person;
import com.help.repository.person.PersonRepository;
import com.help.service.ServiceException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PersonRepository personRepository;

    public UserDetailsServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Person currentUser;
        currentUser = personRepository.findPersonByEmail(email).orElseThrow(
                () -> new ServiceException("User not found!")
        );

        return new User(email, currentUser.getPassword(),
                true, true, true, true,
                AuthorityUtils.createAuthorityList(currentUser.getRole().toString()));
    }
}
