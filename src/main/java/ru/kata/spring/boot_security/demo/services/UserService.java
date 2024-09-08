package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.exceptionHandling.NoSuchUserException;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    public boolean addUser(User user) {
        if (userRepository.findByEmail(user.getUsername()) != null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NoSuchUserException("There is no user with ID = " + id + " in Database");
        }
        userRepository.deleteById(id);
        return true;
    }

    public boolean changeUser(Long id, User user) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NoSuchUserException("There is no user with ID = " + id + " in Database");
        }
        if (!user.getPassword().isEmpty()) {
            userRepository.findById(id).get().setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.findById(id).get().setFirstName(user.getFirstName());
        userRepository.findById(id).get().setLastName(user.getLastName());
        userRepository.findById(id).get().setAge(user.getAge());
        userRepository.findById(id).get().setEmail(user.getEmail());
        userRepository.findById(id).get().setRoles(user.getRoles());
        return true;
    }

    public User findById(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NoSuchUserException("There is no user with ID = " + id + " in Database");
        }
        return userRepository.findById(id).get();
    }
}
