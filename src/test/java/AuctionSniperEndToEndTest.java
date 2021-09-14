import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.marshall.goos.ApplicationRunner;
import com.marshall.goos.FakeAuctionServer;

@Testcontainers
public class AuctionSniperEndToEndTest {
    @Container
    public GenericContainer ejabberd = new GenericContainer(DockerImageName.parse("ejabberd/ecs"))
            .withExposedPorts(5222)
            .waitingFor(Wait.forLogMessage(".*Start accepting TCP connections at.*", 1));

    private FakeAuctionServer auction;
    private ApplicationRunner application;

    @BeforeEach
    void setupFakeServer() throws Exception {
        System.out.println(ejabberd.execInContainer("bin/ejabberdctl", "register", "sniper", "localhost", "sniper"));
        System.out.println(ejabberd.execInContainer("bin/ejabberdctl", "register", "auction-item-54321", "localhost", "auction"));
        System.out.println(ejabberd.execInContainer("bin/ejabberdctl", "register", "auction-item-65432", "localhost", "auction"));

        auction = new FakeAuctionServer("item-54321", ejabberd.getMappedPort(5222));
        application = new ApplicationRunner(ejabberd.getMappedPort(5222));
    }

    @Test
    void sniperJoinsAuctionUntilAuctionClose() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @Test
    void sniperMakesAHigherBidButLoses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding();

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @AfterEach
    void stopAuction() {
        auction.stop();
    }

    @AfterEach
    void stopApplication() {
        application.stop();
    }
}
