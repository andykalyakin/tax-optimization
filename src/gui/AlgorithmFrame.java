package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

import misc.Period;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.EnumeratedData;
import de.erichseifert.gral.data.statistics.Histogram1D;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.areas.AreaRenderer;
import de.erichseifert.gral.plots.areas.DefaultAreaRenderer2D;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.Orientation;

/**
 * Base algorithm frame that is extended bz the algorithm frames for specific algorithms
 * @author chris
 * 
 */
public class AlgorithmFrame extends JFrame {

	private static final long serialVersionUID = -8287674932793764458L;

	protected List<Period> periods;
	private String algorithmName;

	private JTable periodTable;
	private PeriodTableModell periodTableModell;
	private JScrollPane scrollPaneConsole;
	private JTextPane console;
	private JButton buttonShowConsole;
	private JButton buttonExport;
	protected JPanel plotsPanel;

	protected final Dimension preferredPlotSize = new Dimension(200, 150);
	protected Dimension windowSize = new Dimension(900, 500);

	public AlgorithmFrame(List<Period> periods, String algorithmName) {
		this.periods = periods;
		this.algorithmName = algorithmName;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setPreferredSize(windowSize);
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

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		periodTable = new JTable();
		periodTableModell = new PeriodTableModell(periods);
		periodTable.setModel(periodTableModell);
		periodTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		periodTable.setTableHeader(null);
		JScrollPane scrollPane = new JScrollPane(periodTable);
		layout.setConstraints(scrollPane, gbc);
		add(scrollPane);

		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		buttonShowConsole = new JButton("Console");
		buttonShowConsole.addActionListener(actionShowConsole());
		layout.setConstraints(buttonShowConsole, gbc);
		add(buttonShowConsole);

		gbc.gridx = 1;
		buttonExport = new JButton("Export");
		buttonExport.addActionListener(actionExport());
		layout.setConstraints(buttonExport, gbc);
		add(buttonExport);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.BOTH;
		console = new JTextPane();
		scrollPaneConsole = new JScrollPane(console);
		scrollPaneConsole.setPreferredSize(new Dimension(300, 200));
		scrollPaneConsole.setVisible(false);
		layout.setConstraints(scrollPaneConsole, gbc);
		add(scrollPaneConsole);

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		plotsPanel = new JPanel();
		layout.setConstraints(plotsPanel, gbc);
		add(plotsPanel);

		pack();
		setVisible(true);
		setTitle(algorithmName);
	}

	private ActionListener actionShowConsole() {
		ActionListener actionShowConsole = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				scrollPaneConsole.setVisible(!scrollPaneConsole.isVisible());
			}
		};
		return actionShowConsole;
	}
	
	private ActionListener actionExport() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showDialog(AlgorithmFrame.this, "save") == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						FileOutputStream output = new FileOutputStream(file);
						for (int row = 0; row < periodTableModell.getRowCount(); row++) {
							for (int col = 0; col < periodTableModell.getColumnCount(); col++) {
								output.write(periodTableModell.getValueAt(row, col).toString().getBytes());
								output.write(",".getBytes());
							}
							output.write("\n".getBytes());
						}
						output.close();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(AlgorithmFrame.this, e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);;
					}
				}
			}
		};
	}

	/**
	 * updates the period view
	 * 
	 * @param periods
	 *            the new periods
	 */
	public void updatePeriodTable(List<Period> periods) {
		periodTableModell.updatePeriodTable(periods);
	}

	/**
	 * print a debug message to the console
	 * 
	 * @param message
	 *            the message to be printed
	 */
	public void printDebugMessage(String message) {
		console.setText(message);
	}

	/**
	 * append a debug message to the console
	 * 
	 * @param message
	 *            the message to be printed
	 */
	public void appendDebugMessage(String message) {
		console.setText(console.getText() + "\n\n" + message);
	}
	
	
	public enum PlotType {
		LinePlot, BarPlot
	}

	/**
	 * Render the default plot "outcome per period" and a algorithm specific plot if desired
	 * @param algorithmSpecificPlotData the data for the algorithm specific plot or null
	 */
	@SuppressWarnings("unchecked")
	public void updatePlots(DataSource algorithmSpecificPlotData, PlotType plotType, String plotTitle,
			String xAxisLabel, String yAxisLabel) {
		plotsPanel.removeAll();
		int plotCount= algorithmSpecificPlotData!=null? 2 : 1;
		plotsPanel.setLayout(new GridLayout(plotCount,1));
		plotsPanel.setPreferredSize(new Dimension(preferredPlotSize.width, preferredPlotSize.height*plotCount));
		plotsPanel.setMinimumSize(new Dimension(preferredPlotSize.width, preferredPlotSize.height*plotCount));
		
		// generate a plot for the outcome per period
		DataTable periodsData = new DataTable(Integer.class, Integer.class);
		for (int i=1; i<periods.size(); ++i)
			periodsData.add(i,periods.get(i).getPeriodMoney());
		plotsPanel.add(createLinePlot(periodsData, "Outcome per period", "Period", "Outcome", Color.blue));
		pack();
		
		if (algorithmSpecificPlotData!=null) {
			if (plotType==PlotType.LinePlot)
				plotsPanel.add(createLinePlot(algorithmSpecificPlotData, plotTitle, xAxisLabel, yAxisLabel, Color.green));
			else
				plotsPanel.add(createBarPlot(algorithmSpecificPlotData, plotTitle, xAxisLabel, yAxisLabel, Color.green));
			pack();
		}
	}

	protected JPanel createLinePlot(DataSource data, String title,
			String xAxisLabel, String yAxisLabel, Color color) {
		XYPlot plot = new XYPlot(data); // plot for the best outcomes per
										// iteration
		plot.getTitle().setText(title);
		// set the labels of the axes
		plot.getAxisRenderer(XYPlot.AXIS_X).setLabel(xAxisLabel);
		plot.getAxisRenderer(XYPlot.AXIS_Y).setLabel(yAxisLabel);
		plot.getAxisRenderer(XYPlot.AXIS_Y).setLabelDistance(4);
		// make a margin for the axes and labels
		plot.setInsets(new Insets2D.Double(20.0, 100.0, 60.0, 40.0));
		LineRenderer lines = new DefaultLineRenderer2D();
		plot.setLineRenderer(data, lines);
		plot.setPointRenderer(data, null);
		plot.getLineRenderer(data).setColor(color);
		// draw the axes outside of the plot
		plot.getAxisRenderer(XYPlot.AXIS_X).setIntersection(-Double.MAX_VALUE);
		plot.getAxisRenderer(XYPlot.AXIS_Y).setIntersection(-Double.MAX_VALUE);
		// set the drawn area if the value never changed (all values are the
		// same). Otherwise it would render an empty range
		if (data.get(1, 0).equals(data.get(1, data.getRowCount() - 1))) {
			int value = Integer.valueOf(data.get(1, 0).toString());
			if (value >= 0)
				plot.getAxis(XYPlot.AXIS_Y).setRange(0, 2.1 * (value + 1));
			else
				plot.getAxis(XYPlot.AXIS_Y).setRange(2.1 * (value + 1), 0);
		} else { // otherwise we have a range
			// we want to enlarge the range a litle bit
			float min = plot.getAxis(XYPlot.AXIS_Y).getMin().floatValue();
			float max = plot.getAxis(XYPlot.AXIS_Y).getMax().floatValue();
			float offset = (max-min)*0.2f;
			plot.getAxis(XYPlot.AXIS_Y).setRange(min-offset, max+offset);
		}
		// draw an area between the graph and the x axis
		AreaRenderer area = new DefaultAreaRenderer2D();
		area.setColor(GraphicsUtils.deriveWithAlpha(color, 64));
		plot.setAreaRenderer(data, area);
		InteractivePanel plotPanel = new InteractivePanel(plot);
		plotPanel.setPreferredSize(preferredPlotSize);
		plotPanel.setMinimumSize(preferredPlotSize);
		return plotPanel;
	}
	
	
	protected JPanel createBarPlot(DataSource data, String title,
			String xAxisLabel, String yAxisLabel, Color color) {
		// Create histogram from data with 10 bars
		final int numberOfBars=10;
		double min = data.getStatistics().get(Statistics.MIN);
		double max = data.getStatistics().get(Statistics.MAX);
		double interval = (max - min)/numberOfBars;
		if (interval==0) { // all values are the same -> set some arbitrary interval to create multiple 0 bars
			interval= min / numberOfBars;
			max = min+numberOfBars*interval;
		}
		Number[] limits = new Number[numberOfBars+1];
		for (int i=0; i<=numberOfBars; ++i)
			limits[i]=min+i*interval+(i==numberOfBars?1:0);
		Histogram1D histogram = new Histogram1D(data, Orientation.VERTICAL, limits);
		// Create a second dimension (x axis) for plotting
		DataSource histogram2d = new EnumeratedData(histogram, (min + min+interval)/2.0, interval);

		// Create new bar plot
		BarPlot plot = new BarPlot(histogram2d);

		// Format plot
		plot.setInsets(new Insets2D.Double(20.0, 85.0, 80.0, 40.0));
		plot.getTitle().setText(title);
		plot.setBarWidth(interval*0.975); // *0.975 to have a small gap between bars

		// Format x axis
		plot.getAxis(BarPlot.AXIS_X).setRange(min, max+0.01*interval);
		plot.getAxisRenderer(BarPlot.AXIS_X).setLabel(xAxisLabel);
		plot.getAxisRenderer(BarPlot.AXIS_X).setLabelDistance(2.7);
		plot.getAxisRenderer(BarPlot.AXIS_X).setTickAlignment(0.0);
		// we don't want to show the ticks but use custom ticks instead
		plot.getAxisRenderer(BarPlot.AXIS_X).setTickSpacing(2*max);
		plot.getAxisRenderer(BarPlot.AXIS_X).setTickLabelRotation(40);
		plot.getAxisRenderer(BarPlot.AXIS_X).setTickLabelDistance(0);
		plot.getAxisRenderer(BarPlot.AXIS_X).setMinorTicksVisible(false);
		HashMap<java.lang.Double,java.lang.String> customXAxisTicks = new HashMap<Double, String>();
		for (int i=0; i<=numberOfBars; ++i)
			customXAxisTicks.put(min+i*interval, String.valueOf(min+i*interval));
		plot.getAxisRenderer(BarPlot.AXIS_X).setCustomTicks(customXAxisTicks);
		// Format y axis
		plot.getAxis(BarPlot.AXIS_Y).setRange(0.0,
				MathUtils.ceil(histogram.getStatistics().get(Statistics.MAX)*1.3, 25.0));
		plot.getAxisRenderer(BarPlot.AXIS_Y).setLabel(yAxisLabel);
		plot.getAxisRenderer(BarPlot.AXIS_Y).setLabelDistance(2);
		plot.getAxisRenderer(BarPlot.AXIS_Y).setTickAlignment(0.0);
		plot.getAxisRenderer(BarPlot.AXIS_Y).setMinorTicksVisible(false);
		plot.getAxisRenderer(BarPlot.AXIS_Y).setIntersection(min);

		// Format bars
		plot.getPointRenderer(histogram2d).setColor(
				GraphicsUtils.deriveWithAlpha(color, 128));
		plot.getPointRenderer(histogram2d).setValueVisible(true);
		plot.getPointRenderer(histogram2d).setValueAlignmentY(7);

		InteractivePanel panel = new InteractivePanel(plot);
		panel.setPannable(false);
		panel.setZoomable(false);
		panel.setPreferredSize(preferredPlotSize);
		panel.setMinimumSize(preferredPlotSize);
		return panel;
	}
}
