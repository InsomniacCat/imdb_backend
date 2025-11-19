package com.imdb.backend.config; // 配置类所在包

// 导入必要的Spring Security相关类
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration // 标识为Spring配置类
@EnableWebSecurity // 启用Web安全功能
public class SecurityConfig {

    // 配置安全过滤链，定义HTTP安全策略
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 配置CORS
                .csrf(csrf -> csrf.disable()) // 禁用CSRF保护
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 设置无状态会话
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll() // 允许所有/api路径的访问
                        .anyRequest().authenticated()); // 其他请求需要认证

        return http.build(); // 构建并返回配置
    }

    // 配置CORS源，定义跨域请求策略
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // 允许所有来源（使用pattern方式）
        configuration.addAllowedMethod("*"); // 允许所有HTTP方法
        configuration.addAllowedHeader("*"); // 允许所有请求头
        configuration.setAllowCredentials(true); // 允许携带凭证

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // 对/api路径应用CORS配置
        return source;
    }
}