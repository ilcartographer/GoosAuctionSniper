package com.marshall.goos;

import javax.swing.*;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class Main {
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_PORT = 1;
    private static final int ARG_USERNAME = 2;
    private static final int ARG_PASSWORD = 3;
    private static final int ARG_ITEM_ID = 4;

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private MainWindow ui;
    @SuppressWarnings("unused") private Chat notToBeGCd;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        var main = new Main();
        main.joinAuction(connection(args[ARG_HOSTNAME], Integer.parseInt(args[ARG_PORT]),
                args[ARG_USERNAME], args[ARG_PASSWORD]), args[ARG_ITEM_ID]);
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        final var chat = connection.getChatManager().createChat(auctionId(itemId, connection), new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ui.showStatus(SniperStatus.LOST.toString());
                    }
                });
            }
        });

        this.notToBeGCd = chat;

        chat.sendMessage(new Message());
    }

    private static XMPPConnection connection(String hostname, int port, String username, String password) throws XMPPException {
        var connection = new XMPPConnection(new ConnectionConfiguration(hostname, port));
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);

        return connection;
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow();
            }
        });
    }
}
