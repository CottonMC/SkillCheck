package io.github.cottonmc.skillworks.api.dice;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dice {

	public static final Pattern PATTERN = Pattern.compile("(?<count>\\d+)\\s*d(?<sides>\\d+)\\s*(?:\\+(?<bonus>\\d+(?!d)))?");

	public static DiceResult roll(String formula) {
		Random rand = new Random();
		Matcher matcher = PATTERN.matcher(formula);
		DiceResult res = new DiceResult();
		while (matcher.find()) {
			int rolls = Integer.parseInt(matcher.group("count"));
			if (rolls < 1) throw new IllegalArgumentException("Must roll at least one die!");
			for (int i = 0; i < rolls; i++) {
				int sides = Integer.parseInt(matcher.group("sides"));
				if (sides < 1) throw new IllegalArgumentException("Die must have at least one side!");
				int roll = rand.nextInt(sides) + 1;
				if (roll == 1) res.fail();
				res.addNatural(roll);
				res.addToTotal(roll);
			}
			res.addToTotal(Integer.parseInt(matcher.group("bonus")));
		}
		return res;
	}
}
