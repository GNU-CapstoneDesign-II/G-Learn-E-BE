package gnu.capstone.G_Learn_E.domain.user.entity;

import java.util.HashMap;
import java.util.Map;

public class UserLevelPolicy {
    // 액션별 고정 XP
    public static final int EXP_SOLVE_PROBLEM   = 10;
    public static final int EXP_RESOLVE_PROBLEM  = 3;
    public static final int EXP_CREATE_WORKBOOK = 50;
    public static final int EXP_UPLOAD_WORKBOOK  = 60;
    public static final int EXP_REVIEW_WORKBOOK = 15;
    public static final int EXP_RECEIVE_LIKE    = 5;

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