// Interface defining the contract for managing user roles
package bankapp.example.bankapp.service;

// ===== Import entity class =====
import bankapp.example.bankapp.entities.Roles;

/**
 * Service interface for handling logic related to user roles (e.g., Admin, User).
 */
public interface RoleService {

    /**
     * Finds a Role entity based on its role name (e.g., "Admin", "User").
     * Useful when assigning roles to users during registration or role-based access control.
     *
     * @param roleName the name of the role to find
     * @return the Roles entity if found; otherwise, null or exception (based on implementation)
     */
    Roles findByRoleName(String roleName);
}
