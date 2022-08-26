package com.demo.service.Impl;

import com.demo.service.IUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author 程啸轩
 * @date 2022/8/26 16:24
 */
@Service
@Log4j2
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RedisService redisService;
    @Autowired
    UserDetailsService userDetailsService;

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<Resource> getUserPermission(Long userId) {
        List<Roles> roles = baseMapper.getUserRoles(userId);
        List<Resource> results = new ArrayList<>();
        for (Roles role : roles) {
            List<Resource> resources = baseMapper.getUserResources(role.getId());
            results.addAll(resources);
        }
        return results;
    }
    @Override
    public boolean registry(UserDTO userDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userDTO.getUsername());
        User userInfo = baseMapper.selectOne(wrapper);
        if (userInfo != null) {
            throw new AppException("用户名已存在");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        return baseMapper.insert(user) > 0;
    }

    @Override
    public String login(String username, String password) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new
                    UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = JwtUtils.generateToken(userDetails.getUsername());
            redisService.set(username, token, JwtUtils.getExpiration());
            return token;
        } catch (AuthenticationException e) {
            log.error("登录失败：{}", e.getMessage());
            throw new AppException("登录失败");
        }
    }
}



