package gui.verification;

import biologicalElements.Pathway;
import net.miginfocom.swing.MigLayout;
import petriNet.verification.PNVerifier;
import petriNet.verification.VerificationIssue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class PNVerificationWindow extends JPanel {
	private final JPanel issuesListPanel = new JPanel(new MigLayout("fillx, wrap 2", "[grow, 0][]"));
	private final JButton verifyButton = new JButton("Verify");
	private final JButton solveAllButton = new JButton("Solve All");
	private final JLabel statusLabel = new JLabel();
	private final Pathway pathway;

	public PNVerificationWindow(final Pathway pathway) {
		super(new MigLayout("fill, wrap 2", "[grow][]", "[][][grow]"));
		this.pathway = pathway;
		verifyButton.addActionListener(e -> verify());
		add(verifyButton, "span 2");
		add(statusLabel, "growx");
		add(solveAllButton);
		add(new JScrollPane(issuesListPanel), "grow, span 2");
		verify();
	}

	private void verify() {
		for (final ActionListener solveAllListener : solveAllButton.getActionListeners()) {
			solveAllButton.removeActionListener(solveAllListener);
		}
		solveAllButton.setEnabled(false);
		verifyButton.setEnabled(false);
		issuesListPanel.removeAll();
		statusLabel.setForeground(Color.BLACK);
		statusLabel.setText("Verifying...");
		final var verifier = new PNVerifier(pathway);
		if (verifier.verify()) {
			statusLabel.setText("Petri net verification successful");
		} else {
			statusLabel.setForeground(Color.RED);
			statusLabel.setText("Petri net verification failed");
			final Map<VerificationIssue, JButton> issueButtonMap = new HashMap<>();
			for (final var issue : verifier.getIssues()) {
				issuesListPanel.add(new JLabel("<html>" + issue.getDescription() + "</html>"), "growx");
				if (issue.isAutoSolvable()) {
					final var solveButton = new JButton("Solve");
					issueButtonMap.put(issue, solveButton);
					solveButton.addActionListener(e -> {
						issueButtonMap.remove(issue);
						solveButton.setEnabled(false);
						issue.solve();
						solveButton.setText("Solved");
					});
					issuesListPanel.add(solveButton);
				} else {
					issuesListPanel.add(new JLabel());
				}
				issuesListPanel.add(new JSeparator(), "growx, span 2");
			}
			solveAllButton.addActionListener(e -> {
				for (final var issue : verifier.getIssues()) {
					if (issue.isAutoSolvable()) {
						final JButton solveButton = issueButtonMap.get(issue);
						if (solveButton != null) {
							issueButtonMap.remove(issue);
							solveButton.setEnabled(false);
							issue.solve();
							solveButton.setText("Solved");
						}
					}
				}
			});
			solveAllButton.setEnabled(true);
		}
		revalidate();
		repaint();
		verifyButton.setEnabled(true);
	}
}
