package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel lateral derecho.
 * Muestra en tiempo real:
 *   - Lista de asientos seleccionados (categoría + precio)
 *   - Subtotal de la selección actual
 *   - Resumen acumulado de ventas del día
 *   - Historial de compras confirmadas
 */
public class PanelControl extends JPanel {

    // ── Colores ───────────────────────────────────────────────────────────────

    // ── Componentes — sección selección ──────────────────────────────────────
    private JLabel  lblContador;
    private JPanel  panelItems;      // lista dinámica de asientos seleccionados
    private JLabel  lblSubtotal;

    // ── Componentes — sección ventas del día ─────────────────────────────────
    private JLabel  lblVendidos;
    private JLabel  lblIngresos;

    // ── Componentes — historial ───────────────────────────────────────────────
    private JTextArea areaHistorial;

    // ─────────────────────────────────────────────────────────────────────────
    public PanelControl() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(EstiloUI.C_FONDO);
        setPreferredSize(new Dimension(260, 0));

        add(crearCardSeleccion());
        add(Box.createVerticalStrut(8));
        add(crearCardVentas());
        add(Box.createVerticalStrut(8));
        add(crearCardHistorial());
        add(Box.createVerticalGlue());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CARD 1 — Selección en tiempo real
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel crearCardSeleccion() {
        JPanel card = baseCard();
        card.setLayout(new BorderLayout(0, 6));

        // Título
        lblContador = new JLabel("Seleccionados: 0 / 6");
        lblContador.setFont(EstiloUI.fuente(Font.BOLD, 12));
        lblContador.setForeground(Color.WHITE);

        // Lista de ítems (dinámica)
        panelItems = new JPanel();
        panelItems.setLayout(new BoxLayout(panelItems, BoxLayout.Y_AXIS));
        panelItems.setBackground(new Color(22, 28, 36));

        JScrollPane scroll = new JScrollPane(panelItems);
        scroll.setPreferredSize(new Dimension(240, 145));
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.C_BORDE));
        scroll.getViewport().setBackground(new Color(22, 28, 36));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Subtotal
        JPanel footerSel = new JPanel(new BorderLayout());
        footerSel.setBackground(EstiloUI.C_PANEL);
        footerSel.setBorder(new MatteBorder(1, 0, 0, 0, EstiloUI.C_BORDE));

        lblSubtotal = new JLabel("Subtotal: $0");
        lblSubtotal.setFont(EstiloUI.fuente(Font.BOLD, 13));
        lblSubtotal.setForeground(EstiloUI.C_AMARILLO);
        lblSubtotal.setBorder(new EmptyBorder(6, 0, 0, 0));

        footerSel.add(lblSubtotal, BorderLayout.CENTER);

        card.add(lblContador, BorderLayout.NORTH);
        card.add(scroll,      BorderLayout.CENTER);
        card.add(footerSel,   BorderLayout.SOUTH);

        mostrarVacio();
        return card;
    }

    // ── Render de la lista de seleccionados ───────────────────────────────────
    /**
     * Llamado desde PanelEstadio cada vez que cambia la selección.
     * @param items  Lista de SeatItem con seatId, tipo y precio
     */
    public void actualizarSeleccion(List<SeatItem> items) {
        panelItems.removeAll();

        int total = 0;

        if (items == null || items.isEmpty()) {
            mostrarVacio();
        } else {
            for (SeatItem item : items) {
                panelItems.add(crearFila(item));
                panelItems.add(Box.createVerticalStrut(2));
                total += item.precio;
            }
        }

        lblContador.setText("Seleccionados: " + (items == null ? 0 : items.size()) + " / 6");
        lblSubtotal.setText("Subtotal: $" + String.format("%,.0f", (double) total));

        panelItems.revalidate();
        panelItems.repaint();
    }

    private void mostrarVacio() {
        panelItems.removeAll();
        JLabel empty = new JLabel("  Ningún asiento seleccionado");
        empty.setFont(EstiloUI.fuente(Font.ITALIC, 11));
        empty.setForeground(new Color(100, 115, 130));
        empty.setBorder(new EmptyBorder(8, 4, 8, 4));
        panelItems.add(empty);
        panelItems.revalidate();
        panelItems.repaint();
    }

    private JPanel crearFila(SeatItem item) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setBackground(new Color(32, 42, 54));
        row.setBorder(new EmptyBorder(4, 6, 4, 6));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        // Punto de color según categoría
        JLabel dot = new JLabel("●");
        dot.setFont(EstiloUI.fuente(Font.PLAIN, 10));
        dot.setForeground(colorCategoria(item.tipo));

        // Texto del asiento
        JLabel info = new JLabel(" " + item.seatId + "  (" + abrevTipo(item.tipo) + ")");
        info.setFont(EstiloUI.fuente(Font.PLAIN, 11));
        info.setForeground(Color.WHITE);

        // Precio
        JLabel precio = new JLabel("$" + String.format("%,.0f", (double) item.precio));
        precio.setFont(EstiloUI.fuente(Font.BOLD, 11));
        precio.setForeground(new Color(100, 200, 255));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izq.setBackground(new Color(32, 42, 54));
        izq.add(dot);
        izq.add(info);

        row.add(izq,    BorderLayout.CENTER);
        row.add(precio, BorderLayout.EAST);
        return row;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CARD 2 — Resumen de ventas del día
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel crearCardVentas() {
        JPanel card = baseCard();
        card.setLayout(new GridLayout(3, 1, 0, 4));

        JLabel titulo = new JLabel("Ventas del día");
        titulo.setFont(EstiloUI.fuente(Font.BOLD, 12));
        titulo.setForeground(Color.WHITE);

        lblVendidos = new JLabel("Boletos vendidos: 0");
        lblVendidos.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblVendidos.setForeground(EstiloUI.C_VERDE);

        lblIngresos = new JLabel("Ingresos totales: $0.00");
        lblIngresos.setFont(EstiloUI.fuente(Font.PLAIN, 12));
        lblIngresos.setForeground(EstiloUI.C_VERDE);

        card.add(titulo);
        card.add(lblVendidos);
        card.add(lblIngresos);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CARD 3 — Historial de compras confirmadas
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel crearCardHistorial() {
        JPanel card = baseCard();
        card.setLayout(new BorderLayout(0, 6));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Historial de compras");
        titulo.setFont(EstiloUI.fuente(Font.BOLD, 12));
        titulo.setForeground(Color.WHITE);

        areaHistorial = new JTextArea();
        areaHistorial.setEditable(false);
        areaHistorial.setFont(EstiloUI.fuenteMono(Font.PLAIN, 10));
        areaHistorial.setBackground(new Color(22, 28, 36));
        areaHistorial.setForeground(new Color(180, 195, 210));
        areaHistorial.setBorder(new EmptyBorder(4, 6, 4, 6));

        JScrollPane scroll = new JScrollPane(areaHistorial);
        scroll.setPreferredSize(new Dimension(240, 130));
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.C_BORDE));
        scroll.getViewport().setBackground(new Color(22, 28, 36));

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ── Card base ─────────────────────────────────────────────────────────────
    private JPanel baseCard() {
        JPanel c = new JPanel();
        c.setBackground(EstiloUI.C_PANEL);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloUI.C_BORDE, 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(260, Integer.MAX_VALUE));
        return c;
    }

    // ── Métodos públicos para VentanaPrincipal ────────────────────────────────
    public void actualizarResumen(int boletos, double ingresos) {
        lblVendidos.setText("Boletos vendidos: " + boletos);
        lblIngresos.setText(String.format("Ingresos totales: $%,.2f", ingresos));
    }

    public void agregarHistorial(String texto) {
        areaHistorial.append(texto + "\n");
        // Auto-scroll al final
        areaHistorial.setCaretPosition(areaHistorial.getDocument().getLength());
    }

    // ── Helpers privados ──────────────────────────────────────────────────────
    private Color colorCategoria(String tipo) {
        switch (tipo) {
            case "VIP":          return EstiloUI.C_VIP;
            case "Preferencial": return EstiloUI.C_PREF;
            default:             return EstiloUI.C_GEN;
        }
    }

    private String abrevTipo(String tipo) {
        switch (tipo) {
            case "VIP":          return "VIP";
            case "Preferencial": return "Pref";
            default:             return "Gral";
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CLASE INTERNA — Datos de un asiento seleccionado
    // ══════════════════════════════════════════════════════════════════════════
    public static class SeatItem {
        public final String seatId;
        public final String tipo;
        public final int    precio;

        public SeatItem(String seatId, String tipo, int precio) {
            this.seatId = seatId;
            this.tipo   = tipo;
            this.precio = precio;
        }
    }
}