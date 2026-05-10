package model;

/**
 * ════════════════════════════════════════════════════════════════════
 * Boleto — Modelo de datos que representa un boleto individual.
 *
 * Esta clase forma parte del paquete MODEL del patrón MVC.
 * Es la entidad principal del sistema: cada compra genera un objeto
 * Boleto que se almacena en la LinkedList<Boleto> de SistemaEstadio.
 *
 * Atributos requeridos según las especificaciones del proyecto:
 *   - idBoleto   → identificador único
 *   - categoria  → VIP, Preferencial o General
 *   - precio     → obtenido del HashMap de precios al momento de la compra
 *   - asiento    → ubicación física en el estadio (fila y columna)
 *   - vendido    → estado del boleto (false = disponible, true = vendido)
 * ════════════════════════════════════════════════════════════════════
 */
public class Boleto {

    // ── Atributos ─────────────────────────────────────────────────────────────

    /** Identificador único del boleto. Se genera con UUID en VentanaPrincipal. */
    private String  idBoleto;

    /** Categoría de la zona: "VIP", "Preferencial" o "General". */
    private String  categoria;

    /**
     * Precio del boleto en pesos.
     * Se consulta del HashMap<String,Double> al momento de confirmar la compra,
     * por lo que refleja el precio vigente en ese instante.
     */
    private double  precio;

    /**
     * Ubicación física del asiento en formato "Fila X, Asiento Y".
     * Este valor se usa también para buscar el boleto en la LinkedList
     * mediante SistemaEstadio.buscarBoleto(fila, col).
     */
    private String  asiento;

    /**
     * Estado del boleto.
     *   false → disponible (valor por defecto al crearse)
     *   true  → vendido (se marca en SistemaEstadio.comprarBoleto())
     */
    private boolean vendido;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Crea un nuevo boleto con estado "disponible" por defecto.
     * Es llamado desde SistemaEstadio.comprarBoleto() una vez validado
     * que el asiento no estaba ocupado en la matriz.
     *
     * @param idBoleto  Identificador único generado externamente (UUID)
     * @param categoria Zona del asiento: "VIP", "Preferencial" o "General"
     * @param precio    Precio vigente consultado del HashMap de precios
     * @param asiento   Ubicación en formato "Fila X, Asiento Y"
     */
    public Boleto(String idBoleto, String categoria, double precio, String asiento) {
        this.idBoleto  = idBoleto;
        this.categoria = categoria;
        this.precio    = precio;
        this.asiento   = asiento;
        this.vendido   = false; // todo boleto inicia como disponible
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** @return Identificador único del boleto */
    public String  getIdBoleto()  { return idBoleto; }

    /** @return Categoría de zona: "VIP", "Preferencial" o "General" */
    public String  getCategoria() { return categoria; }

    /** @return Precio del boleto en pesos al momento de la compra */
    public double  getPrecio()    { return precio; }

    /** @return Ubicación en formato "Fila X, Asiento Y" */
    public String  getAsiento()   { return asiento; }

    /** @return true si el boleto fue vendido, false si sigue disponible */
    public boolean isVendido()    { return vendido; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /**
     * Marca el boleto como vendido.
     * Se llama en SistemaEstadio.comprarBoleto() después de validar
     * que el asiento estaba disponible en la matriz.
     *
     * @param vendido true para marcar como vendido
     */
    public void setVendido(boolean vendido) { this.vendido = vendido; }

    /**
     * Actualiza el precio del boleto.
     * Útil si el precio cambia antes de confirmar la compra.
     *
     * @param precio Nuevo precio en pesos
     */
    public void setPrecio(double precio)    { this.precio = precio; }

    // ── Representación textual ────────────────────────────────────────────────

    /**
     * Representación en texto del boleto.
     * Útil para depuración y para mostrar datos en consola.
     *
     * @return Cadena con todos los datos del boleto
     */
    @Override
    public String toString() {
        return "ID: " + idBoleto
                + " | Cat: "    + categoria
                + " | Precio: $" + precio
                + " | Asiento: " + asiento
                + " | Vendido: " + vendido;
    }
}