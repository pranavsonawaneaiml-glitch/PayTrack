package com.paytrack.backend.dto;

import com.paytrack.backend.model.Role;

public class AuthenticationResponse {
    private String token;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;

    public AuthenticationResponse(String token, String firstName, String lastName, String email, Role role) {
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
