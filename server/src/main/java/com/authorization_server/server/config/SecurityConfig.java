
package com.authorization_server.server.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Configuración del servidor de autorización OAuth2
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .oauth2AuthorizationServer(authorizationServer -> {
                http.securityMatcher(
                    authorizationServer.getEndpointsMatcher()
                );

                authorizationServer.oidc(Customizer.withDefaults());
            })

            .authorizeHttpRequests(authz ->
                authz.anyRequest().authenticated()
            )

            .exceptionHandling(exceptions ->
                exceptions.defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            );

        return http.build();
    }

    // 2. Configuración de seguridad general
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            @Value("${app.frontend-url:http://localhost:3000}") String frontendUrl)
            throws Exception {

        // Esta cadena no guarda sus propias peticiones (/, /favicon.ico, /error...).
        // Si lo hiciera, sobrescribiría la petición /oauth2/authorize guardada por la
        // cadena 1 y, tras el login, el usuario acabaría en una página Whitelabel
        // (status=999 o 404). Leer la petición guardada sigue funcionando.
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setRequestMatcher(request -> false);

        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .requestCache(cache -> cache.requestCache(requestCache))
            // Página de login propia: LoginController + templates/login.html
            .formLogin(login -> login
                .loginPage("/login")
                // Sin flujo OAuth en curso (login abierto directo en :9000),
                // regresar a la consola en vez de a "/" del server.
                .defaultSuccessUrl(frontendUrl + "/login", false)
                .permitAll()
            );

        return http.build();
    }

    // 3. Generación de claves RSA
    @Bean
    public KeyPair keyPair() {

        try {
            KeyPairGenerator keyPairGenerator =
                    KeyPairGenerator.getInstance("RSA");

            keyPairGenerator.initialize(2048);

            return keyPairGenerator.generateKeyPair();

        } catch (Exception ex) {
            throw new IllegalStateException(
                "Error al generar las claves RSA", ex
            );
        }
    }

    // 4. Configuración JWK para firmar los tokens
    @Bean
    public JWKSource<SecurityContext> jwkSource(KeyPair keyPair) {

        RSAPublicKey publicKey =
                (RSAPublicKey) keyPair.getPublic();

        RSAPrivateKey privateKey =
                (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);

        return new ImmutableJWKSet<>(jwkSet);
    }

    // 5. Decodificador JWT
    @Bean
    public JwtDecoder jwtDecoder(KeyPair keyPair) {

        return NimbusJwtDecoder
                .withPublicKey(
                    (RSAPublicKey) keyPair.getPublic()
                )
                .build();
    }

    // 6. Configuración del servidor de autorización
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {

        return AuthorizationServerSettings.builder()
                .build();
    }

    // 7. Registro del cliente OAuth2
    @Bean
    public RegisteredClientRepository registeredClientRepository() {

        RegisteredClient oidcClient =
                RegisteredClient.withId(UUID.randomUUID().toString())

                .clientId("client-app")
                .clientSecret("{noop}secret123")

                .clientAuthenticationMethod(
                    ClientAuthenticationMethod.CLIENT_SECRET_BASIC
                )

                .authorizationGrantType(
                    AuthorizationGrantType.AUTHORIZATION_CODE
                )

                .authorizationGrantType(
                    AuthorizationGrantType.REFRESH_TOKEN
                )

                .authorizationGrantType(
                    AuthorizationGrantType.CLIENT_CREDENTIALS
                )

                .redirectUri(
                    "http://127.0.0.1:8080/login/oauth2/code/client-app"
                )

                .redirectUri(
                    "https://oauth.pstmn.io/v1/callback"
                )

                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("read")
                .scope("write")

                .clientSettings(
                    ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .build()
                )

                .build();

        RegisteredClient frontendClient =
                RegisteredClient.withId(UUID.randomUUID().toString())

                .clientId("frontend-client")

                .clientAuthenticationMethod(
                    ClientAuthenticationMethod.NONE
                )

                .authorizationGrantType(
                    AuthorizationGrantType.AUTHORIZATION_CODE
                )

                .authorizationGrantType(
                    AuthorizationGrantType.REFRESH_TOKEN
                )

                .redirectUri(
                    "http://localhost:3000/callback"
                )

                .redirectUri(
                    "http://127.0.0.1:3000/callback"
                )

                // A dónde regresa el usuario tras /connect/logout
                .postLogoutRedirectUri(
                    "http://localhost:3000/login"
                )

                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("read")
                .scope("write")

                .clientSettings(
                    ClientSettings.builder()
                        // Cliente público (sin secreto): PKCE obligatorio
                        .requireProofKey(true)
                        .requireAuthorizationConsent(false)
                        .build()
                )

                .build();

        return new InMemoryRegisteredClientRepository(oidcClient, frontendClient);
    }

    // 8. Usuario de prueba
    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails user = User.withUsername("admin")
                .password("{noop}password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}