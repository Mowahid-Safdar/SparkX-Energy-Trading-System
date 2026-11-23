
package sparkX;

import javax.swing.SwingUtilities;

import sparkX.data.MarketplaceData;
import sparkX.gui.WelcomeScreen;

public class SparkXApp {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            MarketplaceData marketplaceData =
                new MarketplaceData();

            new WelcomeScreen(marketplaceData);

        });
    }
}
