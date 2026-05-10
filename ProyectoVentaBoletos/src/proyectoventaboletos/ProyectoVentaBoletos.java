package proyectoventaboletos;

import View.EstiloUI;
import View.VentanaPrincipal;
import service.SistemaEstadio;

import javax.swing.SwingUtilities;

public class ProyectoVentaBoletos {

    public static void main(String[] args) {

        // CRÍTICO: inicializar estilos ANTES de crear cualquier componente Swing
        // Esto configura el L&F correcto según la plataforma (Windows o Mac)
        EstiloUI.inicializar();

        // Lanzar la GUI en el hilo de eventos de Swing (EDT)
        // Esto es obligatorio — crear componentes Swing fuera del EDT causa
        // problemas de renderizado especialmente en Mac
        SwingUtilities.invokeLater(() -> {
            SistemaEstadio sistema = new SistemaEstadio(14, 14);
            VentanaPrincipal ventana = new VentanaPrincipal(sistema);
            ventana.setVisible(true);
        });
    }
}