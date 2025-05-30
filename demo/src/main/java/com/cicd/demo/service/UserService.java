package com.cicd.demo.service;

import com.cicd.demo.model.User;
import java.util.Optional;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface UserService {
    User processOAuthPostLogin(OAuth2User oAuth2User);
    Optional<User> findByGithubId(String githubId);
    Optional<User> findByLogin(String login);
}
