package com.transaction.propagation.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Member {
    @Id
    @GeneratedValue
    private Long id;

    private String username;

    public Member() {
    }

    public Member(String username) {
        this.username = username;
    }
}
