/*Manage the updates of map and 
 * submit it to system. Then 
   rebuild the JTable*/

import javax.swing.table.AbstractTableModel;

public class MapModel extends AbstractTableModel {
	private Cell[][] map;
	private int mode = 0;
	
	public void setCells(Cell[][] a){
		map = a;
		this.fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(PCServer.map[columnIndex][5-rowIndex].occupied == 3) {
			if(columnIndex == PCServer.xPos && 5-rowIndex == PCServer.yPos) {
				return "#-H";
			}else {
				return "H";
			}
		}else if(PCServer.map[columnIndex][5-rowIndex].occupied == 2 && PCServer.map[columnIndex][5-rowIndex].critical != 0) {
			if(columnIndex == PCServer.xPos && 5-rowIndex == PCServer.yPos) {
				return "#-V";
			}else {
				return "V";
			}
		}else if(PCServer.map[columnIndex][5-rowIndex].occupied == 1) {
			return "O";
		}else if(columnIndex == PCServer.xPos && 5-rowIndex == PCServer.yPos){
			return "#";
		}else {
			return "";
		}
	}
}

