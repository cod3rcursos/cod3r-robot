package br.com.cod3r.robot.keyboard;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import br.com.cod3r.robot.exception.BlockException;
import br.com.cod3r.robot.exception.ChangeMaxWaitingTimeException;
import br.com.cod3r.robot.exception.ChangeMinWaitingTimeException;
import br.com.cod3r.robot.exception.WaitException;
import br.com.cod3r.robot.helper.OS;

public class KeyboardMapper {

	public static KeySequence getKeyCode(String text) {
		Command command = new Command(text);
		switch (command.text) {
		case "a":
			return new KeySequence("65");
		case "b":
			return new KeySequence("66");
		case "c":
			return new KeySequence("67");
		case "d":
			return new KeySequence("68");
		case "e":
			return new KeySequence("69");
		case "f":
			return new KeySequence("70");
		case "g":
			return new KeySequence("71");
		case "h":
			return new KeySequence("72");
		case "i":
			return new KeySequence("73");
		case "j":
			return new KeySequence("74");
		case "k":
			return new KeySequence("75");
		case "l":
			return new KeySequence("76");
		case "m":
			return new KeySequence("77");
		case "n":
			return new KeySequence("78");
		case "o":
			return new KeySequence("79");
		case "p":
			return new KeySequence("80");
		case "q":
			return new KeySequence("81");
		case "r":
			return new KeySequence("82");
		case "s":
			return new KeySequence("83");
		case "t":
			return new KeySequence("84");
		case "u":
			return new KeySequence("85");
		case "v":
			return new KeySequence("86");
		case "w":
			return new KeySequence("87");
		case "x":
			return new KeySequence("88");
		case "y":
			return new KeySequence("89");
		case "z":
			return new KeySequence("90");
		case "A":
			return new KeySequence("+16 65 -16");
		case "B":
			return new KeySequence("+16 66 -16");
		case "C":
			return new KeySequence("+16 67 -16");
		case "D":
			return new KeySequence("+16 68 -16");
		case "E":
			return new KeySequence("+16 69 -16");
		case "F":
			return new KeySequence("+16 70 -16");
		case "G":
			return new KeySequence("+16 71 -16");
		case "H":
			return new KeySequence("+16 72 -16");
		case "I":
			return new KeySequence("+16 73 -16");
		case "J":
			return new KeySequence("+16 74 -16");
		case "K":
			return new KeySequence("+16 75 -16");
		case "L":
			return new KeySequence("+16 76 -16");
		case "M":
			return new KeySequence("+16 77 -16");
		case "N":
			return new KeySequence("+16 78 -16");
		case "O":
			return new KeySequence("+16 79 -16");
		case "P":
			return new KeySequence("+16 80 -16");
		case "Q":
			return new KeySequence("+16 81 -16");
		case "R":
			return new KeySequence("+16 82 -16");
		case "S":
			return new KeySequence("+16 83 -16");
		case "T":
			return new KeySequence("+16 84 -16");
		case "U":
			return new KeySequence("+16 85 -16");
		case "V":
			return new KeySequence("+16 86 -16");
		case "W":
			return new KeySequence("+16 87 -16");
		case "X":
			return new KeySequence("+16 88 -16");
		case "Y":
			return new KeySequence("+16 89 -16");
		case "Z":
			return new KeySequence("+16 90 -16");
		case "0":
			return new KeySequence("48");
		case "1":
			return new KeySequence("49");
		case "2":
			return new KeySequence("50");
		case "3":
			return new KeySequence("51");
		case "4":
			return new KeySequence("52");
		case "5":
			return new KeySequence("53");
		case "6":
			return new KeySequence("54");
		case "7":
			return new KeySequence("55");
		case "8":
			return new KeySequence("56");
		case "9":
			return new KeySequence("57");
		case "-":
			return new KeySequence("45");
		case "=":
			return new KeySequence("61");
//		case "~":
//			return new KeySequence("[16] 131 [16] 32");
		case "!":
			return new KeySequence("+16 49 -16");
		case "@":
			return new KeySequence("+16 50 -16");
		case "#":
			return new KeySequence("+16 51 -16");
		case "$":
			return new KeySequence("+16 52 -16");
		case "%":
			return new KeySequence("+16 53 -16");
//		case "^":
//			return "[16] 131 [16] 32";
		case "&":
			return new KeySequence("+16 55 -16");
		case "*":
			return new KeySequence("+16 56 -16");
		case "(":
			return new KeySequence("+16 57 -16");
		case ")":
			return new KeySequence("+16 48 -16");
		case "_":
			return new KeySequence("+16 45 -16");
		case "+":
			return new KeySequence("+16 61 -16");
		case "\t":
			return new KeySequence("9");
		case "\n":
			return new KeySequence("10");
		case "\r":
			return new KeySequence("10");
		case "[":
			return new KeySequence("91");
		case "]":
			return new KeySequence("93");
		case "\\":
			return new KeySequence("92");
		case "{":
			return new KeySequence("+16 91 -16");
		case "}":
			return new KeySequence("+16 93 -16");
		case "|":
			return new KeySequence("+16 92 -16");
		case ";":
			return new KeySequence("59");
		case ":":
			return new KeySequence("+16 59 -16");
		case "'":
			return OS.isMac() ? new KeySequence("222 32") : new KeySequence("222");
		case "\"":
			return OS.isMac() ? new KeySequence("+16 222 -16 32") : new KeySequence("+16 222 -16");
		case "/":
			return new KeySequence("111");
		case ",":
			return new KeySequence("44");
		case "<":
			return new KeySequence("+16 44 -16");
		case ".":
			return new KeySequence("46");
		case ">":
			return new KeySequence("+16 46 -16");
		case " ":
			return new KeySequence("32");
		case "[[->]]":
			return new KeySequence("+17 32 -17", command.times);
		case "[[<-]]":
			return new KeySequence("8", command.times);
		case "[[t]]":
			return new KeySequence("9", command.times);
		case "[[e]]":
			return new KeySequence("10", command.times);
		case "[[ ]]":
			return new KeySequence("32", command.times);
		case "[[<]]":
			return new KeySequence("37", command.times);
		case "[[^]]":
			return new KeySequence("38", command.times);
		case "[[>]]":
			return new KeySequence("39", command.times);
		case "[[v]]":
			return new KeySequence("40", command.times);
		case "[[esc]]":
			return new KeySequence("27", command.times);
		case "[[end]]":
			return new KeySequence("35", command.times);
		case "[[home]]":
			return new KeySequence("36", command.times);
		case "[[ms]]":
			throw new WaitException(command.times);
		case "[[s]]":
			throw new WaitException(command.times * 1000);
		case "[[b]]":
			throw new BlockException();
		case "[[min]]":
			throw new ChangeMinWaitingTimeException(command.times);
		case "[[max]]":
			throw new ChangeMaxWaitingTimeException(command.times);
//		case "[[idea.c]]":
//			return "+17 111 -17 38 35";
//		case "[[ctrl+enter]]":
//			return "+17 10 -17";
//		case "[[commentLine]]":
//			return OS.isMac() ? "+157 47 -157" : "";
//		case "[[clearLine]]":
//			return OS.isMac() ? "+157 +16 37 -157 -16 8" : "";
		default:
			return toClipboard(command.text);
		}
	}

	/*
	 * Adiciona texto no clipboard e retorna command + v (mac) ou ctrl + v
	 * (windows/linux)
	 */
	private static KeySequence toClipboard(String string) {
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		return OS.isMac() ? new KeySequence("+157 86 -157") : new KeySequence("+17 86 -17");
	}
}