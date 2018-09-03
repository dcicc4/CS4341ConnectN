package Utilities;

public class Streak {
    public int xL, yL;
    public int xR, yR;
    public int xG, yG;
    public int length;
    public boolean capL, capR, useful, gap;

    public Streak() {
        useful = true;
        gap = false;
        length = 0;
    }

    public void setLeft(int x, int y) {
        xL = x;
        yL = y;
    }

    public void setRight(int x, int y) {
        xR = x;
        yR = y;
    }

    public void setGap (int x, int y) {
        xG = x;
        yG = y;
        gap = true;
    }

    public boolean sized (int winNum){
        length = (xR - xL) + (yR - yL) + 1;
        if (length >= winNum -1){
            return true;
        }
        else
        return false;
    }
}

