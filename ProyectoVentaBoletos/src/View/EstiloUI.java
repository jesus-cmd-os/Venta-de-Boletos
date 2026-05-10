package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Clase utilitaria de estilos y compatibilidad multiplataforma.
 *
 * Problemas que resuelve:
 *  1. Mac (Aqua L&F) ignora setBackground() en JButton → se usa JPanel pintado como botón
 *  2. "Arial" no siempre existe en Mac → se usa una familia con fallback seguro
 *  3. Retina/HiDPI en Mac → se activa antialiasing y se usan tamaños relativos
 *  4. Barra de menú del sistema en Mac resta espacio → se ajusta el tamaño de ventana
 *  5. setOpaque() en paneles se comporta diferente en Aqua → se fuerza siempre
 */
public class EstiloUI {

    // ── Colores del sistema ───────────────────────────────────────────────────
    public static final Color C_VIP      = new Color(24,  95, 165);
    public static final Color C_PREF     = new Color(15, 110,  86);
    public static final Color C_GEN      = new Color(95,  94,  90);
    public static final Color C_OCUPADO  = new Color(123, 45,  45);
    public static final Color C_SELEC    = new Color(241, 196,  15);
    public static final Color C_FONDO    = new Color(28,  35,  45);
    public static final Color C_PANEL    = new Color(38,  48,  60);
    public static final Color C_BORDE    = new Color(55,  70,  88);
    public static final Color C_ACENTO   = new Color(52, 152, 219);
    public static final Color C_VERDE    = new Color(46, 204, 113);
    public static final Color C_AMARILLO = new Color(241, 196,  15);
    public static final Color C_CANCHA   = new Color(39, 174,  96);

    // ── ¿Estamos en Mac? ─────────────────────────────────────────────────────
    public static final boolean ES_MAC =
            System.getProperty("os.name", "").toLowerCase().contains("mac");
    public static EstiloUI EstiloUI;

    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Inicializa el Look and Feel y propiedades globales.
     * Llamar UNA sola vez en ProyectoVentaBoletos.main() antes de crear la ventana.
     */
    public static void inicializar() {
        try {
            // Forzar el L&F del sistema en ambas plataformas para consistencia
            // excepto en Mac donde es mejor usar Nimbus (respeta setBackground)
            if (ES_MAC) {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                // Nimbus sí respeta los colores de botones
                UIManager.put("control",          new ColorUIResource(C_PANEL));
                UIManager.put("nimbusBase",       new ColorUIResource(C_ACENTO));
                UIManager.put("nimbusBlueGrey",   new ColorUIResource(C_PANEL));
                UIManager.put("text",             new ColorUIResource(Color.WHITE));
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            // Si falla, Java usa su L&F por defecto — el sistema sigue funcionando
            System.err.println("L&F no disponible: " + e.getMessage());
        }

        // Antialiasing de texto en todas las plataformas
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // En Mac: usar la barra de menú nativa del sistema
        if (ES_MAC) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "Venta de Boletos");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Fuente segura multiplataforma.
     * Arial existe en Windows; en Mac puede no estar → fallback a Helvetica Neue / SansSerif.
     */
    public static Font fuente(int estilo, int tamano) {
        if (ES_MAC) {
            // Helvetica Neue es la fuente del sistema en Mac, siempre disponible
            Font f = new Font("Helvetica Neue", estilo, escalar(tamano));
            if (f.getFamily().equals("Dialog")) {
                // Fallback si tampoco está
                f = new Font(Font.SANS_SERIF, estilo, escalar(tamano));
            }
            return f;
        }
        return new Font("Arial", estilo, tamano);
    }

    public static Font fuenteMono(int estilo, int tamano) {
        return new Font(Font.MONOSPACED, estilo, ES_MAC ? escalar(tamano) : tamano);
    }

    /**
     * Escala el tamaño de fuente en Mac para compensar la densidad Retina.
     * En pantallas normales se queda igual; en Retina se ve más pequeño
     * si usamos el mismo número de puntos, así que aumentamos levemente.
     */
    private static int escalar(int tamano) {
        return tamano; // Java 2D ya maneja el scaling en Retina desde Java 9+
    }

    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Crea un botón con color de fondo garantizado en Windows y Mac.
     *
     * En Mac con Aqua L&F, setBackground() es ignorado en JButton estándar.
     * La solución: crear un JButton que sobreescribe paintComponent() para
     * dibujar su propio fondo con el color correcto en cualquier L&F.
     */
    public static JButton boton(String texto, Color colorFondo) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color base = colorFondo;
                // Efecto hover/press
                if (getModel().isPressed()) {
                    base = base.darker();
                } else if (getModel().isRollover()) {
                    base = base.brighter();
                }
                if (!isEnabled()) {
                    base = new Color(base.getRed(), base.getGreen(), base.getBlue(), 120);
                }

                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();

                // Dibujar el texto encima
                super.paintComponent(g);
            }
        };

        btn.setFont(fuente(Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(colorFondo);   // para L&F que sí lo respetan
        btn.setOpaque(false);            // dejar que paintComponent dibuje el fondo
        btn.setContentAreaFilled(false); // CRÍTICO en Mac: evita que Aqua pinte encima
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Versión con tamaño fijo predefinido.
     */
    public static JButton boton(String texto, Color colorFondo, int ancho, int alto) {
        JButton btn = boton(texto, colorFondo);
        btn.setPreferredSize(new Dimension(ancho, alto));
        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Crea un botón de asiento (pequeño) compatible con Mac y Windows.
     */
    public static JButton botonAsiento(Color colorFondo, int ancho, int alto) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color base = getBackground();
                if (!isEnabled()) {
                    base = C_OCUPADO;
                } else if (getModel().isPressed()) {
                    base = base.darker();
                }

                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };

        btn.setBackground(colorFondo);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(ancho, alto));
        btn.setMinimumSize(new Dimension(ancho, alto));
        btn.setMaximumSize(new Dimension(ancho, alto));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Panel con fondo garantizado (setOpaque(true) explícito para Mac).
     */
    public static JPanel panel(LayoutManager layout, Color fondo) {
        JPanel p = new JPanel(layout);
        p.setBackground(fondo);
        p.setOpaque(true); // Mac Aqua a veces no respeta esto sin forzarlo
        return p;
    }

    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Calcula el tamaño inicial de la ventana principal.
     * En Mac resta la altura de la barra de menú del sistema (~22px) y el dock.
     */
    public static Dimension tamanoVentana() {
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int ancho = Math.min(1400, (int)(pantalla.width  * 0.92));
        int alto  = Math.min(900,  (int)(pantalla.height * (ES_MAC ? 0.85 : 0.90)));
        return new Dimension(ancho, alto);
    }

    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Label con fuente segura y color.
     */
    public static JLabel label(String texto, int estilo, int tamano, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(estilo, tamano));
        l.setForeground(color);
        return l;
    }
}