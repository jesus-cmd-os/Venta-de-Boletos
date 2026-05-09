package Persistance;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import service.SistemaEstadio;
import model.ReporteVenta;

/**
 * Clase encargada de la persistencia en archivos de texto.
 * Desencola los ReporteVenta generados y los escribe en un .txt diario.
 */
public class GestorArchivos {

    /**
     * Guarda todos los reportes acumulados en la cola en un archivo físico.
     * Formato del archivo: reporte_ventas_ddmmaaaa.txt
     * Si el archivo ya existe (mismo día), agrega al final (append = true).
     */
    public void guardarReporteDiario(SistemaEstadio sistema) {

        // Generar nombre del archivo con la fecha actual
        String fechaActual = new SimpleDateFormat("ddMMyyyy").format(new Date());
        String nombreArchivo = "reporte_ventas_" + fechaActual + ".txt";

        // Verificar que haya reportes antes de abrir el archivo
        if (sistema.getTotalReportesEnCola() == 0) {
            System.out.println("No hay reportes pendientes para guardar.");
            return;
        }

        // try-with-resources: cierra el archivo automáticamente al terminar
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo, true))) {

            writer.println("========================================");
            writer.println("  REPORTE DE VENTAS - " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            writer.println("========================================");

            int totalBoletos = 0;
            double totalIngresos = 0.0;

            // Desencolar FIFO y escribir cada reporte
            while (sistema.getTotalReportesEnCola() > 0) {
                ReporteVenta reporte = sistema.extraerReporte();
                if (reporte != null) {
                    writer.println(reporte.toLineaArchivo());
                    totalBoletos++;
                    totalIngresos += reporte.getTotalGenerado();
                }
            }

            // Resumen al final del bloque
            writer.println("----------------------------------------");
            writer.println("Total boletos vendidos : " + totalBoletos);
            writer.println("Ingreso total generado : $" + String.format("%.2f", totalIngresos));
            writer.println("--- FIN DEL REPORTE ---");
            writer.println();

            System.out.println("Reporte guardado exitosamente en: " + nombreArchivo);

        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }

    /**
     * Muestra en consola los reportes guardados (útil para depuración).
     */
    public void mostrarNombreArchivo() {
        String fechaActual = new SimpleDateFormat("ddMMyyyy").format(new Date());
        System.out.println("Archivo del día: reporte_ventas_" + fechaActual + ".txt");
    }
}