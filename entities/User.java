package com.smartcontact.entities;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="USER")
public class User {
        @Id
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        private int id;
        @NotBlank(message = "Name field is required !!")
        @Size(min = 3,max = 15,message = "min 3 & max 15 characters are allowed")
        private String name;
        @NotBlank(message = "Email field is required !!")
        @Column(unique=true)
        private String email;
        @NotBlank(message = "Password field is required !!")
        private String password;
        private String role;
        private boolean enabled;
        private String imageUrl;
        @Column(length=500)
        private String about;

        @OneToMany(cascade=CascadeType.ALL,orphanRemoval = true,fetch =FetchType.LAZY, mappedBy="user")
        private List<Contact> contacts = new ArrayList<>();

        public List<Contact> getContacts() {
            return contacts;
        }
        public void setContacts(List<Contact> contacts) {
            this.contacts = contacts;
        }

        public User() {
            super();
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }
        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        public String getImageUrl() {
            return imageUrl;
        }
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
        public String getAbout() {
            return about;
        }
        public void setAbout(String about) {
            this.about = about;
        }


        @Override
        public String toString() {
            return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
                    + ", enabled=" + enabled + ", imageUrl=" + imageUrl + ", about=" + about + ", contacts=" + contacts
                    + "]";
        }

    }
