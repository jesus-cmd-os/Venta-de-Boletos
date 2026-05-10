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
 * Panel encargado de dibujar TODO el estadio:
 * - cancha
 * - tribunas
 * - botones de asientos
 * - selección visual
 *
 * Esta clase encapsula toda la lógica visual del estadio.
 */
public class PanelEstadio extends JPanel {

    // ─────────────────────────────────────────
    // Backend
    // ─────────────────────────────────────────
    private SistemaEstadio sistema;

    // ─────────────────────────────────────────
    // Matriz visual de botones
    // ─────────────────────────────────────────
    private JButton[][] botonesAsientos;

    // ─────────────────────────────────────────
    // Lista de asientos seleccionados
    // ─────────────────────────────────────────
    private List<Point> asientosSeleccionados;

    // ─────────────────────────────────────────
    // Máximo permitido
    // ─────────────────────────────────────────
    private static final int MAX_BOLETOS = 6;

    // ─────────────────────────────────────────
    // Colores
    // ─────────────────────────────────────────
    private static final Color COLOR_OCUPADO =
            new Color(231,76,60);

    private static final Color COLOR_SELECCIONADO =
            new Color(241,196,15);

    private static final Color COLOR_PANEL =
            new Color(52,73,94);

    private static final Color COLOR_ACENTO =
            new Color(52,152,219);

    private static final Color COLOR_VIP =
            new Color(155,89,182);

    private static final Color COLOR_PREF =
            new Color(52,152,219);

    private static final Color COLOR_GENERAL =
            new Color(46,204,113);

    /**
     * Constructor principal.
     */
    public PanelEstadio(SistemaEstadio sistema) {

        this.sistema = sistema;

        this.asientosSeleccionados =
                new ArrayList<>();

        construirPanel();
    }

    /**
     * Construye TODO el estadio.
     */
    private void construirPanel() {

        setLayout(new BorderLayout(20,20));

        setBackground(COLOR_PANEL);

        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_ACENTO,
                                2
                        ),
                        new EmptyBorder(
                                20,
                                20,
                                20,
                                20
                        )
                )
        );

        boolean[][] matriz =
                sistema.getMatrizAsientos();

        botonesAsientos =
                new JButton[
                        matriz.length
                ][
                        matriz[0].length
                ];

        // NORTE
        add(
                crearZonaHorizontal(0,2),
                BorderLayout.NORTH
        );

        // SUR
        add(
                crearZonaHorizontal(11,13),
                BorderLayout.SOUTH
        );

        // OESTE
        add(
                crearZonaVertical(3,10,0,2),
                BorderLayout.WEST
        );

        // ESTE
        add(
                crearZonaVertical(3,10,11,13),
                BorderLayout.EAST
        );

        // CANCHA
        add(
                crearCancha(),
                BorderLayout.CENTER
        );
    }

    /**
     * Crea la cancha.
     */
    private JPanel crearCancha() {

        JPanel cancha =
                new JPanel(
                        new BorderLayout()
                );

        cancha.setBackground(
                new Color(39,174,96)
        );

        cancha.setBorder(
                BorderFactory.createLineBorder(
                        Color.WHITE,
                        5
                )
        );

        JLabel texto =
                new JLabel(
                        " CANCHA ",
                        SwingConstants.CENTER
                );

        texto.setForeground(Color.WHITE);

        texto.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        34
                )
        );

        cancha.add(texto,
                BorderLayout.CENTER);

        return cancha;
    }

    /**
     * Crea tribunas horizontales.
     */
    private JPanel crearZonaHorizontal(
            int filaInicio,
            int filaFin
    ) {

        JPanel panel =
                new JPanel(
                        new GridLayout(3,14,4,4)
                );

        panel.setBackground(COLOR_PANEL);

        boolean[][] matriz =
                sistema.getMatrizAsientos();

        for (int i = filaInicio;
             i <= filaFin;
             i++) {

            for (int j = 0;
                 j < 14;
                 j++) {

                JButton btn =
                        crearBotonAsiento(
                                i,
                                j,
                                matriz[i][j]
                        );

                botonesAsientos[i][j] = btn;

                panel.add(btn);
            }
        }

        return panel;
    }

    /**
     * Crea tribunas verticales.
     */
    private JPanel crearZonaVertical(
            int filaInicio,
            int filaFin,
            int colInicio,
            int colFin
    ) {

        JPanel panel =
                new JPanel(
                        new GridLayout(8,3,4,4)
                );

        panel.setBackground(COLOR_PANEL);

        boolean[][] matriz =
                sistema.getMatrizAsientos();

        for (int i = filaInicio;
             i <= filaFin;
             i++) {

            for (int j = colInicio;
                 j <= colFin;
                 j++) {

                JButton btn =
                        crearBotonAsiento(
                                i,
                                j,
                                matriz[i][j]
                        );

                botonesAsientos[i][j] = btn;

                panel.add(btn);
            }
        }

        return panel;
    }

    /**
     * Crea un asiento individual.
     */
    private JButton crearBotonAsiento(
            int fila,
            int col,
            boolean ocupado
    ) {

        TipoZona zona =
                sistema.getMatrizZonas()
                        [fila][col];

        String textoZona = "";

        Color colorZona = COLOR_GENERAL;

        switch (zona) {

            case VIP:

                textoZona = "VIP";

                colorZona = COLOR_VIP;

                break;

            case PREFERENCIAL:

                textoZona = "PREF";

                colorZona = COLOR_PREF;

                break;

            case GENERAL:

                textoZona = "GEN";

                colorZona = COLOR_GENERAL;

                break;
        }

        JButton btn =
                new JButton(
                        "<html><center>" +
                        textoZona +
                        "<br>F" +
                        fila +
                        "-A" +
                        col +
                        "</center></html>"
                );

        btn.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        9
                )
        );

        btn.setForeground(Color.WHITE);

        btn.setBackground(colorZona);

        btn.setFocusPainted(false);

        btn.setBorderPainted(false);

        if (ocupado) {

            btn.setBackground(COLOR_OCUPADO);

            btn.setEnabled(false);
        }

        else {

            btn.addActionListener(
                    e -> seleccionarAsiento(
                            fila,
                            col,
                            btn
                    )
            );
        }

        return btn;
    }

    /**
     * Maneja selección de asientos.
     */
    private void seleccionarAsiento(
            int fila,
            int col,
            JButton btn
    ) {

        Point asiento =
                new Point(fila,col);

        if (asientosSeleccionados
                .contains(asiento)) {

            asientosSeleccionados.remove(asiento);

            restaurarColorOriginal(
                    fila,
                    col,
                    btn
            );
        }

        else {

            if (asientosSeleccionados
                    .size() >= MAX_BOLETOS) {

                JOptionPane.showMessageDialog(
                        this,
                        "Máximo 6 boletos."
                );

                return;
            }

            asientosSeleccionados.add(asiento);

            btn.setBackground(
                    COLOR_SELECCIONADO
            );
        }
    }

    /**
     * Restaura color original.
     */
    private void restaurarColorOriginal(
            int fila,
            int col,
            JButton btn
    ) {

        TipoZona zona =
                sistema.getMatrizZonas()
                        [fila][col];

        switch (zona) {

            case VIP:

                btn.setBackground(COLOR_VIP);

                break;

            case PREFERENCIAL:

                btn.setBackground(COLOR_PREF);

                break;

            case GENERAL:

                btn.setBackground(COLOR_GENERAL);

                break;
        }
    }

    /**
     * Getter de asientos seleccionados.
     */
    public List<Point> getAsientosSeleccionados() {

        return asientosSeleccionados;
    }

    /**
     * Getter de botones.
     */
    public JButton[][] getBotonesAsientos() {

        return botonesAsientos;
    }
}