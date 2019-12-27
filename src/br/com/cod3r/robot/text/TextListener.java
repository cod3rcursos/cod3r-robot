package br.com.cod3r.robot.text;

public interface TextListener {

	public void onType();

	public void onUnblock();
	
	public void onReset();
	
	public void onRollback();
}
