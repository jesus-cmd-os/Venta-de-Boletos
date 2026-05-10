package View;

import Persistance.GestorArchivos;
import service.SistemaEstadio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Ventana principal del sistema.
 * Coordina PanelEstadio y PanelControl.
 * No dibuja el estadio ni gestiona historial directamente.
 */
public class VentanaPrincipal extends JFrame {

    // ── Backend ───────────────────────────────────────────────────────────────
    private final SistemaEstadio sistema;
    private final GestorArchivos gestorArchivos = new GestorArchivos();

    // ── Paneles ───────────────────────────────────────────────────────────────
    private PanelEstadio panelEstadio;
    private PanelControl panelControl;

    // ── Estadísticas acumuladas ───────────────────────────────────────────────
    private int    totalVendidos = 0;
    private double totalIngresos = 0.0;

    // ── Colores ───────────────────────────────────────────────────────────────
    private static final Color C_FONDO = new Color(28, 35, 45);
    private static final Color C_BORDE = new Color(55, 70, 88);

    // ─────────────────────────────────────────────────────────────────────────
    public VentanaPrincipal(SistemaEstadio sistema) {
        this.sistema = sistema;
        configurarVentana();
        construirUI();
    }

    private void configurarVentana() {
        setTitle("Sistema de Venta de Boletos — Estadio");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(C_FONDO);
        setLayout(new BorderLayout(10, 10));
    }

    private void construirUI() {
        add(crearTitulo(),         BorderLayout.NORTH);
        add(crearCentro(),         BorderLayout.CENTER);
        add(crearPanelInferior(),  BorderLayout.SOUTH);
    }

    // ── Título ────────────────────────────────────────────────────────────────
    private JPanel crearTitulo() {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 2));
        p.setBackground(C_FONDO);
        p.setBorder(new EmptyBorder(14, 16, 6, 16));

        JLabel t = new JLabel("ESTADIO ALLIANZ ARENA", SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 22));
        t.setForeground(Color.WHITE);

        JLabel s = new JLabel("Selecciona hasta 6 asientos y confirma la compra", SwingConstants.CENTER);
        s.setFont(new Font("Arial", Font.PLAIN, 12));
        s.setForeground(new Color(120, 135, 150));

        p.add(t);
        p.add(s);
        return p;
    }

    // ── Centro ────────────────────────────────────────────────────────────────
    private JPanel crearCentro() {
        // 1. Crear paneles
        panelControl = new PanelControl();
        panelEstadio = new PanelEstadio(sistema);

        // 2. Conectar PanelEstadio → PanelControl para tiempo real
        panelEstadio.setPanelControl(panelControl);

        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(C_FONDO);
        p.setBorder(new EmptyBorder(0, 12, 0, 12));
        p.add(panelEstadio, BorderLayout.CENTER);
        p.add(panelControl, BorderLayout.EAST);
        return p;
    }

    // ── Panel inferior — botones ──────────────────────────────────────────────
    private JPanel crearPanelInferior() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        p.setBackground(C_FONDO);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDE));

        JButton btnComprar  = crearBoton("Confirmar Compra",   new Color(24, 95, 165));
        JButton btnPrecios  = crearBoton("Actualizar Precios", new Color(142, 68, 173));
        JButton btnGuardar  = crearBoton("Guardar Reporte",    new Color(15, 110, 86));
        JButton btnNueva    = crearBoton("Nueva Sesión",       new Color(60, 70, 85));

        btnComprar.addActionListener(e -> confirmarCompra());
        btnPrecios.addActionListener(e -> actualizarPrecios());
        btnGuardar.addActionListener(e -> guardarReporte());
        btnNueva  .addActionListener(e -> nuevaSesion());

        p.add(btnComprar);
        p.add(btnPrecios);
        p.add(btnGuardar);
        p.add(btnNueva);
        return p;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(180, 40));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACCIONES
    // ══════════════════════════════════════════════════════════════════════════
    private void confirmarCompra() {
        List<Point> seleccionados = panelEstadio.getAsientosSeleccionados();

        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona al menos un asiento.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Armar detalle de la compra
        StringBuilder detalle = new StringBuilder("Confirmar compra de "
                + seleccionados.size() + " boleto(s):\n\n");
        double totalCompra = 0;

        for (Point p : seleccionados) {
            String cat    = sistema.obtenerCategoriaAsiento(p.x, p.y);
            double precio = sistema.obtenerPrecioAsiento(p.x, p.y);
            detalle.append(String.format("  • %s  F%d-A%d  $%,.0f%n", cat, p.x, p.y, precio));
            totalCompra += precio;
        }
        detalle.append(String.format("%n  Total: $%,.2f", totalCompra));

        int resp = JOptionPane.showConfirmDialog(this, detalle.toString(),
                "Confirmar compra", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resp != JOptionPane.YES_OPTION) return;

        // Procesar cada boleto
        JButton[][] botones = panelEstadio.getBotonesAsientos();
        for (Point p : seleccionados) {
            String cat    = sistema.obtenerCategoriaAsiento(p.x, p.y);
            double precio = sistema.obtenerPrecioAsiento(p.x, p.y);
            String id     = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            sistema.comprarBoleto(id, cat, p.x, p.y);

            totalVendidos++;
            totalIngresos += precio;

            // Agregar al historial
            panelControl.agregarHistorial(
                    String.format("%s | %s F%d-A%d | $%,.0f", id, cat, p.x, p.y, precio));
        }

        // Actualizar resumen de ventas
        panelControl.actualizarResumen(totalVendidos, totalIngresos);

        // Marcar asientos en el estadio y limpiar selección
        panelEstadio.marcarOcupados(new ArrayList<>(seleccionados));

        JOptionPane.showMessageDialog(this,
                String.format("¡Compra exitosa! %d boleto(s) — Total: $%,.2f",
                        seleccionados.size(), totalCompra),
                "Compra confirmada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarPrecios() {
        // Panel con 3 campos — uno por categoría
        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 10));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        panel.add(new JLabel("Categoría"));
        panel.add(new JLabel("Nuevo precio ($)"));

        JTextField tfVIP  = new JTextField(
                String.valueOf(sistema.getMapaPrecios().getOrDefault("VIP", 1500.0).intValue()));
        JTextField tfPref = new JTextField(
                String.valueOf(sistema.getMapaPrecios().getOrDefault("Preferencial", 800.0).intValue()));
        JTextField tfGen  = new JTextField(
                String.valueOf(sistema.getMapaPrecios().getOrDefault("General", 400.0).intValue()));

        panel.add(new JLabel("VIP"));          panel.add(tfVIP);
        panel.add(new JLabel("Preferencial")); panel.add(tfPref);
        panel.add(new JLabel("General"));      panel.add(tfGen);

        int resp = JOptionPane.showConfirmDialog(this, panel,
                "Actualizar precios", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resp != JOptionPane.OK_OPTION) return;

        try {
            double vip  = Double.parseDouble(tfVIP.getText().trim());
            double pref = Double.parseDouble(tfPref.getText().trim());
            double gen  = Double.parseDouble(tfGen.getText().trim());

            if (vip <= 0 || pref <= 0 || gen <= 0) {
                JOptionPane.showMessageDialog(this, "Los precios deben ser mayores a 0.",
                        "Precio inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            sistema.actualizarPrecio("VIP",          vip);
            sistema.actualizarPrecio("Preferencial",  pref);
            sistema.actualizarPrecio("General",       gen);

            JOptionPane.showMessageDialog(this,
                    String.format("Precios actualizados:%n  VIP: $%,.0f%n  Preferencial: $%,.0f%n  General: $%,.0f",
                            vip, pref, gen),
                    "Precios actualizados", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa solo números válidos.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarReporte() {
        if (sistema.getTotalReportesEnCola() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay reportes pendientes de guardar.",
                    "Sin reportes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        gestorArchivos.guardarReporteDiario(sistema);
        JOptionPane.showMessageDialog(this,
                "Reporte guardado correctamente en la carpeta del proyecto.",
                "Reporte guardado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void nuevaSesion() {
        int resp = JOptionPane.showConfirmDialog(this,
                "¿Guardar el reporte antes de reiniciar?",
                "Nueva sesión", JOptionPane.YES_NO_CANCEL_OPTION);
        if (resp == JOptionPane.CANCEL_OPTION) return;
        if (resp == JOptionPane.YES_OPTION) guardarReporte();

        dispose();
        new VentanaPrincipal(new SistemaEstadio(14, 14)).setVisible(true);
    }
}