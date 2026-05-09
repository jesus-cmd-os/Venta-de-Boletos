package model;

/**
 * Enum que representa las categorías de boletos disponibles en el estadio.
 * Usar enum evita errores de tipeo al pasar Strings como "VIP", "vip", etc.
 */
public enum Categoria {

    VIP("VIP", 1500.0),
    PREFERENCIAL("Preferencial", 800.0),
    GENERAL("General", 400.0);

    // Atributos de cada categoría
    private final String nombre;
    private double precio; // no es final porque el sistema permite actualizarlo

    // Constructor del enum
    Categoria(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    /**
     * Busca una categoría por su nombre en String.
     * Útil para convertir lo que viene del JComboBox.
     * Ejemplo: Categoria.fromNombre("VIP") → Categoria.VIP
     */
    public static Categoria fromNombre(String nombre) {
        for (Categoria c : values()) {
            if (c.nombre.equalsIgnoreCase(nombre)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Categoría no válida: " + nombre);
    }

    @Override
    public String toString() {
        return nombre + " ($" + precio + ")";
    }
}