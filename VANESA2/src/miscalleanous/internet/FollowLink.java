package miscalleanous.internet;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FollowLink {

	private static final String errMsg = "Error attempting to launch web browser";

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*
		 * String osName = System.getProperty("os.name"); try { if
		 * (osName.startsWith("Mac OS")) { Class fileMgr =
		 * Class.forName("com.apple.eio.FileManager"); Method openURL =
		 * fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
		 * openURL.invoke(null, new Object[] { url }); } else if
		 * (osName.startsWith("Windows")) Runtime.getRuntime().exec(
		 * "rundll32 url.dll,FileProtocolHandler " + url); else { // assume Unix
		 * or Linux String[] browsers = { "firefox", "opera", "konqueror",
		 * "epiphany", "mozilla", "netscape" }; String browser = null; for (int
		 * count = 0; count < browsers.length && browser == null; count++) if
		 * (Runtime.getRuntime() .exec(new String[] { "which", browsers[count]
		 * }) .waitFor() == 0) browser = browsers[count]; if (browser == null)
		 * throw new Exception("Could not find web browser"); else
		 * Runtime.getRuntime().exec(new String[] { browser, url }); } } catch
		 * (Exception e) { JOptionPane.showMessageDialog(null, errMsg + ":\n" +
		 * e.getLocalizedMessage()); }
		 */
	}

}
