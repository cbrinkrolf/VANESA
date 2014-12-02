package graph.jung.graphDrawing;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

import petriNet.Place;

public class DynamicIcon implements Icon {

	private Place p;

	public DynamicIcon(Place p) {
		this.p = p;
	}

	public int getIconWidth() {
		return 5;
	}

	public int getIconHeight() {
		return 5;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		// g.fill3DRect(x, y, getIconWidth(), getIconHeight(), true);
		// g.setColor(new Color(0,0,0));
		// g.drawRect(x, y, 20, 30);
		// g.drawString("bla", x, y);
		// g.drawString("blabla", 0, 0);

		// /Component component1 =null;
		// if (p.isDiscrete())
		// component1 = prepareRenderer(graphLabelRenderer, (int)p.getToken() ,
		// isPicked(v), v);
		// else component1 = prepareRenderer(graphLabelRenderer, new
		// DecimalFormat("#0.00").format(p.getToken()), isPicked(v), v);

		// Dimension d1 = c.getPreferredSize();
		// int h_offset = -d1.width / 2;
		// int v_offset = -d1.height / 2;

		// rendererPane.paintComponent(g, component1, screenDevice, x+h_offset,
		// y+v_offset,
		// d1.width, d1.height, true);
		int x1 = (int) (p.getShape().getBounds2D().getMaxX() - p.getShape()
				.getBounds2D().getMinX());
		//int y1 = (int) (p.getShape().getBounds2D().getMaxY() - p.getShape()
		//		.getBounds2D().getMinY());

		// double x1 = c.getBounds().getMaxX()-c.getBounds().getMinX();
		// double y1 = c.getBounds().getMaxY()-c.getBounds().getMinY();

		boolean discrete = false;
		String tokens = p.getToken() + "";
		if (p.isDiscrete()) {
			tokens = (int) p.getToken() + "";
			discrete = true;
		}

		if (p.hasRef() && p.getRef() instanceof Place) {
			tokens = ((Place) p.getRef()).getToken() + "";
			if (((Place) p.getRef()).isDiscrete()) {
				tokens = (int) ((Place) p.getRef()).getToken() + "";
				discrete = true;
			}
		}
		
		int xpos;
		
		if(discrete){
		xpos = new Double(x - x1 + 19 - 5 * ((double) tokens.length() / 2))
				.intValue();
		}else{
			xpos = new Double(x - x1 + 21 - 5 * ((double) tokens.length() / 2))
			.intValue();
		}
		g.drawString(tokens, xpos, y + 7);

		// }
	}

}
