package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel lateral derecho del sistema.
 * Su función es mostrar información visual al usuario:
 * - Estado de selección de asientos
 * - Resumen de ventas
 * - Historial de compras
 */
public class PanelControl extends JPanel {

    // ─────────────────────────────────────────
    // Componentes gráficos del panel
    // ─────────────────────────────────────────

    // Muestra información del asiento seleccionado actualmente
    private JLabel lblInfoSeleccion;

    // Muestra el total de boletos vendidos
    private JLabel lblTotalBoletos;

    // Muestra el total de ingresos generados
    private JLabel lblTotalIngresos;

    // Área de texto donde se muestra el historial de ventas
    private JTextArea areaHistorial;

    // ─────────────────────────────────────────
    // Paleta de colores del sistema
    // ─────────────────────────────────────────

    // Color base del panel lateral
    private static final Color COLOR_FONDO = new Color(44,62,80);

    // Color de fondo de subpaneles
    private static final Color COLOR_PANEL = new Color(52,73,94);

    // Color de acento para bordes y detalles
    private static final Color COLOR_ACENTO = new Color(52,152,219);

    // Colores que representan cada tipo de zona
    private static final Color COLOR_VIP = new Color(155,89,182);

    private static final Color COLOR_PREF = new Color(52,152,219);

    private static final Color COLOR_GENERAL = new Color(46,204,113);

    /**
     * Constructor del panel.
     * Inicializa toda la interfaz del panel lateral.
     */
    public PanelControl() {

        construirPanel();
    }

    /**
     * Método principal que construye toda la estructura visual del panel.
     * Se organiza en tres secciones:
     * - Información
     * - Resumen
     * - Historial
     */
    private void construirPanel() {

        // Layout vertical para apilar los paneles
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setBackground(COLOR_FONDO);

        setPreferredSize(new Dimension(300, 0));

        // Se agregan las tres secciones principales del panel
        add(crearPanelInfo());

        add(Box.createVerticalStrut(10)); // Separación visual

        add(crearPanelResumen());

        add(Box.createVerticalStrut(10));

        add(crearPanelHistorial());
    }

    /**
     * Crea el panel de información general.
     * Muestra leyenda de colores y asiento seleccionado.
     */
    private JPanel crearPanelInfo() {

        JPanel panel = new JPanel(new GridLayout(5,1,5,8));

        panel.setBackground(COLOR_PANEL);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_ACENTO, 1),
                        new EmptyBorder(10, 10, 10, 10)
                )
        );

        // Título del panel
        JLabel titulo = new JLabel("Información");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 14));

        // Leyenda de colores por zona
        JLabel leyenda1 = new JLabel("VIP = Morado");
        leyenda1.setForeground(COLOR_VIP);

        JLabel leyenda2 = new JLabel("PREF = Azul");
        leyenda2.setForeground(COLOR_PREF);

        JLabel leyenda3 = new JLabel("GEN = Verde");
        leyenda3.setForeground(COLOR_GENERAL);

        // Estado de selección de asiento
        lblInfoSeleccion = new JLabel("Asientos: ninguno");
        lblInfoSeleccion.setForeground(Color.WHITE);

        // Se agregan componentes al panel
        panel.add(titulo);
        panel.add(leyenda1);
        panel.add(leyenda2);
        panel.add(leyenda3);
        panel.add(lblInfoSeleccion);

        return panel;
    }

    /**
     * Crea el panel de resumen de ventas.
     * Muestra boletos vendidos e ingresos totales.
     */
    private JPanel crearPanelResumen() {

        JPanel panel = new JPanel(new GridLayout(2,1,5,5));

        panel.setBackground(COLOR_PANEL);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_ACENTO, 1),
                        new EmptyBorder(10, 10, 10, 10)
                )
        );

        // Etiqueta de boletos vendidos
        lblTotalBoletos = new JLabel("Boletos vendidos: 0");

        // Etiqueta de ingresos totales
        lblTotalIngresos = new JLabel("Ingresos: $0.00");

        // Color para resaltar datos del resumen
        lblTotalBoletos.setForeground(COLOR_GENERAL);
        lblTotalIngresos.setForeground(COLOR_GENERAL);

        panel.add(lblTotalBoletos);
        panel.add(lblTotalIngresos);

        return panel;
    }

    /**
     * Crea el panel de historial de ventas.
     * Muestra todas las compras realizadas.
     */
    private JPanel crearPanelHistorial() {

        JPanel panel = new JPanel(new BorderLayout());

        panel.setBackground(COLOR_PANEL);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_ACENTO, 1),
                        new EmptyBorder(10, 10, 10, 10)
                )
        );

        // Título del historial
        JLabel titulo = new JLabel("Historial");
        titulo.setForeground(Color.WHITE);

        // Área de texto donde se agregan los registros
        areaHistorial = new JTextArea();
        areaHistorial.setEditable(false);

        JScrollPane scroll = new JScrollPane(areaHistorial);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ─────────────────────────────────────────
    // MÉTODOS PÚBLICOS (actualización desde el sistema)
    // ─────────────────────────────────────────

    /**
     * Actualiza la información del asiento seleccionado.
     */
    public void actualizarSeleccion(String texto) {

        lblInfoSeleccion.setText(texto);
    }

    /**
     * Actualiza los datos del resumen de ventas.
     */
    public void actualizarResumen(int boletos, double ingresos) {

        lblTotalBoletos.setText("Boletos vendidos: " + boletos);

        lblTotalIngresos.setText(
                String.format("Ingresos: $%.2f", ingresos)
        );
    }

    /**
     * Agrega una nueva línea al historial de ventas.
     */
    public void agregarHistorial(String texto) {

        areaHistorial.append(texto + "\n");
    }
}