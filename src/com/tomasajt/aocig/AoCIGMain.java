package com.tomasajt.aocig;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class AoCIGMain extends JFrame implements ActionListener, ItemListener {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new AoCIGMain(args);
	}

	Calendar calendar;
	int highestYear;
	int highestDay;
	JLabel dateLabel;
	JComboBox<Integer> yearCombo;
	JLabel decLabel;
	JComboBox<Integer> dayCombo;
	JLabel sessionLabel;
	JTextField sessionField;
	JButton inputButton;

	private AoCIGMain(String[] args) {
		super("Advent of Code input getter");
		calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		highestYear = calendar.get(Calendar.YEAR) - (calendar.get(Calendar.MONTH) < 11 ? 1 : 0);
		highestDay = Math.min(calendar.get(Calendar.DAY_OF_MONTH)
				- (calendar.get(Calendar.HOUR) < 6 && calendar.get(Calendar.AM) == 1 ? 1 : 0), 25);
		Insets insets = this.getInsets();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(insets.left + 400 + insets.right, insets.top + 240 + insets.bottom));
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setLayout(null);
		this.setResizable(false);
		dateLabel = new JLabel("Select your date:");
		yearCombo = new JComboBox<Integer>(integerArrayRange(2015, highestYear));
		decLabel = new JLabel("Dec");
		sessionLabel = new JLabel("Session ID (The text will be white)");
		sessionField = new JTextField(args.length > 0 ? args[0] : "");
		inputButton = new JButton("Get input file");

		yearCombo.setSelectedItem(highestYear);
		yearCombo.addItemListener(this);
		sessionField.setForeground(Color.white);
		sessionField.setSelectedTextColor(sessionField.getSelectionColor());
		inputButton.setActionCommand("get");
		inputButton.addActionListener(this);
		reloadDayCombo();

		this.add(dateLabel);
		this.add(yearCombo);
		this.add(decLabel);
		this.add(sessionLabel);
		this.add(sessionField);
		this.add(inputButton);

		setPosition(dateLabel, 20, 20);
		setPosAndSize(yearCombo, 125, 20, 60, 20);
		setPosition(decLabel, 190, 20);
		setPosition(sessionLabel, 20, 50);
		setPosAndSize(sessionField, 20, 70, 340, 20);
		setPosAndSize(inputButton, 20, 100, 120, 30);
	}

	private static void setPosAndSize(JComponent comp, int x, int y, int width, int height) {
		Insets insets = comp.getParent().getInsets();
		comp.setBounds(x + insets.left, y + insets.top, width, height);
	}

	private static void setPosition(JComponent comp, int x, int y) {
		setPosAndSize(comp, x, y, comp.getPreferredSize().width, comp.getPreferredSize().height);
	}

	private static Integer[] integerArrayRange(int start, int finish) {
		Integer[] arr = new Integer[finish - start + 1];
		for (int i = start; i <= finish; i++) {
			arr[i - start] = i;
		}
		return arr;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand() == "get") {
			try {
				URL u = new URL("https://adventofcode.com/" + yearCombo.getSelectedItem() + "/day/"
						+ dayCombo.getSelectedItem() + "/input");
				URLConnection connection = u.openConnection();
				connection.setRequestProperty("Cookie", "session=" + sessionField.getText());
				Scanner s = new Scanner(connection.getInputStream());
				String path = yearCombo.getSelectedItem() + "_Day" + dayCombo.getSelectedItem() + ".txt";
				File file = new File(path);
				if (file.exists()) {
					if (JOptionPane.showConfirmDialog(this, path + " already exists.\r\nDo you want to override it?",
							"Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
						return;
				}
				FileWriter fw = new FileWriter(file);
				while (s.hasNext()) {
					fw.write(s.nextLine());
					if (s.hasNext()) {
						fw.write("\r\n");
					}
				}
				fw.close();
				JOptionPane.showMessageDialog(this, "Success!\r\nFile daved as " + file.getAbsolutePath());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage() + "\r\n" + Arrays.asList(e.getStackTrace())
						.stream().map(st -> st.toString() + "\r\n").limit(5).reduce((str1, str2) -> str1 + str2).get());
			}

		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		reloadDayCombo();
	}

	private void reloadDayCombo() {
		highestDay = ((Integer) yearCombo.getSelectedItem()).intValue() == highestYear
				? Math.min(calendar.get(Calendar.DAY_OF_MONTH)
						- (calendar.get(Calendar.HOUR) < 6 && calendar.get(Calendar.AM_PM) == Calendar.AM ? 1 : 0), 25)
				: 25;
		Integer selectedItem = dayCombo == null ? highestDay : (Integer) dayCombo.getSelectedItem();
		if (dayCombo != null)
			this.remove(dayCombo);
		dayCombo = new JComboBox<Integer>(integerArrayRange(1, highestDay));
		dayCombo.setSelectedItem(Math.min(highestDay, selectedItem));
		this.add(dayCombo);
		setPosAndSize(dayCombo, 220, 20, 50, 20);
	}
}
