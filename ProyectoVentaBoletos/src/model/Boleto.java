package model;
/**
 * Clase que representa un boleto individual.
 * Atributos según los requerimientos del proyecto.
 */
public class Boleto {
    // Atributos definidos en los requerimientos 
    private String idBoleto;
    private String categoria;
    private double precio;
    private String asiento; // ubicacion del boleto
    private boolean vendido; // estado del boleto
    // inicializar un nuevo boleto
    public Boleto(String idBoleto, String categoria, double precio, String asiento) {
        this.idBoleto = idBoleto;
        this.categoria = categoria;
        this.precio = precio;
        this.asiento = asiento;
        this.vendido = false; // default inicia disponible 
    }
    // getters y setters
    public String getIdBoleto() { return idBoleto; }
    public String getCategoria() { return categoria; }
    public double getPrecio() { return precio; }
    public String getAsiento() { return asiento; }
    public boolean isVendido() { return vendido; }

    public void setVendido(boolean vendido) { this.vendido = vendido; }
    public void setPrecio(double precio) { this.precio = precio; }

    @Override
    public String toString() {
        return "ID: " + idBoleto + " | Cat: " + categoria + " | Precio: $" + precio + " | Asiento: " + asiento;
    }
}

