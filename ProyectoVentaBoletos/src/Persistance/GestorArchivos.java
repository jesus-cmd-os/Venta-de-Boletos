package Persistance;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import service.SistemaEstadio;
import model.ReporteVenta;

/**
 * GestorArchivos — Capa de persistencia del sistema.
 *
 * Responsabilidad única: tomar los reportes de venta acumulados en
 * la Cola FIFO de SistemaEstadio y escribirlos en un archivo .txt.
 *
 * Características del archivo generado:
 *   - Nombre con fecha del día: reporte_ventas_ddMMyyyy.txt
 *   - Si ya existe (misma fecha), se agregan registros al final (append)
 *   - Incluye encabezado, detalle de cada venta y resumen con totales
 *
 * El archivo se cierra automáticamente gracias a try-with-resources,
 * lo que evita fugas de recursos incluso si ocurre un error.
 */
public class GestorArchivos {

    /**
     * Desencola todos los ReporteVenta pendientes de la Cola FIFO
     * y los escribe en el archivo .txt del día.
     *
     * Flujo del método:
     *   1. Genera el nombre del archivo con la fecha actual
     *   2. Valida que haya reportes pendientes (evita crear archivos vacíos)
     *   3. Abre el archivo en modo append (true) para no sobrescribir el día
     *   4. Escribe el encabezado con fecha/hora del momento del guardado
     *   5. Desencola cada ReporteVenta en orden FIFO y escribe su línea
     *   6. Escribe el resumen con totales de boletos e ingresos
     *   7. Cierra el archivo automáticamente (try-with-resources)
     *
     * @param sistema Instancia del sistema del estadio que contiene la Cola FIFO
     */
    public void guardarReporteDiario(SistemaEstadio sistema) {

        // Paso 1: Nombre del archivo basado en la fecha actual del sistema
        String fechaActual  = new SimpleDateFormat("ddMMyyyy").format(new Date());
        String nombreArchivo = "reporte_ventas_" + fechaActual + ".txt";

        // Paso 2: Validación preventiva — no crear archivo si la cola está vacía
        if (sistema.getTotalReportesEnCola() == 0) {
            System.out.println("No hay reportes pendientes para guardar.");
            return;
        }

        // Paso 3-7: try-with-resources garantiza cierre del archivo sin importar errores
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo, true))) {

            //  Encabezado del bloque de reporte
            writer.println("========================================");
            writer.println("  REPORTE DE VENTAS - "
                    + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            writer.println("========================================");

            //  Variables acumuladoras para el resumen final
            int    totalBoletos  = 0;
            double totalIngresos = 0.0;

            //  Desencolar en orden FIFO y escribir cada registro
            // poll() devuelve y elimina el primer elemento de la cola (FIFO)
            // El loop continúa hasta vaciar completamente la cola
            while (sistema.getTotalReportesEnCola() > 0) {
                ReporteVenta reporte = sistema.extraerReporte();
                if (reporte != null) {
                    writer.println(reporte.toLineaArchivo());
                    totalBoletos++;
                    totalIngresos += reporte.getTotalGenerado();
                }
            }

            //  Resumen acumulado del bloque guardado
            writer.println("----------------------------------------");
            writer.println("Total boletos vendidos : " + totalBoletos);
            writer.println("Ingreso total generado : $" + String.format("%.2f", totalIngresos));
            writer.println("--- FIN DEL REPORTE ---");
            writer.println(); // Línea en blanco para separar bloques del mismo día

            System.out.println("Reporte guardado exitosamente en: " + nombreArchivo);

        } catch (IOException e) {
            // Error al acceder al sistema de archivos (permisos, disco lleno, etc.)
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }
}