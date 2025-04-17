package com.MiniLms.LMSBackend.service.securityService;

import com.MiniLms.LMSBackend.model.EmployeeModel;
import com.MiniLms.LMSBackend.model.Role;
import com.MiniLms.LMSBackend.model.StudentModel;
import com.MiniLms.LMSBackend.model.UserType;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
public class UserPrincipal implements UserDetails {
    private final String id;
    private final String name;
    private final String email;
    private final String password;
    private final Role role;
    private final UserType userType;

    public UserPrincipal(StudentModel studentModel){
        this.id = studentModel.getId();
        this.name = studentModel.getName();
        this.email = studentModel.getEmail();
        this.password = studentModel.getPassword();
        this.role = studentModel.getRole();
        this.userType = UserType.STUDENT;
    }
    public UserPrincipal(EmployeeModel employeeModel){
        this.id = employeeModel.getId();
        this.name = employeeModel.getName();
        this.email = employeeModel.getEmail();
        this.password = employeeModel.getPassword();
        this.role = employeeModel.getRole();
        this.userType = UserType.EMPLOYEE;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
