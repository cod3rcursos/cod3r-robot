package br.com.cod3r.robot.text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;

import br.com.cod3r.robot.exception.BlockException;
import br.com.cod3r.robot.exception.ChangeMaxWaitingTimeException;
import br.com.cod3r.robot.exception.ChangeMinWaitingTimeException;
import br.com.cod3r.robot.exception.WaitException;
import br.com.cod3r.robot.helper.StoreData;
import br.com.cod3r.robot.keyboard.KeySequence;
import br.com.cod3r.robot.keyboard.KeyboardMapper;
import br.com.cod3r.robot.keyboard.Typist;

public class TextController implements HotKeyListener {

	private boolean ignoreBlock = false;
	private long minWaitingTime = 90;
	private long maxWaitingTime = 130;

	private static final TextController instance = new TextController();
	private final Text text = new Text();

	private final List<Consumer<TextEvent>> functions = new ArrayList<>();

	private TextController() {
		setupTypingLoop();
	}

	public static TextController getInstance() {
		return instance;
	}

	public void onEvent(Consumer<TextEvent> fn) {
		functions.add(fn);
	}

	public boolean isIgnoreBlock() {
		return ignoreBlock;
	}

	public void setIgnoreBlock(boolean ignoreBlock) {
		this.ignoreBlock = ignoreBlock;
	}

	public long getMinWaitingTime() {
		return minWaitingTime;
	}

	public void setMinWaitingTime(long minWaitingTime) {
		this.minWaitingTime = minWaitingTime;
	}

	public long getMaxWaitingTime() {
		return maxWaitingTime;
	}

	public void setMaxWaitingTime(long maxWaitingTime) {
		this.maxWaitingTime = maxWaitingTime;
	}

	public void load(String originalText) {
		text.load(originalText);
		text.setBlocked(true);
	}

	public String getRemainText() {
		return text.getRemainText();
	}

	public void clear() {
		text.clear();
	}
	
	private void setupTypingLoop() {
		Thread thread = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(getWaitingInterval());
					typeNextSequence();
				} catch (InterruptedException e) {
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	private void typeNextSequence() {
		if (!text.isBlocked()) {
			try {
				String keys = text.next();

				if (keys != null) {
					KeySequence keySequence = KeyboardMapper.getKeyCode(keys);
					Typist.type(keySequence);
				}
				
				boolean finished = keys == null || !text.hasNext();
				if (finished) {
					notifyListeners(TextEventType.FINISHED);
				}

				notifyListeners(TextEventType.TYPED);
			} catch (BlockException e) {
				if (!isIgnoreBlock()) {
					text.setBlocked(true);
				}
			} catch (WaitException e) {
				waitFor(e.getTime());
			} catch (ChangeMinWaitingTimeException e) {
				setMinWaitingTime(e.getTime());
			} catch (ChangeMaxWaitingTimeException e) {
				setMaxWaitingTime(e.getTime());
			}
		}
	}

	@Override
	public void onHotKey(HotKey key) {
		int keyCode = key.keyStroke.getKeyCode();
		int modifiers = key.keyStroke.getModifiers();

		waitForKeyReleased();

		int resetKey = StoreData.getInstance().getResetKey();
		int resetModifiers = StoreData.getInstance().getResetModifiers();
		int nextKey = StoreData.getInstance().getNextKey();
		int nextModifiers = StoreData.getInstance().getNextModifiers();
		int prevKey = StoreData.getInstance().getPrevKey();
		int prevModifiers = StoreData.getInstance().getPrevModifiers();

		if (keyCode == resetKey && modifiers == resetModifiers) {
			text.resetIndex();
		} else if (keyCode == nextKey && modifiers == nextModifiers) {
			text.setBlocked(false);
		} else if (keyCode == prevKey && modifiers == prevModifiers) {
			text.rollbackIndex();
		}
	}

	private long getWaitingInterval() {
		long min = getMinWaitingTime();
		long max = getMaxWaitingTime();
		long diff = max - min;
		return (long) (Math.random() * diff + min);
	}

	private void waitFor(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	private void waitForKeyReleased() {
		waitFor(250);
	}
	
	private void notifyListeners(TextEventType type) {
		functions.forEach(fn -> {
			fn.accept(new TextEvent(type, text.getRemainText()));
		});
	}
}
