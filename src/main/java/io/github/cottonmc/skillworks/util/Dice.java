package io.github.cottonmc.skillworks.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dice {

	public static final Pattern PATTERN = Pattern.compile("(?<count>\\d+)\\s*d(?<sides>\\d+)\\s*(?:\\+(?<bonus>\\d+(?!d)))?");

	public static int roll(String formula) {
		Random rand = new Random();
		Matcher matcher = PATTERN.matcher(formula);
		int result = 0;
		while (matcher.find()) {
			int rolls = Integer.parseInt(matcher.group("count"));
			if (rolls < 1) throw new IllegalArgumentException("Must roll at least one die!");
			for (int i = 0; i < rolls; i++) {
				int sides = Integer.parseInt(matcher.group("sides"));
				if (sides < 1) throw new IllegalArgumentException("Die must have at least one side!");
				int roll = rand.nextInt(sides) + 1;
				if (roll == 1) return -1;
				result += roll;
			}
			result += Integer.parseInt(matcher.group("bonus"));
		}
		return result;
	}

	public static int[] rollWithNatural(String formula) {
		Random rand = new Random();
		Matcher matcher = PATTERN.matcher(formula);
		int[] result = new int[2];
		while (matcher.find()) {
			int rolls = Integer.parseInt(matcher.group("count"));
			if (rolls < 1) throw new IllegalArgumentException("Must roll at least one die!");
			for (int i = 0; i < rolls; i++) {
				int sides = Integer.parseInt(matcher.group("sides"));
				if (sides < 1) throw new IllegalArgumentException("Die must have at least one side!");
				int roll = rand.nextInt(sides) + 1;
				if (roll == 1) return new int[]{-1, 1};
				result[1] += roll;
			}
			result[0] = result[1]+Integer.parseInt(matcher.group("bonus"));
		}
		return result;
	}
}
