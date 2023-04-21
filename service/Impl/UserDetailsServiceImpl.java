package com.smartcontact.service;

import com.smartcontact.entities.User;
import com.smartcontact.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //fetching user from Database
        User user = userRepository.getUserByUserName(username);

        if(user==null){
            throw new UsernameNotFoundException("Could Not Found User !!");
        }
        CustomUserDetails customUserDetails= new CustomUserDetails(user);
        return customUserDetails;
    }
}
