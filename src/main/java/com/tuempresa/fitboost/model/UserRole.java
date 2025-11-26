package com.tuempresa.fitboost.model;

public enum UserRole {
    ADMIN("Administrador", "Gestiona toda la tienda y usuarios"),
    ORDER_MANAGER("Gestor de Pedidos", "Gestiona y coordina pedidos y envíos"),
    CLIENTE("Cliente", "Compra productos y realiza pedidos");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
