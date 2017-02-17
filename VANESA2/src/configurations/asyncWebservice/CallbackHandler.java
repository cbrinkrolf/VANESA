/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

package configurations.asyncWebservice;

import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.axiom.soap.SOAPBody;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;

import gui.MainWindow;

/**
 * 
 * @author mwesterm
 */
public class CallbackHandler implements AxisCallback {
	private AsynchroneWebServiceWrapper webServiceWrapper = AsynchroneWebServiceWrapper
			.getInstance();
	private MessageContext messageContext = null;
	private UUID webServiceIdent = null;

	private Logger logger = Logger.getRootLogger();

	private MainWindow window = MainWindow.getInstance();

	public CallbackHandler(UUID webServiceIdent) {
		this.webServiceIdent = webServiceIdent;
	}

	@Override
	public void onMessage(MessageContext mc) {
		try {
			mc.flush();
		} catch (AxisFault ex) {
			logger.error(ex.getMessage());
		}

		this.messageContext = mc;
	}

	@Override
	public void onFault(MessageContext mc) {
		SOAPBody msg = (SOAPBody) mc.getEnvelope().getBody();
		final String error = new String("Fault detectet: " + msg + "\n"
				+ mc.getEnvelope());

		logger.error(error);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(window, error, "Fault",
						JOptionPane.ERROR_MESSAGE);
				window.closeProgressBar();
			}
		});
	}

	@Override
	public void onError(Exception ex) {
		final String message = ex.getMessage();
		logger.error(message);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(window, message, "Error",
						JOptionPane.ERROR_MESSAGE);
				window.closeProgressBar();
			}
		});
	}

	@Override
	public void onComplete() {
		WebServiceEvent event = new WebServiceEvent(messageContext.getEnvelope().getBody().getFirstElement() ,
				this.webServiceIdent);
		webServiceWrapper.fireEvent(event);

		// System.out.println("Ident: " + event.getWebServiceIdent().toString());
	}
}
