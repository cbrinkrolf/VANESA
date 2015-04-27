package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;



public class ProgressBar{
	
    	JProgressBar bar;
		int bar_max=0;
		Thread thread=null;
		boolean threading = false;
		JPanel mainPanel;
		//Blur blur;
		JPanel glass;
		
		public boolean isOpen  =false;
		public boolean wasClosed  = false;
		
		private boolean finished = false;
		private boolean interupted = false;
		
		public ProgressBar(Thread thread){
			bar = new JProgressBar();
			this.thread = thread;
			threading=true;
		}
		
		public ProgressBar() {
			bar = new JProgressBar();
		}
		
		public void init(int max, String info, boolean indeterminate){	
			//avoids problems when the bar will be closed before it gets initialised
			//(when running in multi threaded environment)
			if (!this.wasClosed) { 
				isOpen = true;
				glass = (JPanel) MainWindowSingleton.getInstance().getGlassPane();
				glass.setVisible(false);
				
				int width = 250;
				int height = 58;
				int labelHeight = 20;
				
				bar_max = max;
				
				JLabel label1 = new JLabel();
				label1.setFont(new Font(info, Font.BOLD, 12));
				label1.setBounds(3,5,width-6,labelHeight);
				label1.setText(info);
				label1.setForeground(Color.WHITE);
				label1.setVerticalTextPosition(SwingConstants.BOTTOM);
				label1.setHorizontalTextPosition(SwingConstants.CENTER);
				
				bar.setBackground(Color.WHITE);
				bar.setPreferredSize(new Dimension(width,20));
				bar.setBorderPainted(true);
				bar.setMaximum(bar_max);
				bar.setStringPainted(true);
				
				if(indeterminate){
					bar.setIndeterminate(true);	
				} else{
					bar.setStringPainted(true);
				}
				
				MigLayout layout = new MigLayout();
				mainPanel = new JPanel(layout);
				mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				mainPanel.setBackground(new Color(66,135,200));
	
				if(threading){
					mainPanel.add(label1, "span 1, align left");
					mainPanel.add(new CloseThreadButton(this), "span 1, align right,wrap");
					mainPanel.add(bar, "span, growx, wrap");
					this.setProgressBarString("");
				}else{
					mainPanel.add(label1, "span 1, align center, wrap");
					mainPanel.add(bar, "span, growx, wrap");
				}
				
			
				mainPanel.setSize(width, height);	
				
				glass.setLayout(new GridBagLayout());
				glass.add(mainPanel, new GridBagConstraints());
				glass.revalidate();
				glass.setVisible(true);
			}	
		}

		public void closeWindow() {
			this.isOpen = false;
			this.wasClosed = true;
			
			bar.setValue(bar_max);
			if (glass!=null) {
				glass.removeAll();
			//	glass.setVisible(false);
			//	w.setLockedPane(false);
			}
		
		}

		public void progess(int count){
			bar.setValue(count);
		}
		
		public void setProgressBarString(String text){
			
				bar.setString(text);
		}

		public Thread getThread() {
			return thread;
		}

		public void setThread(Thread thread) {
			this.thread = thread;
		}
		
		public void closeThread(){
			interupted = true;
			glass.removeAll();
			glass.setVisible(false);
//			thread.interrupt();
			thread.stop();
		}
		
		public boolean isFinished() {
			return finished;
		}
		
		public void run(){
//			glass.setVisible(true);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				System.err.println("Thread has been stopped!");
			}
			finished = !interupted;
//			closeWindow();
		}
		
		public void setVis(boolean vis){
			System.out.println("ProgressBar:setVis "+Thread.currentThread().toString());
			glass.setVisible(vis);
		}
		
		
}

