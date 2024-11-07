package com.certidevs.model;

import lombok.*;

import java.util.StringJoiner;


public class Customer {
    private Long id;
    private String email;
    private String nif;
    private Double salary;
    private Boolean active;

    public Customer(Long id, String email, String nif, Double salary) {
        this.id = id;
        this.email = email;
        this.nif = nif;
        this.salary = salary;
    }

    public Customer(String email, String nif) {
        this.email = email;
        this.nif = nif;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Customer.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("email='" + email + "'")
                .add("nif='" + nif + "'")
                .add("salary=" + salary)
                .toString();
    }
}
