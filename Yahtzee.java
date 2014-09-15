/*
 * File: Yahtzee.java
 * ------------------
 * This program is the implementation of the Yahtzee game.
 */

import acm.graphics.GLabel;
import acm.io.IODialog;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

@SuppressWarnings("serial")
public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {

    /**
     * Prompts the user to enter the number of players and their names. Creates
     * the board for the game.
     */
    public void run() {
        IODialog dialog = getDialog();
        nPlayers = dialog.readInt("Enter number of players");
        playerNames = new String[nPlayers];
        for (int i = 1; i <= nPlayers; i++) {
            playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
        }
        initilaizeScore();
        display = new YahtzeeDisplay(getGCanvas(), playerNames);
        playGame();
    }

    private void initilaizeScore() {
        lowerScore = new int[nPlayers];
        upperScore = new int[nPlayers];
        scoreCard = new boolean[nPlayers][N_CATEGORIES];
    }

    /**
     * Plays the game and rounds the score at the end of 13 rounds per player.
     */
    private void playGame() {
        int roundCount = 0;
        while (roundCount < N_SCORING_CATEGORIES * nPlayers) {
            for (int i = 0; i < playerNames.length; i++) {
                playerRound(i + 1);
                roundCount++;
            }
        }
        for (int i = 0; i < playerNames.length; i++) {
            totalGameScore(i + 1);
        }
    }

    /**
     * Calculates and displays the final scores of each section and grand total.
     * @param playerNumber, int, the player number, between 1 and four.
     */
    private void totalGameScore(int playerNumber) {
        applyBonus(playerNumber);
        display.updateScorecard(UPPER_SCORE, playerNumber,
                upperScore[playerNumber - 1]);
        display.updateScorecard(LOWER_SCORE, playerNumber,
                lowerScore[playerNumber - 1]);
        display.updateScorecard(TOTAL, playerNumber, finalScore(playerNumber));
    }

    /**
     * Calculates the grand total.
     * @param playerNumber, int, the player number, between 1 and four.
     * @return int, finalScore, the final score of the player.
     */
    private int finalScore(int playerNumber) {
        int total = 0;
        if (upperScore[playerNumber - 1] >= BONUS_THRESHOLD) {
            total = upperScore[playerNumber - 1] + lowerScore[playerNumber - 1]
                    + BONUS;
        } else {
            total = upperScore[playerNumber - 1] + lowerScore[playerNumber - 1];
        }
        return total;
    }

    /**
     * Finds out if the player qualifies for a bonus and applies the bonus if
     * needed.
     * @param playerNumber, int, the player number, between 1 and four.
     */
    private void applyBonus(int playerNumber) {
        if (upperScore[playerNumber - 1] >= BONUS_THRESHOLD) {
            display.updateScorecard(UPPER_BONUS, playerNumber, BONUS);
        }
    }

    /**
     * Allows the player to execute a turn of three dice rolls.
     */
    private void playerRound(int playerNumber) {
        int[] diceRolled = rollDice(playerNumber);
        chooseCategory(diceRolled, playerNumber);
    }

    /**
     * Allows the player to roll the dice three times. Keeps track on the turn
     * number and the dice the player wishes to roll. On the first turn the
     * player is forced to roll all 5 dice. In order to get the value of the
     * dice rolled the method calls assignDiceValue and passes to it a boolean
     * array size 5. True means the dice should be rolled, false will keep the
     * dice at its current value. Finally, the method displays the values of the
     * dice on the screen.
     * @param playerNumber, int, the player number, between 1 and four.
     */
    private int[] rollDice(int playerNumber) {
        if (label != null) {
            remove(label);
        }
        showMessage(playerNames[playerNumber - 1] + ", roll the dice");
        int turn = 0;
        boolean[] whichDiceToRoll = new boolean[N_DICE];
        int[] noRoll = new int[N_DICE];
        int[] diceRolled = assignDiceValue(whichDiceToRoll, turn, noRoll);
        display.waitForPlayerToClickRoll(playerNumber);
        display.displayDice(diceRolled);
        turn++;
        while (turn < ROUND_TURNS) {
            whichDiceToRoll = chooseDice(whichDiceToRoll, playerNumber);
            diceRolled = assignDiceValue(whichDiceToRoll, turn, diceRolled);
            display.displayDice(diceRolled);
            turn++;
        }
        return diceRolled;
    }

    /**
     * Assigns values to the dice. If this is the first roll all dice will be
     * assigned a random value between 1 and 6. Otherwise, only dice which were
     * indicated by the user to be re rolled will be assigned new value, the
     * rest will retain their old value.
     * @param whichDiceToRoll, boolean array size 5, true indicates a re roll.
     * @param turn, int, between 0 and 2.
     * @param diceRolled, Integer array, the state of the dice in the previous roll.
     * @return Integer array of size 5 with the value of each dice.
     */
    private int[] assignDiceValue(boolean[] whichDiceToRoll, int turn,
            int[] diceRolled) {
        int[] diceValue = new int[N_DICE];
        if (turn == 0) {
            for (int i = 0; i < diceValue.length; i++) {
                diceValue[i] = rgen.nextInt(1, 6);
            }
        } else {
            for (int i = 0; i < diceValue.length; i++) {
                if (whichDiceToRoll[i]) {
                    diceValue[i] = rgen.nextInt(1, 6);
                    ;
                } else {
                    diceValue[i] = diceRolled[i];
                }
            }
        }
        return diceValue;
    }

    /**
     * Allows the user to indicate which dice to re roll.
     * 
     * @param whichDiceToRoll, boolean array, true if dice should be re rolled.
     * @param playerNumber, int, the player number, between 1 and four.
     * @return whichDiceToRoll, boolean array, true if dice should be re rolled.
     */
    private boolean[] chooseDice(boolean[] whichDiceToRoll, int playerNumber) {
        remove(label);
        showMessage(playerNames[playerNumber - 1]
                + ", pick the dice you'd like to roll again");
        display.waitForPlayerToSelectDice();
        for (int i = 0; i < whichDiceToRoll.length; i++) {
            if (display.isDieSelected(i)) {
                whichDiceToRoll[i] = true;
            } else {
                whichDiceToRoll[i] = false;
            }
        }
        return whichDiceToRoll;
    }

    /**
     * Lets the user choose the category he/she would like the turn to be scored
     * under. If the category is already marked the user is prompt to choose and
     * empty category.
     * @param diceRolled, int array size containing the values of the dice.
     * @param playerNumber, int, the player number, between 1 and four.
     */
    private void chooseCategory(int[] diceRolled, int playerNumber) {
        remove(label);
        showMessage(playerNames[playerNumber - 1]
                + ", choose an empty category for your score");
        int category = display.waitForPlayerToSelectCategory();
        if (scoreCard[playerNumber - 1][category] == false) {
            display.updateScorecard(category, playerNumber,
                    roundScore(category, diceRolled, playerNumber));
            scoreCard[playerNumber - 1][category] = true;
        } else {
            remove(label);
            showMessage("You already picked this category, choose a different one");
            chooseCategory(diceRolled, playerNumber);
        }
    }

    /**
     * Creates a message to show to the user.
     * @param text, String, the text to be shown.
     */
    private void showMessage(String text) {
        label = new GLabel(text);
        label.setFont("Ariel-20");
        add(label, 40, 400);
    }

    /**
     * Checks if the category the user requested is applicable for the dice
     * combination.
     * @param diceRolled, array of size 5 with the values of the dice rolled.
     * @param category, int, the category requested by the user.
     * @return boolean, true if the category is a fit, false if not.
     */
    private boolean checkCategory(int[] diceRolled, int category) {
        CategoryCheck cc = new CategoryCheck();
        if (category <= SIXES || category == CHANCE) {
            return true;
        }
        if (cc.threeKind(diceRolled) && category == THREE_OF_A_KIND) {
            return true;
        }
        if (cc.fourKind(diceRolled) && category == FOUR_OF_A_KIND) {
            return true;
        }
        if (cc.fullHouse(diceRolled) && category == FULL_HOUSE) {
            return true;
        }
        if (cc.yahtzee(diceRolled) && category == YAHTZEE) {
            return true;
        }
        if (cc.largeStraight(diceRolled) && category == LARGE_STRAIGHT) {
            return true;
        }
        if (cc.smallStraight(diceRolled) && category == SMALL_STRAIGHT) {
            return true;
        }
        return false;
    }

    /**
     * Calculates the score of each round based on the category and the dice
     * combination.
     * 
     * @param category, int, the category of the round.
     * @param playerNumber, int, the player number, between 1 and four.
     * @param diceRolled, array of size 5 with the values of the dice rolled.
     * @return int, the score of the round.
     */
    private int roundScore(int category, int[] diceRolled, int playerNumber) {
        int score = 0;
        if (checkCategory(diceRolled, category)) {
            switch (category) {
            case ONES:
                score = calculateUpperScore(diceRolled, 1, playerNumber);
                break;
            case TWOS:
                score = calculateUpperScore(diceRolled, 2, playerNumber);
                break;
            case THREES:
                score = calculateUpperScore(diceRolled, 3, playerNumber);
                break;
            case FOURS:
                score = calculateUpperScore(diceRolled, 4, playerNumber);
                break;
            case FIVES:
                score = calculateUpperScore(diceRolled, 5, playerNumber);
                break;
            case SIXES:
                score = calculateUpperScore(diceRolled, 6, playerNumber);
                break;
            case THREE_OF_A_KIND:
                score = calThreeOrFour(diceRolled);
                lowerScore[playerNumber - 1] += score;
                break;
            case FOUR_OF_A_KIND:
                score = calThreeOrFour(diceRolled);
                lowerScore[playerNumber - 1] += score;
                break;
            case FULL_HOUSE:
                score = 25;
                lowerScore[playerNumber - 1] += score;
                break;
            case SMALL_STRAIGHT:
                score = 30;
                lowerScore[playerNumber - 1] += score;
                break;
            case LARGE_STRAIGHT:
                score = 40;
                lowerScore[playerNumber - 1] += score;
                break;
            case YAHTZEE:
                score = 50;
                lowerScore[playerNumber - 1] += score;
                break;
            case CHANCE:
                score = calThreeOrFour(diceRolled);
                lowerScore[playerNumber - 1] += score;
                break;
            default:
                break;
            }
        }
        return score;
    }

    /**
     * Calculates a turn with three or four of a kind.
     * @param diceRolled, array of size 5 with the values of the dice rolled.
     * @return int, the score.
     */
    private int calThreeOrFour(int[] diceRolled) {
        int score = 0;
        for (Integer dice : diceRolled) {
            score += dice;
        }
        return score;
    }

    /**
     * Calculates the score for any category between ONES and SIXES, and CHANCE.
     * 
     * @param diceRolled, array of size 5 with the values of the dice rolled.
     * @param caseNum, int, the category of the turn.
     * @param playerNumber, int, the player number, between 1 and four.
     * @return int, the score.
     */
    private int calculateUpperScore(int[] diceRolled, int category,
            int playerNumber) {
        int score = 0;
        for (Integer dice : diceRolled) {
            if (dice == category) {
                score += category;
            }
        }
        upperScore[playerNumber - 1] += score;
        return score;
    }

    /* Private instance variables */
    private int nPlayers;
    private String[] playerNames;
    private YahtzeeDisplay display;
    private RandomGenerator rgen = new RandomGenerator();
    private int[] upperScore;
    private int[] lowerScore;
    private boolean[][] scoreCard;;
    GLabel label;

    public static void main(String[] args) {
        new Yahtzee().start(args);
    }
}
