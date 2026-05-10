package proyectoventaboletos;

import View.VentanaPrincipal;
import service.SistemaEstadio;
                 
/**
 * Main del sistema.
 */
public class ProyectoVentaBoletos {

    public static void main(String[] args) {

        // Estadio grande
        SistemaEstadio estadio =
                new SistemaEstadio(14,14);

        VentanaPrincipal ventana =
                new VentanaPrincipal(estadio);

        ventana.setVisible(true);
    }
}