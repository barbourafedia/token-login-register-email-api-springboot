package com.ecommerce.ecommerce.user;

import com.ecommerce.ecommerce.exception.UserAlreadyExistsException;
import com.ecommerce.ecommerce.registration.RegistrationRequest;
import com.ecommerce.ecommerce.registration.token.VerificationToken;
import com.ecommerce.ecommerce.registration.token.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * @author Sampson Alfred
 */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository tokenRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest request) {
       Optional<User> user = this.findByEmail(request.email());
       if (user.isPresent()){
           throw new UserAlreadyExistsException(
                   "User with email "+request.email() + " already exists");
       }
       var newUser = new User();
       newUser.setNom(request.nom());
       newUser.setEmail(request.email());
       newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setDatecreation(request.datecreation());
       newUser.setProfil(request.profil());
        return userRepository.save(newUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(User theUser, String token) {
        var verificationToken = new VerificationToken(token, theUser);
        tokenRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String theToken) {
        VerificationToken token = tokenRepository.findByToken(theToken);
        if(token == null){
            return "Invalid verification token";
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            tokenRepository.delete(token);
            return "Token already expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }
}
