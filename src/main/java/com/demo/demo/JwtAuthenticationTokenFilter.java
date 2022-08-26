package com.demo.demo;

import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 程啸轩
 * @date 2022/8/26 16:20
 */
@Log4j2
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    RedisService redisService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // 从请求头中获取token
        String authHeader = request.getHeader(this.tokenHeader);
        // 截取token
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            // token前面的"Bearer "需要截取
            String authToken = authHeader.substring(this.tokenHead.length());
            System.out.println("authToken:" + authToken);
            //验证token,获取token中的username
            Claims claims = JwtUtils.verifyJwt(authToken);
            if (claims == null) {
                throw new AppException("token异常，请重新登录");
            }
            String username = claims.get("username", String.class);
            // 校验该token是否过期
            log.info("username:{}", username);
            String redisToken = (String) redisService.get(username);
            if (redisToken == null) {
                throw new AppException("token已经过期，请重新登录");
            }
            log.info("token verification succeeded, checking username:{}", username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("authenticated user:{}", username);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

}
