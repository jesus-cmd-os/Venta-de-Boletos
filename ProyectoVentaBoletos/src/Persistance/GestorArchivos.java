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
 * Su función principal es generar un archivo diario con los reportes de ventas.
 * Los reportes se obtienen desde una cola (estructura FIFO) dentro del sistema.
 */
public class GestorArchivos {

    /**
     * Guarda todos los reportes acumulados en la cola en un archivo físico.
     * El archivo se genera con la fecha actual en su nombre:
     * formato -> reporte_ventas_ddMMyyyy.txt
     *
     * Si el archivo ya existe (mismo día), se abre en modo append
     * para seguir agregando información sin sobrescribir.
     */
    public void guardarReporteDiario(SistemaEstadio sistema) {

        // Se obtiene la fecha actual para construir el nombre del archivo
        String fechaActual = new SimpleDateFormat("ddMMyyyy").format(new Date());
        String nombreArchivo = "reporte_ventas_" + fechaActual + ".txt";

        // Validación: si no hay reportes en la cola, no se genera el archivo
        if (sistema.getTotalReportesEnCola() == 0) {
            System.out.println("No hay reportes pendientes para guardar.");
            return;
        }

        // try-with-resources:
        // asegura el cierre automático del archivo al finalizar el bloque
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo, true))) {

            // Encabezado del reporte diario
            writer.println("========================================");
            writer.println("  REPORTE DE VENTAS - " +
                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            writer.println("========================================");

            // Variables acumuladoras para el resumen final
            int totalBoletos = 0;
            double totalIngresos = 0.0;

            // Procesamiento de la cola (FIFO):
            // se van extrayendo los reportes uno por uno
            while (sistema.getTotalReportesEnCola() > 0) {

                ReporteVenta reporte = sistema.extraerReporte();

                if (reporte != null) {
                    // Se escribe cada reporte en el archivo
                    writer.println(reporte.toLineaArchivo());

                    // Se actualizan los totales
                    totalBoletos++;
                    totalIngresos += reporte.getTotalGenerado();
                }
            }

            // Resumen final del reporte diario
            writer.println("----------------------------------------");
            writer.println("Total boletos vendidos : " + totalBoletos);
            writer.println("Ingreso total generado : $" + String.format("%.2f", totalIngresos));
            writer.println("--- FIN DEL REPORTE ---");
            writer.println();

            // Confirmación en consola
            System.out.println("Reporte guardado exitosamente en: " + nombreArchivo);

        } catch (IOException e) {
            // Manejo de errores en caso de fallo al escribir el archivo
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para mostrar en consola el nombre del archivo del día.
     * Útil para depuración o verificación rápida.
     */
    public void mostrarNombreArchivo() {
        String fechaActual = new SimpleDateFormat("ddMMyyyy").format(new Date());
        System.out.println("Archivo del día: reporte_ventas_" + fechaActual + ".txt");
    }
}