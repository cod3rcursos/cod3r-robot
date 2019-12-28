package br.com.cod3r.robot.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import br.com.cod3r.robot.view.config.ConfigView;
import br.com.cod3r.robot.view.editor.EditorView;

public class MainView extends JFrame {

	private static final long serialVersionUID = 1L;

	public MainView() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		EditorView editorView = new EditorView(this);
		ConfigView configView = new ConfigView();

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Editor", editorView);
		tabs.addTab("Config", configView);

		this.setLayout(new BorderLayout());
		this.add(tabs, BorderLayout.CENTER);

		setSize(800, 600);
		setLocationRelativeTo(null);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void main(String[] args) {
		new MainView();
	}
}
