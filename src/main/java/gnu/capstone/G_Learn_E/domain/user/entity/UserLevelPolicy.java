package gnu.capstone.G_Learn_E.domain.user.entity;

import java.util.HashMap;
import java.util.Map;

public class UserLevelPolicy {

    private static final Map<Integer, Integer> levelExpMap = new HashMap<>();

    static {
        for (int level = 1; level <= 50; level++) {
            int requiredExp = (int)(100 * Math.pow(level, 0.7));
            levelExpMap.put(level, requiredExp);
        }
    }

    public static boolean canLevelUp(int currentLevel, int currentExp) {
        if (currentLevel >= 50) return false;
        return currentExp >= getRequiredExp(currentLevel);
    }

    public static int getRequiredExp(int level) {
        return levelExpMap.getOrDefault(level, Integer.MAX_VALUE);
    }
}