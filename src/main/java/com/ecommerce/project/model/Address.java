package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "addresses")
public class Address {

    //one user can have many address

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min=5,message = "Street name must be atleast 5 characters")
    private String street;

    @NotBlank
    @Size(min=5,message = "Building name must be atleast 5 characters")
    private String buildingName;

    @NotBlank
    @Size(min=3,message = "City name must be atleast 3 characters")
    private String city;

    @NotBlank
    @Size(min=2,message = "State name must be atleast 2 characters")
    private String State;

    @NotBlank
    @Size(min=2,message = "Country name must be atleast 2 characters")
    private String country;

    @NotBlank
    @Size(min=6,message = "Pincode name must be atleast 6 characters")
    private String pincode;

    //link to users
    @ToString.Exclude//means user dont get displayed here but will be displayed on UserSide
    @ManyToMany(mappedBy = "addresses")
    private List<User> users=new ArrayList<>();


    public Address(String street, String buildingName, String city, String state, String country, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        State = state;
        this.country = country;
        this.pincode = pincode;
    }
}
