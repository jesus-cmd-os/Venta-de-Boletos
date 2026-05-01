package service;

import model.Boleto;
import java.util.*;

/**
 * Clase principal de lógica que integra las estructuras de datos obligatorias:
 * LinkedList, Matrices, HashMap y Colas (FIFO).
 */
public class SistemaEstadio {
    // 1. Listas Enlazadas: Gestión dinámica de boletos vendidos por categoría
    private LinkedList<Boleto> listaBoletos;
    
    // 2. Matrices: Representación visual y control de disponibilidad de asientos
    private boolean[][] matrizAsientos;
    
    // 3. HashMap: Asociación eficiente de categorías con sus precios
    private HashMap<String, Double> mapaPrecios;
    
    // 4. Cola (FIFO): Almacenamiento de reportes en orden de generación
    private Queue<String> colaReportes;

    /**
     * Constructor que inicializa las dimensiones del estadio y los precios base
     * @param filas Cantidad de filas de asientos.
     * @param columnas Cantidad de columnas de asientos.
     */
    public SistemaEstadio(int filas, int columnas) {
        this.listaBoletos = new LinkedList<>();
        this.matrizAsientos = new boolean[filas][columnas]; // Inicializa en false (disponible)
        this.mapaPrecios = new HashMap<>();
        this.colaReportes = new LinkedList<>();
        
        cargarPreciosIniciales();
    }

    /**
     * Define los precios fijos iniciales mediante el HashMap
     */
    private void cargarPreciosIniciales() {
        mapaPrecios.put("VIP", 1500.0);
        mapaPrecios.put("Preferencial", 800.0);
        mapaPrecios.put("General", 400.0);
    }

    /**
     * Lógica central para la compra de boletos y validación de asientos
     * @return true si la venta se realizó, false si el asiento estaba ocupado.
     */
    public boolean comprarBoleto(String id, String categoria, int fila, int col) {
        // VALIDACIÓN: Verificar en la matriz si el asiento ya está ocupado
        if (matrizAsientos[fila][col]) {
            return false; 
        }

        // HASHMAP: Consultar el precio según la categoría
        double precio = mapaPrecios.getOrDefault(categoria, 0.0);

        //  Datos del nuevo boleto
        String ubicacion = "Fila " + fila + ", Asiento " + col;
        Boleto nuevoBoleto = new Boleto(id, categoria, precio, ubicacion);
        nuevoBoleto.setVendido(true);

        // LINKEDLIST: Agregar a la lista para gestión dinámica
        listaBoletos.add(nuevoBoleto);

        // MATRIZ: Marcar asiento como ocupado (true)
        matrizAsientos[fila][col] = true;

        // COLA: Generar y encolar el reporte de venta (FIFO)[cite: 1]
        String reporte = "ID: " + id + " | " + categoria + " | " + ubicacion + " | Total: $" + precio;
        colaReportes.add(reporte);

        return true;
    }

    /**
     * Permite actualizar el precio de una categoría en el HashMap.
     */
    public void actualizarPrecio(String categoria, double nuevoPrecio) {
        mapaPrecios.put(categoria, nuevoPrecio);
    }

    /**
     * Extrae el siguiente reporte de la cola siguiendo el principio FIFO
     * @return El reporte en formato String o null si la cola está vacía.
     */
    public String extraerReporte() {
        return colaReportes.poll();
    }

    // Getters necesarios para la interacción con la GUI
    public boolean[][] getMatrizAsientos() { return matrizAsientos; }
    public HashMap<String, Double> getMapaPrecios() { return mapaPrecios; }
    public LinkedList<Boleto> getListaBoletos() { return listaBoletos; }
    public int getTotalReportesEnCola() { return colaReportes.size(); }
}