package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "userName"),
        @UniqueConstraint(columnNames = "email")
})//make sure for unique constraints columname must be same as below
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Size(max = 20)
    private  String userName;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    //you have already added AllARgsconstructor annotation
    //if you wanna add manually your wish

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }




    //a role can have multiple user
    //admin can also be a seller etc
    //when user persisted with role then it get merged
    //this is how assign multple roles to users
    @Getter//getters and setter are needed for this
    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE},fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles=new HashSet<>();

    //Seller side of things
    //Users are sellers ,Users with products
    @OneToMany(mappedBy = "user"
    ,cascade = {CascadeType.PERSIST,CascadeType.MERGE},
    orphanRemoval = true)//orphanRemoval is when users are deleted the products are orphans,so user products created by them is are also removed
    private Set<Product> products;

    @ToString.Exclude
    @OneToOne(mappedBy = "user",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Cart cart;

    //Address with users
    //adresses of user/multiple addressed get merged with the ownwer of address i.e user
    @Getter
    @Setter
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name = "user_address",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "address_id"))//this cascade is like merging all addresses of a user who persists it
    private List<Address> addresses=new ArrayList<>();
}
