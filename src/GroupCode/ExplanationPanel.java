package GroupCode;

import OldCode.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ExplanationPanel extends JTextArea {
    private int xSizePanel = 300;
    private int ySizePanel = 300;

    private int xDim, yDim, nodeID;
    private BoardPanel boardPanel;
    private MCTSPlayer bot;
    private ArrayList<Cluster> moves;
    private HashMap<Integer, UCTNode> moveNodePairs = new HashMap<>();

    private FeatureCollector featureCollector;

    private String explanation;

    public ExplanationPanel(){
        setPreferredSize(new Dimension(xSizePanel, ySizePanel));
        explanation = "XAI not enabled!";
        setEditable(false);
        setFocusable(false);
        setBackground(new Color(236,233,216));
        featureCollector = new FeatureCollector();
        nodeID=0;
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
            return ex + "Explanation for move: " + moveID + "\nSims for this move: " + moveNodePairs.get(moveID).simulations +"\nScore for this move: " + moveNodePairs.get(moveID).topScore;
        } else {
            return ex + "Not a legal move!";
        }
    }

    public void congregateMoves(){
        Clusters movesGenerator = new Clusters(boardPanel.getPosition(), 15, 15,0, -1, -1);
        movesGenerator.generateIDs();
        moves = movesGenerator.getClusters(0);
        for (UCTEdge loop=bot.root.child;loop!=null;loop=loop.sibling){
            for(Cluster cluster : moves){
                for (int tile:cluster.shape){
                    if (tile == loop.move){
                        moveNodePairs.put(cluster.ID, loop.child);
                        break;
                    }
                }
            }
        }
        return;
    }

    public void updateFeatures(){
        xDim = boardPanel.getXDim();
        yDim = boardPanel.getYDim();
        getTreeFeautures(bot.root, boardPanel.getPosition());
    }

    public void getTreeFeautures(UCTNode n, byte[] pos){
        featureCollector.resetForNewGame();
        saveTree(n, 0 , pos, null, -1 , -1);
    }

    public int saveTree(UCTNode n, int depth, byte[] pos, byte[] prevPos, int parentID, int move)
    {
        if (n==null || n.simulations < 10) return 0;

        nodeID++;
        int nID = nodeID;

        if (parentID != -1) {
            featureCollector.findGameFeatures(pos, prevPos, xDim, yDim, depth, move, n.topScore, nID);
        } else {
            featureCollector.findGameFeatures(pos, null, xDim, yDim, depth, -1, n.topScore, nID);
        }
        featureCollector.addEdge(nID, parentID);

        int max_depth = depth;
        if (parentID == -1){
            for (UCTEdge loop=n.child;loop!=null;loop=loop.sibling)
            {
                int colorPlayed = pos[loop.move];
                byte[] position = new byte[255];
                System.arraycopy(pos, 0, position, 0, xDim*yDim);
                SameGameBoard.makeMove(position, loop.move, loop.move % 15, loop.move / 15, colorPlayed);
                SameGameBoard.dropDownStones(position, 15, 15);

                int child_depth = saveTree(loop.child, depth+1, position, pos, nID, loop.move);
                if (child_depth > max_depth){
                    max_depth = child_depth;
                }
            }
        } else {
            if (n.child != null){
                UCTEdge bestChild = n.child;
                for (UCTEdge loop=n.child;loop!=null;loop=loop.sibling){
                    if (loop.topScore > bestChild.topScore){
                        bestChild = loop;
                    }
                }

                int colorPlayed = pos[bestChild.move];
                byte[] position = new byte[255];
                System.arraycopy(pos, 0, position, 0, xDim*yDim);
                SameGameBoard.makeMove(position, bestChild.move, bestChild.move % 15, bestChild.move / 15, colorPlayed);
                SameGameBoard.dropDownStones(position, 15, 15);

                int child_depth = saveTree(bestChild.child, depth+1, position, pos, nID, bestChild.move);
                if (child_depth > max_depth){
                    max_depth = child_depth;
                }
            }
        }

        return max_depth;
    }

    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    public void setBot(MCTSPlayer bot) {
        this.bot = bot;
    }
}
