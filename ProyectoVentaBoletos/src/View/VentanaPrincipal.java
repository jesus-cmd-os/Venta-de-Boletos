package View;

import Persistance.GestorArchivos;
import service.SistemaEstadio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Point;
import java.util.List;
import java.util.UUID;

/**
 * Ventana principal del sistema.
 *
 * RESPONSABILIDADES:
 * - coordinar paneles
 * - confirmar compras
 * - guardar reportes
 *
 * NO dibuja el estadio.
 * NO controla historial.
 * NO crea asientos.
 */
public class VentanaPrincipal extends JFrame {

    // ─────────────────────────────────────────
    // Backend
    // ─────────────────────────────────────────
    private SistemaEstadio sistema;

    private GestorArchivos gestorArchivos;

    // ─────────────────────────────────────────
    // Paneles reutilizables
    // ─────────────────────────────────────────
    private PanelEstadio panelEstadio;

    private PanelControl panelControl;

    // ─────────────────────────────────────────
    // Estadísticas
    // ─────────────────────────────────────────
    private int totalVendidos = 0;

    private double totalIngresos = 0;

    // ─────────────────────────────────────────
    // Colores
    // ─────────────────────────────────────────
    private static final Color COLOR_FONDO =
            new Color(44,62,80);

    /**
     * Constructor.
     */
    public VentanaPrincipal(
            SistemaEstadio sistema
    ) {

        this.sistema = sistema;

        this.gestorArchivos =
                new GestorArchivos();

        configurarVentana();

        construirUI();
    }

    /**
     * Configuración base.
     */
    private void configurarVentana() {

        setTitle(
                "Sistema de Venta de Boletos"
        );

        setSize(1400,900);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        getContentPane().setBackground(
                COLOR_FONDO
        );

        setLayout(
                new BorderLayout(10,10)
        );
    }

    /**
     * Construcción general.
     */
    private void construirUI() {

        add(
                crearTitulo(),
                BorderLayout.NORTH
        );

        add(
                crearCentro(),
                BorderLayout.CENTER
        );

        add(
                crearPanelInferior(),
                BorderLayout.SOUTH
        );
    }

    /**
     * Título.
     */
    private JPanel crearTitulo() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                COLOR_FONDO
        );

        panel.setBorder(
                new EmptyBorder(
                        15,
                        15,
                        10,
                        15
                )
        );

        JLabel titulo =
                new JLabel(
                        "ESTADIO ALLIANZ ARENA",
                        SwingConstants.CENTER
                );

        titulo.setForeground(
                Color.WHITE
        );

        titulo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        panel.add(
                titulo,
                BorderLayout.CENTER
        );

        return panel;
    }

    /**
     * Panel central.
     */
    private JPanel crearCentro() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(10,10)
                );

        panel.setBackground(
                COLOR_FONDO
        );

        panel.setBorder(
                new EmptyBorder(
                        10,
                        15,
                        10,
                        15
                )
        );

        // PANEL ESTADIO
        panelEstadio =
                new PanelEstadio(
                        sistema
                );

        // PANEL CONTROL
        panelControl =
                new PanelControl();

        panel.add(
                panelEstadio,
                BorderLayout.CENTER
        );

        panel.add(
                panelControl,
                BorderLayout.EAST
        );

        return panel;
    }

    /**
     * Panel inferior.
     */
    private JPanel crearPanelInferior() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                20,
                                10
                        )
                );

        panel.setBackground(
                COLOR_FONDO
        );

        JButton btnComprar =
                new JButton(
                        "Confirmar Compra"
                );

        JButton btnGuardar =
                new JButton(
                        "Guardar Reporte"
                );

        btnComprar.addActionListener(
                e -> confirmarCompra()
        );

        btnGuardar.addActionListener(
                e -> guardarReporte()
        );

        panel.add(btnComprar);

        panel.add(btnGuardar);

        return panel;
    }

    /**
     * Confirmar compra.
     */
    private void confirmarCompra() {

        List<Point> seleccionados =
                panelEstadio
                        .getAsientosSeleccionados();

        if (seleccionados.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona asientos."
            );

            return;
        }

        double totalCompra = 0;

        for (Point p : seleccionados) {

            totalCompra +=
                    sistema.obtenerPrecioAsiento(
                            p.x,
                            p.y
                    );
        }

        int respuesta =
                JOptionPane.showConfirmDialog(
                        this,

                        "Total: $" +
                        totalCompra +

                        "\n\n¿Confirmar compra?",

                        "Confirmar",

                        JOptionPane.YES_NO_OPTION
                );

        if (respuesta !=
                JOptionPane.YES_OPTION) {

            return;
        }

        JButton[][] botones =
                panelEstadio
                        .getBotonesAsientos();

        for (Point p : seleccionados) {

            String categoria =
                    sistema.obtenerCategoriaAsiento(
                            p.x,
                            p.y
                    );

            double precio =
                    sistema.obtenerPrecioAsiento(
                            p.x,
                            p.y
                    );

            String id =
                    UUID.randomUUID()
                            .toString()
                            .substring(0,8);

            sistema.comprarBoleto(
                    id,
                    categoria,
                    p.x,
                    p.y
            );

            botones[p.x][p.y]
                    .setBackground(
                            new Color(231,76,60)
                    );

            botones[p.x][p.y]
                    .setEnabled(false);

            panelControl.agregarHistorial(
                    String.format(
                            "%s | F%d-A%d | $%.0f",
                            categoria,
                            p.x,
                            p.y,
                            precio
                    )
            );

            totalVendidos++;

            totalIngresos += precio;
        }

        panelControl.actualizarResumen(
                totalVendidos,
                totalIngresos
        );

        JOptionPane.showMessageDialog(
                this,
                "Compra realizada."
        );

        seleccionados.clear();

        panelControl.actualizarSeleccion(
                "Asientos: ninguno"
        );
    }

    /**
     * Guardar reportes.
     */
    private void guardarReporte() {

        gestorArchivos
                .guardarReporteDiario(
                        sistema
                );

        JOptionPane.showMessageDialog(
                this,
                "Reporte guardado."
        );
    }
}