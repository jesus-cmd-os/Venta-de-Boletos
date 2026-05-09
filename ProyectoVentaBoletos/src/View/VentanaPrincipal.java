package View;

import service.SistemaEstadio;
import Persistance.GestorArchivos;
import model.Boleto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedList;

/**
 * Ventana Principal del Sistema de Venta de Boletos.
 * Diseño mejorado con 4 paneles: categoría, asientos, historial y acciones.
 */
public class VentanaPrincipal extends JFrame {

    // ── Backend ──────────────────────────────────────────────────────────────
    private SistemaEstadio sistema;
    private GestorArchivos gestorArchivos;

    // ── Estado de selección ──────────────────────────────────────────────────
    private int filaSeleccionada = -1;
    private int colSeleccionada  = -1;

    // ── Componentes GUI ──────────────────────────────────────────────────────
    private JButton[][]        botonesAsientos;
    private JComboBox<String>  cbCategorias;
    private JLabel             lblPrecioUnitario;
    private JLabel             lblAsientoSeleccionado;
    private JTextArea          areaHistorial;
    private JLabel             lblTotalVendidos;
    private JLabel             lblTotalIngresos;

    // ── Colores del sistema ──────────────────────────────────────────────────
    private static final Color COLOR_DISPONIBLE  = new Color(46, 204, 113);   // verde
    private static final Color COLOR_OCUPADO     = new Color(231, 76, 60);    // rojo
    private static final Color COLOR_SELECCIONADO = new Color(241, 196, 15);  // amarillo
    private static final Color COLOR_FONDO       = new Color(44, 62, 80);     // azul oscuro
    private static final Color COLOR_PANEL       = new Color(52, 73, 94);     // azul medio
    private static final Color COLOR_ACENTO      = new Color(52, 152, 219);   // azul claro

    // ── Contadores locales ───────────────────────────────────────────────────
    private int    totalBoletosVendidos = 0;
    private double totalIngresos        = 0.0;

    // ════════════════════════════════════════════════════════════════════════
    public VentanaPrincipal(SistemaEstadio sistema) {
        this.sistema        = sistema;
        this.gestorArchivos = new GestorArchivos();

        configurarVentana();
        construirUI();
    }

    // ── Configuración base de la ventana ─────────────────────────────────────
    private void configurarVentana() {
        setTitle("🏟  Sistema de Venta de Boletos — Estadio");
        setSize(1100, 750);
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(10, 10));
    }

    // ── Construcción general de la UI ─────────────────────────────────────────
    private void construirUI() {
        add(crearPanelTitulo(),    BorderLayout.NORTH);
        add(crearPanelCentral(),   BorderLayout.CENTER);
        add(crearPanelAcciones(),  BorderLayout.SOUTH);
    }

    // ════════════════════════════════════════════════════════════════════════
    // PANEL NORTE — Título
    // ════════════════════════════════════════════════════════════════════════
    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(15, 20, 5, 20));

        JLabel titulo = new JLabel("🏟  SISTEMA DE VENTA DE BOLETOS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Selecciona categoría → elige asiento → confirma compra", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(149, 165, 166));

        panel.add(titulo,    BorderLayout.CENTER);
        panel.add(subtitulo, BorderLayout.SOUTH);
        return panel;
    }

    // ════════════════════════════════════════════════════════════════════════
    // PANEL CENTRAL — divide en izquierda (asientos) y derecha (control)
    // ════════════════════════════════════════════════════════════════════════
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(5, 15, 5, 15));

        panel.add(crearPanelAsientos(),  BorderLayout.CENTER);
        panel.add(crearPanelDerecho(),   BorderLayout.EAST);
        return panel;
    }

    // ── Panel de asientos (matriz de botones) ────────────────────────────────
    private JPanel crearPanelAsientos() {
        boolean[][] matriz = sistema.getMatrizAsientos();
        int filas    = matriz.length;
        int columnas = matriz[0].length;

        JPanel contenedor = new JPanel(new BorderLayout(0, 8));
        contenedor.setBackground(COLOR_PANEL);
        contenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO, 1),
                new EmptyBorder(10, 10, 10, 10)));

        // Leyenda de colores
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        leyenda.setBackground(COLOR_PANEL);
        leyenda.add(crearItemLeyenda("Disponible",   COLOR_DISPONIBLE));
        leyenda.add(crearItemLeyenda("Ocupado",      COLOR_OCUPADO));
        leyenda.add(crearItemLeyenda("Seleccionado", COLOR_SELECCIONADO));

        // Título del panel
        JLabel titulo = new JLabel("Paso 2: Selecciona tu Asiento", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setForeground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PANEL);
        header.add(titulo,  BorderLayout.NORTH);
        header.add(leyenda, BorderLayout.SOUTH);

        // Matriz de botones
        JPanel gridAsientos = new JPanel(new GridLayout(filas, columnas, 4, 4));
        gridAsientos.setBackground(COLOR_PANEL);
        botonesAsientos = new JButton[filas][columnas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                JButton btn = crearBotonAsiento(i, j, matriz[i][j]);
                botonesAsientos[i][j] = btn;
                gridAsientos.add(btn);
            }
        }

        contenedor.add(header,       BorderLayout.NORTH);
        contenedor.add(gridAsientos, BorderLayout.CENTER);
        return contenedor;
    }

    private JButton crearBotonAsiento(int fila, int col, boolean ocupado) {
        JButton btn = new JButton("<html><center>F" + fila + "<br>A" + col + "</center></html>");
        btn.setFont(new Font("Arial", Font.PLAIN, 9));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (ocupado) {
            btn.setBackground(COLOR_OCUPADO);
            btn.setEnabled(false);
        } else {
            btn.setBackground(COLOR_DISPONIBLE);
            btn.addActionListener(e -> seleccionarAsiento(fila, col, btn));
        }
        return btn;
    }

    private JLabel crearItemLeyenda(String texto, Color color) {
        JLabel lbl = new JLabel("  " + texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(color);
        lbl.setBorder(new EmptyBorder(3, 8, 3, 8));
        return lbl;
    }

    // ── Panel derecho — categoría + info + historial ─────────────────────────
    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);
        panel.setPreferredSize(new Dimension(280, 0));

        panel.add(crearPanelCategoria());
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearPanelResumen());
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearPanelHistorial());
        return panel;
    }

    private JPanel crearPanelCategoria() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 8));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO, 1),
                new EmptyBorder(12, 12, 12, 12)));

        JLabel titulo = new JLabel("Paso 1: Categoría");
        titulo.setFont(new Font("Arial", Font.BOLD, 13));
        titulo.setForeground(Color.WHITE);

        cbCategorias = new JComboBox<>(new String[]{"VIP", "Preferencial", "General"});
        cbCategorias.setFont(new Font("Arial", Font.PLAIN, 13));
        cbCategorias.addActionListener(e -> actualizarPrecio());

        lblPrecioUnitario = new JLabel("Precio: $1,500.00");
        lblPrecioUnitario.setFont(new Font("Arial", Font.BOLD, 14));
        lblPrecioUnitario.setForeground(COLOR_ACENTO);

        lblAsientoSeleccionado = new JLabel("Asiento: ninguno seleccionado");
        lblAsientoSeleccionado.setFont(new Font("Arial", Font.PLAIN, 11));
        lblAsientoSeleccionado.setForeground(new Color(149, 165, 166));

        panel.add(titulo);
        panel.add(cbCategorias);
        panel.add(lblPrecioUnitario);
        panel.add(lblAsientoSeleccionado);
        return panel;
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO, 1),
                new EmptyBorder(10, 12, 10, 12)));

        lblTotalVendidos = new JLabel("Boletos vendidos: 0");
        lblTotalVendidos.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalVendidos.setForeground(COLOR_DISPONIBLE);

        lblTotalIngresos = new JLabel("Ingresos totales: $0.00");
        lblTotalIngresos.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalIngresos.setForeground(COLOR_DISPONIBLE);

        panel.add(lblTotalVendidos);
        panel.add(lblTotalIngresos);
        return panel;
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACENTO, 1),
                new EmptyBorder(10, 12, 10, 12)));

        JLabel titulo = new JLabel("Historial de Ventas");
        titulo.setFont(new Font("Arial", Font.BOLD, 13));
        titulo.setForeground(Color.WHITE);

        areaHistorial = new JTextArea();
        areaHistorial.setEditable(false);
        areaHistorial.setFont(new Font("Monospaced", Font.PLAIN, 10));
        areaHistorial.setBackground(new Color(30, 39, 46));
        areaHistorial.setForeground(new Color(200, 200, 200));
        areaHistorial.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane scroll = new JScrollPane(areaHistorial);
        scroll.setPreferredSize(new Dimension(260, 200));
        scroll.setBorder(null);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        return panel;
    }

    // ════════════════════════════════════════════════════════════════════════
    // PANEL SUR — Botones de acción
    // ════════════════════════════════════════════════════════════════════════
    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_ACENTO));

        JButton btnComprar = crearBotonAccion("✅  Confirmar Compra", new Color(39, 174, 96));
        JButton btnGuardar = crearBotonAccion("💾  Guardar Reporte", new Color(41, 128, 185));
        JButton btnLimpiar = crearBotonAccion("🔄  Nueva Sesión",    new Color(127, 140, 141));

        btnComprar.addActionListener(e -> confirmarCompra());
        btnGuardar.addActionListener(e -> guardarReporte());
        btnLimpiar.addActionListener(e -> nuevaSesion());

        panel.add(btnComprar);
        panel.add(btnGuardar);
        panel.add(btnLimpiar);
        return panel;
    }

    private JButton crearBotonAccion(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ════════════════════════════════════════════════════════════════════════
    // LÓGICA DE EVENTOS
    // ════════════════════════════════════════════════════════════════════════

    private void seleccionarAsiento(int fila, int col, JButton btn) {
        // Limpiar selección anterior
        if (filaSeleccionada != -1 && !sistema.getMatrizAsientos()[filaSeleccionada][colSeleccionada]) {
            botonesAsientos[filaSeleccionada][colSeleccionada].setBackground(COLOR_DISPONIBLE);
        }
        filaSeleccionada = fila;
        colSeleccionada  = col;
        btn.setBackground(COLOR_SELECCIONADO);
        lblAsientoSeleccionado.setText("Asiento: Fila " + fila + " — Col " + col);
    }

    private void actualizarPrecio() {
        String cat   = (String) cbCategorias.getSelectedItem();
        double precio = sistema.getMapaPrecios().getOrDefault(cat, 0.0);
        lblPrecioUnitario.setText(String.format("Precio: $%,.2f", precio));
    }

    private void confirmarCompra() {
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Debes seleccionar un asiento antes de confirmar.",
                    "Sin asiento seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cat    = (String) cbCategorias.getSelectedItem();
        double precio = sistema.getMapaPrecios().getOrDefault(cat, 0.0);
        String id     = "BOL-" + (System.currentTimeMillis() % 100000);

        // Confirmar con el usuario
        int respuesta = JOptionPane.showConfirmDialog(this,
                String.format("¿Confirmar compra?\n\nCategoría : %s\nAsiento   : Fila %d, Col %d\nPrecio    : $%,.2f",
                        cat, filaSeleccionada, colSeleccionada, precio),
                "Confirmar Compra", JOptionPane.YES_NO_OPTION);

        if (respuesta != JOptionPane.YES_OPTION) return;

        boolean exito = sistema.comprarBoleto(id, cat, filaSeleccionada, colSeleccionada);

        if (exito) {
            // Actualizar botón en la matriz
            botonesAsientos[filaSeleccionada][colSeleccionada].setBackground(COLOR_OCUPADO);
            botonesAsientos[filaSeleccionada][colSeleccionada].setEnabled(false);

            // Actualizar contadores
            totalBoletosVendidos++;
            totalIngresos += precio;
            lblTotalVendidos.setText("Boletos vendidos: " + totalBoletosVendidos);
            lblTotalIngresos.setText(String.format("Ingresos totales: $%,.2f", totalIngresos));

            // Agregar al historial visual
            areaHistorial.append(String.format("[%s] F%d-A%d $%,.0f%n",
                    cat.substring(0, 3).toUpperCase(), filaSeleccionada, colSeleccionada, precio));

            // Resetear selección
            filaSeleccionada = -1;
            colSeleccionada  = -1;
            lblAsientoSeleccionado.setText("Asiento: ninguno seleccionado");

            JOptionPane.showMessageDialog(this,
                    "¡Compra exitosa!\nID: " + id,
                    "Compra Confirmada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "El asiento ya está ocupado.",
                    "Asiento no disponible", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarReporte() {
        if (sistema.getTotalReportesEnCola() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay ventas pendientes de guardar.",
                    "Sin reportes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        gestorArchivos.guardarReporteDiario(sistema);
        JOptionPane.showMessageDialog(this,
                "Reporte guardado correctamente.\nRevisa la carpeta del proyecto.",
                "Reporte Guardado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void nuevaSesion() {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Guardar el reporte antes de reiniciar?",
                "Nueva Sesión", JOptionPane.YES_NO_CANCEL_OPTION);

        if (respuesta == JOptionPane.CANCEL_OPTION) return;
        if (respuesta == JOptionPane.YES_OPTION) guardarReporte();

        // Reiniciar backend con nueva instancia 10x10
        sistema = new SistemaEstadio(10, 10);
        totalBoletosVendidos = 0;
        totalIngresos        = 0.0;
        filaSeleccionada     = -1;
        colSeleccionada      = -1;

        // Refrescar UI
        getContentPane().removeAll();
        construirUI();
        revalidate();
        repaint();
    }
}