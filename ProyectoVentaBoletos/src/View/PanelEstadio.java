package View;

import model.TipoZona;
import service.SistemaEstadio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel que dibuja el estadio completo con 4 tribunas y cancha central.
 * Notifica a PanelControl en tiempo real cada vez que el usuario
 * selecciona o deselecciona un asiento.
 */
public class PanelEstadio extends JPanel {

    // ── Backend ───────────────────────────────────────────────────────────────
    private final SistemaEstadio sistema;

    // ── Referencia al panel de control (para actualización en tiempo real) ────
    private PanelControl panelControl;

    // ── Matriz visual de botones ──────────────────────────────────────────────
    private JButton[][] botonesAsientos;

    // ── Selección múltiple ────────────────────────────────────────────────────
    private final List<Point> asientosSeleccionados = new ArrayList<>();
    private static final int MAX_BOLETOS = 6;

    // ── Colores ───────────────────────────────────────────────────────────────
    private static final Color C_VIP     = new Color(24,  95, 165);
    private static final Color C_PREF    = new Color(15, 110,  86);
    private static final Color C_GEN     = new Color(95,  94,  90);
    private static final Color C_OCUPADO = new Color(123, 45,  45);
    private static final Color C_SELEC   = new Color(241, 196,  15);
    private static final Color C_PANEL   = new Color(38,  48,  60);
    private static final Color C_ACENTO  = new Color(52, 152, 219);

    // ── Tamaño de cada botón ──────────────────────────────────────────────────
    private static final int SW  = 24;
    private static final int SH  = 18;
    private static final int GAP = 3;

    // ── Límites del bloque central (cancha) ───────────────────────────────────
    private final int filaCancha1;
    private final int filaCancha2;
    private final int colCancha1;
    private final int colCancha2;

    // ─────────────────────────────────────────────────────────────────────────
    public PanelEstadio(SistemaEstadio sistema) {
        this.sistema = sistema;

        boolean[][] m = sistema.getMatrizAsientos();
        int filas = m.length;
        int cols  = m[0].length;

        this.filaCancha1 = filas / 2 - 1;
        this.filaCancha2 = filas / 2;
        this.colCancha1  = cols  / 2 - 1;
        this.colCancha2  = cols  / 2;

        this.botonesAsientos = new JButton[filas][cols];
        construirPanel();
    }

    /** Inyecta la referencia al PanelControl para notificaciones en tiempo real */
    public void setPanelControl(PanelControl panelControl) {
        this.panelControl = panelControl;
    }

    // ── Construcción principal ────────────────────────────────────────────────
    private void construirPanel() {
        setLayout(new BorderLayout(GAP, GAP));
        setBackground(C_PANEL);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_ACENTO, 2),
                new EmptyBorder(12, 12, 12, 12)));

        add(crearTribunaNorte(), BorderLayout.NORTH);
        add(crearTribunaOeste(), BorderLayout.WEST);
        add(crearCancha(),       BorderLayout.CENTER);
        add(crearTribunaEste(),  BorderLayout.EAST);
        add(crearTribunaSur(),   BorderLayout.SOUTH);
    }

    // ── Cancha ────────────────────────────────────────────────────────────────
    private JPanel crearCancha() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(39, 174, 96));
        p.setBorder(BorderFactory.createLineBorder(new Color(22, 120, 60), 4));
        p.setPreferredSize(new Dimension(180, 120));
        JLabel lbl = new JLabel("CANCHA", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 22));
        lbl.setForeground(new Color(200, 240, 200));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    // ── Tribuna Norte ─────────────────────────────────────────────────────────
    private JPanel crearTribunaNorte() {
        int colTot  = sistema.getMatrizAsientos()[0].length;
        int filaFin = filaCancha1 - 1;
        int filas   = filaFin + 1;

        JPanel wrap = new JPanel(new BorderLayout(0, 2));
        wrap.setBackground(C_PANEL);

        JLabel lbl = new JLabel("Tribuna Norte", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 10));
        lbl.setForeground(new Color(120, 135, 150));

        JPanel grid = new JPanel(new GridLayout(filas, colTot, GAP, GAP));
        grid.setBackground(C_PANEL);

        for (int i = 0; i <= filaFin; i++) {
            for (int j = 0; j < colTot; j++) {
                JButton btn = crearBoton(i, j);
                botonesAsientos[i][j] = btn;
                grid.add(btn);
            }
        }

        wrap.add(lbl,  BorderLayout.NORTH);
        wrap.add(grid, BorderLayout.CENTER);
        return wrap;
    }

    // ── Tribuna Sur ───────────────────────────────────────────────────────────
    private JPanel crearTribunaSur() {
        int totalFilas = sistema.getMatrizAsientos().length;
        int colTot     = sistema.getMatrizAsientos()[0].length;
        int filaIni    = filaCancha2 + 1;
        int filas      = totalFilas - filaIni;

        JPanel wrap = new JPanel(new BorderLayout(0, 2));
        wrap.setBackground(C_PANEL);

        JLabel lbl = new JLabel("Tribuna Sur", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 10));
        lbl.setForeground(new Color(120, 135, 150));

        JPanel grid = new JPanel(new GridLayout(filas, colTot, GAP, GAP));
        grid.setBackground(C_PANEL);

        for (int i = filaIni; i < totalFilas; i++) {
            for (int j = 0; j < colTot; j++) {
                JButton btn = crearBoton(i, j);
                botonesAsientos[i][j] = btn;
                grid.add(btn);
            }
        }

        wrap.add(grid, BorderLayout.CENTER);
        wrap.add(lbl,  BorderLayout.SOUTH);
        return wrap;
    }

    // ── Tribuna Oeste ─────────────────────────────────────────────────────────
    private JPanel crearTribunaOeste() {
        int filaIni = filaCancha1;
        int filaFin = filaCancha2;
        int colFin  = colCancha1 - 1;
        int filas   = filaFin - filaIni + 1;
        int cols    = colFin + 1;

        JPanel wrap = new JPanel(new BorderLayout(2, 0));
        wrap.setBackground(C_PANEL);

        JLabel lbl = new JLabel("Oeste", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 10));
        lbl.setForeground(new Color(120, 135, 150));

        JPanel grid = new JPanel(new GridLayout(filas, cols, GAP, GAP));
        grid.setBackground(C_PANEL);

        for (int i = filaIni; i <= filaFin; i++) {
            for (int j = 0; j <= colFin; j++) {
                JButton btn = crearBoton(i, j);
                botonesAsientos[i][j] = btn;
                grid.add(btn);
            }
        }

        wrap.add(lbl,  BorderLayout.WEST);
        wrap.add(grid, BorderLayout.CENTER);
        return wrap;
    }

    // ── Tribuna Este ──────────────────────────────────────────────────────────
    private JPanel crearTribunaEste() {
        int filaIni = filaCancha1;
        int filaFin = filaCancha2;
        int colIni  = colCancha2 + 1;
        int colTot  = sistema.getMatrizAsientos()[0].length;
        int filas   = filaFin - filaIni + 1;
        int cols    = colTot - colIni;

        JPanel wrap = new JPanel(new BorderLayout(2, 0));
        wrap.setBackground(C_PANEL);

        JLabel lbl = new JLabel("Este", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 10));
        lbl.setForeground(new Color(120, 135, 150));

        JPanel grid = new JPanel(new GridLayout(filas, cols, GAP, GAP));
        grid.setBackground(C_PANEL);

        for (int i = filaIni; i <= filaFin; i++) {
            for (int j = colIni; j < colTot; j++) {
                JButton btn = crearBoton(i, j);
                botonesAsientos[i][j] = btn;
                grid.add(btn);
            }
        }

        wrap.add(grid, BorderLayout.CENTER);
        wrap.add(lbl,  BorderLayout.EAST);
        return wrap;
    }

    // ── Crear botón individual ────────────────────────────────────────────────
    private JButton crearBoton(int fila, int col) {
        boolean  ocupado = sistema.getMatrizAsientos()[fila][col];
        TipoZona zona    = sistema.getMatrizZonas()[fila][col];
        String   cat     = zonaToString(zona);
        double   precio  = sistema.getMapaPrecios().getOrDefault(cat, 0.0);

        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(SW, SH));
        btn.setMinimumSize(new Dimension(SW, SH));
        btn.setMaximumSize(new Dimension(SW, SH));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setToolTipText(cat + "  F" + fila + "-A" + col + "  $" + String.format("%.0f", precio));

        if (ocupado) {
            btn.setBackground(C_OCUPADO);
            btn.setEnabled(false);
        } else {
            btn.setBackground(colorZona(zona));
            btn.addActionListener(e -> seleccionarAsiento(fila, col, btn));
        }
        return btn;
    }

    // ── Lógica de selección ───────────────────────────────────────────────────
    private void seleccionarAsiento(int fila, int col, JButton btn) {
        Point p = new Point(fila, col);

        if (asientosSeleccionados.contains(p)) {
            asientosSeleccionados.remove(p);
            btn.setBackground(colorZona(sistema.getMatrizZonas()[fila][col]));
        } else {
            if (asientosSeleccionados.size() >= MAX_BOLETOS) {
                JOptionPane.showMessageDialog(this,
                        "Máximo " + MAX_BOLETOS + " boletos por compra.",
                        "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            asientosSeleccionados.add(p);
            btn.setBackground(C_SELEC);
        }

        // ── Notificar a PanelControl en tiempo real ───────────────────────────
        notificarPanelControl();
    }

    /**
     * Construye la lista de SeatItem desde los asientos seleccionados
     * y se la pasa a PanelControl para que actualice la vista al instante.
     */
    private void notificarPanelControl() {
        if (panelControl == null) return;

        List<PanelControl.SeatItem> items = new ArrayList<>();
        for (Point p : asientosSeleccionados) {
            TipoZona zona  = sistema.getMatrizZonas()[p.x][p.y];
            String   tipo  = zonaToString(zona);
            int precio = (int) sistema.getMapaPrecios().getOrDefault(tipo, 0.0).doubleValue();
            String   id    = tipo.substring(0, 1) + "-F" + p.x + "-A" + p.y;
            items.add(new PanelControl.SeatItem(id, tipo, precio));
        }
        panelControl.actualizarSeleccion(items);
    }

    // ── Marcar asientos como ocupados tras confirmar compra ───────────────────
    public void marcarOcupados(List<Point> puntos) {
        for (Point p : puntos) {
            JButton btn = botonesAsientos[p.x][p.y];
            if (btn != null) {
                btn.setBackground(C_OCUPADO);
                btn.setEnabled(false);
            }
        }
        asientosSeleccionados.clear();
        notificarPanelControl(); // limpia la lista en tiempo real
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Color colorZona(TipoZona zona) {
        switch (zona) {
            case VIP:          return C_VIP;
            case PREFERENCIAL: return C_PREF;
            default:           return C_GEN;
        }
    }

    private String zonaToString(TipoZona zona) {
        switch (zona) {
            case VIP:          return "VIP";
            case PREFERENCIAL: return "Preferencial";
            default:           return "General";
        }
    }

    // ── Getters para VentanaPrincipal ─────────────────────────────────────────
    public List<Point> getAsientosSeleccionados() { return asientosSeleccionados; }
    public JButton[][] getBotonesAsientos()        { return botonesAsientos; }
}