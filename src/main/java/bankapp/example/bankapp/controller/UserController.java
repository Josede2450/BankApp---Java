// Package declaration
package bankapp.example.bankapp.controller;

// Importing necessary classes and services
import bankapp.example.bankapp.entities.*;
import bankapp.example.bankapp.repository.RoleRepository;
import bankapp.example.bankapp.repository.VerificationTokenRepository;
import bankapp.example.bankapp.service.EmailService;
import bankapp.example.bankapp.service.RoleService;
import bankapp.example.bankapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// Marks this class as a Spring MVC controller
@Controller
@RequestMapping("/users") // Base URL for all user-related endpoints
public class UserController {

    // Automatically injects required service and repository beans
    @Autowired private UserService userService;
    @Autowired private RoleRepository roleRepository;
    @Autowired private RoleService roleService;
    @Autowired private VerificationTokenRepository tokenRepository;
    @Autowired private EmailService emailService;
    @Autowired private PasswordEncoder passwordEncoder;

    // ============================ USER LIST (Admin View) ============================
    @GetMapping
    public String getAllUsers(Model model) {
        List<Users> users = userService.getAllUsers(); // Fetch all users
        model.addAttribute("users", users); // Pass user list to the view
        return "user-list"; // Return the view name
    }

    // ======================== CREATE ACCOUNT FORM (Token Flow) ======================
    @GetMapping("/create")
    public String showCreateForm(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {
        if (token == null || token.isBlank()) { // Validate token
            redirectAttributes.addFlashAttribute("error", "Token is required.");
            return "redirect:/login";
        }

        VerificationToken verificationToken = tokenRepository.findByToken(token); // Retrieve token
        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
            return "redirect:/login";
        }

        Users user = new Users(); // Create empty user
        user.setEmail(verificationToken.getUser().getEmail()); // Pre-fill email from token
        user.setAddress(new Address()); // Prepare empty address

        model.addAttribute("user", user);
        model.addAttribute("token", token);
        return "create-user"; // Show account creation form
    }

    // Handles the POST submission of the create user form
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") Users user,
                           @RequestParam(value = "token", required = false) String token,
                           RedirectAttributes redirectAttributes) {

        if (token != null) {
            VerificationToken verificationToken = tokenRepository.findByToken(token); // Find token
            if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
                return "redirect:/login";
            }

            Users existingUser = verificationToken.getUser(); // Get associated user
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setGender(user.getGender());
            existingUser.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
            existingUser.setEnabled(true); // Activate user
            existingUser.setEmail(existingUser.getEmail().toLowerCase());

            // Attach address to user
            if (user.getAddress() != null) {
                user.getAddress().setUser(existingUser);
                existingUser.setAddress(user.getAddress());
            }

            // Set default role
            existingUser.setRoles(Set.of(roleRepository.findByRoleName("Client")));
            userService.saveUser(existingUser); // Save user
            tokenRepository.delete(verificationToken); // Remove used token

            redirectAttributes.addFlashAttribute("success", "Account created successfully.");
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute("error", "Missing or invalid token.");
        return "redirect:/login";
    }

    // ========================== EDIT USER (Admin) ==============================
    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Integer userId, Model model, RedirectAttributes redirectAttributes) {
        Users user = userService.findById(userId); // Find user by ID
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/management/dashboard";
        }

        model.addAttribute("user", user);
        model.addAttribute("address", user.getAddress());
        model.addAttribute("availableRoles", roleRepository.findAll());
        return "edit-user";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") Integer userId,
                             @ModelAttribute("user") Users updatedUser,
                             @RequestParam(value = "roles", required = false) List<Integer> roleIds,
                             RedirectAttributes redirectAttributes) {

        Users existingUser = userService.findById(userId);
        if (existingUser == null) {
            redirectAttributes.addFlashAttribute("error", "User not found.");
            return "redirect:/management/dashboard";
        }

        // Update basic user info
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setEnabled(updatedUser.isEnabled());

        // Update address if available
        if (existingUser.getAddress() != null && updatedUser.getAddress() != null) {
            Address updatedAddress = updatedUser.getAddress();
            Address address = existingUser.getAddress();

            address.setStreet(updatedAddress.getStreet());
            address.setState(updatedAddress.getState());
            address.setZipCode(updatedAddress.getZipCode());
            address.setCountry(updatedAddress.getCountry());
            address.setAddressType(updatedAddress.getAddressType());
        }

        // Update user roles if provided
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Roles> selectedRoles = roleIds.stream()
                    .map(id -> roleRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            existingUser.setRoles(selectedRoles);
        }

        userService.saveUser(existingUser); // Save updated user
        redirectAttributes.addFlashAttribute("success", "User updated successfully.");
        return "redirect:/management/dashboard";
    }

    // ========================== DELETE USER ==============================
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer userId, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(userId); // Delete user by ID
        redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        return "redirect:/management/dashboard";
    }

    // ========================== ACCOUNT REQUEST FORM (Guest) ==============================
    @GetMapping("/request-account")
    public String showRequestAccountForm() {
        return "request-account"; // Return view for account request
    }

    @PostMapping("/request-account")
    public String handleAccountRequest(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        email = email.trim().toLowerCase(); // Normalize email
        Optional<Users> optionalUser = userService.findOptionalByEmail(email); // Find by email

        if (optionalUser.isPresent()) {
            Users existingUser = optionalUser.get();
            if (existingUser.isEnabled()) {
                redirectAttributes.addFlashAttribute("error", "An account with this email already exists.");
                return "redirect:/login";
            }

            // Resend verification token
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = tokenRepository.findByUser(existingUser);
            if (verificationToken != null) {
                verificationToken.setToken(token);
                verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
            } else {
                verificationToken = new VerificationToken(token, existingUser, LocalDateTime.now().plusHours(24));
            }
            tokenRepository.save(verificationToken);

            // Send email with account setup link
            emailService.sendEmail(email, "Set up your BankApp account",
                    "Hi,\n\nPlease click the link below to create your password:\n" +
                            "http://localhost:8080/users/create?token=" + token);

            redirectAttributes.addFlashAttribute("message", "Setup link re-sent to your email.");
            return "redirect:/login";
        }

        // Create new disabled user if not found
        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setEnabled(false);
        newUser.setRoles(Set.of(roleRepository.findByRoleName("Client")));
        userService.saveUser(newUser);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, newUser, LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        emailService.sendEmail(email, "Set up your BankApp account",
                "Hi,\n\nPlease click the link below to create your password:\n" +
                        "http://localhost:8080/users/create?token=" + token);

        redirectAttributes.addFlashAttribute("message", "Setup link sent to your email.");
        return "redirect:/login";
    }

    // ========================== PASSWORD RESET FLOW ==============================
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password"; // Return password reset request page
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        email = email.trim().toLowerCase();
        Users user = userService.findByEmail(email);

        if (user == null || !user.isEnabled()) {
            redirectAttributes.addFlashAttribute("error", "No active account found with that email.");
            return "redirect:/users/forgot-password";
        }

        // Generate or update reset token
        String token = UUID.randomUUID().toString();
        VerificationToken resetToken = tokenRepository.findByUser(user);
        if (resetToken != null) {
            resetToken.setToken(token);
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(2));
        } else {
            resetToken = new VerificationToken(token, user, LocalDateTime.now().plusHours(2));
        }
        tokenRepository.save(resetToken);

        // Send reset email
        emailService.sendEmail(email, "Password Reset Request",
                "Hi,\n\nClick the link below to reset your password:\n" +
                        "http://localhost:8080/users/reset-password?token=" + token);

        redirectAttributes.addFlashAttribute("message", "Reset link sent to your email.");
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
            return "redirect:/login";
        }

        model.addAttribute("token", token);
        return "reset-password"; // Show password reset form
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token,
                                      @RequestParam("password") String password,
                                      RedirectAttributes redirectAttributes) {

        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token.");
            return "redirect:/login";
        }

        Users user = verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(password)); // Encode and set new password
        userService.saveUser(user); // Save updated user
        tokenRepository.delete(verificationToken); // Remove used token

        redirectAttributes.addFlashAttribute("message", "Password reset successful. You can now log in.");
        return "redirect:/login";
    }
}
