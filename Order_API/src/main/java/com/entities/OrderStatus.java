package com.entities;

public enum OrderStatus {
    CREATED("Commande créée"),
    PAID("Payée"),
    PREPARING("En préparation"),
    READY_FOR_DELIVERY("Prête à être récupérée"),
    DELIVERED("Récupérée"),
    CANCELLED("Annulée");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}