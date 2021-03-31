import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColumnColorRenderer extends DefaultTableCellRenderer {
		Cell[][] cells;
		
		public ColumnColorRenderer(Cell[][] cells) {
			this.cells = cells;
		}
		
		public void setCells(Cell[][] cells) {
			this.cells = cells;
		}
		
    	public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if(row==5 && column==0){
				cell.setBackground(Color.YELLOW);
			}else if(row == 5 && column == 5) {
				cell.setBackground(Color.GREEN);
			}else if (cells[column][5-row].occupied == 2 && cells[column][5-row].critical == 2) {
				cell.setBackground(Color.RED);
			}else if(cells[column][5-row].occupied == 2 && cells[column][5-row].critical == 1) {
				cell.setBackground(Color.BLUE);
			}else if(cells[column][5-row].occupied == 1){
				cell.setBackground(Color.BLACK);
			}else{
				cell.setBackground(Color.WHITE);
			}
			return cell;
		}
	}
