package service;

import Persistance.GestorArchivos;

public class PrimeraPruebaSistema {
    public static void main(String[] args) {
        // 1. Inicializar el sistema con 5 filas y 5 columnas
        SistemaEstadio estadio = new SistemaEstadio(5, 5);
        GestorArchivos persistencia = new GestorArchivos();

        System.out.println("--- INICIANDO PRUEBA DE VENTA ---");

        // 2. Probar ventas exitosas (Uso de HashMap, Matriz y LinkedList)
        estadio.comprarBoleto("V-001", "VIP", 0, 0);
        estadio.comprarBoleto("V-002", "VIP", 0, 1);
        estadio.comprarBoleto("G-050", "General", 4, 4);

        // 3. Probar validación: Intentar comprar un asiento ya ocupado
        boolean exito = estadio.comprarBoleto("V-999", "VIP", 0, 0);
        if (!exito) {
            System.out.println("Validaci\u00F3n exitosa: No se permiti\u00F3 duplicar el asiento (0,0)");
        }

        // 4. Probar Persistencia: Guardar la cola de reportes en el archivo .txt
        System.out.println("Guardando reportes en disco...");
        persistencia.guardarReporteDiario(estadio);

        System.out.println("Prueba finalizada. Busca el archivo .txt en la carpeta del proyecto.");
    }
}
