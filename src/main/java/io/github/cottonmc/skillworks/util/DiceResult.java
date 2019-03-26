package io.github.cottonmc.skillworks.util;

import java.util.ArrayList;
import java.util.List;

public class DiceResult {
	private int total;
	private List<Integer> naturals;
	private boolean critFail;

	public DiceResult() {
		this(0, new ArrayList<>(), false);
	}

	public DiceResult(int total, List<Integer> naturals, boolean critFail) {
		this.total = total;
		this.naturals = naturals;
		this.critFail = critFail;
	}

	public int getTotal() {
		return total;
	}

	public void addToTotal(int amount) {
		total += amount;
	}

	public List<Integer> getNaturals() {
		return naturals;
	}

	public void addNatural(int amount) {
		naturals.add(amount);
	}

	public boolean isCritFail() {
		return critFail;
	}

	public void setCritFail(boolean critFail) {
		this.critFail = critFail;
	}

	public void fail() {
		critFail = true;
	}

	public String getFormattedNaturals() {
		String text = naturals.toString();
		return text.substring(1, text.length()-1);
	}
}
