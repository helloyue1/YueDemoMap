package com.elderly.apartment.service.impl;

import com.elderly.apartment.entity.User;
import com.elderly.apartment.security.CustomUserDetails;
import com.elderly.apartment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        user.setRoles(userService.findRolesByUserId(user.getId()));

        // 如果用户是老人类型(user_type=2)，设置elderlyId为自己的id
        if (user.isElderly()) {
            user.setElderlyId(user.getId());
        }

        return new CustomUserDetails(user);
    }
}
