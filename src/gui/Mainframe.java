package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import misc.Decision;
import misc.Period;
import search.Search;
import search.hillclimbing.Hillclimbing;
import search.monteCarlo.MonteCarlo;
import search.particleSwarm.ParticleSwarm;
import search.simpleTableComputation.SimpleTableComputation;

public class Mainframe extends JFrame {

	private static final long serialVersionUID = -7274316265207846835L;

	private JTextField fieldInterestRate;
	private JTextField fieldStartMoney;
	private JSpinner fieldPeriods;
	private JComboBox<String> comboBoxAlgorithm;

	private JButton buttonRun;
	private JButton buttonImport;
	private JButton buttonSettings;

	private JTable inputTable;
	private InputTableModell inputTableModell;

	public Mainframe() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(500, 235));
		setTitle("Super Rückträger");
		init();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void init() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(3, 3, 3, 3);

		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		fieldInterestRate = new JTextField();
		fieldInterestRate.setPreferredSize(new Dimension(75, 25));
		fieldInterestRate.setText("Interest Rate");
		layout.setConstraints(fieldInterestRate, gbc);
		add(fieldInterestRate);

		gbc.gridx = 1;
		fieldStartMoney = new JTextField();
		fieldStartMoney.setPreferredSize(new Dimension(75, 25));
		fieldStartMoney.setText("Start Money");
		layout.setConstraints(fieldStartMoney, gbc);
		add(fieldStartMoney);

		gbc.gridx = 2;
		fieldPeriods = new JSpinner();
		fieldPeriods.addChangeListener(periodChange());
		fieldPeriods.setPreferredSize(new Dimension(100, 25));
		fieldPeriods.setModel(new SpinnerNumberModel(3, 3, 1000, 1));
		layout.setConstraints(fieldPeriods, gbc);
		add(fieldPeriods);

		gbc.gridx = 0;

		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 3;
		gbc.gridy = 1;
		inputTable = new JTable();
		inputTableModell = new InputTableModell();
		inputTableModell.updatePeriodNumber((int) fieldPeriods.getValue());
		inputTable.setModel(inputTableModell);
		inputTable.setTableHeader(null);
		inputTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(inputTable);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		layout.setConstraints(scrollPane, gbc);
		add(scrollPane);

		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 0;
		gbc.weightx = 0;
		gbc.gridwidth = 1;
		gbc.gridy = 2;

		comboBoxAlgorithm = new JComboBox<>();
		comboBoxAlgorithm.addItem("Hillclimbing");
		comboBoxAlgorithm.addItem("Monte Carlo");
		comboBoxAlgorithm.addItem("Particle Swarm");
		comboBoxAlgorithm.addItem("Compute Table");
		layout.setConstraints(comboBoxAlgorithm, gbc);
		add(comboBoxAlgorithm);

		gbc.gridx = 1;
		buttonRun = new JButton("run");
		buttonRun.addActionListener(runAction());
		layout.setConstraints(buttonRun, gbc);
		add(buttonRun);

		gbc.gridy = 3;

		gbc.gridx = 0;
		buttonImport = new JButton("Import");
		buttonImport.addActionListener(importAction());
		layout.setConstraints(buttonImport, gbc);
		add(buttonImport);

		gbc.gridx = 1;
		buttonSettings = new JButton("Settings");
		buttonSettings.addActionListener(settingsAction());
		layout.setConstraints(buttonSettings, gbc);
		add(buttonSettings);
	}

	private ChangeListener periodChange() {
		ChangeListener periodChangeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() instanceof JSpinner) {
					JSpinner spinner = (JSpinner) e.getSource();
					inputTableModell.updatePeriodNumber((int) spinner
							.getValue());
					repaint();
				}
			}
		};
		return periodChangeListener;
	}

	private ActionListener runAction() {
		ActionListener runActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Integer.parseInt(fieldStartMoney.getText());
					Double.parseDouble(fieldInterestRate.getText());
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(Mainframe.this,
							"Error in field start money or interesst rate",
							"ERROR!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				List<Period> periods = new ArrayList<>();
				Period period = new Period(0, 0, 0, Decision.SHARED, 0); // dummy
																			// period
																			// containing
																			// the
																			// start
																			// money
				period.setPeriodMoney(Integer.valueOf(fieldStartMoney.getText()));
				periods.add(period);
				for (int i = 1; i < inputTableModell.getColumnCount(); i++) {
					Decision decision = (Decision) inputTableModell.getValueAt(
							2, i);
					period = new Period(Integer.valueOf(inputTableModell
							.getValueAt(0, i).toString()),
							Integer.valueOf(inputTableModell.getValueAt(1, i)
									.toString()), decision,
							Integer.valueOf(inputTableModell.getValueAt(3, i)
									.toString()));
					periods.add(period);
				}
				Search search = null;
				Properties properties = new Properties();
				try {
					properties.load(new FileInputStream(
							"etc/TaxOptimization.properties"));
				} catch (IOException e1) {
					File file = new File("etc/TaxOptimization.properties");
					try {
						FileOutputStream outputStream = new FileOutputStream(
								file);
						outputStream
								.write("particleswarm_phi_g=0.35000000000000003\nparticleswarm_phi_p=0.9\nparticleswarm_iterations=100\nhillclimb_stepsize=1.011\nhillclimb_completeOptimization=true\nparticleswarm_omega=0.89\nmontecarlo_iterations=10000\nparticleswarm_particles=1000"
										.getBytes());
						outputStream.close();
						properties.load(new FileInputStream(
								"etc/TaxOptimization.properties"));
					} catch (IOException e2) {
						JOptionPane.showMessageDialog(Mainframe.this,
								"failed to load properties file \n and error while trying to create default "
										+ e2.getMessage(), "ERROR",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				switch (comboBoxAlgorithm.getSelectedItem().toString()) {
				case "Hillclimbing":
					double stepSize = Double.valueOf(properties.get(
							"hillclimb_stepsize").toString());
					boolean completeOptimization = Boolean.valueOf(properties
							.getProperty("hillclimb_completeOptimization"));
					search = new Hillclimbing(periods,
							Double.valueOf(fieldInterestRate.getText()), true,
							stepSize, completeOptimization);
					break;
				case "Monte Carlo":
					int iterations = Integer.valueOf(properties.get(
							"montecarlo_iterations").toString());
					search = new MonteCarlo(periods,
							Float.valueOf(fieldInterestRate.getText()),
							iterations, true);
					break;
				case "Particle Swarm":
					iterations = Integer.valueOf(properties.get(
							"particleswarm_iterations").toString());
					int particles = Integer.valueOf(properties
							.getProperty("particleswarm_particles"));
					search = new ParticleSwarm(periods,
							Float.valueOf(fieldInterestRate.getText()),
							particles, iterations, true);
					break;
				case "Compute Table":
					search = new SimpleTableComputation(periods,
							Float.valueOf(fieldInterestRate.getText()), true);
					break;
				default:
					break;
				}
				search.start();
			}
		};
		return runActionListener;
	}

	private ActionListener importAction() {
		ActionListener importActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new ImportFrame(Mainframe.this);
			}
		};
		return importActionListener;
	}

	/**
	 * lists have to have the same size.
	 * decisions and carrybacks may be null.
	 */
	public void importValues(float interestRate, int startMoney, List<Integer> incomes, List<Integer> decisions, List<Integer> carrybacks) {
		fieldInterestRate.setText(String.valueOf(interestRate));
		fieldStartMoney.setText(String.valueOf(startMoney));
		fieldPeriods.setValue(incomes.size());
		inputTableModell.setValues(incomes, decisions, carrybacks);
	}
	
	private ActionListener settingsAction() {
		ActionListener settingsActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new SettingsFrame();
			}
		};
		return settingsActionListener;
	}

	public static void main(String[] args) {
		for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
			if (laf.getName().equals("Nimbus")) {
				try {
					UIManager.setLookAndFeel(laf.getClassName());
					UIManager.put("Table.alternateRowColor", new Color(195,
							211, 255));
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
			}
		}
		new Mainframe();
	}
}
