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

    public void setLeft(int column, int row) {
        xL = column;
        yL = row;
    }

    public void setRight(int column, int row) {
        xR = column;
        yR = row;
    }

    public void setGap(int column, int row) {
        xG = column;
        yG = row;
        gap = true;
    }

    public boolean good(int winNum) {
        length = (xR - xL) + (yR - yL) + 1;
        if (length >= winNum - 1 && (!capL || !capR)) {
            return true;
        } else
            return false;
    }

    public boolean posPair(Streak other) {
        if (this.yL - this.yR == 0 && other.yL - other.yR == 0) {
            if (Math.abs(this.yL - other.yL) == 1)
                return true;
        }
        return false;
    }
}

