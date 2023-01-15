package com.sk.fresh.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * WebSecurityConfiguration. We have configured OAuth for the requests going out from the webclient.
 * However, do not configure for any incoming requests yet.
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
     @Override
     public void configure(WebSecurity web) {
          web.ignoring().antMatchers("/**");
     }
}