package proyectoventaboletos;

import service.SistemaEstadio;
import View.VentanaPrincipal;

public class ProyectoVentaBoletos {

    public static void main(String[] args) {
        // 1. Inicializamos el backend con el tamaño del estadio (filas, columnas)
        SistemaEstadio estadio = new SistemaEstadio(8, 8);
        
        // 2. Iniciamos la interfaz gráfica
        VentanaPrincipal ventana = new VentanaPrincipal(estadio);
        ventana.setVisible(true);
    }
}