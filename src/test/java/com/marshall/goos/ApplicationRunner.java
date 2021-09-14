package com.marshall.goos;

public class ApplicationRunner {
    public static final String XMPP_HOSTNAME = "localhost";
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";

    private final int xmppPort;

    private AuctionSniperDriver driver;

    public ApplicationRunner(int xmppPort) {
        this.xmppPort = xmppPort;
    }

    public void startBiddingIn(final FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, Integer.toString(xmppPort), SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.setDaemon(true);
        thread.start();

        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(SniperStatus.JOINING);
    }

    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(SniperStatus.BIDDING);
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(SniperStatus.LOST);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}
