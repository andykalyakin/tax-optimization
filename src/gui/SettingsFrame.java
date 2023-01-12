package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class SettingsFrame extends JFrame {

	private static final long serialVersionUID = 5960120851692155057L;

	private Properties properties;
	
	private JSpinner hcStepSize;
	private JCheckBox hcCompleteOptimization;
	private JSpinner mcIterations;
	private JSpinner psIterations;
	private JSpinner psparticles;
	private JSpinner psPhiP;
	private JSpinner psPhiG;
	private JSpinner psOmega;
	
	private JButton buttonSave;
	
	public SettingsFrame() {
		setPreferredSize(new Dimension(640, 480));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		properties = new Properties();
		try {
			properties.load(new FileInputStream("etc/TaxOptimization.properties"));
			init();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Unable to find properties file " + e.getMessage());
		}
	}
	
	private void init() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.insets = new Insets(3, 3, 3, 3);
		gbc.gridx = 0;
		
		gbc.gridy = 0;
		JLabel labelHcStepSize = new JLabel("Hillclimb Iterations");
		layout.setConstraints(labelHcStepSize, gbc);
		add(labelHcStepSize);
		
		gbc.gridy = 1;
		JLabel labelHcCompleteOptimization = new JLabel("Hillclimb complete Optimization");
		layout.setConstraints(labelHcCompleteOptimization, gbc);
		add(labelHcCompleteOptimization);
		
		gbc.gridy = 2;
		JLabel labelMcIterations = new JLabel("Monte Carlo Iterations");
		layout.setConstraints(labelMcIterations, gbc);
		add(labelMcIterations);
		
		gbc.gridy = 3;
		JLabel labelPsIterations = new JLabel("Particle Swarm Iterations");
		layout.setConstraints(labelPsIterations, gbc);
		add(labelPsIterations);
		
		gbc.gridy = 4;
		JLabel labelPsParticles = new JLabel("Particle Swarm particles");
		layout.setConstraints(labelHcStepSize, gbc);
		add(labelPsParticles);
		
		gbc.gridy = 5;
		JLabel labelPsPhiP = new JLabel("Particle Swarm phi p");
		layout.setConstraints(labelPsPhiP, gbc);
		add(labelPsPhiP);

		gbc.gridy = 6;
		JLabel labelPsPhiG = new JLabel("Particle Swarm phi g");
		layout.setConstraints(labelPsPhiG, gbc);
		add(labelPsPhiG);
		
		gbc.gridy = 7;
		JLabel labelPsOmega = new JLabel("Particle Swarm omega");
		layout.setConstraints(labelPsOmega, gbc);
		add(labelPsOmega);
		
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		gbc.gridy = 0;
		hcStepSize = new JSpinner(new SpinnerNumberModel((double) Double.valueOf(properties.get("hillclimb_stepsize").toString()), 1.001, 2, 0.001));
		layout.setConstraints(hcStepSize, gbc);
		add(hcStepSize);
	
		gbc.gridy = 1;
		hcCompleteOptimization = new JCheckBox("", Boolean.valueOf(properties.getProperty("hillclimb_completeOptimization")));
		layout.setConstraints(hcCompleteOptimization, gbc);
		add(hcCompleteOptimization);
		
		
		gbc.gridy = 2;
		mcIterations = new JSpinner(new SpinnerNumberModel((int) Integer.valueOf(properties.get("montecarlo_iterations").toString()), 1, 100000, 100));
		layout.setConstraints(mcIterations, gbc);
		add(mcIterations);
		
		gbc.gridy = 3;
		psIterations = new JSpinner(new SpinnerNumberModel((int) Integer.valueOf(properties.get("particleswarm_iterations").toString()), 1, 100000, 10));
		layout.setConstraints(psIterations, gbc);
		add(psIterations);
		
		gbc.gridy = 4;
		psparticles = new JSpinner(new SpinnerNumberModel((int) Integer.valueOf(properties.get("particleswarm_particles").toString()), 1, 100000, 10));
		layout.setConstraints(psparticles, gbc);
		add(psparticles);
		
		gbc.gridy = 5;
		psPhiP = new JSpinner(new SpinnerNumberModel((double) Double.valueOf(properties.get("particleswarm_phi_p").toString()), 0.1, 1, 0.1));
		layout.setConstraints(psPhiP, gbc);
		add(psPhiP);
		
		gbc.gridy = 6;
		psPhiG = new JSpinner(new SpinnerNumberModel((double) Double.valueOf(properties.get("particleswarm_phi_g").toString()), 0.1, 1, 0.01));
		layout.setConstraints(psPhiG, gbc);
		add(psPhiG);
		
		gbc.gridy = 7;
		psOmega = new JSpinner(new SpinnerNumberModel((double) Double.valueOf(properties.get("particleswarm_omega").toString()), 0.1, 1, 0.01));
		layout.setConstraints(psOmega, gbc);
		add(psOmega);
		
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 2;
		
		buttonSave = new JButton("Save");
		buttonSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveAction();
			}
		});
		layout.setConstraints(buttonSave, gbc);
		add(buttonSave);
		
		pack();
		setVisible(true);
	}
	
	private void saveAction() {
		properties.put("hillclimb_stepsize", String.valueOf(hcStepSize.getValue()));
		properties.put("hillclimb_completeOptimization", String.valueOf(hcCompleteOptimization.isSelected()));
		properties.put("montecarlo_iterations", String.valueOf(mcIterations.getValue()));
		properties.put("particleswarm_iterations", String.valueOf(psIterations.getValue()));
		properties.put("particleswarm_particles", String.valueOf(psparticles.getValue()));
		properties.put("particleswarm_phi_p", String.valueOf(psPhiP.getValue()));
		properties.put("particleswarm_phi_g", String.valueOf(psPhiG.getValue()));
		properties.put("particleswarm_omega", String.valueOf(psOmega.getValue()));
		try {
			properties.store(new FileOutputStream("etc/TaxOptimization.properties"), null);
			// close the frame
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
