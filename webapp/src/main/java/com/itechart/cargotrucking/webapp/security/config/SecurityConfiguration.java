package com.itechart.cargotrucking.webapp.security.config;

import com.itechart.cargotrucking.webapp.security.Role;
import com.itechart.cargotrucking.webapp.security.jwt.JwtFilterConfigurer;
import com.itechart.cargotrucking.webapp.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private JwtProvider jwtProvider;
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfiguration(JwtProvider jwtProvider, UserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.headers().frameOptions().sameOrigin();

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/css/**").permitAll()
                .antMatchers(HttpMethod.GET, "/js/**").permitAll()
                .antMatchers(HttpMethod.GET, "/favicon.ico").permitAll()

                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers("/api/web-socket/**").permitAll()

                .antMatchers(HttpMethod.GET, "/api/users*").hasAnyAuthority(Role.ADMIN.name(), Role.DISPATCHER.name())
                .antMatchers(HttpMethod.GET, "/api/users/*").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/api/users").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.POST, "/api/users").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/api/users/*").hasAuthority(Role.ADMIN.name())

                .antMatchers(HttpMethod.GET, "/api/clients*").hasAuthority(Role.SYS_ADMIN.name())
                .antMatchers(HttpMethod.GET,"/api/clients/*").hasAuthority(Role.SYS_ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/api/clients").hasAuthority(Role.SYS_ADMIN.name())
                .antMatchers(HttpMethod.POST, "/api/clients").hasAuthority(Role.SYS_ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/api/clients/*").hasAuthority(Role.SYS_ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/api/clients/activate*").hasAuthority(Role.SYS_ADMIN.name())

                .antMatchers(HttpMethod.GET, "/api/storages*").hasAnyAuthority(Role.ADMIN.name(), Role.DISPATCHER.name())
                .antMatchers(HttpMethod.GET, "/api/storages/*").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/api/storages").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.POST, "/api/storages").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/api/storages/*").hasAuthority(Role.ADMIN.name())

                .antMatchers(HttpMethod.GET, "/api/product-owners*").hasAnyAuthority(Role.ADMIN.name(), Role.DISPATCHER.name())
                .antMatchers(HttpMethod.GET, "/api/product-owners/*").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/api/product-owners").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.POST, "/api/product-owners").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/api/product-owners/*").hasAuthority(Role.ADMIN.name())

                .antMatchers(HttpMethod.GET, "/api/product-writeoffs*").hasAnyAuthority(Role.MANAGER.name(), Role.COMPANY_OWNER.name())
                .antMatchers(HttpMethod.DELETE, "/api/product-writeoffs").hasAuthority(Role.MANAGER.name())
                .antMatchers(HttpMethod.POST, "/api/product-writeoffs").hasAuthority(Role.MANAGER.name())
                .antMatchers(HttpMethod.PUT, "/api/product-writeoffs/*").hasAuthority(Role.MANAGER.name())

                .antMatchers(HttpMethod.GET, "/api/cars*").hasAnyAuthority(Role.ADMIN.name(), Role.MANAGER.name())
                .antMatchers(HttpMethod.GET, "/api/cars/*").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/api/cars").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.POST, "/api/cars").hasAuthority(Role.ADMIN.name())
                .antMatchers(HttpMethod.PUT, "/api/cars/*").hasAuthority(Role.ADMIN.name())

                .antMatchers(HttpMethod.GET, "/api/invoices*").hasAnyAuthority(Role.DISPATCHER.name(), Role.MANAGER.name(), Role.COMPANY_OWNER.name())
                .antMatchers(HttpMethod.GET, "/api/invoices/*").hasAnyAuthority(Role.DISPATCHER.name(), Role.MANAGER.name(), Role.COMPANY_OWNER.name())
                .antMatchers(HttpMethod.DELETE, "/api/invoices").hasAnyAuthority(Role.DISPATCHER.name(), Role.MANAGER.name())
                .antMatchers(HttpMethod.POST, "/api/invoices").hasAuthority(Role.DISPATCHER.name())
                .antMatchers(HttpMethod.PUT, "/api/invoices/*").hasAuthority(Role.DISPATCHER.name())

                .antMatchers(HttpMethod.POST,"/api/email").hasAuthority(Role.ADMIN.name())

                .antMatchers(HttpMethod.GET, "/api/waybills*").hasAnyAuthority(Role.MANAGER.name(), Role.DRIVER.name(), Role.COMPANY_OWNER.name())
                .antMatchers(HttpMethod.GET, "/api/waybills/*").hasAnyAuthority(Role.DRIVER.name(), Role.COMPANY_OWNER.name(), Role.MANAGER.name())
                .antMatchers(HttpMethod.PUT, "/api/waybills/*").hasAuthority(Role.DRIVER.name())
                .antMatchers(HttpMethod.POST, "/api/waybills").hasAuthority(Role.MANAGER.name())
                .antMatchers(HttpMethod.DELETE, "/api/waybills").hasAuthority(Role.MANAGER.name())

                .antMatchers(HttpMethod.GET, "/api/about").permitAll()

                .antMatchers(HttpMethod.POST, "/api/email/repairing").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/restore/*").permitAll()

                .antMatchers(HttpMethod.POST, "/api/sign-in").permitAll()
                .antMatchers(HttpMethod.POST, "/api/logout").permitAll()
                .antMatchers(HttpMethod.POST, "/api/refresh").permitAll()

                .antMatchers(HttpMethod.GET, "/api/profile").hasAnyAuthority(Role.COMPANY_OWNER.name(), Role.DISPATCHER.name(), Role.MANAGER.name(), Role.DRIVER.name())
                .antMatchers(HttpMethod.PUT, "/api/profile").hasAnyAuthority(Role.COMPANY_OWNER.name(), Role.DISPATCHER.name(), Role.MANAGER.name(), Role.DRIVER.name())
                .antMatchers(HttpMethod.PUT, "/api/profile/change-password").hasAnyAuthority(Role.COMPANY_OWNER.name(), Role.DISPATCHER.name(), Role.MANAGER.name(), Role.DRIVER.name())
                .antMatchers(HttpMethod.PUT, "/api/profile/change-email").hasAnyAuthority(Role.COMPANY_OWNER.name(), Role.DISPATCHER.name(), Role.MANAGER.name(), Role.DRIVER.name())
                .antMatchers(HttpMethod.GET, "/api/profile/confirm-change-email/*").hasAnyAuthority(Role.COMPANY_OWNER.name(), Role.DISPATCHER.name(), Role.MANAGER.name(), Role.DRIVER.name())

                .antMatchers(HttpMethod.POST, "/api/template").hasAuthority(Role.ADMIN.name())

                .antMatchers(HttpMethod.GET, "/api/reports/sysadmin").hasAuthority(Role.SYS_ADMIN.name())
                .antMatchers(HttpMethod.GET, "/api/reports/client").hasAuthority(Role.COMPANY_OWNER.name())

                .anyRequest().authenticated();

        http.apply(new JwtFilterConfigurer(jwtProvider));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
