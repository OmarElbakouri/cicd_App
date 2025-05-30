package com.cicd.demo.service;

import com.cicd.demo.model.User;
import com.cicd.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User processOAuthPostLogin(OAuth2User oAuth2User) {
        String githubId = oAuth2User.getAttribute("id").toString();
        
        Optional<User> existingUser = userRepository.findByGithubId(githubId);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            // Récupérer les attributs de l'utilisateur GitHub
            String login = oAuth2User.getAttribute("login");
            String name = oAuth2User.getAttribute("name");
            
            // Si le nom est null, utiliser le login comme nom
            if (name == null) {
                name = login;
            }
            
            // Créer un nouvel utilisateur
            User newUser = User.builder()
                    .githubId(githubId)
                    .name(name)
                    .email(oAuth2User.getAttribute("email"))
                    .login(login)
                    .avatarUrl(oAuth2User.getAttribute("avatar_url"))
                    .build();
            
            return userRepository.save(newUser);
        }
    }

    @Override
    public Optional<User> findByGithubId(String githubId) {
        return userRepository.findByGithubId(githubId);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }
}
