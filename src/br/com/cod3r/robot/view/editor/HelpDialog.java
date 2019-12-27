package br.com.cod3r.robot.view.editor;

import java.awt.Component;

import javax.swing.JOptionPane;

public class HelpDialog {
	public static void show(Component parent) {
		StringBuilder sb = new StringBuilder();
		sb.append("Ajuda");
		sb.append("\n======================");
		sb.append("\n[[->]] - Ctrl + Espaço");
		sb.append("\n[[<-]] - Backspace");
		sb.append("\n[[t]] - Tab");
		sb.append("\n[[e]] - Enter");
		sb.append("\n[[<]] - Left");
		sb.append("\n[[^]] - Up");
		sb.append("\n[[>]] - Right");
		sb.append("\n[[v]] - Down");
		sb.append("\n[[esc]] - Esc");
		sb.append("\n[[end]] - End");
		sb.append("\n[[2s]] - Espera 2 segundos");
		sb.append("\n[[200ms]] - Espera 200 milissegundos");
		sb.append("\n[[90min]] - Durante a digitação espera no mínimo 90 milissegundos");
		sb.append("\n[[150max]] - Durante a digitação espera no máximo 150 milissegundos");
		sb.append("\n[[b]] - Pausa digitação");
		sb.append("\n[[startHere]] - usado para mudar o 'início' do arquivo");
		sb.append("\n[[// comentario ]] - comentário... :P");
		sb.append("\n\n======================");
		sb.append("\nAtalhos");
		sb.append("\nctrl + s -> salvar");
		sb.append("\nctrl + - -> diminue a fonte");
		sb.append("\nctrl + = -> aumenta a fonte");

		JOptionPane.showMessageDialog(parent, sb.toString());
	}
}
