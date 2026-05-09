package View;

import service.SistemaEstadio;
import javax.swing.*;
import java.awt.*;

/**
 * Interfaz Gráfica Principal del Sistema de Boletos.
 * Diseñada de forma modular para facilitar el trabajo en equipo.
 */
public class VentanaPrincipal extends JFrame {
    
    private SistemaEstadio sistema;
    
    // Variables para rastrear la selección del usuario
    private int filaSeleccionada = -1;
    private int colSeleccionada = -1;
    private JButton[][] botonesAsientos;
    private JComboBox<String> cbCategorias;
    private JLabel lblPrecio;

    public VentanaPrincipal(SistemaEstadio sistema) {
        this.sistema = sistema;
        
        // 1. Configuración básica de la ventana
        setTitle("Punto de Venta - Estadio");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout(10, 10)); 

        // 2. Llamada a los módulos independientes
        inicializarPanelSuperior();
        inicializarPanelCentral();
        inicializarPanelInferior();
    }

    /**
     * MÓDULO 1: Selección de Categorías
     * Gestiona el JComboBox y la actualización de precios mediante el HashMap.
     */
    private void inicializarPanelSuperior() {
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panelNorte.setBorder(BorderFactory.createTitledBorder("Paso 1: Selecciona tu Categoría"));
        
        String[] opciones = {"VIP", "Preferencial", "General"};
        cbCategorias = new JComboBox<>(opciones);
        
        // Consultar precio inicial del HashMap
        double precioInicial = sistema.getMapaPrecios().getOrDefault("VIP", 0.0);
        lblPrecio = new JLabel("Precio Total: $" + precioInicial);
        lblPrecio.setFont(new Font("Arial", Font.BOLD, 14));

        // Listener para actualizar precio cuando cambie la categoría
        cbCategorias.addActionListener(e -> {
            String seleccion = (String) cbCategorias.getSelectedItem();
            double precio = sistema.getMapaPrecios().getOrDefault(seleccion, 0.0);
            lblPrecio.setText("Precio Total: $" + precio);
        });
        
        panelNorte.add(new JLabel("Categoría:"));
        panelNorte.add(cbCategorias);
        panelNorte.add(lblPrecio);
        
        add(panelNorte, BorderLayout.NORTH);
    }

    /**
     * MÓDULO 2: Matriz Visual de Asientos
     * Genera la cuadrícula de botones basada en la matriz del backend.
     */
    private void inicializarPanelCentral() {
        boolean[][] matriz = sistema.getMatrizAsientos();
        int filas = matriz.length;
        int columnas = matriz[0].length;

        JPanel panelCentro = new JPanel(new GridLayout(filas, columnas, 5, 5));
        panelCentro.setBorder(BorderFactory.createTitledBorder("Paso 2: Selecciona tu Asiento"));
        panelCentro.setBackground(new Color(230, 230, 230));
        
        botonesAsientos = new JButton[filas][columnas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                JButton btn = new JButton("F" + i + "-A" + j);
                botonesAsientos[i][j] = btn;

                if (matriz[i][j]) {
                    configurarBotonOcupado(btn);
                } else {
                    configurarBotonDisponible(btn, i, j);
                }
                panelCentro.add(btn);
            }
        }
        
        add(panelCentro, BorderLayout.CENTER);
    }

    /**
     * MÓDULO 3: Confirmación y Compra
     * Ejecuta la lógica final de venta y actualiza las estructuras de datos.
     */
    private void inicializarPanelInferior() {
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panelSur.setBorder(BorderFactory.createTitledBorder("Paso 3: Pago"));
        
        JButton btnComprar = new JButton("Confirmar Compra");
        btnComprar.setPreferredSize(new Dimension(200, 40));
        btnComprar.setBackground(new Color(0, 123, 255));
        btnComprar.setForeground(Color.WHITE);
        
        btnComprar.addActionListener(e -> {
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un asiento primero.");
                return;
            }

            String id = "BOL-" + (System.currentTimeMillis() % 10000);
            String cat = (String) cbCategorias.getSelectedItem();
            
            boolean exito = sistema.comprarBoleto(id, cat, filaSeleccionada, colSeleccionada);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Compra Exitosa!\nID: " + id);
                configurarBotonOcupado(botonesAsientos[filaSeleccionada][colSeleccionada]);
                filaSeleccionada = -1;
            }
        });
        
        panelSur.add(btnComprar);
        add(panelSur, BorderLayout.SOUTH);
    }

    // Métodos auxiliares para mantener el código limpio
    private void configurarBotonOcupado(JButton btn) {
        btn.setBackground(new Color(220, 53, 69));
        btn.setEnabled(false);
        btn.setForeground(Color.WHITE);
    }

    private void configurarBotonDisponible(JButton btn, int f, int c) {
        btn.setBackground(new Color(40, 167, 69));
        btn.setForeground(Color.WHITE);
        btn.addActionListener(e -> {
            // Limpiar selección previa
            if (filaSeleccionada != -1 && !sistema.getMatrizAsientos()[filaSeleccionada][colSeleccionada]) {
                botonesAsientos[filaSeleccionada][colSeleccionada].setBackground(new Color(40, 167, 69));
            }
            // Marcar nueva selección
            filaSeleccionada = f;
            colSeleccionada = c;
            btn.setBackground(new Color(255, 193, 7)); // Amarillo
        });
    }
}