package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase que representa un reporte de venta individual.
 * Cada compra genera un objeto de este tipo que se encola en la Queue<ReporteVenta>.
 */
public class ReporteVenta {

    private String idReporte;
    private String fechaHora;
    private String categoria;
    private String asiento;
    private double totalGenerado;
    private String idBoleto;

    /**
     * Constructor: se llama justo después de confirmar una compra exitosa.
     */
    public ReporteVenta(String idBoleto, String categoria, String asiento, double totalGenerado) {
        this.idBoleto = idBoleto;
        this.categoria = categoria;
        this.asiento = asiento;
        this.totalGenerado = totalGenerado;
        // Fecha y hora se generan automáticamente al momento de la venta
        this.fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        // ID único del reporte basado en timestamp
        this.idReporte = "REP-" + System.currentTimeMillis();
    }

    // Getters
    public String getIdReporte()     { return idReporte; }
    public String getFechaHora()     { return fechaHora; }
    public String getCategoria()     { return categoria; }
    public String getAsiento()       { return asiento; }
    public double getTotalGenerado() { return totalGenerado; }
    public String getIdBoleto()      { return idBoleto; }

    /**
     * Formato de línea para escribir en el archivo .txt.
     * Usado por GestorArchivos al desencolar.
     */
    public String toLineaArchivo() {
        return String.format("[%s] Boleto: %-10s | Categoría: %-12s | Asiento: %-12s | Total: $%.2f",
                fechaHora, idBoleto, categoria, asiento, totalGenerado);
    }

    @Override
    public String toString() {
        return toLineaArchivo();
    }
}