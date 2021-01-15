package miscalleanous.internet;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FollowLink {

	//private static final String errMsg = "Error attempting to launch web browser";

	static Desktop desktop = null;

	public static void openURL(String url) {

		URI uri = null;
		try {
			uri = new URI(url.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (Desktop.isDesktopSupported()) {

			desktop = Desktop.getDesktop();
			try {
				desktop.browse(uri);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
