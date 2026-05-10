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

// Ventana principal de la aplicación. Hereda de JFrame y actúa como el punto
// de entrada visual del sistema. Coordina el panel del estadio, el panel de
// control lateral y los botones de acción de la parte inferior.
public class VentanaPrincipal extends JFrame {

    // Referencia al sistema de negocio que maneja asientos, precios y reportes
    private final SistemaEstadio sistema;

    // Gestor encargado de escribir los reportes en archivos del disco
    private final GestorArchivos gestorArchivos = new GestorArchivos();

    // Panel visual con el mapa de asientos del estadio
    private PanelEstadio panelEstadio;

    // Panel lateral con leyenda, selección activa, ventas e historial
    private PanelControl panelControl;

    // Contadores acumulados de la sesión actual
    private int    totalVendidos = 0;
    private double totalIngresos = 0.0;


    // Constructor: recibe el sistema ya inicializado y construye la interfaz
    public VentanaPrincipal(SistemaEstadio sistema) {
        this.sistema = sistema;
        configurarVentana();
        construirUI();
    }

    // Establece las propiedades básicas de la ventana: título, tamaño,
    // posición en pantalla, color de fondo y layout principal
    private void configurarVentana() {
        setTitle("Sistema de Venta de Boletos — Estadio");

        // Calcula el tamaño adecuado según la resolución del sistema operativo
        Dimension tam = EstiloUI.tamanoVentana();
        setSize(tam);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null); // Centra la ventana en pantalla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(EstiloUI.C_FONDO);
        setLayout(new BorderLayout(10, 10));
    }

    // Ensambla las tres regiones principales de la ventana:
    // título arriba, mapa + panel lateral al centro, botones abajo
    private void construirUI() {
        add(crearTitulo(),        BorderLayout.NORTH);
        add(crearCentro(),        BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
    }


    // SECCIONES DE LA INTERFAZ


    // Crea el encabezado con el nombre del sistema y una instrucción breve
    private JPanel crearTitulo() {
        JPanel p = EstiloUI.panel(new GridLayout(2, 1, 0, 2), EstiloUI.C_FONDO);
        p.setBorder(new EmptyBorder(14, 16, 6, 16));

        JLabel t = EstiloUI.label("ESTADIO — SISTEMA DE VENTA DE BOLETOS",
                Font.BOLD, 20, Color.WHITE);
        t.setHorizontalAlignment(SwingConstants.CENTER);

        // Subtítulo informativo para guiar al usuario
        JLabel s = EstiloUI.label("Selecciona hasta 6 asientos y confirma la compra",
                Font.PLAIN, 12, new Color(120, 135, 150));
        s.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(t);
        p.add(s);
        return p;
    }

    // Crea la zona central: el mapa del estadio a la izquierda y el panel
    // de control lateral a la derecha. También conecta ambos paneles entre sí.
    private JPanel crearCentro() {
        panelControl = new PanelControl();
        panelEstadio = new PanelEstadio(sistema);
        panelEstadio.setPanelControl(panelControl); // El estadio notifica al control al hacer clic

        JPanel p = EstiloUI.panel(new BorderLayout(10, 0), EstiloUI.C_FONDO);
        p.setBorder(new EmptyBorder(0, 12, 0, 12));
        p.add(panelEstadio, BorderLayout.CENTER);
        p.add(panelControl, BorderLayout.EAST);
        return p;
    }

    // Crea la barra inferior con los cuatro botones de acción principales
    private JPanel crearPanelInferior() {
        JPanel p = EstiloUI.panel(new FlowLayout(FlowLayout.CENTER, 20, 10), EstiloUI.C_FONDO);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, EstiloUI.C_BORDE));

        JButton btnComprar = EstiloUI.boton("Confirmar Compra",   new Color(24,  95, 165), 180, 40);
        JButton btnPrecios = EstiloUI.boton("Actualizar Precios", new Color(142, 68, 173), 180, 40);
        JButton btnGuardar = EstiloUI.boton("Guardar Reporte",    new Color(15, 110,  86), 180, 40);
        JButton btnNueva   = EstiloUI.boton("Nueva Sesion",       new Color(60,  70,  85), 180, 40);

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


    // ACCIONES DE LOS BOTONES


    // Valida la selección actual, muestra el desglose al usuario para confirmar,
    // registra cada boleto en el sistema y actualiza el panel de control
    private void confirmarCompra() {
        List<Point> seleccionados = panelEstadio.getAsientosSeleccionados();

        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona al menos un asiento.",
                    "Sin seleccion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Construir el detalle de la compra para mostrarlo en el diálogo
        StringBuilder detalle = new StringBuilder("Confirmar compra de "
                + seleccionados.size() + " boleto(s):\n\n");
        double totalCompra = 0;

        for (Point p : seleccionados) {
            String cat    = sistema.obtenerCategoriaAsiento(p.x, p.y);
            double precio = sistema.obtenerPrecioAsiento(p.x, p.y);
            detalle.append(String.format("  %s  F%d-A%d  $%,.0f\n", cat, p.x, p.y, precio));
            totalCompra += precio;
        }
        detalle.append(String.format("\n  Total: $%,.2f", totalCompra));

        int resp = JOptionPane.showConfirmDialog(this, detalle.toString(),
                "Confirmar compra", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resp != JOptionPane.YES_OPTION) return;

        // Registrar cada boleto con un ID único y actualizar los contadores
        for (Point p : new ArrayList<>(seleccionados)) {
            String cat    = sistema.obtenerCategoriaAsiento(p.x, p.y);
            double precio = sistema.obtenerPrecioAsiento(p.x, p.y);
            String id     = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            sistema.comprarBoleto(id, cat, p.x, p.y);
            totalVendidos++;
            totalIngresos += precio;

            panelControl.agregarHistorial(
                    String.format("%s | %s F%d-A%d | $%,.0f", id, cat, p.x, p.y, precio));
        }

        panelControl.actualizarResumen(totalVendidos, totalIngresos);
        panelEstadio.marcarOcupados(new ArrayList<>(seleccionados)); // Pintar asientos como ocupados

        JOptionPane.showMessageDialog(this,
                String.format("Compra exitosa! %d boleto(s) - Total: $%,.2f",
                        seleccionados.size(), totalCompra),
                "Compra confirmada", JOptionPane.INFORMATION_MESSAGE);
    }

    // Abre un diálogo con un campo por categoría para modificar los precios.
    // Valida que los valores sean numéricos y mayores a cero antes de aplicar.
    private void actualizarPrecios() {
        JPanel panel = EstiloUI.panel(new GridLayout(4, 2, 8, 10), UIManager.getColor("Panel.background"));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        panel.add(new JLabel("Categoria"));
        panel.add(new JLabel("Nuevo precio ($)"));

        // Prellenar los campos con los precios actuales del sistema
        JTextField tfVIP  = new JTextField(
                String.valueOf(sistema.getMapaPrecios().getOrDefault("VIP",         1500.0).intValue()));
        JTextField tfPref = new JTextField(
                String.valueOf(sistema.getMapaPrecios().getOrDefault("Preferencial",  800.0).intValue()));
        JTextField tfGen  = new JTextField(
                String.valueOf(sistema.getMapaPrecios().getOrDefault("General",       400.0).intValue()));

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
                        "Precio invalido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            sistema.actualizarPrecio("VIP",          vip);
            sistema.actualizarPrecio("Preferencial", pref);
            sistema.actualizarPrecio("General",      gen);

            JOptionPane.showMessageDialog(this,
                    String.format("Precios actualizados:\n  VIP: $%,.0f\n  Preferencial: $%,.0f\n  General: $%,.0f",
                            vip, pref, gen),
                    "Precios actualizados", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            // El usuario ingresó texto en lugar de un número
            JOptionPane.showMessageDialog(this, "Ingresa solo numeros validos.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Verifica que haya reportes pendientes y los escribe en disco.
    // Si no hay nada que guardar, avisa al usuario en lugar de fallar silenciosamente.
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

    // Ofrece guardar el reporte antes de reiniciar. Si el usuario acepta,
    // cierra la ventana actual y abre una nueva sesión con el estadio limpio.
    private void nuevaSesion() {
        int resp = JOptionPane.showConfirmDialog(this,
                "Guardar el reporte antes de reiniciar?",
                "Nueva sesion", JOptionPane.YES_NO_CANCEL_OPTION);
        if (resp == JOptionPane.CANCEL_OPTION) return;
        if (resp == JOptionPane.YES_OPTION) guardarReporte();

        dispose(); // Cierra la ventana actual antes de crear la nueva
        SwingUtilities.invokeLater(() ->
                new VentanaPrincipal(new SistemaEstadio(14, 14)).setVisible(true));
    }
}