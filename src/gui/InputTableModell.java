package gui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import misc.Decision;

public class InputTableModell extends AbstractTableModel {

	private static final long serialVersionUID = 5580172268854002274L;

	private List<Integer> periods;
	private List<Integer> incomes;
	private List<Integer> decisions;
	private List<Integer> lossCarryback;

	public InputTableModell() {
		periods = new LinkedList<Integer>();
		incomes = new LinkedList<Integer>();
		decisions = new LinkedList<>();
		lossCarryback = new LinkedList<>();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return String.class;
		} else {
			return Integer.class;
		}
	}

	@Override
	public int getColumnCount() {
		return periods.size() + 1;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return "labels";
		} else {
			return String.valueOf(periods.get(columnIndex - 1));
		}
	}

	@Override
	public int getRowCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			switch (rowIndex) {
			case 0:
				return "Period";
			case 1:
				return "Income";
			case 2:
				return "Decision";
			case 3:
				return "Carry Back";
			default:
				return "";
			}
			
		} else {
			switch (rowIndex) {
			case 0:
				return periods.get(columnIndex - 1);
			case 1:
				return incomes.get(columnIndex - 1);
			case 2:
				return decisions.get(columnIndex - 1) == 0 ? Decision.SHARED : Decision.DIVIDED;
			case 3:
				return lossCarryback.get(columnIndex - 1);
			default:
				return "";
			}
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (rowIndex > 0) && (columnIndex != 0);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (rowIndex) {
		case 0:
			periods.set(columnIndex-1, (int) aValue);
			break;
		case 1:
			incomes.set(columnIndex-1, (int) aValue);
			break;
		case 2:
			if ((int) aValue < 0 || (int) aValue > 1) return;
			decisions.set(columnIndex-1, (int) aValue);
			break;
		case 3:
			lossCarryback.set(columnIndex-1, -1 * Math.abs((int) aValue));
			break;
		default:
			break;
		}
		fireTableStructureChanged();
	}

	public void updatePeriodNumber(int periodNumber) {
		if (periods.size() < periodNumber) {
			for (int i = periods.size(); i < periodNumber; i++) {
				periods.add(i + 1);
				incomes.add(0);
				decisions.add(0);
				lossCarryback.add(0);
			}
		} else {
			for (int i = periods.size(); i > periodNumber; i--) {
				periods.remove(i - 1);
				incomes.remove(i - 1);
				decisions.remove(i - 1);
				lossCarryback.remove(i - 1);
			}
		}
		fireTableStructureChanged();
	}
	
	/**
	 * all lists need to have the same length.
	 * decisions and carrybacks may be null
	 */
	public void setValues(List<Integer> incomes, List<Integer> decisions, List<Integer> carrybacks) {
		updatePeriodNumber(0); // delete all
		updatePeriodNumber(incomes.size());
		this.incomes = incomes;
		if (decisions!=null)
			this.decisions = decisions;
		if (carrybacks!=null)
			this.lossCarryback = carrybacks;
		fireTableStructureChanged();
	}
}
