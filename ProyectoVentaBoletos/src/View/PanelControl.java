package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.List;

/**
 * PanelControl — Panel lateral derecho de la ventana principal.
 *
 * Responsabilidades:
 *   1. Mostrar la leyenda de categorías de boletos (colores y precios).
 *   2. Actualizar en tiempo real la lista de asientos seleccionados.
 *   3. Calcular y mostrar el subtotal de la selección actual.
 *   4. Mostrar el resumen acumulado de ventas de la sesión.
 *   5. Mantener el historial de compras confirmadas.
 *
 * Este panel es completamente pasivo: no contiene lógica de negocio.
 * Solo recibe datos desde VentanaPrincipal y PanelEstadio y los muestra.
 */
public class PanelControl extends JPanel {

    // ── Componentes de la sección "Selección activa"
    /** Muestra cuántos asientos lleva seleccionados el usuario (ej: "2 / 6") */
    private JLabel    lblContador;

    /** Panel dinámico que lista cada asiento seleccionado con su precio */
    private JPanel    panelItems;

    /** Suma acumulada de los precios de los asientos seleccionados actualmente */
    private JLabel    lblSubtotal;

    // ── Componentes de la sección "Ventas del día"
    /** Total de boletos vendidos desde que inició la sesión */
    private JLabel    lblVendidos;

    /** Total de ingresos generados en la sesión actual */
    private JLabel    lblIngresos;

    /** Registro cronológico de todas las compras confirmadas en la sesión */
    private JTextArea areaHistorial;

    // ── Constantes
    /** Máximo de boletos que se pueden seleccionar por compra */
    private static final int MAX_BOLETOS = 6;


    // CONSTRUCTOR


    /**
     * Construye el panel lateral con todas sus secciones apiladas
     * verticalmente en el siguiente orden:
     *   [Leyenda de categorías] → [Selección activa] → [Ventas del día] → [Historial]
     */
    public PanelControl() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(EstiloUI.C_FONDO);
        setOpaque(true);
        setPreferredSize(new Dimension(265, 0));

        add(crearCardLeyenda());       // CARD 0 — Leyenda de tipos de boleto
        add(Box.createVerticalStrut(8));
        add(crearCardSeleccion());     // CARD 1 — Asientos seleccionados en tiempo real
        add(Box.createVerticalStrut(8));
        add(crearCardVentas());        // CARD 2 — Resumen acumulado de la sesión
        add(Box.createVerticalStrut(8));
        add(crearCardHistorial());     // CARD 3 — Historial de compras confirmadas
        add(Box.createVerticalGlue()); // Espacio flexible al final
    }


    // CARD 0 — LEYENDA DE CATEGORÍAS

    /**
     * Crea la tarjeta de leyenda que explica visualmente los tres tipos
     * de boleto disponibles, con su color representativo y precio base.
     *
     * Esta leyenda es estática — no cambia durante la ejecución.
     * Ayuda al usuario a identificar las zonas del estadio en el mapa.
     */
    private JPanel crearCardLeyenda() {
        JPanel card = baseCard();
        card.setLayout(new BorderLayout(0, 8));

        // Título de la sección
        JLabel titulo = new JLabel("Tipos de boleto");
        titulo.setFont(EstiloUI.fuente(Font.BOLD, 12));
        titulo.setForeground(Color.WHITE);

        // Contenedor de los tres ítems de leyenda
        JPanel contenedor = new JPanel(new GridLayout(3, 1, 0, 5));
        contenedor.setBackground(EstiloUI.C_PANEL);
        contenedor.setOpaque(true);

        // Cada ítem muestra: [cuadro de color] [nombre] [$precio]
        contenedor.add(crearItemLeyenda("VIP",          "$1,500  —  Junto a la cancha", EstiloUI.C_VIP));
        contenedor.add(crearItemLeyenda("Preferencial", "$800    —  Zona media",         EstiloUI.C_PREF));
        contenedor.add(crearItemLeyenda("General",      "$400    —  Parte alta",          EstiloUI.C_GEN));

        card.add(titulo,     BorderLayout.NORTH);
        card.add(contenedor, BorderLayout.CENTER);
        return card;
    }

    /**
     * Construye un ítem individual de la leyenda.
     * Contiene un cuadro de color a la izquierda y el texto descriptivo a la derecha.
     *
     * @param nombre    Nombre de la categoría (ej: "VIP")
     * @param descripcion Precio y ubicación en el estadio
     * @param color     Color representativo de esa zona en el mapa
     * @return Panel configurado con el ítem de leyenda
     */
    private JPanel crearItemLeyenda(String nombre, String descripcion, Color color) {
        JPanel item = new JPanel(new BorderLayout(8, 0));
        item.setBackground(new Color(32, 42, 54));
        item.setBorder(new EmptyBorder(5, 8, 5, 8));
        item.setOpaque(true);

        // Cuadro de color que representa visualmente la zona en el estadio
        JPanel cuadroColor = new JPanel();
        cuadroColor.setBackground(color);
        cuadroColor.setOpaque(true);
        cuadroColor.setPreferredSize(new Dimension(18, 18));
        cuadroColor.setBorder(BorderFactory.createLineBorder(color.darker(), 1));

        // Nombre de la categoría en negrita
        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(EstiloUI.fuente(Font.BOLD, 11));
        lblNombre.setForeground(Color.WHITE);

        // Precio y descripción de la ubicación
        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(EstiloUI.fuente(Font.PLAIN, 10));
        lblDesc.setForeground(new Color(160, 175, 190));

        // Panel izquierdo: color + nombre juntos
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        izquierda.setBackground(new Color(32, 42, 54));
        izquierda.setOpaque(true);
        izquierda.add(cuadroColor);
        izquierda.add(lblNombre);

        item.add(izquierda, BorderLayout.WEST);
        item.add(lblDesc,   BorderLayout.CENTER);
        return item;
    }

    // CARD 1 — SELECCIÓN EN TIEMPO REAL

    /**
     * Crea la tarjeta que muestra en tiempo real los asientos que el usuario
     * está seleccionando, junto con su categoría, precio individual y subtotal.
     *
     * Esta tarjeta se actualiza automáticamente con cada clic en el estadio,
     * gracias al método público {@link #actualizarSeleccion(List)}.
     */
    private JPanel crearCardSeleccion() {
        JPanel card = baseCard();
        card.setLayout(new BorderLayout(0, 6));

        // Contador de asientos seleccionados vs. máximo permitido
        lblContador = new JLabel("Seleccionados: 0 / " + MAX_BOLETOS);
        lblContador.setFont(EstiloUI.fuente(Font.BOLD, 12));
        lblContador.setForeground(Color.WHITE);

        // Panel interno donde se renderizan las filas de asientos seleccionados
        panelItems = new JPanel();
        panelItems.setLayout(new BoxLayout(panelItems, BoxLayout.Y_AXIS));
        panelItems.setBackground(new Color(22, 28, 36));
        panelItems.setOpaque(true);

        // Scroll por si hay muchos asientos seleccionados
        JScrollPane scroll = new JScrollPane(panelItems);
        scroll.setPreferredSize(new Dimension(245, 140));
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.C_BORDE));
        scroll.getViewport().setBackground(new Color(22, 28, 36));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Footer con el subtotal de la selección actual
        JPanel footerSel = new JPanel(new BorderLayout());
        footerSel.setBackground(EstiloUI.C_PANEL);
        footerSel.setOpaque(true);
        footerSel.setBorder(new MatteBorder(1, 0, 0, 0, EstiloUI.C_BORDE));

        lblSubtotal = new JLabel("Subtotal: $0");
        lblSubtotal.setFont(EstiloUI.fuente(Font.BOLD, 13));
        lblSubtotal.setForeground(EstiloUI.C_AMARILLO);
        lblSubtotal.setBorder(new EmptyBorder(6, 0, 0, 0));

        footerSel.add(lblSubtotal, BorderLayout.CENTER);

        card.add(lblContador, BorderLayout.NORTH);
        card.add(scroll,      BorderLayout.CENTER);
        card.add(footerSel,   BorderLayout.SOUTH);

        // Mostrar mensaje inicial vacío
        mostrarVacio();
        return card;
    }

    // CARD 2 — VENTAS DEL DÍA

    /**
     * Crea la tarjeta con los acumulados de la sesión:
     * total de boletos vendidos e ingresos generados.
     *
     * Estos valores se actualizan con {@link #actualizarResumen(int, double)}
     * después de cada compra confirmada.
     */
    private JPanel crearCardVentas() {
        JPanel card = baseCard();
        card.setLayout(new GridLayout(3, 1, 0, 4));

        JLabel titulo = new JLabel("Ventas de la sesion");
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

    // CARD 3 — HISTORIAL DE COMPRAS

    /**
     * Crea la tarjeta con el historial cronológico de compras confirmadas.
     * Cada entrada muestra: ID del boleto, categoría, asiento y precio.
     *
     * El área de texto tiene auto-scroll: siempre muestra la última compra.
     * Se actualiza con {@link #agregarHistorial(String)}.
     */
    private JPanel crearCardHistorial() {
        JPanel card = baseCard();
        card.setLayout(new BorderLayout(0, 6));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Historial de compras");
        titulo.setFont(EstiloUI.fuente(Font.BOLD, 12));
        titulo.setForeground(Color.WHITE);

        // Área de texto de solo lectura para el registro de ventas
        areaHistorial = new JTextArea();
        areaHistorial.setEditable(false);
        areaHistorial.setFont(EstiloUI.fuenteMono(Font.PLAIN, 10));
        areaHistorial.setBackground(new Color(22, 28, 36));
        areaHistorial.setForeground(new Color(180, 195, 210));
        areaHistorial.setBorder(new EmptyBorder(4, 6, 4, 6));
        areaHistorial.setOpaque(true);

        JScrollPane scroll = new JScrollPane(areaHistorial);
        scroll.setPreferredSize(new Dimension(245, 120));
        scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.C_BORDE));
        scroll.getViewport().setBackground(new Color(22, 28, 36));

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // MÉTODOS PÚBLICOS — Llamados desde VentanaPrincipal y PanelEstadio

    /**
     * Actualiza la lista de asientos seleccionados y el subtotal en tiempo real.
     *
     * Es llamado automáticamente por PanelEstadio cada vez que el usuario
     * hace clic en un asiento (seleccionar o deseleccionar).
     *
     * @param items Lista de {@link SeatItem} con los asientos actualmente seleccionados.
     *              Si es null o vacía, muestra el mensaje de "ningún asiento".
     */
    public void actualizarSeleccion(List<SeatItem> items) {
        panelItems.removeAll();
        int total = 0;

        if (items == null || items.isEmpty()) {
            mostrarVacio();
        } else {
            // Renderizar una fila por cada asiento seleccionado
            for (SeatItem item : items) {
                panelItems.add(crearFilaAsiento(item));
                panelItems.add(Box.createVerticalStrut(2));
                total += item.precio;
            }
        }

        // Actualizar contador y subtotal
        int cantidad = (items == null) ? 0 : items.size();
        lblContador.setText("Seleccionados: " + cantidad + " / " + MAX_BOLETOS);
        lblSubtotal.setText("Subtotal: $" + String.format("%,.0f", (double) total));

        panelItems.revalidate();
        panelItems.repaint();
    }

    /**
     * Actualiza los contadores de resumen de la sesión.
     * Llamado desde VentanaPrincipal después de cada compra confirmada.
     *
     * @param boletos  Total acumulado de boletos vendidos en la sesión.
     * @param ingresos Total acumulado de ingresos generados en la sesión.
     */
    public void actualizarResumen(int boletos, double ingresos) {
        lblVendidos.setText("Boletos vendidos: " + boletos);
        lblIngresos.setText(String.format("Ingresos totales: $%,.2f", ingresos));
    }

    /**
     * Agrega una línea al historial de compras confirmadas con auto-scroll.
     * Llamado desde VentanaPrincipal una vez por cada boleto confirmado.
     *
     * @param texto Línea a agregar, con formato: "ID | Categoría FX-AY | $precio"
     */
    public void agregarHistorial(String texto) {
        areaHistorial.append(texto + "\n");
        // Desplazar automáticamente hacia la última entrada
        areaHistorial.setCaretPosition(areaHistorial.getDocument().getLength());
    }

    // MÉTODOS PRIVADOS — Construcción interna de filas y estados

    /**
     * Muestra un mensaje de placeholder cuando no hay asientos seleccionados.
     * Se llama al iniciar el panel y al limpiar la selección tras una compra.
     */
    private void mostrarVacio() {
        panelItems.removeAll();
        JLabel empty = new JLabel("  Ningun asiento seleccionado");
        empty.setFont(EstiloUI.fuente(Font.ITALIC, 11));
        empty.setForeground(new Color(100, 115, 130));
        empty.setBorder(new EmptyBorder(8, 4, 8, 4));
        panelItems.add(empty);
        panelItems.revalidate();
        panelItems.repaint();
    }

    /**
     * Construye la fila visual para un asiento seleccionado.
     * Muestra: punto de color de categoría | ID y tipo | precio individual.
     *
     * @param item Datos del asiento: ID, tipo de zona y precio.
     * @return Panel configurado listo para agregar a panelItems.
     */
    private JPanel crearFilaAsiento(SeatItem item) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setBackground(new Color(32, 42, 54));
        row.setBorder(new EmptyBorder(4, 6, 4, 6));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.setOpaque(true);

        // Indicador de color según la categoría del asiento
        JLabel dot = new JLabel("\u25CF"); // carácter "●" sin unicode directo (compatible)
        dot.setFont(EstiloUI.fuente(Font.PLAIN, 10));
        dot.setForeground(colorCategoria(item.tipo));

        // Identificador del asiento y abreviación del tipo
        JLabel info = new JLabel(" " + item.seatId + "  (" + abreviarTipo(item.tipo) + ")");
        info.setFont(EstiloUI.fuente(Font.PLAIN, 11));
        info.setForeground(Color.WHITE);

        // Precio del asiento en azul claro para destacarlo
        JLabel precio = new JLabel("$" + String.format("%,.0f", (double) item.precio));
        precio.setFont(EstiloUI.fuente(Font.BOLD, 11));
        precio.setForeground(new Color(100, 200, 255));

        // Agrupar indicador y nombre en el lado izquierdo
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izquierda.setBackground(new Color(32, 42, 54));
        izquierda.setOpaque(true);
        izquierda.add(dot);
        izquierda.add(info);

        row.add(izquierda, BorderLayout.CENTER);
        row.add(precio,    BorderLayout.EAST);
        return row;
    }

    /**
     * Construye una tarjeta base con el estilo visual del sistema.
     * Todas las secciones del panel usan esta misma estructura base.
     *
     * @return JPanel con fondo, borde y margen configurados.
     */
    private JPanel baseCard() {
        JPanel c = new JPanel();
        c.setBackground(EstiloUI.C_PANEL);
        c.setOpaque(true);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloUI.C_BORDE, 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(265, Integer.MAX_VALUE));
        return c;
    }

    /**
     * Devuelve el color representativo de una categoría de boleto.
     * Usado para pintar el indicador de punto (●) en cada fila.
     *
     * @param tipo "VIP", "Preferencial" o "General"
     * @return Color asociado a esa categoría en EstiloUI
     */
    private Color colorCategoria(String tipo) {
        switch (tipo) {
            case "VIP":          return EstiloUI.C_VIP;
            case "Preferencial": return EstiloUI.C_PREF;
            default:             return EstiloUI.C_GEN;
        }
    }

    /**
     * Abrevia el nombre de la categoría para mostrarlo en espacios reducidos.
     *
     * @param tipo Nombre completo de la categoría
     * @return Abreviación de 3-4 caracteres
     */
    private String abreviarTipo(String tipo) {
        switch (tipo) {
            case "VIP":          return "VIP";
            case "Preferencial": return "Pref";
            default:             return "Gral";
        }
    }

    // CLASE INTERNA — Modelo de datos para un asiento seleccionado

    /**
     * Estructura de datos inmutable que representa un asiento en la selección activa.
     *
     * Es creada por PanelEstadio y pasada a PanelControl mediante
     * {@link #actualizarSeleccion(List)} para actualizar la vista en tiempo real.
     */
    public static class SeatItem {
        /** Identificador legible del asiento, ej: "VIP-F6-A7" */
        public final String seatId;

        /** Categoría: "VIP", "Preferencial" o "General" */
        public final String tipo;

        /** Precio en pesos de este asiento específico */
        public final int precio;

        /**
         * @param seatId  Identificador del asiento formado por zona, fila y columna
         * @param tipo    Categoría del asiento según su zona en la matriz
         * @param precio  Precio del asiento consultado del HashMap de precios
         */
        public SeatItem(String seatId, String tipo, int precio) {
            this.seatId = seatId;
            this.tipo   = tipo;
            this.precio = precio;
        }
    }
}