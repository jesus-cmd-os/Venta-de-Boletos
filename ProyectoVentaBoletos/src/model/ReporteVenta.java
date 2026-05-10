package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ReporteVenta — Modelo de datos para un reporte de venta individual.
 *
 * Cada vez que se confirma una compra en SistemaEstadio.comprarBoleto(),
 * se crea un objeto ReporteVenta que se agrega a la Cola FIFO
 * (Queue<ReporteVenta> colaReportes).
 *
 * Cuando el usuario presiona "Guardar Reporte", GestorArchivos
 * desencola estos objetos en orden FIFO y los escribe en el archivo .txt.
 *
 * La fecha y hora se capturan automáticamente en el constructor,
 * garantizando que el reporte refleje el momento exacto de la venta.
 */
public class ReporteVenta {

    //  Atributos

    /**
     * Identificador único del reporte.
     * Se genera automáticamente usando el timestamp del sistema:
     * "REP-" + System.currentTimeMillis()
     */
    private String idReporte;

    /**
     * Fecha y hora exacta en que se realizó la venta.
     * Formato: "dd/MM/yyyy HH:mm:ss"
     * Se captura automáticamente al crear el objeto.
     */
    private String fechaHora;

    /** Categoría del boleto vendido: "VIP", "Preferencial" o "General" */
    private String categoria;

    /** Ubicación del asiento en formato "Fila X, Asiento Y" */
    private String asiento;

    /** Precio del boleto al momento de la venta (puede diferir si se actualizó) */
    private double totalGenerado;

    /** ID del boleto asociado a esta venta, para trazabilidad */
    private String idBoleto;

    //  Constructor

    /**
     * Crea un reporte de venta y captura la fecha/hora automáticamente.
     * Es llamado desde SistemaEstadio.comprarBoleto() justo después de
     * registrar el boleto en la LinkedList y marcar la matriz.
     *
     * @param idBoleto       ID único del boleto vendido
     * @param categoria      Zona del asiento ("VIP", "Preferencial", "General")
     * @param asiento        Ubicación en formato "Fila X, Asiento Y"
     * @param totalGenerado  Precio cobrado por el boleto en esta transacción
     */
    public ReporteVenta(String idBoleto, String categoria, String asiento, double totalGenerado) {
        this.idBoleto       = idBoleto;
        this.categoria      = categoria;
        this.asiento        = asiento;
        this.totalGenerado  = totalGenerado;

        // La fecha/hora se captura en el momento exacto de la venta
        this.fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        // ID único del reporte basado en timestamp para evitar colisiones
        this.idReporte = "REP-" + System.currentTimeMillis();
    }

    //  Getters

    /** @return Identificador único del reporte (ej: "REP-1714567890123") */
    public String getIdReporte()     { return idReporte; }

    /** @return Fecha y hora de la venta en formato "dd/MM/yyyy HH:mm:ss" */
    public String getFechaHora()     { return fechaHora; }

    /** @return Categoría de la zona del boleto */
    public String getCategoria()     { return categoria; }

    /** @return Ubicación del asiento vendido */
    public String getAsiento()       { return asiento; }

    /** @return Precio cobrado por este boleto */
    public double getTotalGenerado() { return totalGenerado; }

    /** @return ID del boleto al que pertenece este reporte */
    public String getIdBoleto()      { return idBoleto; }

    // Métodos de formato

    /**
     * Genera la línea de texto con formato fijo para escribir en el archivo .txt.
     * GestorArchivos llama a este método al desencolar cada ReporteVenta.
     *
     * Formato de salida:
     * [dd/MM/yyyy HH:mm:ss] Boleto: XXXXXXXX   | Categoría: VIP          | Asiento: Fila X, Asiento Y | Total: $1500.00
     *
     * @return Línea formateada lista para escribir en el archivo de reporte
     */
    public String toLineaArchivo() {
        return String.format(
                "[%s] Boleto: %-10s | Categoria: %-12s | Asiento: %-20s | Total: $%.2f",
                fechaHora, idBoleto, categoria, asiento, totalGenerado
        );
    }

    /**
     * Representación textual del reporte.
     * Delega en toLineaArchivo() para mantener un único formato consistente.
     *
     * @return Mismo formato que toLineaArchivo()
     */
    @Override
    public String toString() {
        return toLineaArchivo();
    }
}