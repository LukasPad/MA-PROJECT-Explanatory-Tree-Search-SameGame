package GroupCode;

import OldCode.BoardPanel;
import OldCode.MCTSPlayer;
import OldCode.SameGameBoard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ExplanationPanel extends JTextArea {
    private int xSizePanel = 300;
    private int ySizePanel = 300;
    private BoardPanel boardPanel;
    private MCTSPlayer bot;
    private ArrayList<Cluster> moves;

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
        // TODO (E): aggregate cluster to one explanation
        // TODO (B): feature collector -> run only once per mcts move (either via boardBanel.humanPlay or computerPlayThread or var in explanationPanel)
        // TODO (B): give feature representations: for tree features just iterate with findGameFeatures over all nodes
        // TODO: extract specific features from feature collector and filter by relevance based on the occurrences in the other nodes (and then compare them either to best move or selected move)
        // TODO: take mcts score into consideration for explanations
        // TODO: compare the extracted features to the lookup table aka. results of data analysis
        // TODO: give explanation based on analysis and extracted features

        // (relevance ranking => score) or (feature rules) => scoring/bad/tactical move

        explanation = getExplanation(boardX, boardY);
        this.repaint();
    }

    private String getExplanation(int boardX, int boardY) {
        String ex = "";
        ex += "MCTS simulations: " + bot.totalSimulations + "\n";

        boolean debug = false;
        if (debug){
            ex += "Available Moves: \n";
            for (int possibleMove : SameGameBoard.generateMoves(boardPanel.getPosition())){
                ex += "Move: x=" + possibleMove%15 +", y="+ Math.floor(possibleMove/15) +  "\n";
            }
        }

        int move = 15*boardY + boardX;
        int moveID = -1;
        for(Cluster cluster: moves){
            for (int tile:cluster.shape){
                if (tile == move){
                    moveID = cluster.ID;
                    break;
                }
            }
        }

        if (SameGameBoard.legalMove(boardPanel.getPosition(), boardPanel.getXDim(), boardPanel.getYDim(), boardX, boardY)) {
            return ex + "Explanation for move " + moveID + "!";
        } else {
            return ex + "Not a legal move!";
        }
    }

    public void congregateMoves(){
        Clusters movesGenerator = new Clusters(boardPanel.getPosition(), 15, 15,0, -1, -1);
        movesGenerator.generateIDs();
        moves = movesGenerator.getClusters(0);
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    public void setBot(MCTSPlayer bot) {
        this.bot = bot;
    }
}
