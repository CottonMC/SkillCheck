package io.github.cottonmc.skillcheck;

import io.github.cottonmc.cottonrpg.data.SimpleCharacterClass;
import io.github.cottonmc.skillcheck.util.CharSheetClass;

public class SkillCheckCharClass extends SimpleCharacterClass implements CharSheetClass {
  public SkillCheckCharClass(int maxLevel) {
    super(maxLevel);
  }

  @Override
  public int getNextLevelCost(int currentLevel) {
    //TODO: make cheaper?
    if (currentLevel > 3) {
      return 30;
    } else {
      return currentLevel == 0 ? 5 : 10 * currentLevel;
    }
  }
}
