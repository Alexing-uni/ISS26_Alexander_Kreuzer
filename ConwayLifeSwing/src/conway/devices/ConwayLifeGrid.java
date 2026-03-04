package conway.devices;

import java.awt.*;
import javax.swing.*;
import main.java.conway.domain.*; // Importamos desde tu JAR

public class ConwayLifeGrid extends JFrame implements IOutDev {
    private static final long serialVersionUID = 1L;
    private JPanel gridPanel;
    private JButton[][] buttons;
    private LifeController controller;
    private Life life; // Referencia al "cerebro" para consultar el estado
    private int rows, cols;

    public ConwayLifeGrid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        setTitle("Conway Life - Sprint 2 (Swing)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gridPanel = new JPanel(new GridLayout(rows, cols));
        buttons = new JButton[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setOpaque(true);
                buttons[i][j].setBackground(Color.WHITE);
                buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                gridPanel.add(buttons[i][j]);
            }
        }

        JButton startBtn = new JButton("Start Game");
        startBtn.addActionListener(e -> { 
            if(controller != null) controller.start(); 
        });
        
        add(gridPanel, BorderLayout.CENTER);
        add(startBtn, BorderLayout.SOUTH);
        
        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Actualizamos este método para recibir también el objeto life
    public void setController(LifeController co, Life life) { 
        this.controller = co; 
        this.life = life;
    }

    @Override
    public void display(String payload) {
        // Imprimimos el estado en la consola
        System.out.println(payload);
        
        if (life != null) {
            SwingUtilities.invokeLater(() -> {
                // SEGÚN EL ERROR: getGrid() ya es el array boolean[][]
                // Accedemos a él directamente
                boolean[][] gridData = life.getGrid(); 
                
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        // Usamos corchetes para acceder a la posición
                        boolean isAlive = gridData[r][c]; 
                        
                        // Actualizamos el color del botón
                        buttons[r][c].setBackground(isAlive ? Color.BLACK : Color.WHITE);
                    }
                }
                gridPanel.repaint();
            });
        }
    }

    @Override public void displayGrid(Grid grid) {} 
    @Override public void displayCell(Cell cell, Grid grid) {}
    @Override public void close() { this.dispose(); }
}