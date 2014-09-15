/*
 * File: CategoryCheck.java
 * ------------------------
 * This file contains the CategoryCheck class which can be used to verify if 
 * a dice combination qualifies for a category choice. 
 */

public class CategoryCheck implements YahtzeeConstants {
    
    /**
     * Finds out if there are at least 3 dice with the same value.
     * @param diceRolled, Integer array, the state of the dice in the last roll.
     * @return true if 3 or more similar dice, else false.
     */
    public boolean threeKind(int[] diceRolled) {
        for (int i = 0; i < diceRolled.length; i++) {
            for (int j = i + 1; j < diceRolled.length; j++) {
                for (int k = j + 1; k < diceRolled.length; k++) {
                    if (diceRolled[i] == diceRolled[j]
                            && diceRolled[j] == diceRolled[k]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Finds out if there are at least 4 dice with the same value.
     * @param diceRolled, Integer array, the state of the dice in the last roll.
     * @return true if 4 or more similar dice, else false.
     */
    public boolean fourKind(int[] diceRolled) {
        for (int i = 0; i < diceRolled.length; i++) {
            for (int j = i + 1; j < diceRolled.length; j++) {
                for (int k = j + 1; k < diceRolled.length; k++) {
                    for (int l = k + 1; l < diceRolled.length; l++) {
                        if (diceRolled[i] == diceRolled[j]
                                && diceRolled[j] == diceRolled[k]
                                && diceRolled[k] == diceRolled[l]) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Finds out if the dice have a combination of 2 of a kind and 3 of another
     * kind.
     * @param diceRolled, Integer array, the state of the dice in the last roll.
     * @return true if the combination exists, else false.
     */
    public boolean fullHouse(int[] diceRolled) {
        int[] repeatCount = new int[6];
        for (int i = 0; i < diceRolled.length; i++) {
            if (diceRolled[i] == 1) {
                repeatCount[0]++;
            }
            if (diceRolled[i] == 2) {
                repeatCount[1]++;
            }
            if (diceRolled[i] == 3) {
                repeatCount[2]++;
            }
            if (diceRolled[i] == 4) {
                repeatCount[3]++;
            }
            if (diceRolled[i] == 5) {
                repeatCount[4]++;
            }
            if (diceRolled[i] == 6) {
                repeatCount[5]++;
            }
        }
        boolean threeKind = false;
        boolean twoKind = false;
        for (Integer count : repeatCount) {
            if (count == 3) {
                threeKind = true;
            }
            if (count == 2) {
                twoKind = true;
            }
        }
        if (threeKind && twoKind) {
            return true;
        }
        return false;
    }
    
    /**
     * Finds out if the dice combination contains one single dice value for all
     * five dice.
     * @param diceRolled, Integer array, the state of the dice in the last roll.
     * @return true if all dice are similar, else false.
     */
    public boolean yahtzee(int[] diceRolled) {
        if (diceRolled[0] == diceRolled[1] && diceRolled[1] == diceRolled[2]
                && diceRolled[2] == diceRolled[3]
                && diceRolled[3] == diceRolled[4]) {
            return true;
        }
        return false;
    }

    /**
     * Finds out if there is a large straight (5 consecutive dice).  For that
     * to happen there can not be any two dice with the same value. 
     * @param diceRolled, Integer array, the state of the dice in the last roll.
     * @return true if long straight, else false. 
     */
    public boolean largeStraight(int[] diceRolled) {
        for (int i = 0; i < diceRolled.length; i++) {
            for (int j = i + 1; j < diceRolled.length; j++) {
                if (diceRolled[i] == diceRolled[j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Finds out if there is a small straight (four consecutive numbers).  In
     * order for that to happen the numbers 3 and 4 must be in the dice 
     * combination.  If they are, proceed to check if the numbers 1 and 2, or 
     * 2 and 5, or 5 and 6 exists. 
     * @param diceRolled, Integer array, the state of the dice in the last roll.
     * @return true if small straight, else false.
     */
    public boolean smallStraight(int[] diceRolled) {
        boolean three = false;
        boolean four = false;
        for (int i = 0; i < diceRolled.length; i++) {
            if (diceRolled[i] == 3) {
                three = true;
            }
            if (diceRolled[i] == 4) {
                four = true;
            }
            if (three && four) {
                for (int j = 0; j < diceRolled.length; j++) {
                    for (int k = 0; k < diceRolled.length; k++) {
                        if (diceRolled[j] == 2 && diceRolled[k] == 1
                                || diceRolled[j] == 2 && diceRolled[k] == 1
                                || diceRolled[j] == 5 && diceRolled[k] == 6
                                || diceRolled[j] == 5 && diceRolled[k] == 2) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
