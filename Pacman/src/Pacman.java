private void drawGameOverScreen(Graphics g) {
    // Background overlay
    g.setColor(new Color(0, 0, 0, 220));
    g.fillRect(0, 0, boardWidth, boardHeight);

    // Font sizes
    final int TITLE_SIZE = 48;         // "GAME OVER" text
    final int SCORE_SIZE = 28;         // Score display
    final int PLAYER_SIZE = 24;        // Player name
    final int BUTTON_SIZE = 22;        // Button text
    
    // Y position tracker
    int y = tileSize * 3;

    // GAME OVER title
    g.setFont(customFont.deriveFont(Font.BOLD, TITLE_SIZE));
    g.setColor(Color.RED);
    String title = "GAME OVER";
    FontMetrics fmTitle = g.getFontMetrics();
    g.drawString(title, (boardWidth - fmTitle.stringWidth(title)) / 2, y);

    // SCORE and RANK
    int potentialRank = getPlayerRank(score);
    y += 70;
    
    g.setFont(customFont.deriveFont(Font.PLAIN, SCORE_SIZE));
    g.setColor(Color.YELLOW);
    
    String scoreStr = "SCORE: " + score;
    FontMetrics fmScore = g.getFontMetrics();
    g.drawString(scoreStr, (boardWidth - fmScore.stringWidth(scoreStr)) / 2, y);

    y += 50;
    String rankStr = "RANK: " + (potentialRank == -1 ? "N/A" : potentialRank);
    g.drawString(rankStr, (boardWidth - fmScore.stringWidth(rankStr)) / 2, y);

    // PLAYER name (always shows "Player: " prefix)
    if (potentialRank != -1) {
        y += 60;
        g.setFont(customFont.deriveFont(Font.PLAIN, PLAYER_SIZE));
        g.setColor(Color.CYAN);
        
        String displayText = "Player: " + (nameEntered ? enteredName : enteredName + "_");
        FontMetrics fmPlayer = g.getFontMetrics();
        g.drawString(displayText, (boardWidth - fmPlayer.stringWidth(displayText)) / 2, y);
    }

    // Buttons
    y += 80;
    g.setFont(customFont.deriveFont(Font.PLAIN, BUTTON_SIZE));
    FontMetrics fmBtn = g.getFontMetrics();

    // RESTART Button
    String restartText = "RESTART";
    int restartX = (boardWidth - fmBtn.stringWidth(restartText)) / 2;
    restartButtonBounds = new Rectangle(restartX - 15, y - 25, fmBtn.stringWidth(restartText) + 30, 40);
    g.setColor(hoveredButton == restartButtonBounds ? Color.WHITE : Color.LIGHT_GRAY);
    g.drawString(restartText, restartX, y);

    // LEADERBOARD Button
    y += 50;
    String leaderboardText = "LEADERBOARD";
    int lbX = (boardWidth - fmBtn.stringWidth(leaderboardText)) / 2;
    leaderboardButtonBounds = new Rectangle(lbX - 15, y - 25, fmBtn.stringWidth(leaderboardText) + 30, 40);
    g.setColor(hoveredButton == leaderboardButtonBounds ? Color.WHITE : Color.LIGHT_GRAY);
    g.drawString(leaderboardText, lbX, y);

    // RETURN TO MENU Button
    y += 50;
    String returnText = "RETURN TO MENU";
    int retX = (boardWidth - fmBtn.stringWidth(returnText)) / 2;
    returnButtonBounds = new Rectangle(retX - 15, y - 25, fmBtn.stringWidth(returnText) + 30, 40);
    g.setColor(hoveredButton == returnButtonBounds ? Color.WHITE : Color.LIGHT_GRAY);
    g.drawString(returnText, retX, y);
}
