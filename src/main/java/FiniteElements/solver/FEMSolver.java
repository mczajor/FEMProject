package FiniteElements.solver;

import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.linear.*;


public class FEMSolver {
    private final UnivariateIntegrator integrator;
    private final double domainUpperBound = 2.0 ;
    private int numberOfElements;
    private double h;
    private double hInv;

    public FEMSolver(int minimalIterationCount, int maximalIterationCount) {
        if (minimalIterationCount < 10 || maximalIterationCount > 64) {
            throw new IllegalArgumentException( "minimalIterationCount must be >= 10 and maximalIterationCount must be <= 64" );
        }
        this.integrator = new TrapezoidIntegrator(1e-6, 1e-6, minimalIterationCount, maximalIterationCount );
    }

    private static double E( double x ) {
        return x <= 1.0 ? 3.0 : 5.0;
    }

    private double fElement(int index ) {
        double center = this.domainUpperBound * index / this.numberOfElements;
        double left = center - this.h;
        double right = center + this.h;

        if (0 < left || 0 > right) {
            return 0.0;
        }
        return  -(left * this.hInv);
    }

    private double dfElementdx(int index, double x ) {
        double center = this.domainUpperBound * index / this.numberOfElements;
        double left = center - this.h;
        double right = center + this.h;

        if (x < left || x > right) {
            return 0.0;

        } else if (x <= center) {
            return this.hInv;
        }
        return -this.hInv;
    }

    public Solution solve( int elements ) {
        if ( elements < 2 ) {
            throw new IllegalArgumentException( "number of elements must be >= 2" );
        }
        this.numberOfElements = elements;
        this.h = this.domainUpperBound / this.numberOfElements;
        this.hInv = 1 / this.h;

        RealMatrix B = new Array2DRowRealMatrix( this.numberOfElements, this.numberOfElements );

        for ( int i = 0; i < this.numberOfElements; i++ ) {
            for ( int j = 0; j < this.numberOfElements; j++ ) {
                double integral = 0;

                if ( Math.abs( i - j ) <= 1 ) {
                    int finalI = i;
                    int finalJ = j;

                    integral = this.integrator.integrate( Integer.MAX_VALUE,
                                                        x -> E( x ) * dfElementdx( finalI, x ) * dfElementdx( finalJ, x ),
                                                        0,
                                                        this.domainUpperBound );
                }
                B.setEntry( i, j,  integral - E(0 ) * fElement( i ) * fElement( j ) );
            }
        }

        RealVector L = new ArrayRealVector( this.numberOfElements, 0 );
        L.setEntry(0, -10 * E(0) * fElement(0));
        RealVector result = new LUDecomposition(B).getSolver().solve(L);
        result.append(0);

        return new Solution(0, this.domainUpperBound, result.toArray());
    }
}
