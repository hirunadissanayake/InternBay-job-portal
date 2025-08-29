package lk.ijse.gdse.main.internbaybackend.service.impl;

import lk.ijse.gdse.main.internbaybackend.dto.UserDTO;
import lk.ijse.gdse.main.internbaybackend.entity.User;
import lk.ijse.gdse.main.internbaybackend.repository.UserRepository;
import lk.ijse.gdse.main.internbaybackend.service.UserService;
import lk.ijse.gdse.main.internbaybackend.util.StatusList;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepo, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public int saveUser(UserDTO userDTO) {
        if (userRepo.existsByEmail(userDTO.getEmail())) {
            return StatusList.Not_Acceptable;
        } else {
            userDTO.setPasswordHash(passwordEncoder.encode(userDTO.getPasswordHash()));
            userRepo.save(modelMapper.map(userDTO, User.class));
            return StatusList.Created;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),   // ✅ FIXED
                getAuthority(user)
        );
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        // Prefix role with ROLE_ (Spring Security convention)
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase()));
        return authorities;
    }

    @Override
    public UserDTO loadUserDetailsByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public boolean ifEmailExists(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public UserDTO searchUser(String email) {
        User user = userRepo.findByEmail(email);
        return (user != null) ? modelMapper.map(user, UserDTO.class) : null;
    }

    @Override
    public int resetPass(UserDTO exuser) {
        exuser.setPasswordHash(passwordEncoder.encode(exuser.getPasswordHash()));
        userRepo.save(modelMapper.map(exuser, User.class));
        return StatusList.Created;
    }

    @Override
    public String getUserRoleByEmail(String email) {
        User user = userRepo.findByEmail(email);
        return (user != null) ? user.getRole().name() : null;
    }

    @Override
    public int loginUser(UserDTO userDTO) {
        Optional<User> userOpt = Optional.ofNullable(userRepo.findByEmail(userDTO.getEmail()));

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(userDTO.getPasswordHash(), user.getPasswordHash())) {
                return StatusList.Created; // login success
            } else {
                return StatusList.Unauthorized; // wrong password
            }
        }
        return StatusList.Not_Acceptable; // email not found
    }
}
