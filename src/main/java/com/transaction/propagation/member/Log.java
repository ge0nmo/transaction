package com.transaction.propagation.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Log {
    @Id @GeneratedValue
    private Long id;

    private String message;

    public Log(String message) {
        this.message = message;
    }
}
