package com.company.testproject.security;

import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;

@ResourceRole(name = "Client Role", code = ClientRole.CODE)
public interface ClientRole {
    String CODE = "client-role";
    @EntityPolicy(entityName = "BankAccount", actions = {
            EntityPolicyAction.READ,
            EntityPolicyAction.UPDATE,
            EntityPolicyAction.CREATE
    })
    @EntityAttributePolicy(entityName = "BankAccount", attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void bankAccount();

    @EntityPolicy(entityName = "Transaction", actions = {
            EntityPolicyAction.READ,
            EntityPolicyAction.CREATE
    })
    @EntityAttributePolicy(entityName = "Transaction", attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void transaction();

    @EntityPolicy(entityName = "Client", actions = {
            EntityPolicyAction.READ,
            EntityPolicyAction.UPDATE
    })
    @EntityAttributePolicy(entityName = "Client", attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void client();

}