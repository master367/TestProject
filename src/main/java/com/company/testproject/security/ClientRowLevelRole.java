package com.company.testproject.security;

import com.company.testproject.entity.BankAccount;
import io.jmix.security.role.annotation.RowLevelRole;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;

@RowLevelRole(name = "Client Row Level", code = ClientRowLevelRole.CODE)
public interface ClientRowLevelRole {
    String CODE = "client-row-level";

    @JpqlRowLevelPolicy(entityClass = BankAccount.class, where = "{E}.client.email = :current_user_username")
    void bankAccount1();
}