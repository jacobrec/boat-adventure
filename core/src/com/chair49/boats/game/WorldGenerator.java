package com.chair49.boats.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.Random;

public class WorldGenerator {
    public static int[][] generatePatch(int width, int height, int depth) {
        SimplexNoise n = new SimplexNoise(3435802);//3435802  (int)(Math.random()*1000000)
        int heights[][][] = new int[depth][height][width];
        for (int i = 0; i < depth; i++) {
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    heights[i][r][c] = (int) ((1 + n.noise(r / (Math.pow(2, i)), c / Math.pow(2, i))) * 255 * i / 8);
                }
            }
        }

        return merge(heights);
    }

    public static Pixmap getPixmap(int[][] merged) {
        int width = merged[0].length;
        int height = merged.length;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                pixmap.setColor(new Color(merged[r][c] / 255f, merged[r][c] / 255f, merged[r][c] / 255f, 1));
                pixmap.setColor(Color.BLUE);
                if (merged[r][c] > 170) {
                    pixmap.setColor(Color.WHITE);
                } else if (merged[r][c] > 150) {
                    pixmap.setColor(Color.FOREST);
                } else if (merged[r][c] > 140) {
                    pixmap.setColor(Color.GOLDENROD);
                } else if (merged[r][c] > 130) {
                    pixmap.setColor(Color.SKY);
                } else if (merged[r][c] < 90)
                    pixmap.setColor(Color.NAVY);
                pixmap.drawPixel(r, height - c);
            }
        }
        return pixmap;
    }

    private static int[][] merge(int[][][] maps) {
        int i;
        int[][] finalmap = new int[maps[0][0].length][maps[0].length];
        for (i = 0; i < maps.length; i++) {
            for (int r = 0; r < maps[0].length; r++) {
                for (int c = 0; c < maps[0][0].length; c++) {
                    finalmap[r][c] += maps[i][r][c];
                }
            }
        }
        for (int r = 0; r < maps[0].length; r++) {
            for (int c = 0; c < maps[0][0].length; c++) {
                finalmap[r][c] = (int) (finalmap[r][c] / (float) i);
            }
        }
        return finalmap;
    }
}

class SimplexNoise { // Simplex noise in 2D, 3D and 4D
    private int grad3[][] = {{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
            {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
            {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}};
    private int p[] = new int[256];
    // To remove the need for index wrapping, double the permutation table length
    private int perm[] = new int[512];

    public SimplexNoise(long seed) {
        for (int i = 0; i < 256; i++) {
            p[i] = i;
        }
        shuffleArray(p, seed);
        for (int i = 0; i < 512; i++) perm[i] = p[i & 255];
    }

    void shuffleArray(int[] ar, long seed) {
        Random rnd = new Random(seed);
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    private int fastfloor(double x) {
        return x > 0 ? (int) x : (int) x - 1;
    }

    private double dot(int g[], double x, double y) {
        return g[0] * x + g[1] * y;
    }

    public double noise(double xin, double yin) {
        double n0, n1, n2;
        final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
        double s = (xin + yin) * F2;
        int i = fastfloor(xin + s);
        int j = fastfloor(yin + s);
        final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
        double t = (i + j) * G2;
        double X0 = i - t;
        double Y0 = j - t;
        double x0 = xin - X0;
        double y0 = yin - Y0;
        int i1, j1;
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        }
        else {
            i1 = 0;
            j1 = 1;
        }
        double x1 = x0 - i1 + G2;
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2;
        double y2 = y0 - 1.0 + 2.0 * G2;
        int ii = i & 255;
        int jj = j & 255;
        int gi0 = perm[ii + perm[jj]] % 12;
        int gi1 = perm[ii + i1 + perm[jj + j1]] % 12;
        int gi2 = perm[ii + 1 + perm[jj + 1]] % 12;
        double t0 = 0.5 - x0 * x0 - y0 * y0;
        if (t0 < 0) n0 = 0.0;
        else {
            t0 *= t0;
            n0 = t0 * t0 * dot(grad3[gi0], x0, y0);
        }
        double t1 = 0.5 - x1 * x1 - y1 * y1;
        if (t1 < 0) n1 = 0.0;
        else {
            t1 *= t1;
            n1 = t1 * t1 * dot(grad3[gi1], x1, y1);
        }
        double t2 = 0.5 - x2 * x2 - y2 * y2;
        if (t2 < 0) n2 = 0.0;
        else {
            t2 *= t2;
            n2 = t2 * t2 * dot(grad3[gi2], x2, y2);
        }
        // scaled so return in [-1,1]
        return 70.0 * (n0 + n1 + n2);
    }

}
