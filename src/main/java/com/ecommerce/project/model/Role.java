package com.ecommerce.project.model;

import com.ecommerce.project.model.Enums.AppRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer roleId;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
     private AppRole roleName;

    public Role(AppRole roleName) {
        this.roleName = roleName;
    }
}
