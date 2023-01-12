package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class ImportFrame extends JFrame {
	private static final long serialVersionUID = 8728007945159347982L;

	Mainframe mainFrame;
	
	private String[][] table; // table[row][column]
	
	private boolean importDecisionsAndCarrybacks = false;
	
	private JTextField fieldFileName;
	private JTextField fieldInterestRate;
	private JTextField fieldStartMoney;
	private JTextField fieldIncomesFrom;
	private JTextField fieldIncomesTo;
	private JTextField fieldDecisionsFrom;
	private JTextField fieldDecisionsTo;
	private JTextField fieldCarrybacksFrom;
	private JTextField fieldCarrybacksTo;
		
	private JTable importTable;
	private ImportTableModell importTableModell;
	
	private final JFileChooser fileChooser = new JFileChooser();
	
	FocusListener focusListener;
	
	public ImportFrame(Mainframe mainframe) {
		this.mainFrame = mainframe;
		setPreferredSize(new Dimension(800, 480));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		init();
		pack();
		checkAllCoordinateInputs();
		this.setTitle("Import");
		setVisible(true);
		checkAllCoordinateInputs();
	}
	
	
	private void init() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.insets = new Insets(3, 3, 3, 3);

		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		gbc.gridx = 0;
		JLabel labelFileName = new JLabel("File:");
		layout.setConstraints(labelFileName, gbc);
		add(labelFileName);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		fieldFileName = new JTextField();
		fieldFileName.setPreferredSize(new Dimension(150, 25));
		fieldFileName.setText("");
		layout.setConstraints(fieldFileName, gbc);
		fieldFileName.setEnabled(false);
		add(fieldFileName);
		
		gbc.gridx = 2;
		gbc.weightx = 0.0;
		JButton buttonSelectFile = new JButton("Choose file");
		buttonSelectFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal==JFileChooser.APPROVE_OPTION) {
					String fileName = "Could not open file.";
					try {
						fileName = fileChooser.getSelectedFile().getCanonicalPath();
					} catch (IOException e1) {}
					fieldFileName.setText(fileName);
					openTable(fileName);
				}
			}
		});
		layout.setConstraints(buttonSelectFile, gbc);
		add(buttonSelectFile);
		
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		importTable = new JTable();
		importTableModell = new ImportTableModell();
		importTable.setModel(importTableModell);
		importTable.setTableHeader(null);
		importTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(importTable);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		layout.setConstraints(scrollPane, gbc);
		add(scrollPane);
		
		gbc.gridx = 2;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		JPanel rightPanel = new JPanel();
		layout.setConstraints(rightPanel, gbc);
		initRightPanel(rightPanel);
		add(rightPanel);
		
		gbc.gridy = 2;
		gbc.gridx = 2;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JButton buttonOkay = new JButton("Import");
		buttonOkay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performImport();
			}
		});
		layout.setConstraints(buttonOkay, gbc);
		add(buttonOkay);
	}
	
	
	private void initRightPanel(JPanel rightPanel) {
		GridBagLayout righPanelLayout = new GridBagLayout();
		rightPanel.setLayout(righPanelLayout);
		
		GridBagConstraints gbcRight = new GridBagConstraints();
		gbcRight.fill = GridBagConstraints.HORIZONTAL;
		
		// interest rate
		gbcRight.gridy = 0;
		gbcRight.gridx = 0;
		JLabel labelInterestRate = new JLabel("Interest Rate:");
		righPanelLayout.setConstraints(labelInterestRate, gbcRight);
		rightPanel.add(labelInterestRate);
		
		gbcRight.gridx = 1;
		fieldInterestRate = new JTextField();
		fieldInterestRate.setPreferredSize(new Dimension(150, 25));
		fieldInterestRate.setText("A1");
		righPanelLayout.setConstraints(fieldInterestRate, gbcRight);
		fieldInterestRate.addFocusListener(getFocusListener());
		rightPanel.add(fieldInterestRate);
		
		// start money
		gbcRight.gridy = 1;
		gbcRight.gridx = 0;
		JLabel labelStartMoney = new JLabel("Start Money:");
		righPanelLayout.setConstraints(labelStartMoney, gbcRight);
		rightPanel.add(labelStartMoney);
		
		gbcRight.gridx = 1;
		fieldStartMoney = new JTextField();
		fieldStartMoney.setPreferredSize(new Dimension(150, 25));
		fieldStartMoney.setText("A2");
		righPanelLayout.setConstraints(fieldStartMoney, gbcRight);
		fieldStartMoney.addFocusListener(getFocusListener());
		rightPanel.add(fieldStartMoney);

		// incomes
		gbcRight.gridy = 2;
		gbcRight.gridx = 0;
		JLabel labelIncomes = new JLabel("Incomes:");
		righPanelLayout.setConstraints(labelIncomes, gbcRight);
		rightPanel.add(labelIncomes);
		
		gbcRight.gridx = 1;
		gbcRight.weightx = 0.5;
		fieldIncomesFrom = new JTextField();
		fieldIncomesFrom.setPreferredSize(new Dimension(150, 25));
		fieldIncomesFrom.setText("A3");
		righPanelLayout.setConstraints(fieldIncomesFrom, gbcRight);
		fieldIncomesFrom.addFocusListener(getFocusListener());
		rightPanel.add(fieldIncomesFrom);
		
		gbcRight.gridx = 2;
		gbcRight.weightx = 0.0;
		JLabel labelTo1 = new JLabel("to");
		righPanelLayout.setConstraints(labelTo1, gbcRight);
		rightPanel.add(labelTo1);
		
		gbcRight.gridx = 3;
		gbcRight.weightx = 0.5;
		fieldIncomesTo = new JTextField();
		fieldIncomesTo.setPreferredSize(new Dimension(150, 25));
		fieldIncomesTo.setText("C3");
		righPanelLayout.setConstraints(fieldIncomesTo, gbcRight);
		fieldIncomesTo.addFocusListener(getFocusListener());
		rightPanel.add(fieldIncomesTo);
		
		// checkbox
		gbcRight.gridy = 3;
		gbcRight.gridx = 0;
		gbcRight.gridwidth = 4;
		JCheckBox checkBox = new JCheckBox("Import decisions and carrybacks.");
		checkBox.setSelected(importDecisionsAndCarrybacks);
		righPanelLayout.setConstraints(checkBox, gbcRight);
		rightPanel.add(checkBox);
		
		// decisions
		gbcRight.gridy = 4;
		gbcRight.gridx = 0;
		gbcRight.gridwidth = 1;
		gbcRight.weightx = 0.0;
		final JLabel labelDecisions = new JLabel("Decisions:");
		righPanelLayout.setConstraints(labelDecisions, gbcRight);
		labelDecisions.setEnabled(importDecisionsAndCarrybacks);
		rightPanel.add(labelDecisions);
		
		gbcRight.gridx = 1;
		fieldDecisionsFrom = new JTextField();
		fieldDecisionsFrom.setPreferredSize(new Dimension(150, 25));
		fieldDecisionsFrom.setText("A4");
		righPanelLayout.setConstraints(fieldDecisionsFrom, gbcRight);
		fieldDecisionsFrom.setEnabled(importDecisionsAndCarrybacks);
		fieldDecisionsFrom.addFocusListener(getFocusListener());
		rightPanel.add(fieldDecisionsFrom);
		
		gbcRight.gridx = 2;
		final JLabel labelTo2 = new JLabel("to");
		righPanelLayout.setConstraints(labelTo2, gbcRight);
		labelTo2.setEnabled(importDecisionsAndCarrybacks);
		rightPanel.add(labelTo2);
		
		gbcRight.gridx = 3;
		fieldDecisionsTo = new JTextField();
		fieldDecisionsTo.setPreferredSize(new Dimension(150, 25));
		fieldDecisionsTo.setText("C4");
		righPanelLayout.setConstraints(fieldDecisionsTo, gbcRight);
		fieldDecisionsTo.setEnabled(importDecisionsAndCarrybacks);
		fieldDecisionsTo.addFocusListener(getFocusListener());
		rightPanel.add(fieldDecisionsTo);
		
		// carrybacks
		gbcRight.gridy = 5;
		gbcRight.gridx = 0;
		gbcRight.weightx = 0.0;
		final JLabel labelCarrybacks = new JLabel("Carrybacks:");
		righPanelLayout.setConstraints(labelCarrybacks, gbcRight);
		labelCarrybacks.setEnabled(importDecisionsAndCarrybacks);
		rightPanel.add(labelCarrybacks);

		gbcRight.gridx = 1;
		fieldCarrybacksFrom = new JTextField();
		fieldCarrybacksFrom.setPreferredSize(new Dimension(150, 25));
		fieldCarrybacksFrom.setText("A5");
		righPanelLayout.setConstraints(fieldCarrybacksFrom, gbcRight);
		fieldCarrybacksFrom.setEnabled(importDecisionsAndCarrybacks);
		fieldCarrybacksFrom.addFocusListener(getFocusListener());
		rightPanel.add(fieldCarrybacksFrom);

		gbcRight.gridx = 2;
		final JLabel labelTo3 = new JLabel("to");
		righPanelLayout.setConstraints(labelTo3, gbcRight);
		labelTo3.setEnabled(importDecisionsAndCarrybacks);
		rightPanel.add(labelTo3);

		gbcRight.gridx = 3;
		fieldCarrybacksTo = new JTextField();
		fieldCarrybacksTo.setPreferredSize(new Dimension(150, 25));
		fieldCarrybacksTo.setText("C5");
		righPanelLayout.setConstraints(fieldCarrybacksTo, gbcRight);
		fieldCarrybacksTo.setEnabled(importDecisionsAndCarrybacks);
		fieldCarrybacksTo.addFocusListener(getFocusListener());
		rightPanel.add(fieldCarrybacksTo);
		
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				importDecisionsAndCarrybacks = !importDecisionsAndCarrybacks;
				labelDecisions.setEnabled(importDecisionsAndCarrybacks);
				fieldDecisionsFrom.setEnabled(importDecisionsAndCarrybacks);
				labelTo2.setEnabled(importDecisionsAndCarrybacks);
				fieldDecisionsTo.setEnabled(importDecisionsAndCarrybacks);
				labelCarrybacks.setEnabled(importDecisionsAndCarrybacks);
				fieldCarrybacksFrom.setEnabled(importDecisionsAndCarrybacks);
				labelTo3.setEnabled(importDecisionsAndCarrybacks);
				fieldCarrybacksTo.setEnabled(importDecisionsAndCarrybacks);
				if (importDecisionsAndCarrybacks)
					checkAllCoordinateInputs();
			}
		});
	}
	
	
	private void openTable(String fileName) {
		LinkedList<String[]> lines = new LinkedList<String[]>();
		int longestLineLength = 0;
		
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		try {
			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) {
				String[] lineEntries = line.split(cvsSplitBy);
				lines.add(lineEntries);
				if (longestLineLength < lineEntries.length)
					longestLineLength = lineEntries.length;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Could not find or open file.", "ERROR!", JOptionPane.ERROR_MESSAGE);
			table = null;
			importTableModell.setTable(null);
			checkAllCoordinateInputs();
			return;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
		}
		
		// make the table rectangular (all lines the same length) and trim the strings
		table = new String[lines.size()][longestLineLength];
		for (int i=0; i<lines.size(); ++i) {
			for (int j=0; j<longestLineLength; ++j) {
				if (j<lines.get(i).length)
					table[i][j]=lines.get(i)[j].trim();
				else
					table[i][j]="";
			}
		}
		importTableModell.setTable(table);
		checkAllCoordinateInputs();
	}
	
	
	/** 
	 * eg: (0,0) for A1, (2,4) for C5, (26,4) for AA5
	 * returns null if an error occured
	 */
	private Point getCoordinateForString(String coordinateAsString) {
		int x, y;
		int splitPosition=0;
		for (int i=0; i<coordinateAsString.length(); ++i) {
			if (Character.isDigit(coordinateAsString.charAt(i))) {
				splitPosition=i;
				break;
			}
		}
		if (splitPosition==0)
			return null;
		
		// set y
		try {
			y = Integer.valueOf(coordinateAsString.substring(splitPosition))-1;
		} catch (Exception e) {
			return null;
		}
		
		// set x
		x=0;
		for (int i=0; i<splitPosition; ++i) {
			if (!Character.isAlphabetic(coordinateAsString.charAt(i)))
				return null;
			char c = Character.toUpperCase(coordinateAsString.charAt(i));
			// this formula is probably wrong for columns > ZZ
			x+= (c-'A'+1)*(int)Math.pow(26,(splitPosition-1-i));
		}
		x--;
		
		return new Point(x,y);
	}
	
	
	/**
	 * Checks whether p is an allowed coordinate on the table
	 */
	private boolean isAllowedCoordinate(Point p) {
		if (p==null)
			return false;
		if (table==null)
			return false;
		if (p.x>=table[0].length) // (all rows have the same length)
			return false;
		if (p.y>=table.length)
			return false;
		return true;
	}
	
	
	/**
	 * returns true, if all entered coordinates are allowed
	 */
	private boolean checkAllCoordinateInputs() {
		JTextField[] inputs;
		if (importDecisionsAndCarrybacks)
			inputs = new JTextField[]{fieldInterestRate,fieldStartMoney,fieldIncomesFrom,fieldIncomesTo,
				fieldDecisionsFrom,fieldDecisionsTo,fieldCarrybacksFrom,fieldCarrybacksTo};
		else
			inputs = new JTextField[]{fieldInterestRate,fieldStartMoney,fieldIncomesFrom,fieldIncomesTo};
		boolean result = true;
		for (JTextField input : inputs) {
			boolean isAllowed = isAllowedCoordinate(getCoordinateForString(input.getText()));
			result &= isAllowed;
			if (!isAllowed)
				input.setBackground(Color.pink);
			else
				input.setBackground(Color.white);
		}
		return result;
	}
	
	
	private FocusListener getFocusListener() {
		if (focusListener==null)
			focusListener =  new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					if (e.getSource() instanceof JTextField) {
						JTextField textField = (JTextField)e.getSource();
						if (!isAllowedCoordinate(getCoordinateForString(textField.getText())))
							textField.setBackground(Color.pink);
						else
							textField.setBackground(Color.white);
					}
				}
				
				@Override
				public void focusGained(FocusEvent arg0) {
					// nothing to do
				}
			};
		return focusListener;
	}
	
	
	private void performImport() {
		if (table==null) {
			JOptionPane.showMessageDialog(this, "Please open a file first.", "ERROR!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!checkAllCoordinateInputs()) {
			JOptionPane.showMessageDialog(this, "Some of your inputs are not allowed. They are marked red.", "ERROR!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			// set the values
			Point coord = getCoordinateForString(fieldInterestRate.getText());
			float interestRate = Float.valueOf(table[coord.y][coord.x]);
			coord = getCoordinateForString(fieldStartMoney.getText());
			int startMoney = Integer.valueOf(table[coord.y][coord.x]);
			
			List<Integer> incomes = retrieveArea(fieldIncomesFrom.getText(), fieldIncomesTo.getText());
			if (incomes==null)
				return;
			
			List<Integer> decisions=null;
			List<Integer> carrybacks=null;
			if (importDecisionsAndCarrybacks) {
				decisions = retrieveArea(fieldDecisionsFrom.getText(), fieldDecisionsTo.getText());
				carrybacks = retrieveArea(fieldCarrybacksFrom.getText(), fieldCarrybacksTo.getText());
				if (decisions==null || carrybacks==null)
					return;
				for (int decision : decisions) 
					if (decision!=0 && decision!=1) {
						JOptionPane.showMessageDialog(this, "For decisions 0 and 1 are the only allowed values.", "ERROR!", JOptionPane.ERROR_MESSAGE);
						return;
					}
				
				if (incomes.size()!=decisions.size() || decisions.size()!=carrybacks.size()) {
					JOptionPane.showMessageDialog(this, "You supplied a different amount of incomes / decisions / carrybacks.", "ERROR!", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			
			mainFrame.importValues(interestRate, startMoney, incomes, decisions, carrybacks);
			
			// close the frame
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "One of your supplied values in the table is not an allowed number.", "ERROR!", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * returns null if an error occured.
	 * Might throw a number format exception
	 */
	private List<Integer> retrieveArea(String from, String to) {
		Point coordFrom = getCoordinateForString(from);
		Point coordTo = getCoordinateForString(to);
		if (coordFrom.x!=coordTo.x && coordFrom.y!=coordTo.y) { // must be aligned along x or y axis
			JOptionPane.showMessageDialog(this, "Incomes, decisions and carrybacks must be aligned along a row or column.", "ERROR!", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		List<Integer> result = new LinkedList<Integer>();
		if (coordFrom.x!=coordTo.x) {
			for (int x=coordFrom.x; x<=coordTo.x; ++x)
				result.add(Integer.valueOf(table[coordFrom.y][x]));
		} else {
			for (int y=coordFrom.y; y<=coordTo.y; ++y)
				result.add(Integer.valueOf(table[y][coordFrom.x]));
		}
		return result;
	}
}
