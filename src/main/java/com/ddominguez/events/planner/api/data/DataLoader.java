package com.ddominguez.events.planner.api.data;

import com.ddominguez.events.planner.api.domain.Role;
import com.ddominguez.events.planner.api.domain.User;
import com.ddominguez.events.planner.api.repository.RoleRepository;
import com.ddominguez.events.planner.api.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    Role adminRole =
        roleRepository
            .findByName("ROLE_ADMIN")
            .orElseGet(
                () -> {
                  Role newRole = new Role();
                  newRole.setName("ROLE_ADMIN");
                  return roleRepository.save(newRole);
                });

    Role userRole =
        roleRepository
            .findByName("ROLE_USER")
            .orElseGet(
                () -> {
                  Role newRole = new Role();
                  newRole.setName("ROLE_USER");
                  return roleRepository.save(newRole);
                });

    if (userRepository.findByUsername("admin").isEmpty()) {
      User admin = new User();
      admin.setName("Administrador");
      admin.setUsername("admin");
      admin.setEmail("admin@events.plan");
      admin.setPassword(passwordEncoder.encode("1Qwerty"));

      Set<Role> adminRoles = new HashSet<>();
      adminRoles.add(adminRole);
      adminRoles.add(userRole);
      admin.setRoles(adminRoles);

      userRepository.save(admin);
      System.out.println("User admin created successfully");
    }

    if (userRepository.findByUsername("user").isEmpty()) {
      User user = new User();
      user.setName("Test user");
      user.setUsername("user");
      user.setEmail("user@events.plan");
      user.setPassword(passwordEncoder.encode("1Qwerty"));

      Set<Role> userRoles = new HashSet<>();
      userRoles.add(userRole);
      user.setRoles(userRoles);

      userRepository.save(user);
      System.out.println("Test User created successfully");
    }
  }
}
