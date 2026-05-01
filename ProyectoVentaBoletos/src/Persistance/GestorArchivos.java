package Persistance;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import service.SistemaEstadio;

/**
 * Clase encargada de la persistencia en archivos de texto.
 */
public class GestorArchivos {

    /**
     * Guarda los reportes acumulados en la cola en un archivo físico.
     * Formato requerido: reporte_ventas_ddmmaaaa.txt.
     */
    public void guardarReporteDiario(SistemaEstadio sistema) {
        // Generar el nombre del archivo con la fecha actual
        String fechaActual = new SimpleDateFormat("ddMMyyyy").format(new Date());
        String nombreArchivo = "reporte_ventas_" + fechaActual + ".txt";

        // Uso de try-with-resources para asegurar que el archivo se cierre
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo, true))) {
            
            writer.println("--- REPORTE DE VENTAS - GENERADO: " + new Date() + " ---");
            
            // Vaciar la cola FIFO siguiendo el flujo de trabajo
            while (sistema.getTotalReportesEnCola() > 0) {
                String linea = sistema.extraerReporte(); // Desencolar
                writer.println(linea);
            }
            
            writer.println("--- FIN DEL REPORTE ---\n");
            System.out.println("Reporte guardado exitosamente en: " + nombreArchivo);

        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
    }
}