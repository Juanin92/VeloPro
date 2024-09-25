package cl.jic.VeloPro.Model.Enum;

public enum Rol {

    MASTER ("UsuarioMaestro"),
    ADMIN ("Administrador"),
    GUEST("Invitado"),
    WAREHOUSE("Coordinador"),
    SELLER ("Vendedor");

    private final String displayName;
    Rol(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
