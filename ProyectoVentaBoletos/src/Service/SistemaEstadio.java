package service;

import model.Boleto;
import model.ReporteVenta;
import model.TipoZona;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Clase principal del sistema del estadio.
 * Aquí se gestiona toda la lógica central:
 * - Venta de boletos
 * - Control de asientos
 * - Asignación de zonas
 * - Manejo de precios
 * - Generación de reportes
 */
public class SistemaEstadio {

    // Lista dinámica donde se almacenan todos los boletos vendidos
    private LinkedList<Boleto> listaBoletos;

    // Matriz que controla si un asiento está ocupado o disponible
    private boolean[][] matrizAsientos;

    // Matriz que define el tipo de zona de cada asiento
    private TipoZona[][] matrizZonas;

    // Mapa que contiene los precios según la categoría del asiento
    private HashMap<String, Double> mapaPrecios;

    // Cola FIFO para almacenar los reportes de venta
    private Queue<ReporteVenta> colaReportes;

    /**
     * Constructor del sistema.
     * Inicializa todas las estructuras de datos del estadio.
     */
    public SistemaEstadio(int filas, int columnas) {

        this.listaBoletos = new LinkedList<>();

        this.matrizAsientos = new boolean[filas][columnas];

        this.matrizZonas = new TipoZona[filas][columnas];

        this.mapaPrecios = new HashMap<>();

        this.colaReportes = new LinkedList<>();

        // Se cargan los precios base del sistema
        cargarPreciosIniciales();

        // Se asignan las zonas del estadio
        inicializarZonas();
    }

    /**
     * Define los precios base de cada categoría de asiento.
     */
    private void cargarPreciosIniciales() {

        mapaPrecios.put("VIP", 1500.0);

        mapaPrecios.put("Preferencial", 800.0);

        mapaPrecios.put("General", 400.0);
    }

    /**
     * Inicializa la distribución de zonas dentro del estadio.
     * Dependiendo de la fila, se asigna una categoría específica.
     */
    private void inicializarZonas() {

        for (int i = 0; i < matrizZonas.length; i++) {

            for (int j = 0; j < matrizZonas[0].length; j++) {

                // Zona VIP: filas cercanas a la cancha
                if (i >= 5 && i <= 8) {

                    matrizZonas[i][j] = TipoZona.VIP;
                }

                // Zona Preferencial: zonas intermedias
                else if ((i >= 3 && i <= 4) || (i >= 9 && i <= 10)) {

                    matrizZonas[i][j] = TipoZona.PREFERENCIAL;
                }

                // Zona General: resto del estadio
                else {

                    matrizZonas[i][j] = TipoZona.GENERAL;
                }
            }
        }
    }

    /**
     * Método principal para la compra de boletos.
     * Valida disponibilidad, asigna asiento y genera el reporte de venta.
     */
    public boolean comprarBoleto(String id, String categoria, int fila, int col) {

        // Validación de límites del asiento
        if (fila < 0 ||
            fila >= matrizAsientos.length ||
            col < 0 ||
            col >= matrizAsientos[0].length) {

            return false;
        }

        // Verifica si el asiento ya está ocupado
        if (matrizAsientos[fila][col]) {

            return false;
        }

        // Obtiene el precio según la categoría
        double precio = mapaPrecios.getOrDefault(categoria, 0.0);

        // Genera la ubicación del asiento
        String ubicacion = "Fila " + fila + ", Asiento " + col;

        // Creación del boleto
        Boleto nuevoBoleto = new Boleto(id, categoria, precio, ubicacion);

        nuevoBoleto.setVendido(true);

        listaBoletos.add(nuevoBoleto);

        // Marca el asiento como ocupado
        matrizAsientos[fila][col] = true;

        // Generación del reporte de venta
        ReporteVenta reporte = new ReporteVenta(id, categoria, ubicacion, precio);

        colaReportes.add(reporte);

        return true;
    }

    /**
     * Obtiene la categoría automáticamente según la zona del asiento.
     */
    public String obtenerCategoriaAsiento(int fila, int col) {

        TipoZona zona = matrizZonas[fila][col];

        switch (zona) {

            case VIP:
                return "VIP";

            case PREFERENCIAL:
                return "Preferencial";

            default:
                return "General";
        }
    }

    /**
     * Obtiene el precio de un asiento según su categoría.
     */
    public double obtenerPrecioAsiento(int fila, int col) {

        String categoria = obtenerCategoriaAsiento(fila, col);

        return mapaPrecios.getOrDefault(categoria, 0.0);
    }

    /**
     * Extrae un reporte de la cola en orden FIFO.
     */
    public ReporteVenta extraerReporte() {

        return colaReportes.poll();
    }

    // ─────────────────────────────────────────────
    // Getters del sistema
    // ─────────────────────────────────────────────

    public boolean[][] getMatrizAsientos() {

        return matrizAsientos;
    }

    public TipoZona[][] getMatrizZonas() {

        return matrizZonas;
    }

    public HashMap<String, Double> getMapaPrecios() {

        return mapaPrecios;
    }

    public LinkedList<Boleto> getListaBoletos() {

        return listaBoletos;
    }

    public int getTotalReportesEnCola() {

        return colaReportes.size();
    }
}