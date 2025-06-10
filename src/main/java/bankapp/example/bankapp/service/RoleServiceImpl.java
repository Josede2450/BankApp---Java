// Implementation of the RoleService interface to manage user roles
package bankapp.example.bankapp.service;

// ===== Import project classes =====
import bankapp.example.bankapp.entities.Roles;               // Entity representing user roles
import bankapp.example.bankapp.repository.RoleRepository;    // Repository for accessing role data

import org.springframework.beans.factory.annotation.Autowired; // For dependency injection
import org.springframework.stereotype.Service;                // Marks this class as a Spring service

// Marks this class as a Spring-managed service bean
@Service
public class RoleServiceImpl implements RoleService {

    // Injects the RoleRepository to access roles from the database
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Finds a role entity by its name (e.g., "User", "Admin").
     * This method is typically used during user registration or access checks.
     *
     * @param roleName the name of the role to find
     * @return the Roles entity, or null if not found
     */
    @Override
    public Roles findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
