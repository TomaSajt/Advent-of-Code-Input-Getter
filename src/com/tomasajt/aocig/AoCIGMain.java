package com.tomasajt.aocig;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AoCIGMain extends JFrame implements ActionListener, ItemListener {

	public static void main(String[] args) {
		new AoCIGMain(args);
	}

	static final long serialVersionUID = 1L;
	ScheduledExecutorService se;
	JSONParser parser;
	Calendar calendar;
	int highestYear;
	int highestDay;
	boolean canPressLeaderboardButton;
	JLabel dateLabel;
	JComboBox<Integer> yearCombo;
	JLabel decLabel;
	JComboBox<Integer> dayCombo;
	JLabel sessionLabel;
	JTextField sessionField;
	JLabel leaderboardLabel;
	JTextField leaderboardField;
	JButton getInputButton;
	JButton showLeaderboardButton;
	JLabel nextUnlock;
	JFrame leaderboardFrame;
	JTextArea leaderboardTextArea;
	JScrollPane leaderboardScrollPane;

	AoCIGMain(String[] args) {
		super("Advent of Code input getter");
		se = Executors.newScheduledThreadPool(0);
		parser = new JSONParser();
		calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTime(new Date());
		highestYear = calendar.get(Calendar.YEAR) - (calendar.get(Calendar.MONTH) < 11 ? 1 : 0);
		canPressLeaderboardButton = true;

		Insets insets = this.getInsets();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(insets.left + 400 + insets.right, insets.top + 240 + insets.bottom));
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setLayout(null);
		setResizable(false);

		dateLabel = new JLabel("Select your date:");
		yearCombo = new JComboBox<Integer>(integerArrayRange(2015, highestYear));
		decLabel = new JLabel("Dec");
		sessionLabel = new JLabel("Session ID");
		sessionField = new JTextField(args.length > 0 ? args[0] : "");
		leaderboardLabel = new JLabel("Leaderboard ID");
		leaderboardField = new JTextField(args.length > 1 ? args[1] : "");
		getInputButton = new JButton("Get input file");
		showLeaderboardButton = new JButton("Show leaderboard");
		nextUnlock = new JLabel();
		leaderboardFrame = new JFrame("Leaderboards");
		leaderboardTextArea = new JTextArea();
		leaderboardScrollPane = new JScrollPane(leaderboardTextArea);

		yearCombo.setSelectedItem(highestYear);
		yearCombo.addItemListener(this);
		reloadDayCombo();
		sessionField.setForeground(Color.white);
		sessionField.setSelectedTextColor(sessionField.getSelectionColor());
		leaderboardField.setForeground(Color.white);
		leaderboardField.setSelectedTextColor(sessionField.getSelectionColor());
		getInputButton.setActionCommand("get_input");
		getInputButton.addActionListener(this);
		showLeaderboardButton.setActionCommand("show_leaderboard");
		showLeaderboardButton.addActionListener(this);
		leaderboardFrame
				.setPreferredSize(new Dimension(insets.left + 400 + insets.right, insets.top + 240 + insets.bottom));
		leaderboardFrame.pack();
		leaderboardFrame.setLocationRelativeTo(null);
		leaderboardFrame.setResizable(false);
		leaderboardTextArea.setEditable(false);
		leaderboardTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));

		add(dateLabel);
		add(yearCombo);
		add(decLabel);
		add(sessionLabel);
		add(sessionField);
		add(leaderboardLabel);
		add(leaderboardField);
		add(getInputButton);
		add(showLeaderboardButton);
		add(nextUnlock);
		leaderboardFrame.add(leaderboardScrollPane);

		setPosition(dateLabel, 20, 20);
		setPosAndSize(yearCombo, 125, 20, 60, 20);
		setPosition(decLabel, 190, 20);
		setPosition(sessionLabel, 20, 50);
		setPosAndSize(sessionField, 20, 70, 220, 20);
		setPosition(leaderboardLabel, 271, 50);
		setPosAndSize(leaderboardField, 270, 70, 90, 20);
		setPosAndSize(getInputButton, 20, 100, 120, 30);
		setPosAndSize(showLeaderboardButton, 200, 100, 160, 30);
		setPosAndSize(nextUnlock, 180, 175, 200, 20);

		se.scheduleAtFixedRate(this::updateNextUnlock, 0, 1, TimeUnit.SECONDS);
	}

	static void setPosAndSize(JComponent comp, int x, int y, int width, int height) {
		Insets insets = comp.getParent().getInsets();
		comp.setBounds(x + insets.left, y + insets.top, width, height);
	}

	static void setPosition(JComponent comp, int x, int y) {
		setPosAndSize(comp, x, y, comp.getPreferredSize().width, comp.getPreferredSize().height);
	}

	static Integer[] integerArrayRange(int start, int finish) {
		Integer[] arr = new Integer[finish - start + 1];
		for (int i = start; i <= finish; i++) {
			arr[i - start] = i;
		}
		return arr;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand() == "get_input") {
			getInput();

		} else if (event.getActionCommand() == "show_leaderboard") {
			showLeaderboard();
		}
	}

	private void getInput() {
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
			/*
			 * JOptionPane.showMessageDialog(this, e.getLocalizedMessage() + "\r\n" +
			 * Arrays.asList(e.getStackTrace()) .stream().map(st -> st.toString() +
			 * "\r\n").limit(5).reduce((str1, str2) -> str1 + str2).get());
			 */
			error("Something went wrong with connecting to adventofcode.com\nMake sure your session ID is correct");
		}

	}

	@SuppressWarnings("unchecked")
	private void showLeaderboard() {
		if (canPressLeaderboardButton) {
			canPressLeaderboardButton = false;
			try {
				URL u = new URL("https://adventofcode.com/" + highestYear + "/leaderboard/private/view/"
						+ leaderboardField.getText() + ".json");
				URLConnection connection = u.openConnection();
				connection.setRequestProperty("Cookie", "session=" + sessionField.getText());
				Scanner s = new Scanner(connection.getInputStream(), "UTF-8");
				String jsonString = s.nextLine();
				s.close();
				List<LeaderboardMember> leaderboardMembers = new ArrayList<LeaderboardMember>();
				Iterator<Map.Entry<Object, Object>> iterator = ((JSONObject) (((JSONObject) parser.parse(jsonString))
						.get("members"))).entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<Object, Object> entry = iterator.next();
					leaderboardMembers.add(new LeaderboardMember((JSONObject) entry.getValue()));

				}
				Collections.sort(leaderboardMembers, new LeaderboardMember.SortByLocalScoreDesc());
				String text = "";
				for (LeaderboardMember leaderboardMember : leaderboardMembers) {
					String memberText = "\r\n  ";
					memberText += padRight(leaderboardMember.getName(), 27, ' ');
					memberText += "  ";
					memberText += "Score: " + padRight(leaderboardMember.getLocalScore(), 5, ' ');
					memberText += "  ";
					memberText += padLeft(leaderboardMember.getStars() + "\u2736", 4, ' ') + "  ";
					text += memberText;
				}
				text += "\r\n";
				leaderboardTextArea.setText(text);
				leaderboardFrame.setVisible(true);

			} catch (Exception e) {
				e.printStackTrace();
				error("Something went wrong with connecting to adventofcode.com\nMake sure both session and leaderboard IDs are correct");
			}
			se.schedule(() -> canPressLeaderboardButton = true, 3, TimeUnit.SECONDS);
		} else {
			info("You have to wait 3 seconds between getting the leaderboards");
		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		reloadDayCombo();
	}

	void reloadDayCombo() {
		highestDay = ((Integer) yearCombo.getSelectedItem()).intValue() == highestYear
				? Math.min(calendar.get(Calendar.DAY_OF_MONTH) - (calendar.get(Calendar.HOUR_OF_DAY) < 5 ? 1 : 0), 25)
				: 25;
		Integer selectedItem = dayCombo == null ? highestDay : (Integer) dayCombo.getSelectedItem();
		if (dayCombo != null)
			this.remove(dayCombo);
		dayCombo = new JComboBox<Integer>(integerArrayRange(1, highestDay));
		dayCombo.setSelectedItem(Math.min(highestDay, selectedItem));
		this.add(dayCombo);
		setPosAndSize(dayCombo, 220, 20, 50, 20);
	}

	void updateNextUnlock() {
		calendar.setTime(new Date());
		if (calendar.get(Calendar.DAY_OF_MONTH) < 25 && calendar.get(Calendar.MONTH) == 11) {
			int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 + calendar.get(Calendar.MINUTE) * 60
					+ calendar.get(Calendar.SECOND);
			int then = 5 * 60 * 60;
			int diff = (then - now + 24 * 60 * 60) % (24 * 60 * 60);
			nextUnlock.setText("Time until next unlock: " + String.format("%02d", diff / (60 * 60)) + ":"
					+ String.format("%02d", diff % (60 * 60) / 60) + ":" + String.format("%02d", diff % 60));
			if (diff == 0)
				reloadDayCombo();
		} else {
			nextUnlock.setText("The event is not in progress");
		}
	}

	String padRight(Object obj, int width, char ch) {
		return String.format("%-" + width + "." + width + "s", obj.toString());
	}

	String padLeft(Object obj, int width, char ch) {
		return String.format("%" + width + "." + width + "s", obj.toString());
	}

	void error(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	void info(String message) {
		JOptionPane.showMessageDialog(this, message, "", JOptionPane.INFORMATION_MESSAGE);
	}
}
