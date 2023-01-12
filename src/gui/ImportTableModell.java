package gui;

import javax.swing.table.AbstractTableModel;

public class ImportTableModell extends AbstractTableModel {
	private static final long serialVersionUID = -6322474656228249548L;
	String[][] table;
	
	public void setTable(String[][] table) {
		this.table = table;
		fireTableStructureChanged();
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	@Override
	public int getColumnCount() {
		return table==null? 0 : table[0].length+1; // all rows have same length
	}

	@Override
	public int getRowCount() {
		return table==null? 0 : table.length+1;
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (row==0)
			return getColumnString(column);
		if (column==0)
			return row;
		return table[row-1][column-1];
	}

	/**
	 * eg: 3:C, 26:Z, 27:AA, 28:AB, 52:AZ, 53:BA
	 */
	private String getColumnString(int column) {
		String result = "";
		column--;
		// this algorithms is probably wrong for columns > ZZ
		while (column>=0) {
			char currentChar = (char)('A'+((column) % 26));
			result = currentChar + result;
			column = column / 26 - 1; // -1 because this is not a numeric system (eg AA!=A , different to 00==0)
		}
	/*	while (column>0) {
			int i = column/27;
			char currentChar = (char) ('A'+column - i*26 -1);
			result = currentChar+result;
			column = i;
		}*/
		return result;
	}
}