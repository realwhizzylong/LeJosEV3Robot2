/*Creating table and frames 
 * also to manage the format 
 of JTable and JFrame*/
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

public class CreatingMap{
	private Vector<String> columnName;
	private Vector<Vector<String>> columnValue;
	private JTable table;
	private boolean a = true;
	private MapModel mm = new MapModel();
	private Cell[][] cells;
	private ColumnColorRenderer ccr;
	
	public void updateMap(Cell[][] cells) {
		mm.setCells(cells);
		this.cells = cells;
		ccr.setCells(cells);
	}
	
	public void CreateTable(final Cell[][] cells) {
		ccr = new ColumnColorRenderer(cells);
		final JFrame frame = new JFrame("Map");
		Container container = frame.getContentPane();
		container.setLayout(new BorderLayout());
		frame.setSize(700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		table = new JTable(mm);
		mm.setCells(cells);
		table.setFont(new Font("Arial", Font.BOLD, 30));
		ccr.setHorizontalAlignment(SwingConstants.CENTER);
		table.setDefaultRenderer(Object.class, ccr);
		table.setRowHeight(100);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		container.add(table.getTableHeader(), BorderLayout.NORTH);
		container.add(table, BorderLayout.CENTER);
		int windowWidth = frame.getWidth();
		int windowHeight = frame.getHeight();
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		frame.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);
		frame.setVisible(true);
	}
}
