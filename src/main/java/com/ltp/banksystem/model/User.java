package com.ltp.banksystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    private Long userId;
    private String userName;
    private String password;

}
