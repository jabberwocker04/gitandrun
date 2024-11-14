package com.sparta.gitandrun.category.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_category")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // 프록시 속성 무시
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    public Category(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
