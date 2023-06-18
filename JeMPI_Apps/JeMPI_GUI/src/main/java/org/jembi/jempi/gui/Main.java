package org.jembi.jempi.gui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class Main extends JPanel {
   public Main() {
      this.setLayout(new GridLayout(1, 0));
      final MyJTable table = new MyJTable(new MyTableModel());
      this.add(new JScrollPane(table));
   }

   public static void main(final String[] args) {
      javax.swing.SwingUtilities.invokeLater(() -> new Main().display());
   }

   private void display() {
      JFrame f = new JFrame("JeMPI GUI");
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      f.setContentPane(this);
      f.pack();
      f.setVisible(true);
   }

   private static class MyTableModel extends AbstractTableModel {
      private final String[] colNames = new String[]{"Aux ID",
                                                     "UID",
                                                     "Created",
                                                     "Given Name",
                                                     "Family Name",
                                                     "Gender",
                                                     "DOB",
                                                     "City",
                                                     "Phone Number",
                                                     "National ID",
                                                     "Score"};
      private int rowIndex = -1;
      private String[] rowData = null;

      MyTableModel() {
         super();
      }

      public int getColumnCount() {
         return colNames.length;
      }

      public int getRowCount() {
         return (int) Cache.getNumberRows();
      }

      @Override
      public String getColumnName(final int col) {
         return colNames[col];
      }

      public Object getValueAt(
            final int row,
            final int col) {
         if (row != rowIndex) {
            rowData = Cache.get(row);
            rowIndex = row;
         }
         return rowData[col];
      }

      @Override
      public Class getColumnClass(final int c) {
         var i = 0;
         while (getValueAt(i, c) == null) {
            i++;
         }
         return getValueAt(i, c).getClass();
      }

      @Override
      public boolean isCellEditable(
            final int row,
            final int col) {
         return col >= 2;
      }

   }

   private static class MyJTable extends JTable {

      private DefaultTableCellRenderer goldenRecordRenderer;
      private DefaultTableCellRenderer interactionRenderer;

      MyJTable() {
         super();
         setUp();
      }

      MyJTable(final TableModel tm) {
         super(tm);
         setUp();
      }

      MyJTable(
            final Object[][] data,
            final Object[] columns) {
         super(data, columns);
         setUp();
      }

      MyJTable(
            final int rows,
            final int columns) {
         super(rows, columns);
         setUp();
      }

      private int setColWidth(
            final String label,
            final int charWidth,
            final int chars) {
         final var width = charWidth * chars;
         this.getColumn(label).setMinWidth(width);
         this.getColumn(label).setMaxWidth(width);
         this.getColumn(label).setPreferredWidth(width);
         this.getColumn(label).setWidth(width);
         return width;
      }

      private void setUp() {
         this.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
         FontMetrics metrics = this.getFontMetrics(this.getFont());
         this.setRowHeight(Math.round((metrics.getHeight() * 1.4F)));
         final var charWidth = Math.round(metrics.charWidth('X') * 1.1F);
         var totalWidth = 0;
         totalWidth += setColWidth("Aux ID", charWidth, 17);
         totalWidth += setColWidth("UID", charWidth, 10);
         totalWidth += setColWidth("Created", charWidth, 28);
         totalWidth += setColWidth("Given Name", charWidth, 15);
         totalWidth += setColWidth("Family Name", charWidth, 15);
         totalWidth += setColWidth("Gender", charWidth, 7);
         totalWidth += setColWidth("DOB", charWidth, 10);
         totalWidth += setColWidth("City", charWidth, 15);
         totalWidth += setColWidth("Phone Number", charWidth, 15);
         totalWidth += setColWidth("National ID", charWidth, 20);
         totalWidth += setColWidth("Score", charWidth, 10);
         this.setPreferredScrollableViewportSize(new Dimension(totalWidth, 1100));
         this.setFillsViewportHeight(true);
      }

      @Override
      public TableCellRenderer getCellRenderer(
            final int row,
            final int column) {
         if (goldenRecordRenderer == null) {
            goldenRecordRenderer = new DefaultTableCellRenderer();
            goldenRecordRenderer.setBackground(new Color(0xde, 0xed, 0xfe));
         }
         if (interactionRenderer == null) {
            interactionRenderer = new DefaultTableCellRenderer();
            interactionRenderer.setBackground(new Color(0xfe, 0xf0, 0xde));
         }
         final var score = getValueAt(row, 10);
         if (score == null) {
            return goldenRecordRenderer;
         } else {
            return interactionRenderer;
         }
      }
   }

}
