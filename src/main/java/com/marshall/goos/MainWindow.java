package com.marshall.goos;

import static com.marshall.goos.Main.MAIN_WINDOW_NAME;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class MainWindow extends JFrame {
    public static final String SNIPER_STATUS_NAME = "sniper status";
    private final JLabel sniperStatus = createLabel(SniperStatus.JOINING.toString());

    public MainWindow() {
        super("Auction Sniper");
        setName(MAIN_WINDOW_NAME);
        add(sniperStatus);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void showStatus(String status) {
        sniperStatus.setText(status);
    }

    private static JLabel createLabel(String initialText) {
        JLabel result = new JLabel(initialText);
        result.setName(SNIPER_STATUS_NAME);
        result.setBorder(new LineBorder(Color.BLACK));
        return result;
    }
}
