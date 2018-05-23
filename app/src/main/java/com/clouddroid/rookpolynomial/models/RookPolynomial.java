package com.clouddroid.rookpolynomial.models;

import android.text.Html;
import android.text.Spanned;


public class RookPolynomial {
    private static final int F_CHECK = 2;
    private static final int F_LOCK = 1;

    private int[][] matrix;
    private int[] factors;

    public RookPolynomial( int[][] matrix ) {
        this.matrix = matrix;

        calculate();
    }

    private void calculate() {
        factors = new int[matrix.length];

        for( int i=0; i<matrix.length; i++ )
            factors[i] = calculateFactor( i+1 );
    }

    private int calculateFactor( int figures ) {
        return recursiveFactor( figures, -1, -1 );
    }

    private int recursiveFactor( int figures, int prevX, int prevY ) {
        int factor = 0;
        if( figures == 0 )
            return 1;

        if( prevX != -1 && prevY != -1 )
            matrix[prevY][prevX] = F_CHECK;

        for( int i = prevY + 1; matrix.length-i >= figures; i++ ) {
            for( int j = 0; j < matrix.length; j++ ) {
                if( isFieldAvailable( j, i ) ) {
                    factor += recursiveFactor( figures - 1, j, i );
                }
            }
        }

        if( prevX != -1 && prevY != -1 )
            matrix[prevY][prevX] = 0;

        return factor;
    }

    private boolean isFieldAvailable( int x, int y ) {
        int l = matrix.length;
        int col = x;
        int row = y % l;

        for( int i = 0; i < l; i++ )
            if( matrix[row][i] == F_CHECK )
                return false;

        for (int[] aMatrix : matrix) {
            if (aMatrix[col] == F_CHECK) {
                return false;
            }
        }

        return matrix[y][x] == 0;
    }

    public String toString(){
        StringBuilder s = new StringBuilder("1");
        for( int i=0; i<matrix.length; i++ )
            if( factors[i] != 0 )
                s.append(" + ").append(factors[i]).append("x^").append(i + 1);
        return s.toString();
    }

    public Spanned toHtmlString(){
        StringBuilder s = new StringBuilder("1");
        for( int i=0; i<matrix.length; i++ )
            if( factors[i] != 0 )
                s.append(" + ").append(factors[i]).append("x<sup>").append(i + 1).append("</sup>");
        return Html.fromHtml(s.toString());
    }
}
