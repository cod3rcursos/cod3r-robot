package br.com.cod3r.robot.keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class KeySequence {
	
	private List<Integer> keyCodes = new ArrayList<>(); 
	private List<KeyAction> keyActions = new ArrayList<>();
	
	public KeySequence(String sequence) {
		this(sequence, 1);
	}
	
	public KeySequence(String sequence, int times) {
		if(sequence == null || sequence.isEmpty()) {
			return;
		}
		
		String[] keys = sequence.split(" ");
		
		for (int i = 0; i < times; i++) {			
			for(String key: keys) {
				if(key.startsWith("+")) {
					keyCodes.add(Integer.parseInt(key));
					keyActions.add(KeyAction.PRESS);
				} else if(key.startsWith("-")) {
					keyCodes.add(Math.abs(Integer.parseInt(key)));
					keyActions.add(KeyAction.RELEASE);
				} else {
					keyCodes.add(Integer.parseInt(key));
					keyActions.add(KeyAction.PRESS);
					
					keyCodes.add(Integer.parseInt(key));
					keyActions.add(KeyAction.RELEASE);				
				}
			}
		}
	}
	
	public void forEach(BiConsumer<Integer, KeyAction> fn) {
		for (int i = 0; i < keyCodes.size(); i++) {
			fn.accept(keyCodes.get(i), keyActions.get(i));
		}
	}
}
