package GroupCode;

import OldCode.BoardPanel;
import OldCode.MCTSPlayer;
import OldCode.SameGameBoard;

import javax.swing.*;
import java.awt.*;

public class ExplanationPanel extends JTextArea {
    private int xSizePanel = 300;
    private int ySizePanel = 300;
    private BoardPanel boardPanel;
    private MCTSPlayer bot;

    private String explanation;

    public ExplanationPanel(){
        setPreferredSize(new Dimension(xSizePanel, ySizePanel));
        explanation = "XAI not enabled!";
        setEditable(false);
        setFocusable(false);
        setBackground(new Color(236,233,216));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        xSizePanel=this.getWidth();
        ySizePanel=this.getHeight();
        this.setText(explanation);
    }

    public void updateExplanation(int boardX, int boardY) {
        explanation = getExplanation(boardX, boardY);
        this.repaint();
    }

    private String getExplanation(int boardX, int boardY) {
        String ex = "";
        ex += "MCTS simulations: " + bot.totalSimulations + "\n";
        if (SameGameBoard.legalMove(boardPanel.getPosition(), boardPanel.getXDim(), boardPanel.getYDim(), boardX, boardY)) {
            return ex + "Explanation for move " + boardX + ", " + boardY + "!";
        } else {
            return ex + "Not a legal move!";
        }
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    public void setBot(MCTSPlayer bot) {
        this.bot = bot;
    }
}
