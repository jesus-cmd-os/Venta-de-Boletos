package model;

/**
 * Categoria — Enumeración de categorías de boletos con precio incluido.
 *
 * A diferencia de TipoZona (que clasifica celdas de la matriz),
 * Categoria incluye el nombre visible y el precio de cada tipo,
 * lo que la hace útil para mostrar información en la GUI y para
 * convertir texto proveniente de componentes Swing como JComboBox.
 *
 * Ventaja de usar enum con atributos:
 *   - El precio puede consultarse directamente: Categoria.VIP.getPrecio()
 *   - El precio puede actualizarse: Categoria.VIP.setPrecio(2000.0)
 *   - Se evitan errores de tipeo comparados con usar Strings directos
 */
public enum Categoria {

    //  Valores del enum con nombre y precio base
    VIP("VIP",                  1500.0),
    PREFERENCIAL("Preferencial", 800.0),
    GENERAL("General",           400.0);

    // Atributos de cada valor

    /** Nombre legible de la categoría para mostrar en la GUI */
    private final String nombre;

    /**
     * Precio base de la categoría en pesos.
     * No es final porque el sistema permite actualizarlo
     * desde el botón "Actualizar Precios" en VentanaPrincipal.
     */
    private double precio;

    //  Constructor del enum

    /**
     * Constructor privado (los enums no pueden instanciarse externamente).
     *
     * @param nombre Nombre visible de la categoría
     * @param precio Precio base en pesos
     */
    Categoria(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    // Getters y setters

    /** @return Nombre visible de la categoría (ej: "VIP") */
    public String getNombre() { return nombre; }

    /** @return Precio actual de la categoría en pesos */
    public double getPrecio() { return precio; }

    /**
     * Actualiza el precio de esta categoría.
     * Llamado cuando el usuario usa "Actualizar Precios" en la GUI.
     *
     * @param precio Nuevo precio en pesos (debe ser mayor a 0)
     */
    public void setPrecio(double precio) { this.precio = precio; }

    // Método utilitario

    /**
     * Convierte un String al valor del enum correspondiente.
     * Es útil para transformar lo que el usuario selecciona en un
     * JComboBox ("VIP", "Preferencial", "General") al tipo enum.
     *
     * Ejemplo: Categoria.fromNombre("VIP") → Categoria.VIP
     *
     * @param nombre Nombre de la categoría (no distingue mayúsculas)
     * @return El valor del enum que corresponde al nombre dado
     * @throws IllegalArgumentException si el nombre no coincide con ninguna categoría
     */
    public static Categoria fromNombre(String nombre) {
        for (Categoria c : values()) {
            if (c.nombre.equalsIgnoreCase(nombre)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Categoria no valida: " + nombre);
    }

    // Representación textual

    /**
     * Texto descriptivo para mostrar en componentes Swing como JComboBox.
     *
     * @return Cadena con nombre y precio actual, ej: "VIP ($1500.0)"
     */
    @Override
    public String toString() {
        return nombre + " ($" + precio + ")";
    }
}