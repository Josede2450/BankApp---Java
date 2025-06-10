package bankapp.example.bankapp.entities;

import jakarta.persistence.*;
import java.util.Set;

/**
 * Entity representing user roles (e.g., Admin, Client).
 * Maps to the 'roles' table in the database.
 */
@Entity
@Table(name = "roles")
public class Roles {

    // ------------------------------------------------------------------------
    // 📌 Core Fields
    // ------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented primary key
    private Integer roleId;

    private String roleName;     // e.g., "Admin", "Client"
    private String description;  // Optional description of the role

    // ------------------------------------------------------------------------
    // 📌 Relationships
    // ------------------------------------------------------------------------

    /**
     * Many-to-many relationship with Users.
     * This is the inverse side of the join, controlled by the 'roles' field in Users.
     * 'mappedBy' prevents a second join table from being created.
     */
    @ManyToMany(mappedBy = "roles")
    private Set<Users> users;

    // ------------------------------------------------------------------------
    // ✅ Getters and Setters
    // ------------------------------------------------------------------------

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Users> getUsers() {
        return users;
    }

    public void setUsers(Set<Users> users) {
        this.users = users;
    }
}
